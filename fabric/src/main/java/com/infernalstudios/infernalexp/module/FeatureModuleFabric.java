package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.registration.FuelRegistry;
import com.infernalstudios.infernalexp.registration.holders.ItemDataHolder;
import com.infernalstudios.infernalexp.world.feature.ModPlacedFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.Map;

public class FeatureModuleFabric {
    public static void registerFeatures() {
        for (Map.Entry<ResourceKey<Feature<?>>, Feature<?>> entry : ModFeatures.getFeatureRegistry().entrySet()) {
            // Register feature
            Registry.register(BuiltInRegistries.FEATURE, entry.getKey(), entry.getValue());
        }

        registerPlacement();
    }

    private static void registerPlacement() {
        BiomeModifications.addFeature(BiomeSelectors.tag(ModTags.Biomes.IS_GLOWSTONE_CANYON),
                GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.DULLTHORNS);

        BiomeModifications.addFeature(BiomeSelectors.tag(ModTags.Biomes.IS_GLOWSTONE_CANYON),
                GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.LUMINOUS_FUNGUS);

        BiomeModifications.addFeature(BiomeSelectors.tag(ModTags.Biomes.IS_GLOWSTONE_CANYON),
                GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.GLOWLIGHT_FIRE);

        BiomeModifications.addFeature(BiomeSelectors.tag(ModTags.Biomes.IS_GLOWSTONE_CANYON),
                GenerationStep.Decoration.UNDERGROUND_DECORATION, ModPlacedFeatures.GLOWSTONE_SPIKE);
    }
}
