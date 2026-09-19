package com.infernalstudios.infernalexp.client.block.render;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.block.VolatileGeyserBlock;
import com.infernalstudios.infernalexp.block.entity.VolatileGeyserBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class VolatileGeyserRenderer implements BlockEntityRenderer<VolatileGeyserBlockEntity> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(IEConstants.MOD_ID, "textures/block/volatile_geyser.png");
    private static final ResourceLocation GLOWMASK = ResourceLocation.fromNamespaceAndPath(IEConstants.MOD_ID, "textures/block/volatile_geyser_glowmask.png");

    private static final float TEXTURE_SIZE = 32.0F;

    private static final float MIN = 3.0F / 16.0F;
    private static final float MAX = 13.0F / 16.0F;
    private static final float TOP = 12.0F / 16.0F;

    private static final float[] UV_NORTH = {0, 0, 10, 12};
    private static final float[] UV_EAST = {10, 0, 20, 12};
    private static final float[] UV_SOUTH = {0, 12, 10, 24};
    private static final float[] UV_WEST = {10, 12, 20, 24};
    private static final float[] UV_UP = {30, 10, 20, 0};
    private static final float[] UV_DOWN = {30, 10, 20, 20};

    public VolatileGeyserRenderer(BlockEntityRendererProvider.Context context) {
    }

    private static void quad(VertexConsumer buffer, PoseStack.Pose pose, int light, Direction normal, float[] uv,
                             float x0, float y0, float z0, float x1, float y1, float z1,
                             float x2, float y2, float z2, float x3, float y3, float z3) {
        float u0 = uv[0] / TEXTURE_SIZE;
        float v0 = uv[1] / TEXTURE_SIZE;
        float u1 = uv[2] / TEXTURE_SIZE;
        float v1 = uv[3] / TEXTURE_SIZE;

        vertex(buffer, pose, light, normal, x0, y0, z0, u0, v0);
        vertex(buffer, pose, light, normal, x1, y1, z1, u0, v1);
        vertex(buffer, pose, light, normal, x2, y2, z2, u1, v1);
        vertex(buffer, pose, light, normal, x3, y3, z3, u1, v0);
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, int light, Direction normal,
                               float x, float y, float z, float u, float v) {
        buffer.addVertex(pose, x, y, z)
                .setColor(-1)
                .setUv(u, v)
                .setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, normal.getStepX(), normal.getStepY(), normal.getStepZ());
    }

    private static void renderCube(VertexConsumer buffer, PoseStack.Pose pose, int light) {
        quad(buffer, pose, light, Direction.NORTH, UV_NORTH, MAX, TOP, MIN, MAX, 0, MIN, MIN, 0, MIN, MIN, TOP, MIN);
        quad(buffer, pose, light, Direction.SOUTH, UV_SOUTH, MIN, TOP, MAX, MIN, 0, MAX, MAX, 0, MAX, MAX, TOP, MAX);
        quad(buffer, pose, light, Direction.WEST, UV_WEST, MIN, TOP, MIN, MIN, 0, MIN, MIN, 0, MAX, MIN, TOP, MAX);
        quad(buffer, pose, light, Direction.EAST, UV_EAST, MAX, TOP, MAX, MAX, 0, MAX, MAX, 0, MIN, MAX, TOP, MIN);
        quad(buffer, pose, light, Direction.UP, UV_UP, MIN, TOP, MIN, MIN, TOP, MAX, MAX, TOP, MAX, MAX, TOP, MIN);
        quad(buffer, pose, light, Direction.DOWN, UV_DOWN, MIN, 0, MAX, MIN, 0, MIN, MAX, 0, MIN, MAX, 0, MAX);
    }

    @Override
    public void render(@NotNull VolatileGeyserBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Direction facing = blockEntity.getBlockState().getValue(VolatileGeyserBlock.FACING);

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);

        switch (facing) {
            case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(180));
            case NORTH -> poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            case SOUTH -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
            case WEST -> poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            case EAST -> poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
            default -> {
            }
        }

        poseStack.translate(-0.5F, -0.5F, -0.5F);

        PoseStack.Pose pose = poseStack.last();
        renderCube(bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), pose, packedLight);
        renderCube(bufferSource.getBuffer(RenderType.entityTranslucentEmissive(GLOWMASK)), pose, LightTexture.FULL_BRIGHT);

        poseStack.popPose();
    }
}
