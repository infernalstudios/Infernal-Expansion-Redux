package com.infernalstudios.infernalexp.world.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record SingleBlockFeatureConfig(BlockStateProvider block, boolean spread) implements FeatureConfiguration {
    public static Codec<SingleBlockFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BlockStateProvider.CODEC.fieldOf("block").forGetter(SingleBlockFeatureConfig::block),
                    Codec.BOOL.fieldOf("spread").forGetter(SingleBlockFeatureConfig::spread)
            ).apply(instance, SingleBlockFeatureConfig::new));
}
