package net.sumik.sync.api.networking;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.sumik.sync.common.utils.client.PlayerUtil;
import net.sumik.sync.common.utils.reflect.Activator;
import net.sumik.sync.common.utils.reflect.ClassUtil;
import net.sumik.sync.networking.SyncPackets;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Represents a packet that can be sent from a server to a client.
 */
public interface ClientPlayerPacket extends PlayerPacket {
    /**
     * Sends the packet to all players that are currently connected to the server.
     * @param server The server.
     */
    default void sendToAll(MinecraftServer server) {
        PlayerList playerList = server.getPlayerList();
        this.send(playerList.getPlayers());
    }

    /**
     * Sends the packet to the client.
     * @param player Player that should receive the packet.
     */
    default void send(ServerPlayer player) {
        SyncPackets.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), this);
    }

    /**
     * Sends the packet to all players within a certain radius of a position.
     * @param world The server world.
     * @param pos The position.
     * @param radius The radius.
     */
    default void send(ServerLevel world, BlockPos pos, double radius) {
        PacketDistributor.TargetPoint target =
                new PacketDistributor.TargetPoint(
                        pos.getX(), pos.getY(), pos.getZ(),
                        radius, world.dimension());

        SyncPackets.CHANNEL.send(
                PacketDistributor.NEAR.with(() -> target),
                this);
    }

    /**
     * Sends the packet to the client.
     * @param players Players that should receive the packet.
     */
    default void send(Collection<ServerPlayer> players) {
        this.send(players.stream());
    }

    /**
     * Sends the packet to the client.
     * @param players Players that should receive the packet.
     */
    default void send(Stream<ServerPlayer> players) {
        players.forEach(this::send);
    }

    /**
     * @return true if the packet should be executed on the render thread;
     * otherwise, false.
     */
    default boolean isRenderTask() {
        return false;
    }

    /**
     * @return Identifier of the world the packet should be executed in.
     */
    @Nullable
    default ResourceLocation getTargetWorldId() {
        return null;
    }

    /**
     * This method is called after the packet is delivered to the client side.
     *
     * @param ctx The network context.
     */
    @OnlyIn(Dist.CLIENT)
    default void execute(NetworkEvent.Context ctx) {
        Minecraft client = Minecraft.getInstance();


        PlayerUtil.recordPlayerUpdate(this.getTargetWorldId(), (player, w, c) -> {
            ClientPacketListener handler = client.getConnection();
            if (handler == null) {
                return;
            }

            if (this.isBackgroundTask()) {
                this.execute(client, player, handler, ctx);
            } else if (this.isRenderTask()) {
                RenderSystem.recordRenderCall(() -> this.execute(client, player, handler, ctx));
            } else {
                client.execute(() -> this.execute(client, player, handler, ctx));
            }
        });
    }

    /**
     * This method is called after the packet is delivered to the client side.
     *
     * @param client The client.
     * @param player The player.
     * @param handler The network handler that received this packet
     * @param ctx The network context.
     */
    @OnlyIn(Dist.CLIENT)
    default void execute(Minecraft client, LocalPlayer player, ClientPacketListener handler, NetworkEvent.Context ctx) { }

    /**
     * Handles the packet received from the network.
     * This is called by Forge's networking system.
     *
     * @param contextSupplier The network context supplier.
     */
    default void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();

        if (ctx.getDirection().getReceptionSide().isClient()) {
            boolean hasCustomExecute = ClassUtil.getMethod(this.getClass(), "execute",
                            Minecraft.class, LocalPlayer.class, ClientPacketListener.class, NetworkEvent.Context.class)
                    .map(method -> !method.getDeclaringClass().equals(ClientPlayerPacket.class))
                    .orElse(false);

            if (hasCustomExecute || !this.isBackgroundTask()) {
                execute(ctx);
            } else {
                // Execute immediately for background tasks without custom execute
                Minecraft client = Minecraft.getInstance();
                ClientPacketListener handler = client.getConnection();
                if (handler != null) {
                    LocalPlayer player = client.player;
                    if (player != null) {
                        this.execute(client, player, handler, ctx);
                    }
                }
            }
        }

        ctx.setPacketHandled(true);
    }

    /**
     * Encodes this packet to the buffer.
     *
     * @param buffer The buffer to write to.
     */
    default void encode(FriendlyByteBuf buffer) {
        this.write(buffer);
    }

    /**
     * Registers a client side handler for the specified packet.
     * Note: In Forge, registration is handled through SyncPackets.initClient()
     * This method exists for API compatibility.
     *
     * @param type Class of the packet.
     * @param <T> The packet type.
     */
    @OnlyIn(Dist.CLIENT)
    static <T extends ClientPlayerPacket> void register(Class<T> type) {
        // In Forge, registration is handled differently through SyncPackets.initClient()
        // This method exists for API compatibility but the actual registration
        // happens in SyncPackets using the Forge SimpleChannel system

        // The registration logic from Fabric would be:
        // Supplier<T> supplier = Activator.createSupplier(type).orElseThrow();
        // But in Forge we use the SimpleChannel registration in SyncPackets
    }
}