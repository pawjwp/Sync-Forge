package net.pawjwp.sync.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class MatrixStackStorage {
    private static PoseStack modelMatrixStack;

    public static void saveModelMatrixStack(PoseStack matrixStack) {
        modelMatrixStack = matrixStack;
    }

    public static PoseStack getModelMatrixStack() {
        return modelMatrixStack;
    }
}