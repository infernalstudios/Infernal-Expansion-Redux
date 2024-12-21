package com.infernalstudios.infernalexp.world.feature.custom;

import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.world.feature.NetherFeature;
import com.infernalstudios.infernalexp.world.feature.config.SingleBlockFeatureConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class NetherPlantFeature extends NetherFeature<SingleBlockFeatureConfig> {
    public static final Feature<SingleBlockFeatureConfig> INSTANCE = new NetherPlantFeature(SingleBlockFeatureConfig.CODEC);

    public NetherPlantFeature(Codec<SingleBlockFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(BlockPos pos, FeaturePlaceContext<SingleBlockFeatureConfig> context) {
        SingleBlockFeatureConfig config = context.config();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();

        this.setBlock(level, pos, config.block().getState(random, pos));

        return true;
    }

    @Override
    public boolean isValidPos(LevelReader world, BlockPos pos) {
        return world.getBlockState(pos.below()).is(ModBlocks.SHIMMER_SAND.get());
    }
}
