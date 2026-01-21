package com.infernalstudios.infernalexp.entities.ai.blindsight;

import com.infernalstudios.infernalexp.entities.BlindsightEntity;
import com.infernalstudios.infernalexp.entities.GlowsquitoEntity;
import com.infernalstudios.infernalexp.module.ModParticleTypes;
import com.infernalstudios.infernalexp.module.ModSounds;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class BlindsightAttackGoal extends MeleeAttackGoal {
    private final BlindsightEntity blindsight;
    private int attackCooldown = 0;

    public BlindsightAttackGoal(BlindsightEntity entity, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(entity, speedModifier, followingTargetEvenIfNotSeen);
        this.blindsight = entity;
    }

    @Override
    public void tick() {
        LivingEntity target = this.blindsight.getTarget();
        if (target == null) {
            this.blindsight.wantsToTongueAttack = false;
            return;
        }

        if (this.blindsight.wantsToTongueAttack) {
            if (this.blindsight.attackAnimationTimer <= 0 && this.attackCooldown > 0) {
                this.blindsight.wantsToTongueAttack = false;
                this.blindsight.tongueTarget = null;
                this.blindsight.jumpCount = 0;
            } else {
                executeTongueAttackLogic();
            }
            return;
        }

        if (this.attackCooldown > 0) this.attackCooldown--;

        this.blindsight.getLookControl().setLookAt(target, 30.0F, 30.0F);

        double dx = target.getX() - this.blindsight.getX();
        double dz = target.getZ() - this.blindsight.getZ();
        float targetYRot = (float) (Mth.atan2(dz, dx) * (double) (180F / (float) Math.PI)) - 90.0F;
        ((BlindsightMoveControl) this.blindsight.getMoveControl()).setDirection(targetYRot, true);

        if (this.blindsight.attackAnimationTimer <= 0 && !this.blindsight.wantsToTongueAttack) {
            ((BlindsightMoveControl) this.blindsight.getMoveControl()).setSpeed(1.0D);
        } else {
            ((BlindsightMoveControl) this.blindsight.getMoveControl()).setSpeed(0.0D);
        }

        if (target instanceof GlowsquitoEntity || target.isBaby()) {
            handleEating(target);
            return;
        }

        if (this.attackCooldown <= 0 && this.blindsight.onGround() && this.blindsight.alertTimer == 0) {
            int threshold = 3 + this.blindsight.getRandom().nextInt(3);
            if (this.blindsight.jumpCount >= threshold && this.blindsight.distanceToSqr(target) < 64.0D) {
                this.startTongueAttack(target);
                return;
            }
        }

        if (this.blindsight.distanceToSqr(target) < this.getAttackReachSqr(target) && this.attackCooldown <= 0 && this.blindsight.onGround()) {
            this.blindsight.swing(InteractionHand.MAIN_HAND);
            this.blindsight.triggerAnim("attackController", "bite");
            this.blindsight.doHurtTarget(target);
            this.blindsight.attackAnimationTimer = 10;
            this.attackCooldown = 20;
            this.blindsight.setDeltaMovement(0, this.blindsight.getDeltaMovement().y, 0);
        }
    }

    private void startTongueAttack(LivingEntity target) {
        this.blindsight.wantsToTongueAttack = true;
        this.blindsight.jumpCount = 0;
        this.blindsight.triggerAnim("attackController", "tongue_attack");
        this.blindsight.playSound(ModSounds.BLINDSIGHT_LICK.get(), 1.0F, 1.0F);

        this.blindsight.attackAnimationTimer = 27;
        this.blindsight.tongueTarget = target;
        this.attackCooldown = 40;

        this.blindsight.setDeltaMovement(Vec3.ZERO);
    }

    private void executeTongueAttackLogic() {
        if (this.blindsight.tongueTarget == null) return;

        this.blindsight.getLookControl().setLookAt(this.blindsight.tongueTarget, 30.0F, 30.0F);

        double d0 = this.blindsight.tongueTarget.getX() - this.blindsight.getX();
        double d1 = this.blindsight.tongueTarget.getZ() - this.blindsight.getZ();
        float f = (float)(Mth.atan2(d1, d0) * (double)(180F / (float)Math.PI)) - 90.0F;

        ((BlindsightMoveControl) this.blindsight.getMoveControl()).setDirection(f, true);

        if (this.blindsight.attackAnimationTimer <= 17) {
            this.blindsight.getNavigation().moveTo(this.blindsight.tongueTarget, 1.0D);
        }

        if (this.blindsight.attackAnimationTimer == 10 && this.blindsight.tongueTarget.isAlive()) {
            double maxReachSqr = 32.0D;
            double distSqr = this.blindsight.distanceToSqr(this.blindsight.tongueTarget);

            if (distSqr < maxReachSqr && this.blindsight.hasLineOfSight(this.blindsight.tongueTarget)) {
                Vec3 lookDir = this.blindsight.getViewVector(1.0F).normalize();
                Vec3 targetDir = this.blindsight.tongueTarget.position().subtract(this.blindsight.position()).normalize();
                double dot = lookDir.dot(targetDir);

                if (dot > 0.85D) {
                    this.blindsight.doTongueDamage(this.blindsight.tongueTarget);
                    Vec3 pull = this.blindsight.position().subtract(this.blindsight.tongueTarget.position()).normalize().scale(0.5);
                    this.blindsight.tongueTarget.push(pull.x, 0.2, pull.z);
                }
            }
        }
    }

    private void handleEating(LivingEntity target) {
        double distSqr = this.blindsight.distanceToSqr(target);
        double reach = this.blindsight.getBbWidth() + target.getBbWidth() + 1.0D;
        double reachSqr = reach * reach;

        boolean canEat = distSqr <= reachSqr && (this.blindsight.onGround() || target instanceof GlowsquitoEntity);

        if (canEat && this.attackCooldown <= 0) {
            this.attackCooldown = 20;
            this.blindsight.triggerAnim("attackController", "bite");
            this.blindsight.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);

            if (this.blindsight.level() instanceof ServerLevel serverLevel) {
                if (target instanceof GlowsquitoEntity) {
                    for (int i = 0; i < 2; i++) {
                        double speedX = (this.blindsight.getRandom().nextDouble() - 0.5D) * 0.2D;
                        double speedY = 0.1D + (this.blindsight.getRandom().nextDouble() * 0.1D);
                        double speedZ = (this.blindsight.getRandom().nextDouble() - 0.5D) * 0.2D;
                        serverLevel.sendParticles(ModParticleTypes.GLOWSQUITO_WING,
                                target.getX(), target.getY() + 0.5D, target.getZ(),
                                0, speedX, speedY, speedZ, 1.0D);
                    }
                } else {
                    for (int i = 0; i < 8; i++) {
                        serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.COOKED_BEEF)),
                                target.getX(), target.getY() + 0.5D, target.getZ(),
                                1, 0.0D, 0.1D, 0.0D, 0.1D);
                    }
                }
            }

            target.kill();
            this.blindsight.heal(4.0F);
            this.blindsight.triggerAnim("attackController", "swallow");
            this.blindsight.attackAnimationTimer = 20;
        }
    }
}