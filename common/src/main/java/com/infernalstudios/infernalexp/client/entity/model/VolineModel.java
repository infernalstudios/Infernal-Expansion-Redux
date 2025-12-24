package com.infernalstudios.infernalexp.client.entity.model;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.entities.VolineEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class VolineModel extends GeoModel<VolineEntity> {

    @Override
    public ResourceLocation getModelResource(VolineEntity entity) {
        return new ResourceLocation(IEConstants.MOD_ID, "geo/" + (entity.isGrown() ? "voline_big.geo.json" : "voline.geo.json"));
    }

    @Override
    public ResourceLocation getTextureResource(VolineEntity entity) {
        String base = entity.isGrown() ? "voline_big" : "voline";
        String suffix = entity.isSleeping() ? "_sleeping" : "";
        return new ResourceLocation(IEConstants.MOD_ID, "textures/entity/" + base + suffix + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(VolineEntity entity) {
        return new ResourceLocation(IEConstants.MOD_ID, "animations/" + (entity.isGrown() ? "voline_big.animation.json" : "voline.animation.json"));
    }
}