package com.infernalstudios.infernalexp.world.feature;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.world.feature.config.SingleBlockFeatureConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class NetherFeature<F extends FeatureConfiguration> extends Feature<F> {
    public NetherFeature(Codec<F> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<F> context) {
        IEConstants.LOG.info("" + context.origin());

        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();

        List<BlockPos> positions = new ArrayList<>();
        for (int i = 0; i < level.getMaxBuildHeight(); i++) {
            if (level.isEmptyBlock(pos.atY(i)) && this.isValidPos(level, pos.atY(i)))
                positions.add(pos.atY(i));
        }
        if (positions.isEmpty()) return false;
        Collections.shuffle(positions);
        IEConstants.LOG.info(positions + "");
        pos = positions.get(0);

        boolean success = this.generate(pos, context);
        if (Math.random() < 0.9 && context.config() instanceof SingleBlockFeatureConfig single && single.spread()) {
            FeaturePlaceContext<F> contextnext =
                    new FeaturePlaceContext<>(context.topFeature(),
                            context.level(),
                            context.chunkGenerator(),
                            context.random(),
                            context.origin().east(context.random().nextIntBetweenInclusive(-5, 5))
                                    .north(context.random().nextIntBetweenInclusive(-5, 5)),
                            context.config());
            //if (level.hasChunk(contextnext.origin().getX() / 16, contextnext.origin().getY() / 16))
                success |= this.place(contextnext);
        }

        return success;
    }

    public abstract boolean generate(BlockPos pos, FeaturePlaceContext<F> context);

    public abstract boolean isValidPos(LevelReader world, BlockPos pos);
}
