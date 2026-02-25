package net.sumik.sync.compat.diet;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.sumik.sync.api.shell.ShellStateComponent;
import com.illusivesoulworks.diet.common.capability.DietCapability;

public class DietShellStateComponent extends ShellStateComponent {
    private final Map<String, Float> dietValues = new HashMap<>();

    @Override
    public String getId() {
        return "sync:diet";
    }

    @Override
    public void clone(ShellStateComponent component) {
        this.dietValues.clear();
        DietShellStateComponent other = component.as(DietShellStateComponent.class);
        if (other != null) {
            this.dietValues.putAll(other.dietValues);
        }
    }

    public static DietShellStateComponent fromPlayer(ServerPlayer player) {
        DietShellStateComponent component = new DietShellStateComponent();
        DietCapability.get(player).ifPresent(tracker -> {
            component.dietValues.putAll(tracker.getValues());
        });
        return component;
    }

    public void applyToPlayer(ServerPlayer player) {
        DietCapability.get(player).ifPresent(tracker -> {
            if (this.dietValues.isEmpty()) {
                tracker.load(new CompoundTag());
            } else {
                tracker.setValues(this.dietValues);
            }
            tracker.sync();
        });
    }

    @Override
    protected void readComponentNbt(CompoundTag nbt) {
        this.dietValues.clear();
        for (String key : nbt.getAllKeys()) {
            this.dietValues.put(key, nbt.getFloat(key));
        }
    }

    @Override
    protected CompoundTag writeComponentNbt(CompoundTag nbt) {
        this.dietValues.forEach(nbt::putFloat);
        return nbt;
    }
}
