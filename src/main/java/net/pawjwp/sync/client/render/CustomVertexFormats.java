package net.pawjwp.sync.client.render;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class CustomVertexFormats {
    public static final VertexFormat POSITION_COLOR_OVERLAY_LIGHT_NORMAL = new VertexFormat(
            ImmutableMap.<String, VertexFormatElement>builder()
                    .put("Position", DefaultVertexFormat.ELEMENT_POSITION)
                    .put("Color", DefaultVertexFormat.ELEMENT_COLOR)
                    .put("UV1", DefaultVertexFormat.ELEMENT_UV1)
                    .put("UV2", DefaultVertexFormat.ELEMENT_UV2)
                    .put("Normal", DefaultVertexFormat.ELEMENT_NORMAL)
                    .put("Padding", DefaultVertexFormat.ELEMENT_PADDING)
                    .build()
    );
}