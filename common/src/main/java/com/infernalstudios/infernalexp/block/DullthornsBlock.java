package com.infernalstudios.infernalexp.block;

import com.infernalstudios.infernalexp.block.parent.NetherPlantBlock;
import com.infernalstudios.infernalexp.module.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class DullthornsBlock extends NetherPlantBlock {
    public static void applyEffect(Entity entity) {
        if (entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(ModEffects.LUMINOUS.get(), 200, 0));
        }
        entity.hurt(entity.damageSources().cactus(), 1.0F);
    }

    public static final BooleanProperty TIP = BooleanProperty.create("is_tip");

    public DullthornsBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(TIP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TIP);
    }

    @Override
    public boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos) {
        return super.mayPlaceOn(floor, world, pos) || floor.is(this);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Entity entity) {
        super.entityInside(state, world, pos, entity);
        applyEffect(entity);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Block.box(3, 0, 3, 13, 16, 13);
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(TIP, true);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState other, @NotNull LevelAccessor world,
                                           @NotNull BlockPos pos, @NotNull BlockPos otherPos) {
        BlockState result = super.updateShape(state, direction, other, world, pos, otherPos);
        if (!result.is(this)) return result;

        if (direction == Direction.UP && !other.is(this))
            return result.setValue(TIP, true);
        if (direction == Direction.UP && other.is(this))
            return result.setValue(TIP, false);
        return result;
    }
}