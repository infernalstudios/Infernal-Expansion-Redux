package com.infernalstudios.infernalexp.client.entity.render;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.entities.VolineEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class VolineGlowLayer extends GeoRenderLayer<VolineEntity> {
    public VolineGlowLayer(GeoRenderer<VolineEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, VolineEntity entity, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        String base = entity.isGrown() ? "voline_big" : "voline";
        String suffix = entity.isSleeping() ? "_sleeping" : "";

        ResourceLocation texture = new ResourceLocation(IEConstants.MOD_ID, "textures/entity/" + base + suffix + "_glow.png");
        RenderType glowRenderType = RenderType.eyes(texture);

        getRenderer().reRender(bakedModel, poseStack, bufferSource, entity, glowRenderType, bufferSource.getBuffer(glowRenderType), partialTick, 15728640, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
    }
}