package com.infernalstudios.infernalexp.client.entity.render;

import com.infernalstudios.infernalexp.client.entity.model.WarpbeetleModel;
import com.infernalstudios.infernalexp.entities.WarpbeetleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WarpbeetleRenderer extends GeoEntityRenderer<WarpbeetleEntity> {
    public WarpbeetleRenderer(EntityRendererProvider.Context context) {
        super(context, new WarpbeetleModel());
    }
}