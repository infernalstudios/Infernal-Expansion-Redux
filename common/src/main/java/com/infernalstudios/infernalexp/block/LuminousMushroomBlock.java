package com.infernalstudios.infernalexp.block;

import com.infernalstudios.infernalexp.block.entity.LuminousMushroomBlockEntity;
import com.infernalstudios.infernalexp.block.parent.NetherPlantBlock;
import com.infernalstudios.infernalexp.module.ModBlockEntityTypes;
import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModTags;
import com.infernalstudios.infernalexp.world.feature.ModConfiguredFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LuminousMushroomBlock extends NetherPlantBlock implements EntityBlock, BonemealableBlock {
    public static final BooleanProperty LIT = BooleanProperty.create("is_lit");
    public static final BooleanProperty FLOOR = BooleanProperty.create("is_floor");

    public static final VoxelShape BOX = Block.box(4, 0, 4, 12, 8, 12);
    public static final VoxelShape BOX_REVERSED = Block.box(4, 6, 4, 12, 16, 12);

    public LuminousMushroomBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false).setValue(FLOOR, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT).add(FLOOR);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return state.getValue(FLOOR) ? BOX : BOX_REVERSED;
    }

    @Override
    public boolean canSurvive(BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        return state.getValue(FLOOR) ? level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)
                : level.getBlockState(pos.above()).isFaceSturdy(level, pos.above(), Direction.DOWN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelReader world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        boolean up = world.getBlockState(pos.above()).isFaceSturdy(world, pos, Direction.DOWN);
        boolean down = world.getBlockState(pos.below()).isFaceSturdy(world, pos, Direction.UP);

        if (down && up) {
            if (context.getNearestLookingVerticalDirection() == Direction.UP)
                return this.defaultBlockState().setValue(FLOOR, false);
        }
        else if (up)
            return this.defaultBlockState().setValue(FLOOR, false);
        return this.defaultBlockState();
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Entity entity) {
        super.entityInside(state, world, pos, entity);
        if (!state.getValue(LIT) && entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0));
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new LuminousMushroomBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (!world.isClientSide()) {
            return createTickerHelper(type, ModBlockEntityTypes.LUMINOUS_MUSHROOM.get(), LuminousMushroomBlockEntity::tick);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> type, BlockEntityType<E> correctType, BlockEntityTicker<? super E> ticker) {
        return type == correctType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state, boolean isClient) {
        BlockState groundState = level.getBlockState(pos.below());
        return groundState.is(ModBlocks.SHIMMER_SAND.get()) || groundState.is(ModTags.Blocks.GLOW_FIRE_BASE_BLOCKS);
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        return (double) random.nextFloat() < 0.4D;
    }

    @Override
    public void performBonemeal(@NotNull ServerLevel level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        // Updated to use HUGE_LUMINOUS_MUSHROOM
        ConfiguredFeature<?, ?> feature = level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE)
                .getOrThrow(ModConfiguredFeatures.HUGE_LUMINOUS_MUSHROOM);

        if (state.getValue(FLOOR)) {
            level.removeBlock(pos, false);

            if (!feature.place(level, level.getChunkSource().getGenerator(), random, pos)) {
                level.setBlock(pos, state, 3);
            }
        }
    }
}