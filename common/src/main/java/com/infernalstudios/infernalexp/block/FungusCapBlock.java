package com.infernalstudios.infernalexp.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class FungusCapBlock extends Block {
    public FungusCapBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void fallOn(@NotNull Level world, @NotNull BlockState state, @NotNull BlockPos pos, Entity entity, float distance) {
        if (entity.isSuppressingBounce())
            super.fallOn(world, state, pos, entity, distance);
        else
            entity.causeFallDamage(distance, 0.0F, world.damageSources().fall());
    }

    @Override
    public void updateEntityAfterFallOn(@NotNull BlockGetter world, Entity entity) {
        if (entity.isSuppressingBounce())
            super.updateEntityAfterFallOn(world, entity);
        else
            this.bounceUp(entity);
    }

    private void bounceUp(Entity entity) {
        Vec3 vec = entity.getDeltaMovement();
        if (vec.y < 0.0) {
            entity.setDeltaMovement(vec.x, -vec.y * 0.7, vec.z);
        }
    }
}
