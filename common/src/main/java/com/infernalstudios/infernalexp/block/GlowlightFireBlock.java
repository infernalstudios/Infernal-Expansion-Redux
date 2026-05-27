package com.infernalstudios.infernalexp.block;

import com.infernalstudios.infernalexp.module.ModTags;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class GlowlightFireBlock extends BaseFireBlock {
    public static final MapCodec<GlowlightFireBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    propertiesCodec(),
                    Codec.FLOAT.fieldOf("fire_damage").forGetter(block -> block.glowlightFireDamage)
            ).apply(instance, GlowlightFireBlock::new)
    );

    public final float glowlightFireDamage;

    public GlowlightFireBlock(Properties properties, float damage) {
        super(properties, damage);
        this.glowlightFireDamage = damage;
    }

    public static boolean canSurviveOnBlock(BlockState state) {
        return state.is(ModTags.Blocks.GLOW_FIRE_BASE_BLOCKS);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseFireBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean canBurn(@NotNull BlockState state) {
        return true;
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos neighborPos) {
        return direction == Direction.DOWN && !this.canSurvive(state, level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, LevelReader world, BlockPos pos) {
        return canSurviveOnBlock(world.getBlockState(pos.below()));
    }
}