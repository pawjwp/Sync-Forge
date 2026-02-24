package net.sumik.sync.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.ForgeRegistries;
import net.sumik.sync.Sync;
import net.sumik.sync.api.event.PlayerSyncEvents;
import net.sumik.sync.api.shell.ShellState;
import net.sumik.sync.api.shell.ShellStateContainer;
import net.sumik.sync.common.block.AbstractShellContainerBlock;
import net.sumik.sync.common.block.ShellConstructorBlock;
import net.sumik.sync.common.config.SyncConfig;
import net.sumik.sync.common.entity.damage.FingerstickDamageSource;
import net.sumik.sync.common.utils.BlockPosUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShellConstructorBlockEntity extends AbstractShellContainerBlockEntity implements IEnergyStorage {
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> this);

    public ShellConstructorBlockEntity(BlockPos pos, BlockState state) {
        super(SyncBlockEntities.SHELL_CONSTRUCTOR.get(), pos, state);
    }

    @Override
    public void onServerTick(Level world, BlockPos pos, BlockState state) {
        super.onServerTick(world, pos, state);
        if (ShellConstructorBlock.isOpen(state)) {
            ShellConstructorBlock.setOpen(state, world, pos, BlockPosUtil.hasPlayerInside(pos, world));
        }
    }

    @Override
    public InteractionResult onUse(Level world, BlockPos pos, Player player, InteractionHand hand) {
        PlayerSyncEvents.ShellConstructionFailureReason failureReason = this.beginShellConstruction(player);
        if (failureReason == null) {
            return InteractionResult.SUCCESS;
        } else {
            player.displayClientMessage(failureReason.toText(), true);
            return InteractionResult.CONSUME;
        }
    }

    @Nullable
    private PlayerSyncEvents.ShellConstructionFailureReason beginShellConstruction(Player player) {
        PlayerSyncEvents.ShellConstructionFailureReason failureReason = this.shell == null
                ? PlayerSyncEvents.ALLOW_SHELL_CONSTRUCTION.invoker().allowShellConstruction(player, this)
                : PlayerSyncEvents.ShellConstructionFailureReason.OCCUPIED;

        if (failureReason != null) {
            return failureReason;
        }

        if (!this.checkAndConsumeRequiredItem(player)) {
            return PlayerSyncEvents.ShellConstructionFailureReason.MISSING_REQUIRED_ITEM;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            SyncConfig config = SyncConfig.getInstance();

            float damage = serverPlayer.server.isHardcore() ? config.hardcoreFingerstickDamage() : config.fingerstickDamage();

            boolean isCreative = !serverPlayer.gameMode.isSurvival();
            boolean isLowOnHealth = (player.getHealth() + player.getAbsorptionAmount()) <= damage;
            boolean hasTotemOfUndying = player.getMainHandItem().is(Items.TOTEM_OF_UNDYING) || player.getOffhandItem().is(Items.TOTEM_OF_UNDYING);
            if (isLowOnHealth && !isCreative && !hasTotemOfUndying && config.warnPlayerInsteadOfKilling()) {
                return PlayerSyncEvents.ShellConstructionFailureReason.NOT_ENOUGH_HEALTH;
            }

            player.hurt(FingerstickDamageSource.fingerstick(player), damage);
            this.shell = ShellState.empty(serverPlayer, this.worldPosition);
            if (isCreative && config.enableInstantShellConstruction()) {
                this.shell.setProgress(ShellState.PROGRESS_DONE);
            }
            this.setChanged();
            this.sync();
        }
        return null;
    }

    private boolean checkAndConsumeRequiredItem(Player player) {
        SyncConfig config = SyncConfig.getInstance();
        String requiredItemName = config.shellConstructionRequiredItem();
        if (requiredItemName == null || requiredItemName.isEmpty()) {
            return true;
        }

        ResourceLocation itemId = ResourceLocation.tryParse(requiredItemName);
        if (itemId == null) {
            Sync.LOGGER.warn("Invalid item format in config: " + requiredItemName);
            return true;
        }

        Item requiredItem = ForgeRegistries.ITEMS.getValue(itemId);
        if (requiredItem == null) {
            Sync.LOGGER.warn("Invalid item configured for shell construction: " + requiredItemName);
            return true;
        }

        boolean isCreative = player.isCreative();
        if (isCreative && !config.consumeItemInCreative()) {
            return player.getInventory().contains(new ItemStack(requiredItem));
        }

        int requiredCount = config.shellConstructionItemCount();
        int foundCount = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() == requiredItem) {
                foundCount += stack.getCount();
                if (foundCount >= requiredCount) {
                    break;
                }
            }
        }

        if (foundCount < requiredCount) {
            return false;
        }

        if (!isCreative || config.consumeItemInCreative()) {
            int toRemove = requiredCount;
            for (int i = 0; i < player.getInventory().items.size(); i++) {
                ItemStack stack = player.getInventory().items.get(i);
                if (stack.getItem() == requiredItem) {
                    int removeFromStack = Math.min(toRemove, stack.getCount());
                    stack.shrink(removeFromStack);
                    toRemove -= removeFromStack;
                    if (toRemove <= 0) {
                        break;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        ShellConstructorBlockEntity bottom = (ShellConstructorBlockEntity) this.getBottomPart().orElse(null);
        if (bottom == null || bottom.shell == null || bottom.shell.getProgress() >= ShellState.PROGRESS_DONE) {
            return 0;
        }

        int capacity = (int) SyncConfig.getInstance().shellConstructorCapacity();
        int missingFE = (int) Math.ceil((ShellState.PROGRESS_DONE - bottom.shell.getProgress()) * capacity);
        int accepted = Math.min(maxReceive, missingFE);

        if (accepted > 0 && !simulate) {
            bottom.shell.setProgress(bottom.shell.getProgress() + (float) accepted / capacity);
            bottom.setChanged();
        }

        return accepted;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        ShellConstructorBlockEntity bottom = (ShellConstructorBlockEntity) this.getBottomPart().orElse(null);
        if (bottom == null || bottom.shell == null) {
            return 0;
        }
        int cap = (int) SyncConfig.getInstance().shellConstructorCapacity();
        return (int) (bottom.shell.getProgress() * cap);
    }

    @Override
    public int getMaxEnergyStored() {
        ShellConstructorBlockEntity bottom = (ShellConstructorBlockEntity) this.getBottomPart().orElse(null);
        return bottom != null && bottom.shell != null
                ? (int) SyncConfig.getInstance().shellConstructorCapacity()
                : 0;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        if (cap == ShellStateContainer.CAPABILITY) {
            if (AbstractShellContainerBlock.isBottom(this.getBlockState())) {
                return LazyOptional.of(() -> this).cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyHandler.invalidate();
    }
}