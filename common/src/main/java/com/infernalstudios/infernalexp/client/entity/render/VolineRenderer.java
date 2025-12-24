package com.infernalstudios.infernalexp.client.entity.render;

import com.infernalstudios.infernalexp.client.entity.model.VolineModel;
import com.infernalstudios.infernalexp.entities.VolineEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class VolineRenderer extends GeoEntityRenderer<VolineEntity> {

    public VolineRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new VolineModel());

        this.addRenderLayer(new VolineGlowLayer(this));

        this.shadowRadius = 0.7F;
    }
}