package com.infernalstudios.infernalexp.client.entity.render;

import com.infernalstudios.infernalexp.client.entity.model.GlowsilkMothModel;
import com.infernalstudios.infernalexp.entities.GlowsilkMothEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GlowsilkMothRenderer extends GeoEntityRenderer<GlowsilkMothEntity> {
    public GlowsilkMothRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GlowsilkMothModel());
    }

    @Override
    protected int getBlockLightLevel(@NotNull GlowsilkMothEntity entity, @NotNull BlockPos pos) {
        return 15;
    }
}