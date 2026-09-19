package com.infernalstudios.infernalexp.client.entity.render;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.client.entity.layer.EmissiveLayer;
import com.infernalstudios.infernalexp.client.entity.model.IEHierarchicalModel;
import com.infernalstudios.infernalexp.client.entity.model.VolineBigModel;
import com.infernalstudios.infernalexp.client.entity.model.VolineModel;
import com.infernalstudios.infernalexp.entities.VolineEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class VolineRenderer extends MobRenderer<VolineEntity, IEHierarchicalModel<VolineEntity>> {

    private final IEHierarchicalModel<VolineEntity> smallModel;
    private final IEHierarchicalModel<VolineEntity> grownModel;

    public VolineRenderer(EntityRendererProvider.Context context) {
        super(context, new VolineModel(context.bakeLayer(VolineModel.LAYER_LOCATION)), 0.7F);
        this.smallModel = this.model;
        this.grownModel = new VolineBigModel(context.bakeLayer(VolineBigModel.LAYER_LOCATION));
        this.addLayer(new EmissiveLayer<>(this, VolineRenderer::textureOf));
    }

    private static ResourceLocation textureOf(VolineEntity entity) {
        String base = entity.isGrown() ? "voline_big" : "voline";
        String suffix = entity.isGrown() && entity.isSleeping() ? "_sleeping" : "";
        return ResourceLocation.fromNamespaceAndPath(IEConstants.MOD_ID, "textures/entity/" + base + suffix + ".png");
    }

    @Override
    public void render(@NotNull VolineEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight) {
        this.model = entity.isGrown() ? this.grownModel : this.smallModel;
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    protected void scale(@NotNull VolineEntity entity, @NotNull PoseStack poseStack, float partialTick) {
        float scale = entity.getSizeFactor();
        poseStack.scale(scale, scale, scale);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull VolineEntity entity) {
        return textureOf(entity);
    }
}
