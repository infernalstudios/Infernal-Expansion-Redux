package com.infernalstudios.infernalexp.client.block.render;

import com.infernalstudios.infernalexp.block.entity.VolatileGeyserBlockEntity;
import com.infernalstudios.infernalexp.client.block.model.VolatileGeyserModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class VolatileGeyserRenderer extends GeoBlockRenderer<VolatileGeyserBlockEntity> {
    public VolatileGeyserRenderer(BlockEntityRendererProvider.Context context) {
        super(new VolatileGeyserModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public void rotateBlock(Direction facing, PoseStack poseStack) {
        poseStack.translate(0, 0.5, 0);

        switch (facing) {
            case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(180));
            case UP -> poseStack.mulPose(Axis.XP.rotationDegrees(0));
            case NORTH -> poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            case SOUTH -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
            case WEST -> poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            case EAST -> poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
        }

        poseStack.translate(0, -0.5, 0);
    }
}