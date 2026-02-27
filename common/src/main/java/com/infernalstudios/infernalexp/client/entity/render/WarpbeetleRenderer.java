package com.infernalstudios.infernalexp.client.entity.render;

import com.infernalstudios.infernalexp.client.entity.model.WarpbeetleModel;
import com.infernalstudios.infernalexp.entities.WarpbeetleEntity;
import com.infernalstudios.infernalexp.module.ModEntityTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Objects;

public class WarpbeetleRenderer extends GeoEntityRenderer<WarpbeetleEntity> {

    private WarpbeetleEntity dummyBeetle;
    private boolean isRenderingBackpack = false;

    public WarpbeetleRenderer(EntityRendererProvider.Context context) {
        super(context, new WarpbeetleModel());
    }

    @Override
    public void render(WarpbeetleEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        if (entity.isPassenger() && entity.getVehicle() instanceof Player && !isRenderingBackpack) {
            return;
        }

        poseStack.pushPose();

        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    public void renderBackpack(WarpbeetleEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        this.isRenderingBackpack = true;

        Level currentLevel = Minecraft.getInstance().level;

        if (dummyBeetle == null || dummyBeetle.level() != currentLevel) {
            dummyBeetle = ModEntityTypes.WARPBEETLE.get().create(Objects.requireNonNull(currentLevel));
        }

        if (dummyBeetle != null) {
            dummyBeetle.tickCount = entity.tickCount;
            dummyBeetle.setFlying(entity.isFlying());

            dummyBeetle.yBodyRot = 0;
            dummyBeetle.setYRot(0);
            dummyBeetle.setYHeadRot(0);
            dummyBeetle.setXRot(0);
            dummyBeetle.yBodyRotO = 0;
            dummyBeetle.yRotO = 0;
            dummyBeetle.yHeadRotO = 0;
            dummyBeetle.xRotO = 0;
            dummyBeetle.walkAnimation.setSpeed(0.0F);

            long instanceId = this.getInstanceId(dummyBeetle);
            AnimationState<WarpbeetleEntity> animationState =
                    new AnimationState<>(dummyBeetle, 0, 0, partialTick, false);
            this.getGeoModel().handleAnimations(dummyBeetle, instanceId, animationState);

            if (entity.isFlying()) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees(45.0F));
            }

            ResourceLocation texture = ((EntityRenderer<WarpbeetleEntity>) this).getTextureLocation(entity);
            RenderType renderType = this.getRenderType(dummyBeetle, texture, bufferSource, partialTick);

            this.actuallyRender(
                    poseStack,
                    dummyBeetle,
                    this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(dummyBeetle)),
                    renderType,
                    bufferSource,
                    bufferSource.getBuffer(renderType),
                    true,
                    partialTick,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    1.0f, 1.0f, 1.0f, 1.0f
            );

            if (entity.isFlying()) {
                poseStack.popPose();
            }
        }

        this.isRenderingBackpack = false;
    }
}