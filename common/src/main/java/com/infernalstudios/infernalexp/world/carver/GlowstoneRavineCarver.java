package com.infernalstudios.infernalexp.world.carver;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CanyonWorldCarver;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.function.Function;

public class GlowstoneRavineCarver extends CanyonWorldCarver {
    public GlowstoneRavineCarver(Codec<CanyonCarverConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean carveBlock(CarvingContext context, CanyonCarverConfiguration config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> biomePos, CarvingMask carvingMask, BlockPos.MutableBlockPos mutablePos, BlockPos.MutableBlockPos p_159294_, Aquifer aquifer, MutableBoolean mutableBoolean) {
        if (this.canReplaceBlock(config, chunk.getBlockState(mutablePos))) {
            BlockState blockstate;
            if (mutablePos.getY() <= context.getMinGenY() + 31) {
                blockstate = LAVA.createLegacyBlock();
            } else {
                blockstate = CAVE_AIR;
            }

            chunk.setBlockState(mutablePos, blockstate, false);

            return true;
        } else {
            return false;
        }
    }
}
