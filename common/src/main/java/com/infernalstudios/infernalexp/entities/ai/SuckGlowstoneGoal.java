package com.infernalstudios.infernalexp.entities.ai;

import com.infernalstudios.infernalexp.entities.GlowsquitoEntity;
import com.infernalstudios.infernalexp.module.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class SuckGlowstoneGoal extends Goal {
    private final GlowsquitoEntity mob;
    private final Level level;
    private int eatAnimationTick;
    private int timeoutCounter;
    private BlockPos targetPos = BlockPos.ZERO;
    private Direction targetFace = Direction.NORTH;

    private long nextUseTime;

    public SuckGlowstoneGoal(GlowsquitoEntity mob) {
        this.mob = mob;
        this.level = mob.level();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.level.getGameTime() < this.nextUseTime) return false;
        if (this.mob.isAggressive() || this.mob.getTarget() != null) return false;

        if (this.mob.getRandom().nextInt(50) != 0) return false;

        BlockPos pos = this.findGlowstoneOrDimstone();
        if (pos != null) {
            this.targetPos = pos;
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.timeoutCounter > 400 && !this.mob.isEating()) return false;
        if (this.targetPos == null || !isValidTarget(this.level.getBlockState(this.targetPos))) return false;

        if (!this.level.isEmptyBlock(this.targetPos.relative(this.targetFace))) return false;

        return !this.mob.isAggressive();
    }

    @Override
    public void start() {
        this.eatAnimationTick = 0;
        this.timeoutCounter = 0;
        this.moveToTarget();
    }

    @Override
    public void stop() {
        this.eatAnimationTick = 0;
        this.timeoutCounter = 0;
        this.targetPos = BlockPos.ZERO;
        this.mob.setEating(false);
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.timeoutCounter++;

        double destX = this.targetPos.getX() + 0.5D + this.targetFace.getStepX();
        double destY = this.targetPos.getY() + 0.5D + this.targetFace.getStepY();
        double destZ = this.targetPos.getZ() + 0.5D + this.targetFace.getStepZ();

        double distSqr = this.mob.distanceToSqr(destX, destY, destZ);

        if (this.mob.isEating()) {
            this.mob.setPos(destX, destY, destZ);
            this.mob.setDeltaMovement(Vec3.ZERO);
            this.faceTargetInstantly();

            this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);

            if (this.eatAnimationTick == 1) {
                if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                    BlockState currentState = this.level.getBlockState(targetPos);

                    if (currentState.is(Blocks.GLOWSTONE)) {
                        this.level.levelEvent(2001, targetPos, Block.getId(currentState));
                        this.level.setBlock(targetPos, ModBlocks.DIMSTONE.get().defaultBlockState(), 3);
                        this.mob.ate();
                        this.nextUseTime = this.level.getGameTime() + 400L;
                    } else if (currentState.is(ModBlocks.DIMSTONE.get())) {
                        this.level.levelEvent(2001, targetPos, Block.getId(currentState));
                        this.level.setBlock(targetPos, ModBlocks.DULLSTONE.get().defaultBlockState(), 3);
                        this.mob.ate();
                        this.nextUseTime = this.level.getGameTime() + 400L;
                    }
                }
                this.mob.setEating(false);
                this.stop();
            }
            return;
        }

        if (distSqr < 2.25D) {
            this.mob.getNavigation().stop();
            this.faceTargetInstantly();

            Vec3 moveVec = new Vec3(destX - this.mob.getX(), destY - this.mob.getY(), destZ - this.mob.getZ());
            Vec3 normVec = moveVec.normalize().scale(0.1D);
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(normVec));

            if (distSqr < 0.1D) {
                this.mob.setEating(true);
                this.eatAnimationTick = this.adjustedTickDelay(80);
                this.mob.setDeltaMovement(Vec3.ZERO);
                this.mob.setPos(destX, destY, destZ);
            }
        }
        else {
            this.mob.setEating(false);
            if (this.timeoutCounter % 10 == 0 || this.mob.getNavigation().isDone()) {
                this.moveToTarget();
            }
        }
    }

    private void faceTargetInstantly() {
        double d0 = this.targetPos.getX() + 0.5D - this.mob.getX();
        double d2 = this.targetPos.getZ() + 0.5D - this.mob.getZ();
        float targetYaw = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;

        this.mob.setYRot(targetYaw);
        this.mob.setYHeadRot(targetYaw);
        this.mob.setYBodyRot(targetYaw);
    }

    private void moveToTarget() {
        double destX = targetPos.getX() + 0.5D + targetFace.getStepX();
        double destY = targetPos.getY() + 0.5D + targetFace.getStepY();
        double destZ = targetPos.getZ() + 0.5D + targetFace.getStepZ();
        this.mob.getNavigation().moveTo(destX, destY, destZ, 1.0D);
    }

    private boolean isValidTarget(BlockState state) {
        return state.is(Blocks.GLOWSTONE) || state.is(ModBlocks.DIMSTONE.get());
    }

    private BlockPos findGlowstoneOrDimstone() {
        BlockPos mobPos = this.mob.blockPosition();
        BlockPos bestPos = null;
        double bestDist = Double.MAX_VALUE;

        for (BlockPos pos : BlockPos.betweenClosed(mobPos.offset(-10, -5, -10), mobPos.offset(10, 5, 10))) {
            if (isValidTarget(this.level.getBlockState(pos))) {
                double dist = this.mob.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

                if (dist < bestDist) {
                    for (Direction dir : Direction.Plane.HORIZONTAL) {
                        BlockPos sidePos = pos.relative(dir);

                        if (this.level.isEmptyBlock(sidePos)) {
                            Path path = this.mob.getNavigation().createPath(sidePos, 1);
                            if (path != null && path.canReach()) {
                                bestPos = pos.immutable();
                                bestDist = dist;
                                this.targetFace = dir;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return bestPos;
    }
}