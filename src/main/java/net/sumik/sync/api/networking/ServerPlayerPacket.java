package net.sumik.sync.api.networking;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.sumik.sync.networking.SyncPackets;

/**
 * Represents a packet that can be sent from a client to a server.
 */
public interface ServerPlayerPacket extends PlayerPacket {
    /**
     * Sends the packet to the server.
     */
    @OnlyIn(Dist.CLIENT)
    default void send() {
        SyncPackets.sendToServer(this);
    }

    /**
     * This method is called after the packet is delivered to the server side.
     *
     * @param server The server.
     * @param player The player, who sent the packet from the client side.
     * @param ctx The network context.
     */
    void execute(MinecraftServer server, ServerPlayer player, NetworkEvent.Context ctx);

    /**
     * Handles the packet on the server side.
     * @param ctx The network context.
     */
    default void handle(NetworkEvent.Context ctx) {
        if (ctx.getDirection().getReceptionSide().isServer()) {
            ServerPlayer player = ctx.getSender();
            MinecraftServer server = player.getServer();

            if (this.isBackgroundTask()) {
                this.execute(server, player, ctx);
            } else {
                server.execute(() -> this.execute(server, player, ctx));
            }
        }
        ctx.setPacketHandled(true);
    }
}