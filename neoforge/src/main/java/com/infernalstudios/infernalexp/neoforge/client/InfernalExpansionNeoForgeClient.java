package com.infernalstudios.infernalexp.neoforge.client;

import com.infernalstudios.infernalexp.client.IECommonClient;
import com.infernalstudios.infernalexp.client.entity.render.WarpbeetleRenderer;
import com.infernalstudios.infernalexp.client.layer.WarpbeetleBackpackLayer;
import com.infernalstudios.infernalexp.client.particle.GlowsquitoWingParticle;
import com.infernalstudios.infernalexp.client.particle.GlowstoneSparkleParticle;
import com.infernalstudios.infernalexp.client.particle.TongueWhipSlashParticle;
import com.infernalstudios.infernalexp.config.ClothConfigConstructor;
import com.infernalstudios.infernalexp.module.ModBlockEntityRenderers;
import com.infernalstudios.infernalexp.module.ModEntityRenderers;
import com.infernalstudios.infernalexp.module.ModModelLayers;
import com.infernalstudios.infernalexp.module.ModParticleTypes;
import com.infernalstudios.infernalexp.platform.Services;
import com.infernalstudios.infernalexp.registration.holders.BlockDataHolder;
import com.infernalstudios.infernalexp.registration.holders.EntityTypeDataHolder;
import com.infernalstudios.infernalexp.registration.util.RegistryObject;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.Map;
import java.util.function.Supplier;

public class InfernalExpansionNeoForgeClient {
    public static void init(IEventBus modEventBus, ModContainer container) {
        IECommonClient.init();
        if (Services.PLATFORM.isModLoaded("cloth_config")) {
            container.registerExtensionPoint(IConfigScreenFactory.class, (c, parent) -> AutoConfig.getConfigScreen(ClothConfigConstructor.class, parent).get());
        }

        modEventBus.addListener(InfernalExpansionNeoForgeClient::clientSetup);
        modEventBus.addListener(InfernalExpansionNeoForgeClient::registerEntityRenderers);
        modEventBus.addListener(InfernalExpansionNeoForgeClient::registerLayerDefinitions);
        modEventBus.addListener(InfernalExpansionNeoForgeClient::registerParticleProviders);
        modEventBus.addListener(InfernalExpansionNeoForgeClient::addEntityLayers);
    }

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (Map.Entry<ModelLayerLocation, Supplier<LayerDefinition>> entry : ModModelLayers.getLayerRegistry().entrySet()) {
            event.registerLayerDefinition(entry.getKey(), entry.getValue());
        }
    }

    private static void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(IECommonClient::initItemProperties);

        for (BlockDataHolder<?> block : BlockDataHolder.getCutoutBlocks()) {
            ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout());
        }
    }

    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (Map.Entry<EntityTypeDataHolder<?>, EntityRendererProvider<?>> entry : ModEntityRenderers.getEntityRendererRegistry().entrySet()) {
            registerEntityRendererHelper(event, entry.getKey(), entry.getValue());
        }

        for (Map.Entry<RegistryObject<? extends BlockEntityType<?>>, BlockEntityRendererProvider<?>> entry : ModBlockEntityRenderers.getRegistry().entrySet()) {
            registerBlockEntityRendererHelper(event, entry.getKey(), entry.getValue());
        }
    }

    public static void addEntityLayers(EntityRenderersEvent.AddLayers event) {
        WarpbeetleRenderer beetleRenderer = new WarpbeetleRenderer(event.getContext());

        PlayerRenderer defaultPlayer = event.getSkin(PlayerSkin.Model.WIDE);
        if (defaultPlayer != null) {
            defaultPlayer.addLayer(new WarpbeetleBackpackLayer(defaultPlayer, beetleRenderer));
        }

        PlayerRenderer slimPlayer = event.getSkin(PlayerSkin.Model.SLIM);
        if (slimPlayer != null) {
            slimPlayer.addLayer(new WarpbeetleBackpackLayer(slimPlayer, beetleRenderer));
        }
    }

    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticleTypes.GLOWSTONE_SPARKLE, GlowstoneSparkleParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.GLOWSQUITO_WING, GlowsquitoWingParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.TONGUE_WHIP_SLASH, TongueWhipSlashParticle.Provider::new);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> void registerEntityRendererHelper(EntityRenderersEvent.RegisterRenderers event, EntityTypeDataHolder<?> typeHolder, EntityRendererProvider<?> provider) {
        event.registerEntityRenderer((EntityType<T>) typeHolder.get(), (EntityRendererProvider<T>) provider);
    }

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> void registerBlockEntityRendererHelper(EntityRenderersEvent.RegisterRenderers event, RegistryObject<?> typeHolder, BlockEntityRendererProvider<?> provider) {
        event.registerBlockEntityRenderer((BlockEntityType<T>) typeHolder.get(), (BlockEntityRendererProvider<T>) provider);
    }
}