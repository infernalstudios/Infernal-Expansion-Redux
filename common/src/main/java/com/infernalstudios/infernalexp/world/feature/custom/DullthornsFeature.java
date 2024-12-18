package com.infernalstudios.infernalexp.world.feature.custom;

import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.world.feature.NetherFeature;
import com.infernalstudios.infernalexp.world.feature.config.DullthornsFeatureConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class DullthornsFeature extends NetherFeature<DullthornsFeatureConfig> {
    public static final Feature<DullthornsFeatureConfig> INSTANCE = new DullthornsFeature(DullthornsFeatureConfig.CODEC);

    public DullthornsFeature(Codec<DullthornsFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<DullthornsFeatureConfig> context) {
        return super.place(context);
    }

    @Override
    public boolean generate(BlockPos pos, FeaturePlaceContext<DullthornsFeatureConfig> context) {
        DullthornsFeatureConfig config = context.config();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();

        for (int i = 0; i < config.size().sample(random); i++) {
            if (level.isEmptyBlock(pos)) {
                this.setBlock(level, pos, config.stem().getState(random, pos));
            }
            else return true;
            pos = pos.above();
        }
        this.setBlock(level, pos, config.tip().getState(random, pos));

        return true;
    }

    @Override
    public boolean isValidPos(LevelReader world, BlockPos pos) {
        return world.getBlockState(pos.below()).is(ModBlocks.SHIMMER_SAND.get());
    }
}
