package net.pawjwp.sync.common.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.pawjwp.sync.client.render.SyncRenderers;
import net.pawjwp.sync.common.block.entity.SyncBlockEntities;

import java.util.function.Consumer;

public class ShellStorageItem extends BlockItem {
    public ShellStorageItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return SyncRenderers.createItemRenderer(SyncBlockEntities.SHELL_STORAGE.get(), getBlock()).getCustomRenderer();
            }
        });
    }
}