package com.infernalstudios.infernalexp.client.entity.render;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.client.entity.model.WarpbeetleModel;
import com.infernalstudios.infernalexp.entities.WarpbeetleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class WarpbeetleRenderer extends MobRenderer<WarpbeetleEntity, WarpbeetleModel> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(IEConstants.MOD_ID, "textures/entity/warpbeetle.png");

    private boolean isRenderingBackpack = false;

    public WarpbeetleRenderer(EntityRendererProvider.Context context) {
        super(context, new WarpbeetleModel(context.bakeLayer(WarpbeetleModel.LAYER_LOCATION)), 0.3F);
    }

    @Override
    public void render(@NotNull WarpbeetleEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight) {
        if (entity.isPassenger() && entity.getVehicle() instanceof Player && !this.isRenderingBackpack) {
            return;
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    protected void scale(@NotNull WarpbeetleEntity entity, @NotNull PoseStack poseStack, float partialTick) {
        if (entity.isBaby()) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
        }
    }

    public void renderBackpack(WarpbeetleEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        this.isRenderingBackpack = true;

        poseStack.pushPose();

        if (entity.isFlying()) {
            poseStack.mulPose(Axis.XP.rotationDegrees(45.0F));
        }

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.501F, 0.0F);

        WarpbeetleModel model = this.getModel();
        model.setupAnim(entity, 0.0F, 0.0F, entity.tickCount + partialTick, 0.0F, 0.0F);

        RenderType renderType = model.renderType(this.getTextureLocation(entity));
        VertexConsumer buffer = bufferSource.getBuffer(renderType);
        model.renderToBuffer(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, -1);

        poseStack.popPose();

        this.isRenderingBackpack = false;
    }

    @Override
    protected float getShadowRadius(@NotNull WarpbeetleEntity entity) {
        return super.getShadowRadius(entity) * (entity.isBaby() ? 0.5F : 1.0F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull WarpbeetleEntity entity) {
        return TEXTURE;
    }
}
