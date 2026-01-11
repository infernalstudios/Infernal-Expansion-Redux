package com.infernalstudios.infernalexp.entities.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class AvoidCampfiresGoal extends Goal {
    private final PathfinderMob mob;
    private final double avoidDistance;
    private final double speedModifier;
    private Vec3 avoidFrom;

    public AvoidCampfiresGoal(PathfinderMob mob, double avoidDistance, double speedModifier) {
        this.mob = mob;
        this.avoidDistance = avoidDistance;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.mob.getTarget() != null) {
            return false;
        }

        BlockPos campfirePos = findNearbyCampfire();
        if (campfirePos != null) {
            this.avoidFrom = new Vec3(campfirePos.getX() + 0.5, campfirePos.getY() + 0.5, campfirePos.getZ() + 0.5);
            return true;
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.mob.getTarget() != null) {
            return false;
        }

        if (this.avoidFrom == null) {
            return false;
        }

        return this.mob.distanceToSqr(this.avoidFrom) < this.avoidDistance * this.avoidDistance;
    }

    @Override
    public void start() {
        if (this.avoidFrom != null) {
            Vec3 moveToPos = this.getMoveAwayPosition();
            this.mob.getNavigation().moveTo(moveToPos.x, moveToPos.y, moveToPos.z, this.speedModifier);
        }
    }

    @Override
    public void tick() {
        if (this.mob.getNavigation().isDone() && this.avoidFrom != null) {
            Vec3 moveToPos = this.getMoveAwayPosition();
            this.mob.getNavigation().moveTo(moveToPos.x, moveToPos.y, moveToPos.z, this.speedModifier);
        }
    }

    private Vec3 getMoveAwayPosition() {
        Vec3 mobPos = this.mob.position();
        Vec3 direction = mobPos.subtract(this.avoidFrom).normalize();

        return mobPos.add(direction.scale(this.avoidDistance));
    }

    private BlockPos findNearbyCampfire() {
        BlockPos mobPos = this.mob.blockPosition();
        int horizontalRange = 8;
        int verticalRange = 16;

        for (BlockPos pos : BlockPos.betweenClosed(
                mobPos.offset(-horizontalRange, -verticalRange, -horizontalRange),
                mobPos.offset(horizontalRange, verticalRange, horizontalRange))) {

            BlockState state = this.mob.level().getBlockState(pos);

            if (state.is(BlockTags.CAMPFIRES) &&
                    state.hasProperty(CampfireBlock.LIT) &&
                    state.getValue(CampfireBlock.LIT)) {

                return pos.immutable();
            }
        }

        return null;
    }
}