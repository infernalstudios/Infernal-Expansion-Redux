package com.infernalstudios.infernalexp.world.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record HangingMushroomFeatureConfig(IntProvider size, IntProvider radius, BlockStateProvider stem, BlockStateProvider tip) implements FeatureConfiguration {
    public static Codec<HangingMushroomFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    IntProvider.POSITIVE_CODEC.fieldOf("size").forGetter(HangingMushroomFeatureConfig::size),
                    IntProvider.POSITIVE_CODEC.fieldOf("radius").forGetter(HangingMushroomFeatureConfig::radius),
                    BlockStateProvider.CODEC.fieldOf("stem").forGetter(HangingMushroomFeatureConfig::stem),
                    BlockStateProvider.CODEC.fieldOf("tip").forGetter(HangingMushroomFeatureConfig::tip)
            ).apply(instance, HangingMushroomFeatureConfig::new));
}
