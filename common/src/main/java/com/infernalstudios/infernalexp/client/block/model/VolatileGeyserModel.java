package com.infernalstudios.infernalexp.client.block.model;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.block.entity.VolatileGeyserBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class VolatileGeyserModel extends GeoModel<VolatileGeyserBlockEntity> {
    @Override
    public ResourceLocation getModelResource(VolatileGeyserBlockEntity animatable) {
        return new ResourceLocation(IEConstants.MOD_ID, "geo/volatile_geyser.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(VolatileGeyserBlockEntity animatable) {
        return new ResourceLocation(IEConstants.MOD_ID, "textures/block/volatile_geyser.png");
    }

    @Override
    public ResourceLocation getAnimationResource(VolatileGeyserBlockEntity animatable) {
        return new ResourceLocation(IEConstants.MOD_ID, "animations/volatile_geyser.animation.json");
    }
}