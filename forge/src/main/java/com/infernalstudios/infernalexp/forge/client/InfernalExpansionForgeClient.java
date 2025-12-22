package com.infernalstudios.infernalexp.forge.client;

import com.infernalstudios.infernalexp.client.IECommonClient;
import com.infernalstudios.infernalexp.client.particle.GlowstoneSparkleParticle;
import com.infernalstudios.infernalexp.client.particle.InfectionParticle;
import com.infernalstudios.infernalexp.module.ModEntityRenderers;
import com.infernalstudios.infernalexp.module.ModModelLayers;
import com.infernalstudios.infernalexp.module.ModParticleTypes;
import com.infernalstudios.infernalexp.registration.holders.BlockDataHolder;
import com.infernalstudios.infernalexp.registration.holders.EntityTypeDataHolder;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Map;
import java.util.function.Supplier;

public class InfernalExpansionForgeClient {
    public static void init() {
        IECommonClient.init();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(InfernalExpansionForgeClient::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(InfernalExpansionForgeClient::registerEntityRenderers);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(InfernalExpansionForgeClient::registerLayerDefinitions);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(InfernalExpansionForgeClient::registerParticleProviders);
    }

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (Map.Entry<ModelLayerLocation, Supplier<LayerDefinition>> entry : ModModelLayers.getLayerRegistry().entrySet()) {
            event.registerLayerDefinition(entry.getKey(), entry.getValue());
        }
    }

    private static void clientSetup(final FMLClientSetupEvent event) {
        for (BlockDataHolder<?> block : BlockDataHolder.getCutoutBlocks()) {
            ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout());
        }
    }

    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (Map.Entry<EntityTypeDataHolder<?>, EntityRendererProvider<?>> entry : ModEntityRenderers.getEntityRendererRegistry().entrySet()) {
            registerEntityRendererHelper(event, entry.getKey(), entry.getValue());
        }
    }

    // Helper method to capture the wildcard generic <T>
    @SuppressWarnings("unchecked")
    private static <T extends Entity> void registerEntityRendererHelper(EntityRenderersEvent.RegisterRenderers event, EntityTypeDataHolder<?> typeHolder, EntityRendererProvider<?> provider) {
        event.registerEntityRenderer((EntityType<T>) typeHolder.get(), (EntityRendererProvider<T>) provider);
    }

    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticleTypes.GLOWSTONE_SPARKLE, GlowstoneSparkleParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.INFECTION, InfectionParticle.Factory::new);
    }
}
