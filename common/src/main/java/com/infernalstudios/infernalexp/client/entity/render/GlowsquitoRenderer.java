package com.infernalstudios.infernalexp.client.entity.render;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.client.entity.layer.EmissiveLayer;
import com.infernalstudios.infernalexp.client.entity.model.GlowsquitoModel;
import com.infernalstudios.infernalexp.entities.GlowsquitoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GlowsquitoRenderer extends MobRenderer<GlowsquitoEntity, GlowsquitoModel> {

    public GlowsquitoRenderer(EntityRendererProvider.Context context) {
        super(context, new GlowsquitoModel(context.bakeLayer(GlowsquitoModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new EmissiveLayer<>(this, GlowsquitoRenderer::textureOf));
    }

    private static ResourceLocation textureOf(GlowsquitoEntity entity) {
        String texture = "glowsquito.png";
        String variant = entity.getVariant();

        if (entity.hasCustomName() && "glowseeyou".equalsIgnoreCase(Objects.requireNonNull(entity.getCustomName()).getString())) {
            texture = "glowsquito_halloween.png";
        } else if (variant != null && !variant.isEmpty()) {
            texture = "glowsquito_" + variant + ".png";
        }

        return ResourceLocation.fromNamespaceAndPath(IEConstants.MOD_ID, "textures/entity/glowsquito/" + texture);
    }

    @Override
    protected void scale(@NotNull GlowsquitoEntity entity, @NotNull PoseStack poseStack, float partialTick) {
        if (entity.isBaby()) {
            poseStack.scale(0.4F, 0.4F, 0.4F);
        }
    }

    @Override
    protected float getShadowRadius(@NotNull GlowsquitoEntity entity) {
        return super.getShadowRadius(entity) * (entity.isBaby() ? 0.4F : 1.0F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull GlowsquitoEntity entity) {
        return textureOf(entity);
    }
}
