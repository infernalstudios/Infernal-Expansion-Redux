package com.infernalstudios.infernalexp.entities.ai;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.entities.BlindsightEntity;
import com.infernalstudios.infernalexp.entities.ai.blindsight.BlindsightMoveControl;
import com.infernalstudios.infernalexp.module.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class ExtinguishFireGoal extends Goal {
    private final BlindsightEntity blindsight;
    private BlockPos targetPos;

    public ExtinguishFireGoal(BlindsightEntity entity) {
        this.blindsight = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!IECommon.getConfig().common.mobInteractions.blindsightExtinguishFire) return false;
        if (!this.blindsight.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) return false;
        if (this.blindsight.getTarget() != null) return false;
        if (this.blindsight.alertTimer > 0) return false;
        if (this.blindsight.getRandom().nextInt(20) != 0) return false;

        BlockPos entityPos = this.blindsight.blockPosition();
        int radius = 10;

        for (BlockPos pos : BlockPos.betweenClosed(entityPos.offset(-radius, -2, -radius), entityPos.offset(radius, 4, radius))) {
            BlockState state = this.blindsight.level().getBlockState(pos);
            if (state.is(BlockTags.FIRE) || state.is(ModBlocks.GLOWLIGHT_FIRE.get())) {
                this.targetPos = pos.immutable();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetPos != null &&
                (this.blindsight.level().getBlockState(this.targetPos).is(BlockTags.FIRE) ||
                        this.blindsight.level().getBlockState(this.targetPos).is(ModBlocks.GLOWLIGHT_FIRE.get()));
    }

    @Override
    public void start() {
        this.blindsight.getNavigation().moveTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), 1.0D);
    }

    @Override
    public void stop() {
        this.blindsight.getNavigation().stop();
        this.targetPos = null;
    }

    @Override
    public void tick() {
        if (this.targetPos == null) return;

        double d0 = this.targetPos.getX() + 0.5D - this.blindsight.getX();
        double d1 = this.targetPos.getZ() + 0.5D - this.blindsight.getZ();
        float f = (float) (net.minecraft.util.Mth.atan2(d1, d0) * (double) (180F / (float) Math.PI)) - 90.0F;

        if (this.blindsight.getMoveControl() instanceof BlindsightMoveControl moveControl) {
            moveControl.setDirection(f, false);
        }

        if (this.blindsight.getNavigation().isDone()) {
            this.blindsight.getNavigation().moveTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), 1.0D);
        }

        AABB fireBox = new AABB(this.targetPos).inflate(1.0);
        if (this.blindsight.getBoundingBox().intersects(fireBox) && this.blindsight.onGround()) {
            this.blindsight.level().removeBlock(this.targetPos, false);
            this.blindsight.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0F, 1.0F);

            if (this.blindsight.level() instanceof ServerLevel serverLevel) {
                Vec3 targetCenter = Vec3.atCenterOf(this.targetPos);
                serverLevel.sendParticles(ParticleTypes.SMOKE, targetCenter.x, targetCenter.y, targetCenter.z, 10, 0.2, 0.2, 0.2, 0.05);
            }

            this.blindsight.triggerAnim("attackController", "land");
            this.blindsight.attackAnimationTimer = 10;

            this.targetPos = null;
        }
    }
}