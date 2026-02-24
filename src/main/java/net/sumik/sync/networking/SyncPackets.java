package net.sumik.sync.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.sumik.sync.Sync;
import net.sumik.sync.api.networking.*;

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

    public static void sendToServer(Object packet) {
        CHANNEL.sendToServer(packet);
    }
}
