package com.infernalstudios.infernalexp.world.feature.custom;

import com.infernalstudios.infernalexp.block.LuminousMushroomBlock;
import com.infernalstudios.infernalexp.block.SupportedBlock;
import com.infernalstudios.infernalexp.world.feature.NetherFeature;
import com.infernalstudios.infernalexp.world.feature.config.SingleBlockFeatureConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class SupportedBlockFeature extends NetherFeature<SingleBlockFeatureConfig> {
    public static final Feature<SingleBlockFeatureConfig> INSTANCE = new SupportedBlockFeature(SingleBlockFeatureConfig.CODEC);

    public SupportedBlockFeature(Codec<SingleBlockFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(BlockPos pos, FeaturePlaceContext<SingleBlockFeatureConfig> context) {
        WorldGenLevel world = context.level();
        RandomSource random = context.random();

        Direction dir = Direction.stream()
                .filter(d -> world.getBlockState(pos.relative(d)).isFaceSturdy(world, pos.relative(d), d.getOpposite()))
                .findAny().orElse(null);
        if (dir == null) return false;

        BlockState state = context.config().block().getState(random, pos);
        if (state.hasProperty(SupportedBlock.FACING)) state = state.setValue(SupportedBlock.FACING, dir);
        if (state.hasProperty(LuminousMushroomBlock.FLOOR)) {
            if (dir.getAxis() == Direction.Axis.Y)
                state = state.setValue(LuminousMushroomBlock.FLOOR, dir == Direction.DOWN);
            else return true;
        }

        this.setBlock(world, pos, state);

        return true;
    }

    @Override
    public boolean isValidPos(LevelReader world, BlockPos pos) {
        return Direction.stream().anyMatch(d -> {
            BlockState state = world.getBlockState(pos.relative(d));
            return state.isFaceSturdy(world, pos.relative(d), d.getOpposite()) && !state.is(Blocks.BEDROCK);
        });
    }
}