package com.infernalstudios.infernalexp.client.entity.render;

import com.infernalstudios.infernalexp.client.entity.model.BlindsightModel;
import com.infernalstudios.infernalexp.entities.BlindsightEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BlindsightRenderer extends GeoEntityRenderer<BlindsightEntity> {
    public BlindsightRenderer(EntityRendererProvider.Context context) {
        super(context, new BlindsightModel());
    }
}