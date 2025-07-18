package net.sumik.sync.client.gui.controller;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public final class DeathScreenController {
    private static boolean suspended;

    public static boolean isSuspended() {
        return suspended;
    }

    public static void suspend() {
        suspended = true;
    }

    public static void restore() {
        suspended = false;
    }

    static {
        MinecraftForge.EVENT_BUS.register(DeathScreenController.class);
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        restore();
    }
}