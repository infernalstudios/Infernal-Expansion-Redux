package com.infernalstudios.infernalexp.world.feature.custom;

import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.world.feature.NetherFeature;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class GlowstoneSpikeFeature extends NetherFeature<NoneFeatureConfiguration> {
    public static final Feature<NoneFeatureConfiguration> INSTANCE = new GlowstoneSpikeFeature(NoneFeatureConfiguration.CODEC);

    public GlowstoneSpikeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(BlockPos pos, FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        RandomSource random = context.random();

        BlockPos tip = pos.offset(random.nextInt(16) - 8, 0, random.nextInt(16) - 8);
        if (!world.isEmptyBlock(pos.below()))
            tip = tip.above(random.nextInt(10) + 10);
        else
            tip = tip.below(random.nextInt(10) + 10);
        if (!world.isEmptyBlock(tip)) return false;

        BlockState glowstone = Blocks.GLOWSTONE.defaultBlockState();
        BlockState dimstone = ModBlocks.DIMSTONE.get().defaultBlockState();
        BlockState dullstone = ModBlocks.DULLSTONE.get().defaultBlockState();
        boolean dark = random.nextBoolean();

        List<BlockPos> base = generateSphere(random.nextInt(2) + 1);
        List<BlockPos> line;
        for (BlockPos a : base) {
            if (world.isEmptyBlock(pos.offset(a)))
                this.setBlock(world, pos.offset(a), dark ? dullstone : glowstone);

            if (a.getY() == 0) {
                line = generateLine(pos.offset(a), tip);
                for (int i = 0; i < line.size(); i++) {
                    BlockPos b = line.get(i);
                    BlockState state;

                    float chance = (float) i / line.size() + random.nextFloat() * 0.3f - 0.15f;
                    if (dark) chance = 1 - chance;

                    if (chance <= 0.33) state = glowstone;
                    else if (chance <= 0.66) state = dimstone;
                    else state = dullstone;

                    this.setBlock(world, b, state);
                }
            }
        }

        return true;
    }

    @Override
    public boolean isValidPos(LevelReader world, BlockPos pos) {
        BlockState below = world.getBlockState(pos.below());
        return !below.is(Blocks.BEDROCK)
                && (below.isSolid() || !below.is(Blocks.LAVA) || !world.isEmptyBlock(pos.below()))
                && (world.getBlockState(pos.above()).is(ModBlocks.DULLSTONE.get()) || !world.isEmptyBlock(pos.below()));
    }

    private static List<BlockPos> generateSphere(float radius) {
        List<BlockPos> posList = new ArrayList<>();

        // Checks distance away from the center to see if the point is within the circle
        for (int x = (int) -radius; x <= radius; x++) {
            for (int y = (int) -radius; y <= radius; y++) {
                for (int z = (int) -radius; z <= radius; z++) {
                    if ((x * x) + (y * y) + (z * z) <= (radius * radius)) {
                        posList.add(new BlockPos(x, y, z));
                    }
                }
            }
        }

        return posList;
    }

    private static List<BlockPos> generateLine(BlockPos startPos, BlockPos endPos) {
        List<BlockPos> posList = new ArrayList<>();

        Vec3 vec1 = new Vec3(startPos.getX(), startPos.getY(), startPos.getZ());
        Vec3 vec2 = new Vec3(endPos.getX(), endPos.getY(), endPos.getZ());

        Vec3 diffVec = vec2.subtract(vec1);
        Vec3 incVec = diffVec.scale(1 / diffVec.length());

        for (int i = 0; i <= (int) diffVec.length(); i++) {
            posList.add(new BlockPos((int) vec1.x, (int) vec1.y, (int) vec1.z));
            vec1 = vec1.add(incVec);
        }

        return posList;
    }
}
