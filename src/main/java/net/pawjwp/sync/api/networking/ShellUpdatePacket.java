package net.pawjwp.sync.api.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.pawjwp.sync.Sync;
import net.pawjwp.sync.api.shell.Shell;
import net.pawjwp.sync.api.shell.ShellState;

import java.util.Collection;
import java.util.List;

public class ShellUpdatePacket implements ClientPlayerPacket {
    private ResourceLocation worldId;
    private boolean isArtificial;
    private Collection<ShellState> states;

    public ShellUpdatePacket() {
        this.states = List.of();
    }

    public ShellUpdatePacket(ResourceLocation worldId, boolean isArtificial, Collection<ShellState> states) {
        this.worldId = worldId;
        this.isArtificial = isArtificial;
        this.states = states == null ? List.of() : states;
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(Sync.MOD_ID, "packet.shell.update");
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.worldId);
        buffer.writeBoolean(this.isArtificial);
        buffer.writeVarInt(this.states.size());
        this.states.forEach(x -> buffer.writeNbt(x.writeNbt(new CompoundTag())));
    }

    @Override
    public void read(FriendlyByteBuf buffer) {
        this.worldId = buffer.readResourceLocation();
        this.isArtificial = buffer.readBoolean();
        this.states = buffer.readList(subBuffer -> ShellState.fromNbt(subBuffer.readNbt()));
    }

    @Override
    public ResourceLocation getTargetWorldId() {
        return this.worldId;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void execute(Minecraft client, LocalPlayer player, ClientPacketListener handler, NetworkEvent.Context ctx) {
        Shell shell = (Shell)player;
        shell.changeArtificialStatus(this.isArtificial);
        shell.setAvailableShellStates(this.states.stream());
    }
}