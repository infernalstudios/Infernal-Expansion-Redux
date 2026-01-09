package com.infernalstudios.infernalexp.client.entity.render;

import com.infernalstudios.infernalexp.client.entity.model.GlowsquitoModel;
import com.infernalstudios.infernalexp.entities.GlowsquitoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class GlowsquitoRenderer extends GeoEntityRenderer<GlowsquitoEntity> {

    public GlowsquitoRenderer(EntityRendererProvider.Context context) {
        super(context, new GlowsquitoModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}