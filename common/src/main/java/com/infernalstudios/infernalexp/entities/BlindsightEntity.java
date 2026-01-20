package com.infernalstudios.infernalexp.entities;

import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModEffects;
import com.infernalstudios.infernalexp.module.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.UUID;

public class BlindsightEntity extends Monster implements GeoEntity {

    private static final UUID SPEED_MODIFIER_ATTACK_UUID = UUID.fromString("4F23F2F2-822D-4E38-9226-538222954848");
    private static final UUID ATTACK_DAMAGE_MODIFIER_UUID = UUID.fromString("23423423-822D-4E38-9226-538222954848");

    private static final EntityDataAccessor<Boolean> IS_JUMPING = SynchedEntityData.defineId(BlindsightEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(BlindsightEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation IDLE_RARE = RawAnimation.begin().thenLoop("idle_rare");
    private static final RawAnimation JUMP_LOOP = RawAnimation.begin().thenLoop("jump_loop");
    private static final RawAnimation BITE = RawAnimation.begin().thenPlay("bite");
    private static final RawAnimation SWALLOW = RawAnimation.begin().thenPlay("swallow");
    private static final RawAnimation TONGUE_ATTACK = RawAnimation.begin().thenPlay("tongue_attack");
    private static final RawAnimation ALERT = RawAnimation.begin().thenPlay("luminous_player_alert");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public float targetSquish;
    public float squish;
    public float oSquish;
    private boolean wasOnGround;

    private int jumpAttackCounter = 0;
    private int nextTongueAttackThreshold;

    private boolean hasPlayedWarning = false;
    private int alertTimer = 0;
    private int attackTimer = 0;

    public BlindsightEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new BlindsightMoveControl(this);
        this.nextTongueAttackThreshold = this.random.nextInt(3) + 3;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.FOLLOW_RANGE, 6.0D);
    }

