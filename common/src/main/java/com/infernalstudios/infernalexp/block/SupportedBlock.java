package com.infernalstudios.infernalexp.block;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.module.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public class SupportedBlock extends Block {
    public static final DirectionProperty FACING = DirectionProperty.create("facing");
    public static final Map<Direction, VoxelShape> SHAPES = Map.of(
            Direction.DOWN, Block.box(3, 0, 3, 13, 10, 13),
            Direction.UP, Block.box(3, 6, 3, 13, 16, 13),
            Direction.NORTH, Block.box(3, 3, 0, 13, 13, 10),
            Direction.SOUTH, Block.box(3, 3, 6, 13, 13, 16),
            Direction.EAST, Block.box(6, 3, 3, 16, 13, 13),
            Direction.WEST, Block.box(0, 3, 3, 10, 13, 13)
    );

    private final Supplier<Item> pickItem;

    public SupportedBlock(Properties properties, Supplier<Item> pickItem) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.DOWN));
        this.pickItem = pickItem;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull BlockState state) {
        return new ItemStack(this.pickItem.get());
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader world, @NotNull BlockPos pos) {
        if (this == ModBlocks.PLANTED_QUARTZ.get() && !IECommon.getConfig().common.worldGeneration.enablePlantedQuartz)
            return false;
        if (this == ModBlocks.BURIED_BONE.get() && !IECommon.getConfig().common.worldGeneration.enableBuriedBone)
            return false;

        BlockPos blockPos = pos.relative(state.getValue(FACING));
        BlockState support = world.getBlockState(blockPos);
        return support.isFaceSturdy(world, blockPos, state.getValue(FACING));
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
        return this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }
}