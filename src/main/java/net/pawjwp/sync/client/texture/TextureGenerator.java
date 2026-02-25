package net.pawjwp.sync.client.texture;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface TextureGenerator {
    Stream<AbstractTexture> generateTextures();
}