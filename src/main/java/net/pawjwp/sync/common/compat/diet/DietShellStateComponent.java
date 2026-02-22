package net.pawjwp.sync.common.compat.diet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.pawjwp.sync.api.shell.ShellStateComponent;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class DietShellStateComponent extends ShellStateComponent {
    private static final Capability<?> DIET_TRACKER_CAPABILITY;

    static {
        try {
            Class<?> dietCapabilityClass = Class.forName("com.illusivesoulworks.diet.common.capability.DietCapability");
            java.lang.reflect.Field capabilityField = dietCapabilityClass.getDeclaredField("DIET_TRACKER");
            capabilityField.setAccessible(true);
            DIET_TRACKER_CAPABILITY = (Capability<?>) capabilityField.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Diet capability", e);
        }
    }

    private final ServerPlayer player;
    private Map<String, Float> dietValues = new HashMap<>();

    public DietShellStateComponent() {
        this(null);
    }

    public DietShellStateComponent(ServerPlayer player) {
        this.player = player;
        if (player != null) {
            loadFromPlayer(player);
        }
    }

    @Override
    public String getId() {
        return "diet";
    }

    private void loadFromPlayer(ServerPlayer player) {
        player.getCapability(DIET_TRACKER_CAPABILITY).ifPresent(trackerObj -> {
            try {
                Method getValues = trackerObj.getClass().getMethod("getValues");
                @SuppressWarnings("unchecked")
                Map<String, Float> values = (Map<String, Float>) getValues.invoke(trackerObj);
                this.dietValues = new HashMap<>(values);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load diet data from player", e);
            }
        });
    }

    private void applyToPlayer(ServerPlayer player) {
        var trackerObj = player.getCapability(DIET_TRACKER_CAPABILITY).orElseThrow(
            () -> new IllegalStateException("Player missing Diet capability")
        );

        try {
            if (this.dietValues.isEmpty()) {
                // Reset to config defaults by reinitializing the suite
                Method initSuite = trackerObj.getClass().getMethod("initSuite");
                initSuite.invoke(trackerObj);
            } else {
                // Apply stored values
                Method setValues = trackerObj.getClass().getMethod("setValues", Map.class);
                setValues.invoke(trackerObj, this.dietValues);
            }

            Method sync = trackerObj.getClass().getMethod("sync");
            sync.invoke(trackerObj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply diet data to player", e);
        }
    }

    @Override
    public void applyTo(ServerPlayer player) {
        applyToPlayer(player);
    }

    @Override
    public void clone(ShellStateComponent component) {
        DietShellStateComponent other = component.as(DietShellStateComponent.class);
        this.dietValues = new HashMap<>(other.dietValues);

        if (this.player != null) {
            applyToPlayer(this.player);
        }
    }

    @Override
    protected void readComponentNbt(CompoundTag nbt) {
        for (String key : nbt.getAllKeys()) {
            this.dietValues.put(key, nbt.getFloat(key));
        }
    }

    @Override
    protected CompoundTag writeComponentNbt(CompoundTag nbt) {
        for (Map.Entry<String, Float> entry : this.dietValues.entrySet()) {
            nbt.putFloat(entry.getKey(), entry.getValue());
        }
        return nbt;
    }
}
