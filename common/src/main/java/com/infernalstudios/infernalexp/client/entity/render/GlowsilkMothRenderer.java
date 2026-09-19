package com.infernalstudios.infernalexp.client.entity.render;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.client.entity.model.GlowsilkMothModel;
import com.infernalstudios.infernalexp.entities.GlowsilkMothEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class GlowsilkMothRenderer extends MobRenderer<GlowsilkMothEntity, GlowsilkMothModel> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(IEConstants.MOD_ID, "textures/entity/glowsilk_moth.png");

    public GlowsilkMothRenderer(EntityRendererProvider.Context context) {
        super(context, new GlowsilkMothModel(context.bakeLayer(GlowsilkMothModel.LAYER_LOCATION)), 0.3F);
    }

    @Override
    protected int getBlockLightLevel(@NotNull GlowsilkMothEntity entity, @NotNull BlockPos pos) {
        return 15;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull GlowsilkMothEntity entity) {
        return TEXTURE;
    }
}
