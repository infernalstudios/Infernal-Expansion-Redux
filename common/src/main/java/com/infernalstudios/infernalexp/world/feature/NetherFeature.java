package com.infernalstudios.infernalexp.world.feature;

import com.infernalstudios.infernalexp.mixin.accessor.WorldGenRegionAccessor;
import com.infernalstudios.infernalexp.world.feature.config.SingleBlockFeatureConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
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

    private static boolean ensureCanWrite(WorldGenLevel level, BlockPos pos) {
        if (!(level instanceof WorldGenRegion world)) return true;

        WorldGenRegionAccessor self = (WorldGenRegionAccessor) world;

        int x = SectionPos.blockToSectionCoord(pos.getX());
        int z = SectionPos.blockToSectionCoord(pos.getZ());
        ChunkPos chunkPos = world.getCenter();
        int sx = Math.abs(chunkPos.x - x);
        int sz = Math.abs(chunkPos.z - z);
        if (sx <= self.getWriteRadiusCutoff() && sz <= self.getWriteRadiusCutoff()) {
            if (self.getCenter().isUpgrading())
                return pos.getY() >= world.getMinBuildHeight() && pos.getY() < world.getMaxBuildHeight();
            return true;
        }
        return false;
    }

    @Override
    public boolean place(FeaturePlaceContext<F> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();

        List<BlockPos> positions = new ArrayList<>();
        for (int i = 0; i < level.getMaxBuildHeight(); i++) {
            if (this.isTargetBlock(level, pos.atY(i)) && this.isValidPos(level, pos.atY(i)))
                positions.add(pos.atY(i));
        }
        if (positions.isEmpty()) return false;
        Collections.shuffle(positions);
        pos = positions.get(0);

        boolean success = this.generate(pos, context);
        if (Math.random() < 0.85 && context.config() instanceof SingleBlockFeatureConfig single && single.spread()) {
            FeaturePlaceContext<F> contextnext =
                    new FeaturePlaceContext<>(context.topFeature(),
                            context.level(),
                            context.chunkGenerator(),
                            context.random(),
                            context.origin().east(context.random().nextIntBetweenInclusive(-4, 4))
                                    .north(context.random().nextIntBetweenInclusive(-4, 4)),
                            context.config());
            if (ensureCanWrite(level, contextnext.origin()))
                success |= this.place(contextnext);
        }

        return success;
    }

    public abstract boolean generate(BlockPos pos, FeaturePlaceContext<F> context);

    public abstract boolean isValidPos(LevelReader world, BlockPos pos);

    protected boolean isTargetBlock(WorldGenLevel level, BlockPos pos) {
        return level.isEmptyBlock(pos);
    }
}
