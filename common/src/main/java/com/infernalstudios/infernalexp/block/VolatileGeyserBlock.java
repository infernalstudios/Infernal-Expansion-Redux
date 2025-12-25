package com.infernalstudios.infernalexp.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class VolatileGeyserBlock extends Block {
    protected static final VoxelShape SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 12.0D, 13.0D);

    public VolatileGeyserBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        if (entity instanceof LivingEntity) {
            double jumpHeight = 0.9D;

            if (level.getBlockState(pos.below()).is(Blocks.MAGMA_BLOCK)) {
                jumpHeight = 1.3D;
            }

            entity.setDeltaMovement(entity.getDeltaMovement().x, jumpHeight, entity.getDeltaMovement().z);
            entity.hurtMarked = true;
        }
        super.stepOn(level, pos, state, entity);
    }
}