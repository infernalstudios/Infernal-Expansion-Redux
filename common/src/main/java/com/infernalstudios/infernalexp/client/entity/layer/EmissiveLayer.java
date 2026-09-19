package com.infernalstudios.infernalexp.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class EmissiveLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    private final Function<T, ResourceLocation> baseTexture;

    public EmissiveLayer(RenderLayerParent<T, M> parent, Function<T, ResourceLocation> baseTexture) {
        super(parent);
        this.baseTexture = baseTexture;
    }

    public static ResourceLocation glowmaskOf(ResourceLocation texture) {
        String path = texture.getPath();
        int dot = path.lastIndexOf('.');
        String masked = dot == -1 ? path + "_glowmask" : path.substring(0, dot) + "_glowmask" + path.substring(dot);
        return ResourceLocation.fromNamespaceAndPath(texture.getNamespace(), masked);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) {
            return;
        }

        RenderType renderType = RenderType.entityTranslucentEmissive(glowmaskOf(this.baseTexture.apply(entity)));
        VertexConsumer buffer = bufferSource.getBuffer(renderType);

        this.getParentModel().renderToBuffer(poseStack, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, -1);
    }
}
