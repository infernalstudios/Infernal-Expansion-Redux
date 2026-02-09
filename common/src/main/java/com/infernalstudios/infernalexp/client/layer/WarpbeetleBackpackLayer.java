package com.infernalstudios.infernalexp.client.layer;

import com.infernalstudios.infernalexp.client.entity.render.WarpbeetleRenderer;
import com.infernalstudios.infernalexp.entities.WarpbeetleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import org.jetbrains.annotations.NotNull;

public class WarpbeetleBackpackLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private final WarpbeetleRenderer beetleRenderer;

    public WarpbeetleBackpackLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent, WarpbeetleRenderer beetleRenderer) {
        super(parent);
        this.beetleRenderer = beetleRenderer;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (player.getPassengers().isEmpty()) return;

        player.getPassengers().stream()
                .filter(e -> e instanceof WarpbeetleEntity)
                .map(e -> (WarpbeetleEntity) e)
                .findFirst()
                .ifPresent(beetle -> {
                    poseStack.pushPose();

                    this.getParentModel().body.translateAndRotate(poseStack);

                    poseStack.translate(0.0D, -0.2D, 0.45D);

                    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));

                    beetleRenderer.renderBackpack(beetle, 0f, partialTick, poseStack, bufferSource, packedLight);

                    poseStack.popPose();
                });
    }
}