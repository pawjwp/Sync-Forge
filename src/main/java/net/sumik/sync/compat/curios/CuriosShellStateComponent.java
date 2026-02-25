package net.sumik.sync.compat.curios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.sumik.sync.api.shell.ShellStateComponent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public class CuriosShellStateComponent extends ShellStateComponent {
    private final Map<String, List<ItemStack>> curiosStacks = new HashMap<>();
    private final Map<String, List<ItemStack>> cosmeticStacks = new HashMap<>();

    @Override
    public String getId() {
        return "sync:curios";
    }

    @Override
    public void clone(ShellStateComponent component) {
        CuriosShellStateComponent other = component.as(CuriosShellStateComponent.class);
        if (other != null) {
            this.curiosStacks.clear();
            this.cosmeticStacks.clear();
            other.curiosStacks.forEach((id, stacks) -> {
                List<ItemStack> copies = new ArrayList<>();
                stacks.forEach(stack -> copies.add(stack.copy()));
                this.curiosStacks.put(id, copies);
            });
            other.cosmeticStacks.forEach((id, stacks) -> {
                List<ItemStack> copies = new ArrayList<>();
                stacks.forEach(stack -> copies.add(stack.copy()));
                this.cosmeticStacks.put(id, copies);
            });
        }
    }

    public static CuriosShellStateComponent fromPlayer(ServerPlayer player) {
        CuriosShellStateComponent component = new CuriosShellStateComponent();
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> handler.getCurios().forEach((id, stackHandler) -> {
            List<ItemStack> items = new ArrayList<>();
            IDynamicStackHandler stacks = stackHandler.getStacks();

            for (int i = 0; i < stacks.getSlots(); i++) {
                items.add(stacks.getStackInSlot(i).copy());
            }

            component.curiosStacks.put(id, items);
            if (stackHandler.hasCosmetic()) {
                List<ItemStack> cosmeticItems = new ArrayList<>();
                IDynamicStackHandler cosmeticStacks = stackHandler.getCosmeticStacks();

                for (int i = 0; i < cosmeticStacks.getSlots(); i++) {
                    cosmeticItems.add(cosmeticStacks.getStackInSlot(i).copy());
                }

                component.cosmeticStacks.put(id, cosmeticItems);
            }
        }));
        return component;
    }

    public void applyToPlayer(ServerPlayer player) {
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            handler.getCurios().forEach((id, stackHandler) -> {
                IDynamicStackHandler stacks = stackHandler.getStacks();

                for (int i = 0; i < stacks.getSlots(); i++) {
                    stacks.setStackInSlot(i, ItemStack.EMPTY);
                }

                if (stackHandler.hasCosmetic()) {
                    IDynamicStackHandler cosmeticStacks = stackHandler.getCosmeticStacks();

                    for (int i = 0; i < cosmeticStacks.getSlots(); i++) {
                        cosmeticStacks.setStackInSlot(i, ItemStack.EMPTY);
                    }
                }
            });
            this.curiosStacks.forEach((id, items) -> {
                ICurioStacksHandler curio = (ICurioStacksHandler) handler.getCurios().get(id);
                if (curio != null) {
                    IDynamicStackHandler stacks = curio.getStacks();

                    for (int i = 0; i < items.size() && i < stacks.getSlots(); i++) {
                        stacks.setStackInSlot(i, items.get(i).copy());
                    }
                }
            });
            this.cosmeticStacks.forEach((id, items) -> {
                ICurioStacksHandler curio = (ICurioStacksHandler) handler.getCurios().get(id);
                if (curio != null && curio.hasCosmetic()) {
                    IDynamicStackHandler stacks = curio.getCosmeticStacks();

                    for (int i = 0; i < items.size() && i < stacks.getSlots(); i++) {
                        stacks.setStackInSlot(i, items.get(i).copy());
                    }
                }
            });
        });
    }

    @Override
    protected void readComponentNbt(CompoundTag nbt) {
        this.curiosStacks.clear();
        this.cosmeticStacks.clear();
        if (nbt.contains("CuriosStacks")) {
            CompoundTag stacksTag = nbt.getCompound("CuriosStacks");

            for (String key : stacksTag.getAllKeys()) {
                ListTag list = stacksTag.getList(key, Tag.TAG_COMPOUND);
                List<ItemStack> items = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {
                    items.add(ItemStack.of(list.getCompound(i)));
                }

                this.curiosStacks.put(key, items);
            }
        }

        if (nbt.contains("CosmeticStacks")) {
            CompoundTag stacksTag = nbt.getCompound("CosmeticStacks");

            for (String key : stacksTag.getAllKeys()) {
                ListTag list = stacksTag.getList(key, Tag.TAG_COMPOUND);
                List<ItemStack> items = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {
                    items.add(ItemStack.of(list.getCompound(i)));
                }

                this.cosmeticStacks.put(key, items);
            }
        }
    }

    @Override
    protected CompoundTag writeComponentNbt(CompoundTag nbt) {
        CompoundTag stacksTag = new CompoundTag();
        this.curiosStacks.forEach((id, items) -> {
            ListTag list = new ListTag();

            for (ItemStack stack : items) {
                list.add(stack.save(new CompoundTag()));
            }

            stacksTag.put(id, list);
        });
        nbt.put("CuriosStacks", stacksTag);
        CompoundTag cosmeticTag = new CompoundTag();
        this.cosmeticStacks.forEach((id, items) -> {
            ListTag list = new ListTag();

            for (ItemStack stack : items) {
                list.add(stack.save(new CompoundTag()));
            }

            cosmeticTag.put(id, list);
        });
        nbt.put("CosmeticStacks", cosmeticTag);
        return nbt;
    }
}
