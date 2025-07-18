package net.sumik.sync.common.block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sumik.sync.Sync;
import net.sumik.sync.common.block.SyncBlocks;

public class SyncBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Sync.MOD_ID);

    public static final RegistryObject<BlockEntityType<ShellStorageBlockEntity>> SHELL_STORAGE =
            BLOCK_ENTITIES.register("shell_storage",
                    () -> BlockEntityType.Builder.of(ShellStorageBlockEntity::new,
                            SyncBlocks.SHELL_STORAGE.get()).build(null));

    public static final RegistryObject<BlockEntityType<ShellConstructorBlockEntity>> SHELL_CONSTRUCTOR =
            BLOCK_ENTITIES.register("shell_constructor",
                    () -> BlockEntityType.Builder.of(ShellConstructorBlockEntity::new,
                            SyncBlocks.SHELL_CONSTRUCTOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<TreadmillBlockEntity>> TREADMILL =
            BLOCK_ENTITIES.register("treadmill",
                    () -> BlockEntityType.Builder.of(TreadmillBlockEntity::new,
                            SyncBlocks.TREADMILL.get()).build(null));
}