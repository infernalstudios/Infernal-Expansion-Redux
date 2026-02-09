package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.client.entity.render.*;
import com.infernalstudios.infernalexp.registration.holders.EntityTypeDataHolder;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class ModEntityRenderers {

    private static final Map<EntityTypeDataHolder<?>, EntityRendererProvider<?>> ENTITY_RENDERER_REGISTRY = new HashMap<>();

    public static <T extends Entity> void register(EntityTypeDataHolder<T> entityType, EntityRendererProvider<T> renderer) {
        ENTITY_RENDERER_REGISTRY.put(entityType, renderer);
    }

    public static Map<EntityTypeDataHolder<?>, EntityRendererProvider<?>> getEntityRendererRegistry() {
        return ENTITY_RENDERER_REGISTRY;
    }

    public static void load() {
        register(ModEntityTypes.VOLINE, VolineRenderer::new);
        register(ModEntityTypes.GLOWSQUITO, GlowsquitoRenderer::new);
        register(ModEntityTypes.BLINDSIGHT, BlindsightRenderer::new);
        register(ModEntityTypes.WARPBEETLE, WarpbeetleRenderer::new);
        register(ModEntityTypes.GLOWSILK_MOTH, GlowsilkMothRenderer::new);
        register(ModEntityTypes.GLOWSILK_ARROW, GlowsilkArrowRenderer::new);
        register(ModEntityTypes.THROWABLE_MAGMA_CREAM, ThrownItemRenderer::new);
        register(ModEntityTypes.THROWABLE_FIRE_CHARGE, (context) -> new ThrownItemRenderer<>(context, 0.75F, true));
    }
}