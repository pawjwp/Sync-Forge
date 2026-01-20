package net.sumik.sync.common.compat.thirst;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.sumik.sync.api.shell.ShellStateComponent;

import java.lang.reflect.Method;

public class ThirstShellStateComponent extends ShellStateComponent {
    private static final Capability<?> PLAYER_THIRST_CAPABILITY;

    static {
        try {
            Class<?> modCapabilitiesClass = Class.forName("dev.ghen.thirst.foundation.common.capability.ModCapabilities");
            java.lang.reflect.Field capabilityField = modCapabilitiesClass.getDeclaredField("PLAYER_THIRST");
            capabilityField.setAccessible(true);
            PLAYER_THIRST_CAPABILITY = (Capability<?>) capabilityField.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Thirst capability", e);
        }
    }

    private final ServerPlayer player;
    private int thirstLevel = 20;
    private int quenchedLevel = 5;
    private float thirstExhaustion = 0.0f;

    public ThirstShellStateComponent() {
        this(null);
    }

    public ThirstShellStateComponent(ServerPlayer player) {
        this.player = player;
        if (player != null) {
            loadFromPlayer(player);
        }
    }

    @Override
    public String getId() {
        return "thirst";
    }

    private void loadFromPlayer(ServerPlayer player) {
        var thirstObj = player.getCapability(PLAYER_THIRST_CAPABILITY).orElseThrow(
            () -> new IllegalStateException("Player missing Thirst capability")
        );

        try {
            Method getThirst = thirstObj.getClass().getMethod("getThirst");
            Method getQuenched = thirstObj.getClass().getMethod("getQuenched");
            Method getExhaustion = thirstObj.getClass().getMethod("getExhaustion");

            this.thirstLevel = (Integer) getThirst.invoke(thirstObj);
            this.quenchedLevel = (Integer) getQuenched.invoke(thirstObj);
            this.thirstExhaustion = (Float) getExhaustion.invoke(thirstObj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load thirst data from player", e);
        }
    }

    private void applyToPlayer(ServerPlayer player) {
        var thirstObj = player.getCapability(PLAYER_THIRST_CAPABILITY).orElseThrow(
            () -> new IllegalStateException("Player missing Thirst capability")
        );

        try {
            Method setThirst = thirstObj.getClass().getMethod("setThirst", int.class);
            Method setQuenched = thirstObj.getClass().getMethod("setQuenched", int.class);
            Method setExhaustion = thirstObj.getClass().getMethod("setExhaustion", float.class);
            Method updateThirstData = thirstObj.getClass().getMethod("updateThirstData", net.minecraft.world.entity.player.Player.class);

            setThirst.invoke(thirstObj, this.thirstLevel);
            setQuenched.invoke(thirstObj, this.quenchedLevel);
            setExhaustion.invoke(thirstObj, this.thirstExhaustion);
            updateThirstData.invoke(thirstObj, player);
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply thirst data to player", e);
        }


    }

    @Override
    public void clone(ShellStateComponent component) {
        ThirstShellStateComponent other = component.as(ThirstShellStateComponent.class);
        this.thirstLevel = other.thirstLevel;
        this.quenchedLevel = other.quenchedLevel;
        this.thirstExhaustion = other.thirstExhaustion;

        if (this.player != null) {
            applyToPlayer(this.player);
        }
    }

    @Override
    protected void readComponentNbt(CompoundTag nbt) {
        this.thirstLevel = nbt.getInt("thirst");
        this.quenchedLevel = nbt.getInt("quenched");
        this.thirstExhaustion = nbt.getFloat("exhaustion");
    }

    @Override
    protected CompoundTag writeComponentNbt(CompoundTag nbt) {
        nbt.putInt("thirst", this.thirstLevel);
        nbt.putInt("quenched", this.quenchedLevel);
        nbt.putFloat("exhaustion", this.thirstExhaustion);
        return nbt;
    }
}
