package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.world.feature.custom.DullthornsFeature;
import com.infernalstudios.infernalexp.world.feature.custom.GlowstoneSpikeFeature;
import com.infernalstudios.infernalexp.world.feature.custom.NetherPlantFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ModFeatures {
    /** Map of all Feature Resource Keys to their Feature. */
    private static final Map<ResourceKey<Feature<?>>, Feature<?>> FEATURE_REGISTRY = new HashMap<>();

    public static Feature<?> register(String name, Feature<?> feature) {
        return register(ResourceKey.create(Registries.FEATURE, IECommon.id(name)), feature);
    }

    public static Feature<?> register(ResourceKey<Feature<?>> id, Feature<?> feature) {
        FEATURE_REGISTRY.put(id, feature);
        return feature;
    }

    public static Map<ResourceKey<Feature<?>>, Feature<?>> getFeatureRegistry() {
        return FEATURE_REGISTRY;
    }

    // Called in the mod initializer / constructor in order to make sure that items are registered
    public static void load() {}


    public static final Feature<?> DULLTHORNS = register("dullthorns", DullthornsFeature.INSTANCE);
    public static final Feature<?> NETHER_PLANT = register("nether_plant", NetherPlantFeature.INSTANCE);

    public static final Feature<?> GLOWSTONE_SPIKE = register("glowstone_spike", GlowstoneSpikeFeature.INSTANCE);
}
