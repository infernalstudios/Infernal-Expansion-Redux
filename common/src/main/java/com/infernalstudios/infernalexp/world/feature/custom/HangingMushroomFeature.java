package com.infernalstudios.infernalexp.world.feature.custom;

import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModTags;
import com.infernalstudios.infernalexp.world.feature.NetherFeature;
import com.infernalstudios.infernalexp.world.feature.config.HangingMushroomFeatureConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.NyliumBlock;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class HangingMushroomFeature extends NetherFeature<HangingMushroomFeatureConfig> {
    public static final Feature<HangingMushroomFeatureConfig> INSTANCE = new HangingMushroomFeature(HangingMushroomFeatureConfig.CODEC);

    public HangingMushroomFeature(Codec<HangingMushroomFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(BlockPos pos, FeaturePlaceContext<HangingMushroomFeatureConfig> context) {
        HangingMushroomFeatureConfig config = context.config();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();

        int size = config.size().sample(random);
        if (!level.isEmptyBlock(pos.below(size)))
            return false;

        for (int i = 0; i < size; i++) {
            if (level.isEmptyBlock(pos)) {
                this.setBlock(level, pos, config.stem().getState(random, pos));
            }
            else return true;
            pos = pos.below();
        }

        int radius = config.radius().sample(random);
        BlockPos p;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                p = pos.east(x).north(z);
                if (x * x + z * z < radius * radius && level.isEmptyBlock(p))
                    this.setBlock(level, p, config.tip().getState(random, p));
            }
        }

        return true;
    }

    @Override
    public boolean isValidPos(LevelReader world, BlockPos pos) {
        return world.getBlockState(pos.above()).is(ModBlocks.DULLSTONE.get());
    }
}
