package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.world.carver.ModConfiguredCarvers;
import com.infernalstudios.infernalexp.world.feature.ModPlacedFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.Map;

public class CarverModuleFabric {
    public static void registerCarvers() {
        for (Map.Entry<ResourceKey<WorldCarver<?>>, WorldCarver<?>> entry : ModCarvers.getCarverRegistry().entrySet()) {
            // Register carver
            Registry.register(BuiltInRegistries.CARVER, entry.getKey(), entry.getValue());
        }
    }
}
