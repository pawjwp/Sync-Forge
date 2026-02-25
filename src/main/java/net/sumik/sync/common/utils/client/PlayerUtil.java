package net.sumik.sync.common.utils.client;

import com.google.common.collect.Queues;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.sumik.sync.Sync;
import net.sumik.sync.common.utils.WorldUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

@OnlyIn(Dist.CLIENT)
public final class PlayerUtil {
    private static final ResourceLocation ANY_WORLD = new ResourceLocation(Sync.MOD_ID, "any_world");
    private static final Minecraft CLIENT = Minecraft.getInstance();
    private static final ConcurrentMap<ResourceLocation, ConcurrentLinkedQueue<PlayerUpdate>> UPDATES = new ConcurrentHashMap<>();

    static {
        MinecraftForge.EVENT_BUS.register(PlayerUtil.class);
    }

    public static void recordPlayerUpdate(PlayerUpdate playerUpdate) {
        recordPlayerUpdate(null, playerUpdate);
    }

    public static void recordPlayerUpdate(ResourceLocation worldId, PlayerUpdate playerUpdate) {
        worldId = worldId == null ? ANY_WORLD : worldId;

        if (CLIENT.player != null && existsInTargetWorld(CLIENT.player, worldId)) {
            playerUpdate.onLoad(CLIENT.player, CLIENT.player.clientLevel, CLIENT);
        } else {
            UPDATES.computeIfAbsent(worldId, id -> Queues.newConcurrentLinkedQueue()).add(playerUpdate);
        }
    }

    private static boolean existsInTargetWorld(Entity entity, ResourceLocation worldId) {
        return worldId == ANY_WORLD || WorldUtil.isOf(worldId, entity.level());
    }

    private static void executeUpdates(LocalPlayer player, ClientLevel world, ConcurrentLinkedQueue<PlayerUpdate> queue) {
        if (queue == null) {
            return;
        }

        while (!queue.isEmpty()) {
            queue.poll().onLoad(player, world, CLIENT);
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide()) {
            return;
        }
        boolean isClientPlayer = event.getEntity() == CLIENT.player
                || (CLIENT.player == null && event.getEntity() instanceof LocalPlayer);
        if (isClientPlayer) {
            LocalPlayer player = (LocalPlayer) event.getEntity();
            ClientLevel world = (ClientLevel) event.getLevel();
            ResourceLocation worldId = WorldUtil.getId(world);
            executeUpdates(player, world, UPDATES.get(worldId));
            executeUpdates(player, world, UPDATES.get(ANY_WORLD));
        }
    }

    @FunctionalInterface
    public interface PlayerUpdate {
        void onLoad(LocalPlayer player, ClientLevel world, Minecraft client);
    }

    private PlayerUtil() {
    }
}