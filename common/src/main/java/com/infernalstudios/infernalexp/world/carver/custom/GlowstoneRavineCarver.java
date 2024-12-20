package com.infernalstudios.infernalexp.world.carver.custom;

import com.infernalstudios.infernalexp.module.ModBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CanyonWorldCarver;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.function.Function;

public class GlowstoneRavineCarver extends CanyonWorldCarver {
    public static final WorldCarver<CanyonCarverConfiguration> INSTANCE = new GlowstoneRavineCarver(CanyonCarverConfiguration.CODEC);

    public GlowstoneRavineCarver(Codec<CanyonCarverConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean carveBlock(CarvingContext context, CanyonCarverConfiguration config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> biomePos, CarvingMask carvingMask, BlockPos.MutableBlockPos mutablePos, BlockPos.MutableBlockPos p_159294_, Aquifer aquifer, MutableBoolean mutableBoolean) {
        if (this.canReplaceBlock(config, chunk.getBlockState(mutablePos))) {
            BlockState current = chunk.getBlockState(mutablePos);
            BlockState above = chunk.getBlockState(mutablePos.above());

            BlockState blockstate;
            if (mutablePos.getY() <= context.getMinGenY() + 31) {
                blockstate = LAVA.createLegacyBlock();
            } else if (current.is(ModBlocks.SHIMMER_SAND.get()) && above.isAir())
                blockstate = ModBlocks.GLIMMER_GRAVEL.get().defaultBlockState();
            else
                blockstate = CAVE_AIR;

            chunk.setBlockState(mutablePos, blockstate, false);

            return true;
        } else {
            return false;
        }
    }
}
