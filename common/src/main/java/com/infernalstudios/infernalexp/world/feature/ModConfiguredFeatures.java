package com.infernalstudios.infernalexp.world.feature;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.block.DullthornsBlock;
import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.world.feature.config.DullthornsFeatureConfig;
import com.infernalstudios.infernalexp.world.feature.config.HangingMushroomFeatureConfig;
import com.infernalstudios.infernalexp.world.feature.config.SingleBlockFeatureConfig;
import com.infernalstudios.infernalexp.world.feature.custom.*;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> DULLTHORNS = create("dullthorns");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LUMINOUS_MUSHROOM = create("luminous_mushroom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GLOWLIGHT_FIRE = create("glowlight_fire");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HANGING_BROWN_MUSHROOM = create("hanging_brown_mushroom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GLOWSTONE_SPIKE = create("glowstone_spike");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEATH_PIT = create("death_pit");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PLANTED_QUARTZ = create("planted_quartz");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BURIED_BONE = create("buried_bone");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BASALT_IRON_ORE = create("basalt_iron_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GSC_BLACKSTONE_BLOBS = create("gsc_blackstone_blobs");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GSC_SPRING_OPEN = create("gsc_spring_open");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GSC_SPRING_CLOSED = create("gsc_spring_closed");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HUGE_LUMINOUS_MUSHROOM = create("huge_luminous_mushroom");

    public static ResourceKey<ConfiguredFeature<?, ?>> create(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, IECommon.makeID(name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC config) {
        context.register(key, new ConfiguredFeature<>(feature, config));
    }

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        register(context, DULLTHORNS, DullthornsFeature.INSTANCE,
                new DullthornsFeatureConfig(UniformInt.of(4, 7),
                        BlockStateProvider.simple(ModBlocks.DULLTHORNS.get()),
                        BlockStateProvider.simple(ModBlocks.DULLTHORNS.get().defaultBlockState().setValue(DullthornsBlock.TIP, true))));

        register(context, LUMINOUS_MUSHROOM, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.LUMINOUS_MUSHROOM.get())),
                        List.of(ModBlocks.SHIMMER_SAND.get())));

        register(context, GLOWLIGHT_FIRE, NetherPlantFeature.INSTANCE,
                new SingleBlockFeatureConfig(BlockStateProvider.simple(ModBlocks.GLOWLIGHT_FIRE.get()), true));

        register(context, HANGING_BROWN_MUSHROOM, HangingMushroomFeature.INSTANCE,
                new HangingMushroomFeatureConfig(UniformInt.of(3, 7),
                        ConstantInt.of(4),
                        BlockStateProvider.simple(Blocks.MUSHROOM_STEM.defaultBlockState()),
                        BlockStateProvider.simple(Blocks.BROWN_MUSHROOM_BLOCK.defaultBlockState().setValue(HugeMushroomBlock.UP, false))));

        register(context, GLOWSTONE_SPIKE, GlowstoneSpikeFeature.INSTANCE,
                new NoneFeatureConfiguration());

        register(context, DEATH_PIT, DeathPitFeature.INSTANCE,
                new HangingMushroomFeatureConfig(UniformInt.of(12, 30),
                        UniformInt.of(2, 3),
                        BlockStateProvider.simple(Blocks.AIR.defaultBlockState()),
                        new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                                .add(ModBlocks.GLOWSILK_COCOON.get().defaultBlockState(), 1)
                                .add(ModBlocks.GLOWLIGHT_FIRE.get().defaultBlockState(), 6)
                                .add(Blocks.AIR.defaultBlockState(), 12))));

        register(context, PLANTED_QUARTZ, SupportedBlockFeature.INSTANCE,
                new SingleBlockFeatureConfig(BlockStateProvider.simple(ModBlocks.PLANTED_QUARTZ.get()), true));

        register(context, BURIED_BONE, SupportedBlockFeature.INSTANCE,
                new SingleBlockFeatureConfig(BlockStateProvider.simple(ModBlocks.BURIED_BONE.get()), true));

        register(context, BASALT_IRON_ORE, Feature.ORE,
                new OreConfiguration(new BlockMatchTest(Blocks.BASALT), ModBlocks.BASALT_IRON_ORE.get().defaultBlockState(), 10));

        register(context, GSC_BLACKSTONE_BLOBS, Feature.REPLACE_BLOBS,
                new ReplaceSphereConfiguration(ModBlocks.DULLSTONE.get().defaultBlockState(), Blocks.BLACKSTONE.defaultBlockState(), UniformInt.of(2, 3)));

        register(context, GSC_SPRING_OPEN, Feature.SPRING,
                new SpringConfiguration(Fluids.LAVA.defaultFluidState(), false, 4, 1, HolderSet.direct(Block::builtInRegistryHolder, Blocks.NETHERRACK, ModBlocks.DULLSTONE.get(), ModBlocks.DIMSTONE.get())));

        register(context, GSC_SPRING_CLOSED, Feature.SPRING,
                new SpringConfiguration(Fluids.LAVA.defaultFluidState(), false, 5, 0, HolderSet.direct(Block::builtInRegistryHolder, Blocks.NETHERRACK, ModBlocks.DULLSTONE.get(), ModBlocks.DIMSTONE.get())));

        register(context, HUGE_LUMINOUS_MUSHROOM, Feature.HUGE_BROWN_MUSHROOM,
                new HugeMushroomFeatureConfiguration(
                        BlockStateProvider.simple(ModBlocks.LUMINOUS_MUSHROOM_BLOCK.get().defaultBlockState().setValue(HugeMushroomBlock.UP, true).setValue(HugeMushroomBlock.DOWN, false)),
                        BlockStateProvider.simple(Blocks.MUSHROOM_STEM.defaultBlockState().setValue(HugeMushroomBlock.UP, false).setValue(HugeMushroomBlock.DOWN, false)),
                        3));
    }
}