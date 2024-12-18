package com.infernalstudios.infernalexp.world.feature;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.block.DullthornsBlock;
import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModFeatures;
import com.infernalstudios.infernalexp.world.feature.config.DullthornsFeatureConfig;
import com.infernalstudios.infernalexp.world.feature.config.SingleBlockFeatureConfig;
import com.infernalstudios.infernalexp.world.feature.custom.DullthornsFeature;
import com.infernalstudios.infernalexp.world.feature.custom.GlowstoneSpikeFeature;
import com.infernalstudios.infernalexp.world.feature.custom.NetherPlantFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NetherForestVegetationConfig;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class ModConfiguredFeatures {
    public static ResourceKey<ConfiguredFeature<?, ?>> create(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, IECommon.id(name));
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

        register(context, LUMINOUS_FUNGUS, NetherPlantFeature.INSTANCE,
                new SingleBlockFeatureConfig(BlockStateProvider.simple(ModBlocks.LUMINOUS_FUNGUS.get()), true));

        register(context, GLOWLIGHT_FIRE, NetherPlantFeature.INSTANCE,
                new SingleBlockFeatureConfig(BlockStateProvider.simple(ModBlocks.GLOWLIGHT_FIRE.get()), true));

        register(context, GLOWSTONE_SPIKE, GlowstoneSpikeFeature.INSTANCE,
                new NoneFeatureConfiguration());
    }

    public static final ResourceKey<ConfiguredFeature<?, ?>> DULLTHORNS = create("dullthorns");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LUMINOUS_FUNGUS = create("luminous_fungus");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GLOWLIGHT_FIRE = create("glowlight_fire");

    public static final ResourceKey<ConfiguredFeature<?, ?>> GLOWSTONE_SPIKE = create("glowstone_spike");
}
