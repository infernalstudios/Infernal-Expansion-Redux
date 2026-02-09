package com.infernalstudios.infernalexp.client.entity.render;

import com.infernalstudios.infernalexp.client.entity.model.WarpbeetleModel;
import com.infernalstudios.infernalexp.entities.WarpbeetleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WarpbeetleRenderer extends GeoEntityRenderer<WarpbeetleEntity> {

    private boolean isRenderingBackpack = false;

    public WarpbeetleRenderer(EntityRendererProvider.Context context) {
        super(context, new WarpbeetleModel());
    }

    @Override
    public void render(WarpbeetleEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        if (entity.isPassenger() && entity.getVehicle() instanceof Player && !isRenderingBackpack) {
            return;
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    public void renderBackpack(WarpbeetleEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        this.isRenderingBackpack = true;

        ResourceLocation texture = ((EntityRenderer<WarpbeetleEntity>) this).getTextureLocation(entity);
        RenderType renderType = this.getRenderType(entity, texture, bufferSource, partialTick);

        var geoModel = this.getGeoModel();
        var bakedModel = geoModel.getBakedModel(geoModel.getModelResource(entity));

        this.actuallyRender(
                poseStack,
                entity,
                bakedModel,
                renderType,
                bufferSource,
                bufferSource.getBuffer(renderType),
                true,
                partialTick,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1.0f, 1.0f, 1.0f, 1.0f
        );

        this.isRenderingBackpack = false;
    }
}