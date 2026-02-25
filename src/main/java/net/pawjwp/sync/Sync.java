package net.pawjwp.sync;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.pawjwp.sync.client.render.CustomGameRenderer;
import net.pawjwp.sync.client.render.SyncRenderers;
import net.pawjwp.sync.common.block.SyncBlocks;
import net.pawjwp.sync.common.block.entity.SyncBlockEntities;
import net.pawjwp.sync.common.command.SyncCommands;
import net.pawjwp.sync.common.config.SyncConfig;
import net.pawjwp.sync.common.item.SyncCreativeTabs;
import net.pawjwp.sync.common.item.SyncItems;
import net.pawjwp.sync.compat.curios.CuriosCompat;
import net.pawjwp.sync.compat.diet.DietCompat;
import net.pawjwp.sync.compat.thirst.ThirstCompat;
import net.pawjwp.sync.networking.SyncPackets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Sync.MOD_ID)
public class Sync {
    public static final String MOD_ID = "sync";
    public static final String NAME = "Sync";
    public static final Logger LOGGER = LogManager.getLogger("sync");

    public Sync() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SyncConfig.COMMON_SPEC, "sync-common.toml");
        SyncBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        SyncCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        SyncBlocks.BLOCKS.register(modEventBus);
        SyncItems.ITEMS.register(modEventBus);
        SyncPackets.init();
        SyncCommands.init();
        modEventBus.addListener(this::commonSetup);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(this::onClientSetup);
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(CuriosCompat::init);
        event.enqueueWork(DietCompat::init);
        event.enqueueWork(ThirstCompat::init);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        CustomGameRenderer.initClient();
        SyncRenderers.initClient();
    }
}