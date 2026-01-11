package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.client.entity.render.GlowsilkArrowRenderer;
import com.infernalstudios.infernalexp.client.entity.render.GlowsilkMothRenderer;
import com.infernalstudios.infernalexp.client.entity.render.GlowsquitoRenderer;
import com.infernalstudios.infernalexp.client.entity.render.VolineRenderer;
import com.infernalstudios.infernalexp.registration.holders.EntityTypeDataHolder;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
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
        register(ModEntityTypes.GLOWSILK_ARROW, GlowsilkArrowRenderer::new);
        register(ModEntityTypes.GLOWSILK_MOTH, GlowsilkMothRenderer::new);
    }
}
