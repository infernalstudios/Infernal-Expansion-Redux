package com.infernalstudios.infernalexp.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShroomlightTearBlock extends Block {
    public TagKey<Block> growableOn;
    public static final VoxelShape BOX = Block.box(4, 4, 4, 12, 16, 12);
    public static final VoxelShape BOX_REVERSED = Block.box(4, 0, 4, 12, 12, 12);

    public static final BooleanProperty UP = BooleanProperty.create("is_up");

    public ShroomlightTearBlock(Properties properties, TagKey<Block> growableOn) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(UP, false));
        this.growableOn = growableOn;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UP);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return state.getValue(UP) ? BOX_REVERSED : BOX;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, @NotNull BlockPos pos) {
        BlockPos blockPos = state.getValue(UP) ? pos.below() : pos.above();
        BlockState support = world.getBlockState(blockPos);
        return support.is(this.growableOn);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState other, @NotNull LevelAccessor world,
                                           @NotNull BlockPos pos, @NotNull BlockPos otherPos) {
        BlockState result = super.updateShape(state, direction, other, world, pos, otherPos);
        if (!result.is(this)) return result;

        return this.canSurvive(state, world, pos) ? result : Blocks.AIR.defaultBlockState();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level world = context.getLevel();
        if (world.getBiome(context.getClickedPos()).is(Biomes.WARPED_FOREST))
            return this.defaultBlockState().setValue(UP, true);
        return this.defaultBlockState();
    }
}
