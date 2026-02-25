package net.sumik.sync.compat.thirst;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.sumik.sync.api.shell.ShellStateComponent;
import dev.ghen.thirst.foundation.common.capability.ModCapabilities;

public class ThirstShellStateComponent extends ShellStateComponent {
    private int thirst = 20;
    private int quenched = 5;
    private float exhaustion = 0.0f;

    @Override
    public String getId() {
        return "sync:thirst";
    }

    @Override
    public void clone(ShellStateComponent component) {
        ThirstShellStateComponent other = component.as(ThirstShellStateComponent.class);
        if (other != null) {
            this.thirst = other.thirst;
            this.quenched = other.quenched;
            this.exhaustion = other.exhaustion;
        } else {
            this.thirst = 20;
            this.quenched = 5;
            this.exhaustion = 0.0f;
        }
    }

    public static ThirstShellStateComponent fromPlayer(ServerPlayer player) {
        ThirstShellStateComponent component = new ThirstShellStateComponent();
        player.getCapability(ModCapabilities.PLAYER_THIRST).ifPresent(thirstCap -> {
            component.thirst = thirstCap.getThirst();
            component.quenched = thirstCap.getQuenched();
            component.exhaustion = thirstCap.getExhaustion();
        });
        return component;
    }

    public void applyToPlayer(ServerPlayer player) {
        player.getCapability(ModCapabilities.PLAYER_THIRST).ifPresent(thirstCap -> {
            thirstCap.setThirst(this.thirst);
            thirstCap.setQuenched(this.quenched);
            thirstCap.setExhaustion(this.exhaustion);
            thirstCap.updateThirstData(player);
        });
    }

    @Override
    protected void readComponentNbt(CompoundTag nbt) {
        this.thirst = nbt.getInt("thirst");
        this.quenched = nbt.getInt("quenched");
        this.exhaustion = nbt.getFloat("exhaustion");
    }

    @Override
    protected CompoundTag writeComponentNbt(CompoundTag nbt) {
        nbt.putInt("thirst", this.thirst);
        nbt.putInt("quenched", this.quenched);
        nbt.putFloat("exhaustion", this.exhaustion);
        return nbt;
    }
}
