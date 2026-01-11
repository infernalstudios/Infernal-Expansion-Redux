package com.infernalstudios.infernalexp.client.entity.model;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.entities.GlowsilkMothEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GlowsilkMothModel extends GeoModel<GlowsilkMothEntity> {
    @Override
    public ResourceLocation getModelResource(GlowsilkMothEntity object) {
        return new ResourceLocation(IEConstants.MOD_ID, "geo/glowsilk_moth.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GlowsilkMothEntity object) {
        return new ResourceLocation(IEConstants.MOD_ID, "textures/entity/glowsilk_moth.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GlowsilkMothEntity animatable) {
        return new ResourceLocation(IEConstants.MOD_ID, "animations/glowsilk_moth.animation.json");
    }
}