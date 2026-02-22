package net.pawjwp.sync.common.compat.curios;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.pawjwp.sync.api.shell.ShellStateComponent;

import java.lang.reflect.Method;

public class CuriosShellStateComponent extends ShellStateComponent {
    private static final Capability<?> CURIOS_INVENTORY_CAPABILITY;

    static {
        try {
            Class<?> curiosCapabilityClass = Class.forName("top.theillusivec4.curios.api.CuriosCapability");
            java.lang.reflect.Field capabilityField = curiosCapabilityClass.getDeclaredField("INVENTORY");
            capabilityField.setAccessible(true);
            CURIOS_INVENTORY_CAPABILITY = (Capability<?>) capabilityField.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Curios capability", e);
        }
    }

    private final ServerPlayer player;
    private ListTag curiosData = new ListTag();

    public CuriosShellStateComponent() {
        this(null);
    }

    public CuriosShellStateComponent(ServerPlayer player) {
        this.player = player;
        if (player != null) {
            loadFromPlayer(player);
        }
    }

    @Override
    public String getId() {
        return "curios";
    }

    private void loadFromPlayer(ServerPlayer player) {
        var curiosHandler = player.getCapability(CURIOS_INVENTORY_CAPABILITY).orElseThrow(
            () -> new IllegalStateException("Player missing Curios capability")
        );

        try {
            Method saveInventory = curiosHandler.getClass().getMethod("saveInventory", boolean.class);
            this.curiosData = (ListTag) saveInventory.invoke(curiosHandler, false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load curios data from player", e);
        }
    }

    private void applyToPlayer(ServerPlayer player) {
        var curiosHandler = player.getCapability(CURIOS_INVENTORY_CAPABILITY).orElseThrow(
            () -> new IllegalStateException("Player missing Curios capability")
        );

        try {
            // First, clear the current curios inventory
            Method saveInventory = curiosHandler.getClass().getMethod("saveInventory", boolean.class);
            saveInventory.invoke(curiosHandler, true); // true = clear while saving

            // Then load the new inventory data (even if empty)
            Method loadInventory = curiosHandler.getClass().getMethod("loadInventory", ListTag.class);
            loadInventory.invoke(curiosHandler, this.curiosData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply curios data to player", e);
        }
    }

    @Override
    public void applyTo(ServerPlayer player) {
        applyToPlayer(player);
    }

    @Override
    public void clone(ShellStateComponent component) {
        CuriosShellStateComponent other = component.as(CuriosShellStateComponent.class);
        this.curiosData = other.curiosData.copy();

        if (this.player != null) {
            applyToPlayer(this.player);
        }
    }

    @Override
    protected void readComponentNbt(CompoundTag nbt) {
        this.curiosData = nbt.getList("CuriosInventory", 10); // 10 = CompoundTag type
    }

    @Override
    protected CompoundTag writeComponentNbt(CompoundTag nbt) {
        if (!this.curiosData.isEmpty()) {
            nbt.put("CuriosInventory", this.curiosData);
        }
        return nbt;
    }
}
