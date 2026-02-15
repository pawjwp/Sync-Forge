package net.pawjwp.sync.api.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.pawjwp.sync.Sync;

public class ShellDestroyedPacket implements ClientPlayerPacket {
    private BlockPos pos;

    public ShellDestroyedPacket() {
        this.pos = BlockPos.ZERO;
    }

    public ShellDestroyedPacket(BlockPos pos) {
        this.pos = pos == null ? BlockPos.ZERO : pos;
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(Sync.MOD_ID, "packet.shell.destroyed");
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.pos);
    }

    @Override
    public void read(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void execute(Minecraft client, LocalPlayer player, ClientPacketListener handler, NetworkEvent.Context ctx) {
        for (int i = 0; i < 3; ++i) {
            player.clientLevel.addDestroyBlockEffect(this.pos, Blocks.DEEPSLATE.defaultBlockState());
            player.clientLevel.addDestroyBlockEffect(this.pos.above(), Blocks.DEEPSLATE.defaultBlockState());
        }
        player.clientLevel.playSound(player, this.pos, SoundEvents.DEEPSLATE_BREAK, SoundSource.BLOCKS, 1F, player.getVoicePitch());
    }
}