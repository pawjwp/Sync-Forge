package net.pawjwp.sync.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pawjwp.sync.Sync;

public class SyncBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Sync.MOD_ID);

    public static final RegistryObject<Block> SHELL_STORAGE = BLOCKS.register("shell_storage",
            () -> new ShellStorageBlock(BlockBehaviour.Properties.copy(Blocks.GLASS)
                    .mapColor(MapColor.COLOR_GRAY)
                    .requiresCorrectToolForDrops()
                    .strength(1.8F)
                    .sound(SoundType.GLASS)
                    .noOcclusion()
                    .isValidSpawn(SyncBlocks::never)
                    .isRedstoneConductor(SyncBlocks::never)
                    .isSuffocating(SyncBlocks::never)
                    .isViewBlocking(SyncBlocks::never)));

    public static final RegistryObject<Block> SHELL_CONSTRUCTOR = BLOCKS.register("shell_constructor",
            () -> new ShellConstructorBlock(BlockBehaviour.Properties.copy(Blocks.GLASS)
                    .mapColor(MapColor.COLOR_GRAY)
                    .requiresCorrectToolForDrops()
                    .strength(1.8F)
                    .sound(SoundType.GLASS)
                    .noOcclusion()
                    .isValidSpawn(SyncBlocks::never)
                    .isRedstoneConductor(SyncBlocks::never)
                    .isSuffocating(SyncBlocks::never)
                    .isViewBlocking(SyncBlocks::never)));

    public static final RegistryObject<Block> TREADMILL = BLOCKS.register("treadmill",
            () -> new TreadmillBlock(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .mapColor(MapColor.COLOR_GRAY)
                    .requiresCorrectToolForDrops()
                    .strength(1.8F)
                    .isValidSpawn(SyncBlocks::never)
                    .isRedstoneConductor(SyncBlocks::never)
                    .isSuffocating(SyncBlocks::never)
                    .isViewBlocking(SyncBlocks::never)));

    private static boolean never(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    private static Boolean never(BlockState state, BlockGetter world, BlockPos pos, EntityType<?> type) {
        return false;
    }
}