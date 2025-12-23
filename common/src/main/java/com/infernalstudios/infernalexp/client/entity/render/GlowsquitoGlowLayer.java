
package com.infernalstudios.infernalexp.client.entity.render;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.client.entity.model.GlowsquitoModel;
import com.infernalstudios.infernalexp.entities.GlowsquitoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class GlowsquitoGlowLayer extends RenderLayer<GlowsquitoEntity, GlowsquitoModel<GlowsquitoEntity>> {
    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(IEConstants.MOD_ID,
            "textures/entity/glowsquito_glow.png");

    public GlowsquitoGlowLayer(RenderLayerParent<GlowsquitoEntity, GlowsquitoModel<GlowsquitoEntity>> renderer) {
        super(renderer);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, MultiBufferSource buffer, int packedLight, @NotNull GlowsquitoEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.eyes(GLOW_TEXTURE));

        this.getParentModel().renderToBuffer(poseStack, vertexConsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}