package com.infernalstudios.infernalexp.fabric.client;

import com.infernalstudios.infernalexp.client.IECommonClient;
import com.infernalstudios.infernalexp.client.particle.GlowsquitoWingParticle;
import com.infernalstudios.infernalexp.client.particle.GlowstoneSparkleParticle;
import com.infernalstudios.infernalexp.client.particle.TongueWhipSlashParticle;
import com.infernalstudios.infernalexp.client.sound.GlowsquitoFlightSound;
import com.infernalstudios.infernalexp.entities.GlowsquitoEntity;
import com.infernalstudios.infernalexp.module.ModBlockEntityRenderers;
import com.infernalstudios.infernalexp.module.ModEntityRenderers;
import com.infernalstudios.infernalexp.module.ModModelLayers;
import com.infernalstudios.infernalexp.module.ModParticleTypes;
import com.infernalstudios.infernalexp.registration.holders.BlockDataHolder;
import com.infernalstudios.infernalexp.registration.holders.EntityTypeDataHolder;
import com.infernalstudios.infernalexp.registration.util.RegistryObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Map;
import java.util.function.Supplier;

public class InfernalExpansionFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        IECommonClient.init();
        IECommonClient.initItemProperties();
        registerEntityRenderers();
        registerBlockEntityRenderers();
        registerLayerDefinitions();
        registerBlockRenderTypes();
        registerParticleProviders();

        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof GlowsquitoEntity glowsquito) {
                Minecraft.getInstance().getSoundManager().play(new GlowsquitoFlightSound(glowsquito));
            }
        });
    }

    private void registerLayerDefinitions() {
        for (Map.Entry<ModelLayerLocation, Supplier<LayerDefinition>> entry : ModModelLayers.getLayerRegistry().entrySet()) {
            EntityModelLayerRegistry.registerModelLayer(entry.getKey(), entry.getValue()::get);
        }
    }

    private void registerEntityRenderers() {
        for (Map.Entry<EntityTypeDataHolder<?>, EntityRendererProvider<?>> entry : ModEntityRenderers.getEntityRendererRegistry().entrySet()) {
            registerRendererHelper(entry.getKey(), entry.getValue());
        }
    }

    private void registerBlockRenderTypes() {
        for (BlockDataHolder<?> block : BlockDataHolder.getCutoutBlocks()) {
            BlockRenderLayerMap.INSTANCE.putBlock(block.get(), RenderType.cutout());
        }
    }

    private void registerBlockEntityRenderers() {
        for (Map.Entry<RegistryObject<? extends BlockEntityType<?>>, BlockEntityRendererProvider<?>> entry : ModBlockEntityRenderers.getRegistry().entrySet()) {
            registerBlockEntityRendererHelper(entry.getKey(), entry.getValue());
        }
    }

    private void registerParticleProviders() {
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.GLOWSTONE_SPARKLE, GlowstoneSparkleParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.GLOWSQUITO_WING, GlowsquitoWingParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.TONGUE_WHIP_SLASH, TongueWhipSlashParticle.Provider::new);
    }

    // Helper methods to capture wildcard generics
    @SuppressWarnings("unchecked")
    private <T extends BlockEntity> void registerBlockEntityRendererHelper(RegistryObject<?> typeHolder, BlockEntityRendererProvider<?> provider) {
        BlockEntityRendererRegistry.register((BlockEntityType<T>) typeHolder.get(), (BlockEntityRendererProvider<T>) provider);
    }

    @SuppressWarnings("unchecked")
    private <T extends Entity> void registerRendererHelper(EntityTypeDataHolder<?> typeHolder, EntityRendererProvider<?> provider) {
        EntityRendererRegistry.register((EntityType<T>) typeHolder.get(), (EntityRendererProvider<T>) provider);
    }
}