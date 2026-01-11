package com.infernalstudios.infernalexp.fabric.module;

import com.infernalstudios.infernalexp.module.ModBiomes;
import com.infernalstudios.infernalexp.module.ModEntityTypes;
import com.infernalstudios.infernalexp.module.ModFeatures;
import com.infernalstudios.infernalexp.world.feature.ModPlacedFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biomes;
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
        registerSpawns();
    }

    private static void registerPlacement() {
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.NETHER_WASTES),
                GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.PLANTED_QUARTZ);

        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.SOUL_SAND_VALLEY),
                GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.BURIED_BONE);

        BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.BASALT_DELTAS),
                GenerationStep.Decoration.TOP_LAYER_MODIFICATION, ModPlacedFeatures.BASALT_IRON_ORE);
    }

    public static void registerSpawns() {
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(Biomes.NETHER_WASTES),
                MobCategory.MONSTER,
                ModEntityTypes.VOLINE.get(),
                45,
                1,
                3
        );
    }
}
