package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.client.block.render.VolatileGeyserRenderer;
import com.infernalstudios.infernalexp.registration.util.RegistryObject;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.HashMap;
import java.util.Map;

public class ModBlockEntityRenderers {

    private static final Map<RegistryObject<? extends BlockEntityType<?>>, BlockEntityRendererProvider<?>> BLOCK_ENTITY_RENDERER_REGISTRY = new HashMap<>();

    /**
     * Registers a Block Entity Renderer.
     *
     * @param blockEntityType The RegistryObject for the Block Entity Type.
     * @param renderer        The renderer factory (usually Constructor::new).
     */
    public static <T extends BlockEntity> void register(RegistryObject<BlockEntityType<T>> blockEntityType, BlockEntityRendererProvider<T> renderer) {
        BLOCK_ENTITY_RENDERER_REGISTRY.put(blockEntityType, renderer);
    }

    public static Map<RegistryObject<? extends BlockEntityType<?>>, BlockEntityRendererProvider<?>> getRegistry() {
        return BLOCK_ENTITY_RENDERER_REGISTRY;
    }

    public static void load() {
        register(ModBlockEntityTypes.VOLATILE_GEYSER, VolatileGeyserRenderer::new);
    }
}