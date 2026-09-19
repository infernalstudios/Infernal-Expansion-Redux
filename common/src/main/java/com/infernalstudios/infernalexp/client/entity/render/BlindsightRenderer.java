package com.infernalstudios.infernalexp.client.entity.render;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.client.entity.layer.EmissiveLayer;
import com.infernalstudios.infernalexp.client.entity.model.BlindsightModel;
import com.infernalstudios.infernalexp.entities.BlindsightEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BlindsightRenderer extends MobRenderer<BlindsightEntity, BlindsightModel> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(IEConstants.MOD_ID, "textures/entity/blindsight.png");
    private static final ResourceLocation TEXTURE_OPEN = ResourceLocation.fromNamespaceAndPath(IEConstants.MOD_ID, "textures/entity/blindsight_open.png");

    public BlindsightRenderer(EntityRendererProvider.Context context) {
        super(context, new BlindsightModel(context.bakeLayer(BlindsightModel.LAYER_LOCATION)), 0.7F);
        this.addLayer(new EmissiveLayer<>(this, BlindsightRenderer::textureOf));
    }

    private static ResourceLocation textureOf(BlindsightEntity entity) {
        return entity.isWatchingLuminous() ? TEXTURE_OPEN : TEXTURE;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BlindsightEntity entity) {
        return textureOf(entity);
    }
}
