package com.infernalstudios.infernalexp.client.entity.model;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.entities.WarpbeetleEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WarpbeetleModel extends GeoModel<WarpbeetleEntity> {

    @Override
    public ResourceLocation getModelResource(WarpbeetleEntity entity) {
        return new ResourceLocation(IEConstants.MOD_ID, "geo/warpbeetle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WarpbeetleEntity entity) {
        return new ResourceLocation(IEConstants.MOD_ID, "textures/entity/warpbeetle.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WarpbeetleEntity entity) {
        return new ResourceLocation(IEConstants.MOD_ID, "animations/warpbeetle.animation.json");
    }
}