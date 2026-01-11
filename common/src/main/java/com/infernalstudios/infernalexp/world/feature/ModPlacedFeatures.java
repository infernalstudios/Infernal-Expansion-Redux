package com.infernalstudios.infernalexp.world.feature;

import com.infernalstudios.infernalexp.IECommon;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> DULLTHORNS = create("dullthorns");
    public static final ResourceKey<PlacedFeature> LUMINOUS_FUNGUS = create("luminous_fungus");
    public static final ResourceKey<PlacedFeature> GLOWLIGHT_FIRE = create("glowlight_fire");
    public static final ResourceKey<PlacedFeature> HANGING_BROWN_MUSHROOM = create("hanging_brown_mushroom");
    public static final ResourceKey<PlacedFeature> GLOWSTONE_SPIKE = create("glowstone_spike");
    public static final ResourceKey<PlacedFeature> DEATH_PIT = create("death_pit");
    public static final ResourceKey<PlacedFeature> PLANTED_QUARTZ = create("planted_quartz");
    public static final ResourceKey<PlacedFeature> BURIED_BONE = create("buried_bone");
    public static final ResourceKey<PlacedFeature> BASALT_IRON_ORE = create("basalt_iron_ore");
    public static final ResourceKey<PlacedFeature> GSC_BLACKSTONE_BLOBS = create("gsc_blackstone_blobs");
    public static final ResourceKey<PlacedFeature> GSC_SPRING_OPEN = create("gsc_spring_open");
    public static final ResourceKey<PlacedFeature> GSC_SPRING_CLOSED = create("gsc_spring_closed");
    public static final ResourceKey<PlacedFeature> HUGE_LUMINOUS_FUNGUS = create("huge_luminous_fungus");
    public static final ResourceKey<PlacedFeature> ORE_BASALT_IRON_SHORES = create("ore_basalt_iron_shores");

    public static ResourceKey<PlacedFeature> create(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, IECommon.makeID(name));
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

        register(context, LUMINOUS_FUNGUS,
                configLookup.getOrThrow(ModConfiguredFeatures.LUMINOUS_FUNGUS),
                CountPlacement.of(5),
                InSquarePlacement.spread(),
                PlacementUtils.FULL_RANGE,
                BiomeFilter.biome()
        );

        register(context, DULLTHORNS, configLookup.getOrThrow(ModConfiguredFeatures.DULLTHORNS),
                InSquarePlacement.spread(),
                PlacementUtils.FULL_RANGE,
                BiomeFilter.biome());

        register(context, GLOWLIGHT_FIRE, configLookup.getOrThrow(ModConfiguredFeatures.GLOWLIGHT_FIRE),
                RarityFilter.onAverageOnceEvery(2),
                InSquarePlacement.spread(),
                PlacementUtils.FULL_RANGE,
                BiomeFilter.biome());

        register(context, HANGING_BROWN_MUSHROOM, configLookup.getOrThrow(ModConfiguredFeatures.HANGING_BROWN_MUSHROOM),
                RarityFilter.onAverageOnceEvery(4),
                InSquarePlacement.spread(),
                PlacementUtils.FULL_RANGE,
                BiomeFilter.biome());

        register(context, GLOWSTONE_SPIKE, configLookup.getOrThrow(ModConfiguredFeatures.GLOWSTONE_SPIKE),
                RarityFilter.onAverageOnceEvery(4),
                InSquarePlacement.spread(),
                PlacementUtils.FULL_RANGE,
                BiomeFilter.biome());

        register(context, DEATH_PIT, configLookup.getOrThrow(ModConfiguredFeatures.DEATH_PIT),
                RarityFilter.onAverageOnceEvery(3),
                InSquarePlacement.spread(),
                PlacementUtils.FULL_RANGE,
                BiomeFilter.biome());

        register(context, PLANTED_QUARTZ, configLookup.getOrThrow(ModConfiguredFeatures.PLANTED_QUARTZ),
                RarityFilter.onAverageOnceEvery(2),
                InSquarePlacement.spread(),
                PlacementUtils.FULL_RANGE,
                BiomeFilter.biome());

        register(context, BURIED_BONE, configLookup.getOrThrow(ModConfiguredFeatures.BURIED_BONE),
                RarityFilter.onAverageOnceEvery(2),
                InSquarePlacement.spread(),
                PlacementUtils.FULL_RANGE,
                BiomeFilter.biome());

        register(context, BASALT_IRON_ORE, configLookup.getOrThrow(ModConfiguredFeatures.BASALT_IRON_ORE),
                InSquarePlacement.spread(),
                CountPlacement.of(20),
                HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(10), VerticalAnchor.belowTop(10)),
                BiomeFilter.biome());

        register(context, ORE_BASALT_IRON_SHORES, configLookup.getOrThrow(ModConfiguredFeatures.ORE_BASALT_IRON_SHORES),
                CountPlacement.of(256),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(192)),
                BiomeFilter.biome());

        register(context, GSC_BLACKSTONE_BLOBS, configLookup.getOrThrow(ModConfiguredFeatures.GSC_BLACKSTONE_BLOBS),
                CountPlacement.of(3),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(128)),
                BiomeFilter.biome());

        register(context, GSC_SPRING_OPEN, configLookup.getOrThrow(ModConfiguredFeatures.GSC_SPRING_OPEN),
                CountPlacement.of(8),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(4), VerticalAnchor.absolute(124)),
                BiomeFilter.biome());

        register(context, GSC_SPRING_CLOSED, configLookup.getOrThrow(ModConfiguredFeatures.GSC_SPRING_CLOSED),
                CountPlacement.of(16),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(10), VerticalAnchor.absolute(118)),
                BiomeFilter.biome());

        register(context, HUGE_LUMINOUS_FUNGUS, configLookup.getOrThrow(ModConfiguredFeatures.HUGE_LUMINOUS_FUNGUS),
                BlockPredicateFilter.forPredicate(BlockPredicate.replaceable()));
    }
}