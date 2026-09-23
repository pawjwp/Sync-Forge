package net.pawjwp.sync.compat.journeymap;

import journeymap.common.events.ServerEventHandler;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;

public class JourneyMapCompat {
    // Sends an event upon death to the client and server waypoint managers
    public static void makeDeathWaypoint(ServerPlayer player) {
        player.connection.send(new ClientboundEntityEventPacket(player, (byte)3));

        if (ModList.get().isLoaded("journeymap")) {
            new ServerEventHandler().onPlayerDeath(player);
        }
    }
}