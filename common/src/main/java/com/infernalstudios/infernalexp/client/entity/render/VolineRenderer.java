package com.infernalstudios.infernalexp.client.entity.render;

import com.infernalstudios.infernalexp.client.entity.model.VolineModel;
import com.infernalstudios.infernalexp.entities.VolineEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class VolineRenderer extends GeoEntityRenderer<VolineEntity> {

    public VolineRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new VolineModel());

        this.addRenderLayer(new VolineGlowLayer(this));

        this.shadowRadius = 0.7F;
    }

    @Override
    public void preRender(PoseStack poseStack, VolineEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float scale = animatable.getSizeFactor();
        this.scaleWidth = scale;
        this.scaleHeight = scale;

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}