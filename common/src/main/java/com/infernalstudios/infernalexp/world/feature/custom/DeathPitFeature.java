package com.infernalstudios.infernalexp.world.feature.custom;

import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModTags;
import com.infernalstudios.infernalexp.world.feature.NetherFeature;
import com.infernalstudios.infernalexp.world.feature.config.DullthornsFeatureConfig;
import com.infernalstudios.infernalexp.world.feature.config.SingleBlockFeatureConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class DeathPitFeature extends NetherFeature<DullthornsFeatureConfig> {
    public static final Feature<DullthornsFeatureConfig> INSTANCE = new DeathPitFeature(DullthornsFeatureConfig.CODEC);

    public DeathPitFeature(Codec<DullthornsFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(BlockPos pos, FeaturePlaceContext<DullthornsFeatureConfig> context) {
        DullthornsFeatureConfig config = context.config();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();

        int radius = random.nextInt(2) + 2;
        int depth = config.size().sample(random);
        pos = pos.below(depth);
        if (pos.getY() <= level.getMinBuildHeight()) return false;


        BlockPos p;
        BlockState state;
        for (int i = 0; i < depth; i++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {

                    p = pos.east(x).north(z);
                    if (x*x + z*z <= radius * radius
                            && level.getBlockState(p).is(ModTags.Blocks.GLOWSTONE_CANYON_CARVER_REPLACEABLES)) {

                        if (p.getY() <= level.getMinBuildHeight() + 31)
                            state = Blocks.LAVA.defaultBlockState();
                        else if (i == 0)
                            state = config.tip().getState(random, p);
                        else if (level.getBlockState(p).is(ModBlocks.SHIMMER_SAND.get()) && level.isEmptyBlock(p.above()))
                            state = ModBlocks.GLIMMER_GRAVEL.get().defaultBlockState();
                        else
                            state = config.stem().getState(random, p);

                        this.setBlock(level, p, state);
                    }
                }
            }

            pos = pos.above();
        }

        return true;
    }

    @Override
    public boolean isValidPos(LevelReader world, BlockPos pos) {
        return world.getBlockState(pos.below()).is(ModBlocks.SHIMMER_SAND.get());
    }
}
