package com.infernalstudios.infernalexp.fabric.module;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.module.ModEntityTypes;
import com.infernalstudios.infernalexp.module.ModFeatures;
import com.infernalstudios.infernalexp.world.feature.ModPlacedFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.Map;

public class FeatureModuleFabric {
    public static void registerFeatures() {
        for (Map.Entry<ResourceKey<Feature<?>>, Feature<?>> entry : ModFeatures.getFeatureRegistry().entrySet()) {
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

        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(Biomes.WARPED_FOREST),
                MobCategory.AMBIENT,
                ModEntityTypes.WARPBEETLE.get(),
                50,
                1,
                5
        );

        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(Biomes.BASALT_DELTAS),
                MobCategory.AMBIENT,
                ModEntityTypes.GLOWSILK_MOTH.get(),
                1,
                1,
                1
        );

        BiomeModifications.create(new ResourceLocation(IEConstants.MOD_ID, "glowsilk_moth_spawn_cost"))
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.includeByKey(Biomes.BASALT_DELTAS),
                        context -> context.getSpawnSettings().setSpawnCost(ModEntityTypes.GLOWSILK_MOTH.get(), 0.4D, 1.0D));
    }
}