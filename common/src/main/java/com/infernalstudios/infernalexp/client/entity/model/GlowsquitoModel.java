package com.infernalstudios.infernalexp.client.entity.model;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.entities.GlowsquitoEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import java.util.Objects;

public class GlowsquitoModel extends GeoModel<GlowsquitoEntity> {

    @Override
    public ResourceLocation getModelResource(GlowsquitoEntity entity) {
        return new ResourceLocation(IEConstants.MOD_ID, "geo/glowsquito.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GlowsquitoEntity entity) {
        String texture = "glowsquito.png";

        if (entity.hasCustomName() && "glowseeyou".equalsIgnoreCase(Objects.requireNonNull(entity.getCustomName()).getString())) {
            texture = "glowsquito_halloween.png";
        } else if (entity.isShroomnightPowered()) {
            texture = "glowsquito_shroomnight.png";
        } else if (entity.isShroomlightPowered()) {
            texture = "glowsquito_shroomlight.png";
        }

        return new ResourceLocation(IEConstants.MOD_ID, "textures/entity/" + texture);
    }

    @Override
    public ResourceLocation getAnimationResource(GlowsquitoEntity entity) {
        return new ResourceLocation(IEConstants.MOD_ID, "animations/glowsquito.animation.json");
    }
}