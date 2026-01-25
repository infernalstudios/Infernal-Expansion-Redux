package com.infernalstudios.infernalexp.entities.ai;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.compat.GardensOfTheDeadCompat;
import com.infernalstudios.infernalexp.compat.NetherExpCompat;
import com.infernalstudios.infernalexp.entities.GlowsquitoEntity;
import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModSounds;
import com.infernalstudios.infernalexp.module.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class SuckGlowstoneGoal extends Goal {
    private final GlowsquitoEntity mob;
    private final Level level;
    private int slurpSoundCooldown;
    private int eatAnimationTick;
    private int timeoutCounter;
    private BlockPos targetPos = BlockPos.ZERO;
    private Direction targetFace = Direction.NORTH;

    private Vec3 latchPos = Vec3.ZERO;

    private long nextUseTime;

    public SuckGlowstoneGoal(GlowsquitoEntity mob) {
        this.mob = mob;
        this.level = mob.level();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!IECommon.getConfig().common.mobInteractions.glowsquitoBlockSucking) return false;
        if (!this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) return false;
        if (this.level.getGameTime() < this.nextUseTime) return false;
        if (this.mob.isAggressive() || this.mob.getTarget() != null) return false;
        if (this.mob.getRandom().nextInt(50) != 0) return false;

        return this.findGlowstoneOrDimstone();
    }

    @Override
    public boolean canContinueToUse() {
        if (this.timeoutCounter > 400 && !this.mob.isEating()) return false;
        if (this.targetPos == null || !isValidTarget(this.level.getBlockState(this.targetPos))) return false;

        BlockPos latchBlockPos = this.targetPos.relative(this.targetFace);
        if (!this.level.isEmptyBlock(latchBlockPos)) return false;

        return !this.mob.isAggressive();
    }

    @Override
    public void start() {
        this.eatAnimationTick = 0;
        this.timeoutCounter = 0;

        if (this.targetPos == null || !isValidTarget(this.level.getBlockState(this.targetPos))) {
            this.stop();
            return;
        }

        this.latchPos = new Vec3(
                this.targetPos.getX() + 0.5D + this.targetFace.getStepX(),
                this.targetPos.getY() + 0.5D + this.targetFace.getStepY(),
                this.targetPos.getZ() + 0.5D + this.targetFace.getStepZ());

        this.moveToTarget();
    }

    @Override
    public void stop() {
        this.eatAnimationTick = 0;
        this.timeoutCounter = 0;
        this.targetPos = BlockPos.ZERO;
        this.mob.setEating(false);
        this.mob.getNavigation().stop();
        this.slurpSoundCooldown = 0;
    }

    @Override
    public void tick() {
        this.timeoutCounter++;

        double distSqr = this.mob.distanceToSqr(this.latchPos);

        if (distSqr < 1.0D && !this.mob.isEating()) {
            this.mob.setEating(true);
            this.mob.getNavigation().stop();
            this.slurpSoundCooldown = 0;
        }

        if (this.mob.isEating()) {
            if (this.slurpSoundCooldown-- <= 0) {
                float pitch = 0.9F + this.mob.getRandom().nextFloat() * 0.3F;
                this.mob.playSound(ModSounds.GLOWSQUITO_SLURP.get(), 0.8F, pitch);
                this.slurpSoundCooldown = 10 + this.mob.getRandom().nextInt(5);
            }

            if (distSqr > 0.05D) {
                Vec3 moveVec = this.latchPos.subtract(this.mob.position());
                if (moveVec.lengthSqr() > 0.0001) {
                    moveVec = moveVec.normalize().scale(0.2D);
                }
                this.mob.setDeltaMovement(moveVec);

                this.mob.getLookControl().setLookAt(this.latchPos.x, this.latchPos.y, this.latchPos.z, 30.0F, 30.0F);
                return;
            }

            this.mob.setPos(this.latchPos.x, this.latchPos.y, this.latchPos.z);
            this.mob.setDeltaMovement(Vec3.ZERO);

            float targetYaw = this.targetFace.getOpposite().toYRot();
            this.mob.setYRot(targetYaw);
            this.mob.setYHeadRot(targetYaw);
            this.mob.setYBodyRot(targetYaw);
            this.mob.setXRot(0.0F);

            if (this.eatAnimationTick == 0) {
                this.eatAnimationTick = this.adjustedTickDelay(80);
            }

            this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);

            if (this.eatAnimationTick == 1) {
                if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                    BlockState currentState = this.level.getBlockState(targetPos);
                    BlockState newState = null;

                    if (currentState.is(Blocks.GLOWSTONE)) {
                        newState = ModBlocks.DIMSTONE.get().defaultBlockState();
                        this.mob.playSound(SoundEvents.GLASS_BREAK, 1.0F, 1.0F);
                    } else if (currentState.is(ModBlocks.DIMSTONE.get())) {
                        newState = ModBlocks.DULLSTONE.get().defaultBlockState();
                        this.mob.playSound(ModSounds.BLOCK_DULLSTONE_BREAK.get(), 1.0F, 1.0F);
                    } else if (currentState.is(Blocks.SHROOMLIGHT)) {
                        newState = ModBlocks.HOLLOWLIGHT.get().defaultBlockState();
                        this.mob.resetPowers();
                        this.mob.setShroomlightPowered(true);
                        this.mob.setShroomlightTimer(6000);
                        this.mob.playSound(SoundEvents.SHROOMLIGHT_BREAK, 1.0F, 1.0F);
                    } else if (NetherExpCompat.isShroomnight(currentState.getBlock()) && NetherExpCompat.HOLLOWNIGHT != null) {
                        newState = NetherExpCompat.HOLLOWNIGHT.get().defaultBlockState();
                        this.mob.resetPowers();
                        this.mob.setShroomnightPowered(true);
                        this.mob.setShroomnightTimer(6000);
                        this.mob.playSound(SoundEvents.SHROOMLIGHT_BREAK, 1.0F, 1.0F);
                    } else if (GardensOfTheDeadCompat.isShroomblight(currentState.getBlock()) && GardensOfTheDeadCompat.HOLLOWBLIGHT != null) {
                        newState = GardensOfTheDeadCompat.HOLLOWBLIGHT.get().defaultBlockState();
                        this.mob.resetPowers();
                        this.mob.setShroomlightPowered(true); // TODO: shroomblight powered
                        this.mob.setShroomlightTimer(6000);
                        this.mob.playSound(SoundEvents.SHROOMLIGHT_BREAK, 1.0F, 1.0F);
                    } else if (GardensOfTheDeadCompat.isShroombright(currentState.getBlock()) && GardensOfTheDeadCompat.HOLLOWBRIGHT != null) {
                        newState = GardensOfTheDeadCompat.HOLLOWBRIGHT.get().defaultBlockState();
                        this.mob.resetPowers();
                        this.mob.setShroomlightPowered(true); // TODO: shroombright powered
                        this.mob.setShroomlightTimer(6000);
                        this.mob.playSound(SoundEvents.SHROOMLIGHT_BREAK, 1.0F, 1.0F);
                    }

                    if (newState != null) {
                        this.level.levelEvent(2001, targetPos, Block.getId(currentState));
                        this.level.setBlock(targetPos, newState, 3);
                        this.mob.ate();
                        this.nextUseTime = this.level.getGameTime() + 600L;
                    }
                }
                this.mob.setEating(false);
                this.stop();
            }
            return;
        }

        this.mob.getLookControl().setLookAt(this.latchPos.x, this.latchPos.y, this.latchPos.z, 30.0F, 30.0F);

        if (this.timeoutCounter % 10 == 0) {
            this.moveToTarget();
        }
    }

    private void moveToTarget() {
        this.mob.getNavigation().moveTo(this.latchPos.x, this.latchPos.y, this.latchPos.z, 1.0D);
    }

    private boolean isValidTarget(BlockState state) {
        if (state.is(ModTags.Blocks.GLOWSQUITO_SUCKABLES) ||
                state.is(Blocks.GLOWSTONE) ||
                state.is(ModBlocks.DIMSTONE.get()) ||
                state.is(Blocks.SHROOMLIGHT)) {
            return true;
        }

        if (NetherExpCompat.isShroomnight(state.getBlock())) {
            return true;
        }

        if (GardensOfTheDeadCompat.isShroomblight(state.getBlock())) {
            return true;
        }

        return false;
    }

    private boolean findGlowstoneOrDimstone() {
        BlockPos mobPos = this.mob.blockPosition();

        List<Candidate> candidates = new ArrayList<>();
        int maxCandidates = 3;

        for (BlockPos pos : BlockPos.betweenClosed(mobPos.offset(-6, -5, -6), mobPos.offset(6, 5, 6))) {
            if (isValidTarget(this.level.getBlockState(pos))) {

                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    BlockPos sidePos = pos.relative(dir);

                    if (this.level.isEmptyBlock(sidePos)) {

                        AABB checkBox = new AABB(sidePos).inflate(0.5);
                        List<GlowsquitoEntity> others = this.level.getEntitiesOfClass(GlowsquitoEntity.class, checkBox);
                        if (!others.isEmpty()) {
                            continue;
                        }

                        Vec3 eyePos = this.mob.getEyePosition(1.0f);
                        Vec3 targetFaceCenter = new Vec3(
                                pos.getX() + 0.5 + dir.getStepX() * 0.49,
                                pos.getY() + 0.5 + dir.getStepY() * 0.49,
                                pos.getZ() + 0.5 + dir.getStepZ() * 0.49
                        );

                        BlockHitResult hit = this.level.clip(new ClipContext(
                                eyePos,
                                targetFaceCenter,
                                ClipContext.Block.COLLIDER,
                                ClipContext.Fluid.NONE,
                                this.mob
                        ));

                        if (hit.getType() == HitResult.Type.BLOCK &&
                                hit.getBlockPos().equals(pos) &&
                                hit.getDirection() == dir) {

                            double dist = this.mob.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                            candidates.add(new Candidate(pos.immutable(), dir, dist));

                            if (candidates.size() >= maxCandidates) {
                                break;
                            }
                        }
                    }
                }
            }
            if (candidates.size() == maxCandidates) break;
        }

        if (!candidates.isEmpty()) {
            Candidate chosen = candidates.get(this.mob.getRandom().nextInt(candidates.size()));
            this.targetPos = chosen.pos;
            this.targetFace = chosen.face;
            return true;
        }

        return false;
    }

    private record Candidate(BlockPos pos, Direction face, double dist) {
    }
}