    public static boolean checkBlindsightSpawnRules(EntityType<BlindsightEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return Monster.checkMonsterSpawnRules(entityType, level, spawnType, pos, random);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_JUMPING, false);
        this.entityData.define(IS_ATTACKING, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new BlindsightFloatGoal(this));
        this.goalSelector.addGoal(1, new BlindsightAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(2, new ExtinguishFireGoal(this, 1.2D));
        this.goalSelector.addGoal(3, new BlindsightHopGoal(this));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new BlindsightRandomDirectionGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> entity.hasEffect(ModEffects.LUMINOUS.get()) && !(entity instanceof BlindsightEntity)));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, GlowsquitoEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, LivingEntity::isBaby));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        this.squish += (this.targetSquish - this.squish) * 0.5F;
        this.oSquish = this.squish;
        super.tick();

        if (this.onGround() && !this.wasOnGround) {
            this.targetSquish = -0.5F;
        } else if (!this.onGround() && this.wasOnGround) {
            this.targetSquish = 1.0F;
        }

        this.wasOnGround = this.onGround();
        this.alterSquishAmount();

        if (this.attackTimer > 0) this.attackTimer--;
        if (this.alertTimer > 0) this.alertTimer--;

        if (!this.level().isClientSide && this.alertTimer <= 0 && this.attackTimer <= 0 && this.onGround() && this.getTarget() == null) {
            if (this.random.nextInt(400) == 0) {
                this.triggerAnim("attackController", "idle_rare");
            }
        }

        if (!this.level().isClientSide) {
            this.entityData.set(IS_ATTACKING, this.attackTimer > 0 || this.alertTimer > 0);
            this.entityData.set(IS_JUMPING, !this.onGround());

            LivingEntity target = this.getTarget();
            boolean isLuminousTarget = target != null && target.hasEffect(ModEffects.LUMINOUS.get());

            AttributeInstance speedAttr = this.getAttribute(Attributes.MOVEMENT_SPEED);
            AttributeInstance followRangeAttr = this.getAttribute(Attributes.FOLLOW_RANGE);
            AttributeInstance attackAttr = this.getAttribute(Attributes.ATTACK_DAMAGE);

            if (isLuminousTarget) {
                if (speedAttr != null && speedAttr.getModifier(SPEED_MODIFIER_ATTACK_UUID) == null) {
                    speedAttr.addTransientModifier(new AttributeModifier(SPEED_MODIFIER_ATTACK_UUID, "Luminous speed boost", 0.25D, AttributeModifier.Operation.ADDITION));
                }
                if (followRangeAttr != null) followRangeAttr.setBaseValue(48.0D);

                if (attackAttr != null && attackAttr.getModifier(ATTACK_DAMAGE_MODIFIER_UUID) == null) {
                    attackAttr.addTransientModifier(new AttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID, "Luminous damage boost", 4.0D, AttributeModifier.Operation.ADDITION));
                }

                if (!this.hasPlayedWarning) {
                    this.triggerAnim("attackController", "alert");
                    this.alertTimer = 30;
                    this.hasPlayedWarning = true;
                }
            } else {
                if (speedAttr != null) speedAttr.removeModifier(SPEED_MODIFIER_ATTACK_UUID);

                if (followRangeAttr != null) {
                    followRangeAttr.setBaseValue(target != null ? 12.0D : 6.0D);
                }

                if (attackAttr != null) attackAttr.removeModifier(ATTACK_DAMAGE_MODIFIER_UUID);
                this.hasPlayedWarning = false;
            }
        }
    }

    protected void alterSquishAmount() {
        this.targetSquish *= 0.6F;
    }

    protected int getJumpDelay() {
        return this.random.nextInt(20) + 10;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, @NotNull DamageSource source) {
        return false;
    }

    @Override
    protected void jumpFromGround() {
        Vec3 vec3 = this.getDeltaMovement();
        float jumpPower = 0.42F + (random.nextFloat() * 0.1F);

        if (this.getTarget() instanceof GlowsquitoEntity) {
            jumpPower = 0.85F;
        }

        this.setDeltaMovement(vec3.x, jumpPower, vec3.z);
        this.hasImpulse = true;
    }

    public void performJump() {
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, this.getJumpBoostPower(), vec3.z);
        this.hasImpulse = true;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.BLINDSIGHT_AMBIENT.get();
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return ModSounds.BLINDSIGHT_HURT.get();
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return ModSounds.BLINDSIGHT_DEATH.get();
    }

    protected SoundEvent getJumpSound() {
        return ModSounds.BLINDSIGHT_LEAP.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            if (event.getAnimatable().entityData.get(IS_ATTACKING)) {
                return PlayState.STOP;
            }
            if (event.getAnimatable().entityData.get(IS_JUMPING)) {
                return event.setAndContinue(JUMP_LOOP);
            }
            return event.setAndContinue(IDLE);
        }));

        controllers.add(new AnimationController<>(this, "attackController", 0, event -> PlayState.STOP)
                .triggerableAnim("bite", BITE)
                .triggerableAnim("swallow", SWALLOW)
                .triggerableAnim("tongue_attack", TONGUE_ATTACK)
                .triggerableAnim("alert", ALERT)
                .triggerableAnim("idle_rare", IDLE_RARE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entity) {
        boolean flag = super.doHurtTarget(entity);
        if (flag && entity instanceof LivingEntity livingTarget) {
            float knockbackStrength = 1.5F;
            livingTarget.knockback(knockbackStrength, Mth.sin(this.getYRot() * ((float) Math.PI / 180F)), -Mth.cos(this.getYRot() * ((float) Math.PI / 180F)));
            livingTarget.addEffect(new MobEffectInstance(ModEffects.LUMINOUS.get(), 600));
        }
        return flag;
    }

    static class BlindsightMoveControl extends MoveControl {
        private final BlindsightEntity blindsight;
        private float yRot;
        private int jumpDelay;
        private boolean isAggressive;

        public BlindsightMoveControl(BlindsightEntity blindsight) {
            super(blindsight);
            this.blindsight = blindsight;
            this.yRot = 180.0F * blindsight.getYRot() / (float) Math.PI;
        }

        public void setDirection(float yRot, boolean aggressive) {
            this.yRot = yRot;
            this.isAggressive = aggressive;
        }

        public void setSpeed(double speedIn) {
            this.speedModifier = speedIn;
            this.operation = Operation.MOVE_TO;
        }

        public void tick() {
            if (this.blindsight.alertTimer > 0) {
                this.mob.setSpeed(0.0F);
                return;
            }

            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();

            if (this.operation != Operation.MOVE_TO) {
                this.mob.setZza(0.0F);
            } else {
                this.operation = Operation.WAIT;
                if (this.mob.onGround()) {
                    this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                    if (this.jumpDelay-- <= 0) {
                        this.jumpDelay = this.blindsight.getJumpDelay();
                        if (this.isAggressive) {
                            this.jumpDelay /= 3;
                        }

                        this.blindsight.getJumpControl().jump();
                        this.blindsight.playSound(this.blindsight.getJumpSound(), this.blindsight.getSoundVolume(), 1.0F);
                        this.blindsight.performJump();
                    } else {
                        this.blindsight.xxa = 0.0F;
                        this.blindsight.zza = 0.0F;
                        this.mob.setSpeed(0.0F);
                    }
                } else {
                    this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }
            }
        }
    }

    static class BlindsightRandomDirectionGoal extends Goal {
        private final BlindsightEntity blindsight;
        private float chosenDegrees;
        private int nextRandomizeTime;

        public BlindsightRandomDirectionGoal(BlindsightEntity blindsight) {
            this.blindsight = blindsight;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return this.blindsight.getTarget() == null && (this.blindsight.onGround() || this.blindsight.isInWater() || this.blindsight.isInLava() || this.blindsight.hasEffect(MobEffects.LEVITATION));
        }

        public void tick() {
            if (--this.nextRandomizeTime <= 0) {
                this.nextRandomizeTime = 40 + this.blindsight.getRandom().nextInt(60);
                this.chosenDegrees = (float) this.blindsight.getRandom().nextInt(360);
            }
            ((BlindsightMoveControl) this.blindsight.getMoveControl()).setDirection(this.chosenDegrees, false);
        }
    }

    static class BlindsightHopGoal extends Goal {
        private final BlindsightEntity blindsight;

        public BlindsightHopGoal(BlindsightEntity blindsight) {
            this.blindsight = blindsight;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return !this.blindsight.isPassenger();
        }

        public void tick() {
            ((BlindsightMoveControl) this.blindsight.getMoveControl()).setSpeed(1.0D);
        }
    }

    static class BlindsightFloatGoal extends Goal {
        private final BlindsightEntity blindsight;

        public BlindsightFloatGoal(BlindsightEntity blindsight) {
            this.blindsight = blindsight;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
            blindsight.getNavigation().setCanFloat(true);
        }

        public boolean canUse() {
            return (this.blindsight.isInWater() || this.blindsight.isInLava());
        }

        public void tick() {
            if (this.blindsight.getRandom().nextFloat() < 0.8F) {
                this.blindsight.getJumpControl().jump();
            }
            ((BlindsightMoveControl) this.blindsight.getMoveControl()).setSpeed(1.2D);
        }
    }

    static class BlindsightAttackGoal extends MeleeAttackGoal {
        private final BlindsightEntity blindsight;

        public BlindsightAttackGoal(BlindsightEntity entity, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(entity, speedModifier, followingTargetEvenIfNotSeen);
            this.blindsight = entity;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && this.blindsight.getTarget() != null && this.blindsight.alertTimer <= 0;
        }

        @Override
        public void tick() {
            super.tick();
            if (this.blindsight.getTarget() != null) {
                this.blindsight.lookAt(this.blindsight.getTarget(), 10.0F, 10.0F);
                ((BlindsightMoveControl) this.blindsight.getMoveControl()).setDirection(this.blindsight.getYRot(), true);
            }
        }

        @Override
        protected void checkAndPerformAttack(@NotNull LivingEntity target, double distToEnemySqr) {
            double attackReachSqr = this.getAttackReachSqr(target);

            if (target instanceof GlowsquitoEntity || target.isBaby()) {
                double eatingReach = attackReachSqr * 2.0D;
                if (distToEnemySqr <= eatingReach && this.getTicksUntilNextAttack() <= 0) {
                    this.resetAttackCooldown();

                    this.blindsight.lookAt(target, 360.0F, 360.0F);

                    this.blindsight.triggerAnim("attackController", "bite");
                    this.blindsight.attackTimer = 10;
                    this.blindsight.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);

                    target.remove(Entity.RemovalReason.KILLED);
                    this.blindsight.heal(4.0F);

                    this.blindsight.triggerAnim("attackController", "swallow");
                    return;
                }
            }

            if (this.blindsight.jumpAttackCounter >= this.blindsight.nextTongueAttackThreshold) {
                double tongueReachSqr = attackReachSqr * 4.0D;
                if (distToEnemySqr <= tongueReachSqr && this.getTicksUntilNextAttack() <= 0) {
                    this.resetAttackCooldown();
                    this.blindsight.lookAt(target, 360.0F, 360.0F);
                    this.blindsight.triggerAnim("attackController", "tongue_attack");
                    this.blindsight.attackTimer = 26;
                    this.blindsight.doHurtTarget(target);

                    this.blindsight.jumpAttackCounter = 0;
                    this.blindsight.nextTongueAttackThreshold = this.blindsight.getRandom().nextInt(3) + 3;
                    return;
                }
            }

            if (distToEnemySqr <= attackReachSqr && this.getTicksUntilNextAttack() <= 0) {
                this.resetAttackCooldown();
                this.blindsight.lookAt(target, 360.0F, 360.0F);
                this.blindsight.triggerAnim("attackController", "bite");
                this.blindsight.attackTimer = 10;
                this.blindsight.doHurtTarget(target);
                this.blindsight.jumpAttackCounter++;
            }
        }
    }

    static class ExtinguishFireGoal extends Goal {
        private final BlindsightEntity blindsight;
        private final double speedModifier;
        private BlockPos targetPos;

        public ExtinguishFireGoal(BlindsightEntity entity, double speed) {
            this.blindsight = entity;
            this.speedModifier = speed;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
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
            return this.targetPos != null && this.blindsight.level().getBlockState(this.targetPos).is(BlockTags.FIRE)
                    && this.blindsight.distanceToSqr(Vec3.atCenterOf(this.targetPos)) < 256.0D;
        }

        @Override
        public void start() {
            this.blindsight.getNavigation().moveTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), this.speedModifier);
        }

        @Override
        public void tick() {
            ((BlindsightMoveControl) this.blindsight.getMoveControl()).setSpeed(this.speedModifier);

            if (this.targetPos != null && this.blindsight.blockPosition().distSqr(this.targetPos) <= 4.0D) {
                this.blindsight.level().removeBlock(this.targetPos, false);
                this.blindsight.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0F, 1.0F);
                this.blindsight.triggerAnim("attackController", "bite");
                this.blindsight.attackTimer = 10;
                this.blindsight.heal(2.0F);
                this.targetPos = null;
            }
        }
    }
}