package net.pawjwp.sync.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.pawjwp.sync.Sync;
import net.pawjwp.sync.api.networking.*;
import net.pawjwp.sync.api.networking.*;

public final class SyncPackets {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Sync.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void init() {
        CHANNEL.registerMessage(
                packetId++,
                SynchronizationRequestPacket.class,
                (packet, buffer) -> packet.write(buffer),
                buffer -> {
                    SynchronizationRequestPacket packet = new SynchronizationRequestPacket();
                    packet.read(buffer);
                    return packet;
                },
                (packet, contextSupplier) -> packet.handle(contextSupplier.get())
        );
    }

    @OnlyIn(Dist.CLIENT)
    public static void initClient() {
        CHANNEL.registerMessage(
                packetId++,
                ShellUpdatePacket.class,
                (packet, buffer) -> packet.write(buffer),
                buffer -> {
                    ShellUpdatePacket packet = new ShellUpdatePacket();
                    packet.read(buffer);
                    return packet;
                },
                (packet, contextSupplier) -> packet.handle(contextSupplier)
        );

        CHANNEL.registerMessage(
                packetId++,
                ShellStateUpdatePacket.class,
                (packet, buffer) -> packet.write(buffer),
                buffer -> {
                    ShellStateUpdatePacket packet = new ShellStateUpdatePacket();
                    packet.read(buffer);
                    return packet;
                },
                (packet, contextSupplier) -> packet.handle(contextSupplier)
        );

        CHANNEL.registerMessage(
                packetId++,
                SynchronizationResponsePacket.class,
                (packet, buffer) -> packet.write(buffer),
                buffer -> {
                    SynchronizationResponsePacket packet = new SynchronizationResponsePacket();
                    packet.read(buffer);
                    return packet;
                },
                (packet, contextSupplier) -> packet.handle(contextSupplier)
        );

        CHANNEL.registerMessage(
                packetId++,
                PlayerIsAlivePacket.class,
                (packet, buffer) -> packet.write(buffer),
                buffer -> {
                    PlayerIsAlivePacket packet = new PlayerIsAlivePacket();
                    packet.read(buffer);
                    return packet;
                },
                (packet, contextSupplier) -> packet.handle(contextSupplier)
        );

        CHANNEL.registerMessage(
                packetId++,
                ShellDestroyedPacket.class,
                (packet, buffer) -> packet.write(buffer),
                buffer -> {
                    ShellDestroyedPacket packet = new ShellDestroyedPacket();
                    packet.read(buffer);
                    return packet;
                },
                (packet, contextSupplier) -> packet.handle(contextSupplier)
        );
    }

    /**
     * Helper method to send a packet to the server
     */
    public static void sendToServer(Object packet) {
        CHANNEL.sendToServer(packet);
    }

    /**
     * Helper method to send a packet to a specific player
     */
    public static void sendToPlayer(Object packet, net.minecraft.server.level.ServerPlayer player) {
        CHANNEL.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player), packet);
    }

    /**
     * Helper method to send a packet to all players
     */
    public static void sendToAllPlayers(Object packet) {
        CHANNEL.send(net.minecraftforge.network.PacketDistributor.ALL.noArg(), packet);
    }

    /**
     * Helper method to send a packet to all players tracking a chunk
     */
    public static void sendToAllTracking(Object packet, net.minecraft.world.level.chunk.LevelChunk chunk) {
        CHANNEL.send(net.minecraftforge.network.PacketDistributor.TRACKING_CHUNK.with(() -> chunk), packet);
    }
}