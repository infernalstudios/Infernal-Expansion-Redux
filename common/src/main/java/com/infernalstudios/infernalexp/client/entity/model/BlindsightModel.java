package com.infernalstudios.infernalexp.client.entity.model;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.entities.BlindsightEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlindsightModel extends GeoModel<BlindsightEntity> {

    @Override
    public ResourceLocation getModelResource(BlindsightEntity entity) {
        return new ResourceLocation(IEConstants.MOD_ID, "geo/blindsight.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlindsightEntity entity) {
        return new ResourceLocation(IEConstants.MOD_ID, "textures/entity/blindsight.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BlindsightEntity entity) {
        return new ResourceLocation(IEConstants.MOD_ID, "animations/blindsight.animation.json");
    }
}