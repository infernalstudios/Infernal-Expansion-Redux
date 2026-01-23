package com.infernalstudios.infernalexp;

import com.infernalstudios.infernalexp.config.ClothConfigConstructor;
import com.infernalstudios.infernalexp.config.IEConfig;
import com.infernalstudios.infernalexp.datagen.config.ConfiguredData;
import com.infernalstudios.infernalexp.module.*;
import com.infernalstudios.infernalexp.platform.Services;
import com.infernalstudios.infernalexp.registration.StrippableRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.ComposterBlock;

public class IECommon {
    private static IEConfig CONFIG;

    public static void init() {
        ConfiguredData.register();

        if (isClothConfigLoaded()) {
            registerConfig();
        } else {
            CONFIG = new IEConfig();
        }

        ModBlocks.load();
        StrippableRegistry.register(ModBlocks.WAXED_GLOWSTONE.get(), net.minecraft.world.level.block.Blocks.GLOWSTONE);
        ModItems.load();
        ModFireTypes.load();
        ModEntityTypes.load();
        ModBlockEntityTypes.load();
        ModCreativeTabs.load();
        ModBiomes.load();
        ModFeatures.load();
        ModStructureTypes.load();
        ModEffects.load();
        ModCarvers.load();
        ModSurfaceRuleConditions.load();
        ModParticleTypes.load();
        ModSounds.load();
    }

    public static ResourceLocation makeID(String name) {
        return new ResourceLocation(IEConstants.MOD_ID, name);
    }

    private static boolean isClothConfigLoaded() {
        return Services.PLATFORM.isModLoaded("cloth_config") || Services.PLATFORM.isModLoaded("cloth-config");
    }

    private static void registerConfig() {
        AutoConfig.register(ClothConfigConstructor.class, GsonConfigSerializer::new);
        ConfigHolder<ClothConfigConstructor> holder = AutoConfig.getConfigHolder(ClothConfigConstructor.class);

        CONFIG = new IEConfig();
        syncConfig(holder.getConfig());

        holder.registerSaveListener((manager, data) -> {
            syncConfig(data);
            return InteractionResult.PASS;
        });
    }

    /**
     * Updates the main config instance with values from the Cloth Config wrapper.
     */
    private static void syncConfig(ClothConfigConstructor data) {
        CONFIG.client = data.client;
        CONFIG.common = data.common;
    }

    public static IEConfig getConfig() {
        return CONFIG;
    }

    public static void registerCompostables() {
        ModBlocks.getBlockRegistry().values().forEach(holder -> {
            if (holder.isCompostable() && holder.hasItem()) {
                ComposterBlock.COMPOSTABLES.put(holder.get().asItem(), holder.getCompostChance());
            }
        });
    }

    @Deprecated
    public static <T> T log(T message) {
        return log(message, 0);
    }

    public static <T> T log(T message, int level) {
        if (level == 0)
            IEConstants.LOG.info("[InfernalExpansion] {}", message);
        else if (level == 1)
            IEConstants.LOG.warn("[InfernalExpansion] {}", message);
        else if (level == 2)
            IEConstants.LOG.error("[InfernalExpansion] {}", message);
        return message;
    }
}