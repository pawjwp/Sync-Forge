package net.sumik.sync;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.sumik.sync.client.render.CustomGameRenderer;
import net.sumik.sync.client.render.SyncRenderers;
import net.sumik.sync.common.block.SyncBlocks;
import net.sumik.sync.common.block.entity.SyncBlockEntities;
import net.sumik.sync.common.command.SyncCommands;
import net.sumik.sync.common.compat.thirst.ThirstCompat;
import net.sumik.sync.common.config.SyncConfig;
import net.sumik.sync.common.item.SyncCreativeTabs;
import net.sumik.sync.common.item.SyncItems;
import net.sumik.sync.networking.SyncPackets;
import org.slf4j.Logger;

@Mod(Sync.MOD_ID)
public class Sync {
    public static final String MOD_ID = "sync";
    public static final String NAME = "Sync";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Sync() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SyncConfig.SPEC, "sync-common.toml");
        SyncBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        SyncCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        SyncBlocks.BLOCKS.register(modEventBus);
        SyncItems.ITEMS.register(modEventBus);
        SyncPackets.init();
        SyncCommands.init();

        // Initialize optional mod compatibility
        ThirstCompat.init();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(this::onClientSetup);
        }
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        CustomGameRenderer.initClient();
        SyncRenderers.initClient();
        SyncPackets.initClient();
    }

    public static ResourceLocation locate(String location) {
        return new ResourceLocation(MOD_ID, location);
    }
}