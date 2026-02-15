package net.pawjwp.sync.common.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.pawjwp.sync.Sync;

public class SyncCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Sync.MOD_ID);

    public static final RegistryObject<CreativeModeTab> SYNC_TAB = CREATIVE_MODE_TABS.register("sync_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(SyncItems.SYNC_CORE.get()))
                    .title(Component.translatable("creativetab.sync"))
                    .displayItems((parameters, output) -> {
                        output.accept(SyncItems.SYNC_CORE.get());
                        output.accept(SyncItems.SHELL_STORAGE.get());
                        output.accept(SyncItems.SHELL_CONSTRUCTOR.get());
                        output.accept(SyncItems.TREADMILL.get());
                    }).build()
    );
}