package com.infernalstudios.infernalexp.world.feature;

import com.infernalstudios.infernalexp.IECommon;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

import java.util.List;

public class ModPlacedFeatures {
    public static ResourceKey<PlacedFeature> create(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, IECommon.id(name));
    }

    private static void register(BootstapContext<PlacedFeature> context,
                                 ResourceKey<PlacedFeature> key,
                                 Holder<ConfiguredFeature<?, ?>> config,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(config, List.copyOf(modifiers)));
    }

    private static void register(BootstapContext<PlacedFeature> context,
                                 ResourceKey<PlacedFeature> key,
                                 Holder<ConfiguredFeature<?, ?>> config,
                                 PlacementModifier... modifiers) {
        register(context, key, config, List.of(modifiers));
    }

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        var configLookup = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, DULLTHORNS, configLookup.getOrThrow(ModConfiguredFeatures.DULLTHORNS),
                BiomeFilter.biome());

        register(context, LUMINOUS_FUNGUS, configLookup.getOrThrow(ModConfiguredFeatures.LUMINOUS_FUNGUS),
                BiomeFilter.biome());

        register(context, GLOWLIGHT_FIRE, configLookup.getOrThrow(ModConfiguredFeatures.GLOWLIGHT_FIRE),
                BiomeFilter.biome());

        register(context, GLOWSTONE_SPIKE, configLookup.getOrThrow(ModConfiguredFeatures.GLOWSTONE_SPIKE),
                RarityFilter.onAverageOnceEvery(3),
                BiomeFilter.biome());

        register(context, DEATH_PIT, configLookup.getOrThrow(ModConfiguredFeatures.DEATH_PIT),
                RarityFilter.onAverageOnceEvery(3),
                BiomeFilter.biome());

        register(context, PLANTED_QUARTZ, configLookup.getOrThrow(ModConfiguredFeatures.PLANTED_QUARTZ),
                RarityFilter.onAverageOnceEvery(2),
                BiomeFilter.biome());

        register(context, BURIED_BONE, configLookup.getOrThrow(ModConfiguredFeatures.BURIED_BONE),
                RarityFilter.onAverageOnceEvery(2),
                BiomeFilter.biome());
    }



    public static final ResourceKey<PlacedFeature> DULLTHORNS = create("dullthorns");
    public static final ResourceKey<PlacedFeature> LUMINOUS_FUNGUS = create("luminous_fungus");
    public static final ResourceKey<PlacedFeature> GLOWLIGHT_FIRE = create("glowlight_fire");

    public static final ResourceKey<PlacedFeature> GLOWSTONE_SPIKE = create("glowstone_spike");
    public static final ResourceKey<PlacedFeature> DEATH_PIT = create("death_pit");

    public static final ResourceKey<PlacedFeature> PLANTED_QUARTZ = create("planted_quartz");
    public static final ResourceKey<PlacedFeature> BURIED_BONE = create("buried_bone");
}
