package com.infernalstudios.infernalexp.client.entity.model;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.entities.GlowsquitoEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GlowsquitoModel extends GeoModel<GlowsquitoEntity> {

    @Override
    public ResourceLocation getModelResource(GlowsquitoEntity entity) {
        return new ResourceLocation(IEConstants.MOD_ID, "geo/glowsquito.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GlowsquitoEntity entity) {
        return new ResourceLocation(IEConstants.MOD_ID, "textures/entity/" + (entity.getBred() ? "glowsquito_shroomlight.png" : "glowsquito.png"));
    }

    @Override
    public ResourceLocation getAnimationResource(GlowsquitoEntity entity) {
        return new ResourceLocation(IEConstants.MOD_ID, "animations/glowsquito.animation.json");
    }
}