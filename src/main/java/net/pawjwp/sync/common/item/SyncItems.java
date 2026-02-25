package net.pawjwp.sync.common.item;

import net.pawjwp.sync.Sync;
import net.pawjwp.sync.common.block.SyncBlocks;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SyncItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Sync.MOD_ID);

    public static final RegistryObject<Item> SYNC_CORE = ITEMS.register("sync_core",
            () -> new Item(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> SHELL_CONSTRUCTOR = ITEMS.register("shell_constructor",
            () -> new ShellConstructorItem(SyncBlocks.SHELL_CONSTRUCTOR.get(), new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SHELL_STORAGE = ITEMS.register("shell_storage",
            () -> new ShellStorageItem(SyncBlocks.SHELL_STORAGE.get(), new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TREADMILL = ITEMS.register("treadmill",
            () -> new TreadmillItem(SyncBlocks.TREADMILL.get(), new Item.Properties().stacksTo(1)));
}