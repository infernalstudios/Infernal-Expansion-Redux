package com.infernalstudios.infernalexp.entities;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.entities.ai.ExtinguishFireGoal;
import com.infernalstudios.infernalexp.entities.ai.blindsight.*;
import com.infernalstudios.infernalexp.module.ModEffects;
import com.infernalstudios.infernalexp.module.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
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

import java.util.UUID;

public class BlindsightEntity extends Monster implements GeoEntity {

    public static final UUID SPEED_MODIFIER_ATTACK_UUID = UUID.fromString("4F23F2F2-822D-4E38-9226-538222954848");
    public static final UUID ATTACK_DAMAGE_MODIFIER_UUID = UUID.fromString("23423423-822D-4E38-9226-538222954848");
    public static final UUID FOLLOW_RANGE_MODIFIER_UUID = UUID.fromString("71479901-5231-4835-8968-072619894352");

    private static final EntityDataAccessor<Boolean> IS_JUMPING = SynchedEntityData.defineId(BlindsightEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(BlindsightEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_RESTING = SynchedEntityData.defineId(BlindsightEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_WATCHING_LUMINOUS = SynchedEntityData.defineId(BlindsightEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation IDLE_RARE = RawAnimation.begin().thenLoop("idle_rare");
    private static final RawAnimation JUMP_LOOP = RawAnimation.begin().thenLoop("jump_loop");
    private static final RawAnimation BITE = RawAnimation.begin().thenPlay("bite");
    private static final RawAnimation SWALLOW = RawAnimation.begin().thenPlay("swallow");
    private static final RawAnimation TONGUE_ATTACK_TELEGRAPHED = RawAnimation.begin().thenPlay("tongue_attack_telegraphed");
    private static final RawAnimation TONGUE_ATTACK_IMMEDIATE = RawAnimation.begin().thenPlay("tongue_attack_immediate");
    private static final RawAnimation ALERT = RawAnimation.begin().thenPlay("luminous_player_alert");
    private static final RawAnimation LAND = RawAnimation.begin().thenPlay("land");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public float targetSquish;
    public float squish;
    public float oSquish;
    public int alertTimer = 0;
    public int attackAnimationTimer = 0;
    public int jumpCount = 0;
    public boolean wantsToTongueAttack = false;
    public boolean isBigJumping = false;
    public LivingEntity tongueTarget;
    public int consecutiveHops = 0;
    public int hopsUntilIdle;
    public int attackCooldown = 0;
    public int damageTriggerTick = 10;
    private boolean wasOnGround;
    private boolean hasPlayedWarning = false;
    private boolean playRareIdle = false;
    private boolean wasResting = false;
    private int jumpsUntilBigJump = 0;

    public BlindsightEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new BlindsightMoveControl(this);
        this.hopsUntilIdle = 6 + this.random.nextInt(3);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public static boolean checkBlindsightSpawnRules(EntityType<BlindsightEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return Monster.checkMonsterSpawnRules(entityType, level, spawnType, pos, random);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_JUMPING, false);
        this.entityData.define(IS_ATTACKING, false);
        this.entityData.define(IS_RESTING, false);
        this.entityData.define(IS_WATCHING_LUMINOUS, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new BlindsightFloatGoal(this));
        this.goalSelector.addGoal(1, new BlindsightAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(2, new ExtinguishFireGoal(this));
        this.goalSelector.addGoal(3, new BlindsightRestGoal(this));
        this.goalSelector.addGoal(4, new BlindsightRandomDirectionGoal(this));
        this.goalSelector.addGoal(5, new BlindsightKeepOnJumpingGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> entity.hasEffect(ModEffects.LUMINOUS.get()) && !(entity instanceof BlindsightEntity)));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, GlowsquitoEntity.class, true) {
            @Override
            protected @NotNull AABB getTargetSearchArea(double targetDistance) {
                return this.mob.getBoundingBox().inflate(16.0D, 8.0D, 16.0D);
            }
        });
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, GlowsilkMothEntity.class, true));
        if (IECommon.getConfig().common.mobInteractions.blindsightEatBabyMobs) {
            this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, LivingEntity::isBaby));
        }
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, BlindsightEntity.class, 10, true, false,
                (entity) -> entity.distanceToSqr(this) < 64.0D));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.handleSlimeSquish();
        }

        if (this.alertTimer > 0) this.alertTimer--;
        if (this.attackAnimationTimer > 0) this.attackAnimationTimer--;
        if (this.attackCooldown > 0) this.attackCooldown--;

        if (!this.level().isClientSide) {
            this.handleLuminousTargetLogic();

            this.entityData.set(IS_ATTACKING, this.attackAnimationTimer > 0 || this.alertTimer > 0);
            this.entityData.set(IS_JUMPING, !this.onGround());
        } else {
            boolean isResting = this.isResting();
            if (isResting && !this.wasResting) {
                this.playRareIdle = this.getRandom().nextFloat() < 0.5F;
            }
            this.wasResting = isResting;
        }
    }

    private void handleSlimeSquish() {
        this.squish += (this.targetSquish - this.squish) * 0.5F;
        this.oSquish = this.squish;

        if (this.onGround() && !this.wasOnGround) {
            this.targetSquish = -0.5F;
            if (this.isBigJumping && this.alertTimer == 0 && this.attackAnimationTimer == 0) {
                this.triggerAnim("attackController", "land");
            }
            this.isBigJumping = false;
        } else if (!this.onGround() && this.wasOnGround) {
            this.targetSquish = 1.0F;
        }

        this.wasOnGround = this.onGround();
        this.targetSquish *= 0.6F;
    }

    public void performTongueAttack(LivingEntity target, boolean immediate) {
        this.wantsToTongueAttack = true;
        this.jumpCount = 0;
        this.tongueTarget = target;
        this.playSound(ModSounds.BLINDSIGHT_ALERT.get(), 1.0F, 1.0F);

        if (immediate) {
            this.triggerAnim("attackController", "tongue_attack_immediate");
            this.attackAnimationTimer = 17;
            this.damageTriggerTick = 9;
            this.attackCooldown = 25;
        } else {
            this.triggerAnim("attackController", "tongue_attack_telegraphed");
            this.attackAnimationTimer = 27;
            this.damageTriggerTick = 9;
            this.attackCooldown = 35;
        }

        this.setDeltaMovement(Vec3.ZERO);
    }

    private void handleLuminousTargetLogic() {
        LivingEntity target = this.getTarget();
        boolean isLuminous = target != null && target.hasEffect(ModEffects.LUMINOUS.get());

        this.entityData.set(IS_WATCHING_LUMINOUS, isLuminous);

        boolean isAngry = this.getLastHurtByMob() != null;

        double desiredBaseRange = isAngry ? 12.0D : 6.0D;
        if (target instanceof GlowsquitoEntity || target instanceof GlowsilkMothEntity) {
            desiredBaseRange = 20.0D;
        }

        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance damage = this.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance range = this.getAttribute(Attributes.FOLLOW_RANGE);

        if (range != null && range.getBaseValue() != desiredBaseRange) {
            range.setBaseValue(desiredBaseRange);
        }

        if (isLuminous) {
            if (speed != null && speed.getModifier(SPEED_MODIFIER_ATTACK_UUID) == null)
                speed.addTransientModifier(new AttributeModifier(SPEED_MODIFIER_ATTACK_UUID, "Luminous speed", 0.25D, AttributeModifier.Operation.ADDITION));
            if (damage != null && damage.getModifier(ATTACK_DAMAGE_MODIFIER_UUID) == null)
                damage.addTransientModifier(new AttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID, "Luminous dmg", 4.0D, AttributeModifier.Operation.ADDITION));
            if (range != null && range.getModifier(FOLLOW_RANGE_MODIFIER_UUID) == null)
                range.addTransientModifier(new AttributeModifier(FOLLOW_RANGE_MODIFIER_UUID, "Luminous range", 36.0D, AttributeModifier.Operation.ADDITION));

            if (!this.hasPlayedWarning && this.onGround() && target instanceof Player && this.alertTimer == 0 && !this.wantsToTongueAttack && this.attackAnimationTimer == 0) {
                this.triggerAnim("attackController", "alert");
                this.playSound(ModSounds.BLINDSIGHT_ALERT.get(), 1.0F, 1.0F);
                this.alertTimer = 30;
                this.hasPlayedWarning = true;
                ((BlindsightMoveControl) this.getMoveControl()).setSpeed(0.0D);
            }
        } else {
            if (speed != null) speed.removeModifier(SPEED_MODIFIER_ATTACK_UUID);
            if (damage != null) damage.removeModifier(ATTACK_DAMAGE_MODIFIER_UUID);
            if (range != null) range.removeModifier(FOLLOW_RANGE_MODIFIER_UUID);
            this.hasPlayedWarning = false;
        }
    }

    @Override
    protected void jumpFromGround() {
        if (this.wantsToTongueAttack) {
            return;
        }

        float jumpPower = 0.42F + (random.nextFloat() * 0.1F);
        float forwardImpulse;

        LivingEntity target = this.getTarget();

        if (target == null) {
            this.consecutiveHops++;
            forwardImpulse = 0.4F;
        } else {
            this.jumpCount++;

            forwardImpulse = 0.3F;

            if (target instanceof GlowsquitoEntity || target instanceof GlowsilkMothEntity) {
                jumpPower = 1.2F;
                forwardImpulse = 0.6F;
            } else if (target instanceof Player) {
                this.jumpsUntilBigJump--;
                if (this.jumpsUntilBigJump <= 0) {
                    jumpPower = 0.9F;
                    forwardImpulse = 0.8F;
                    this.jumpsUntilBigJump = 4 + this.random.nextInt(2);
                    this.isBigJumping = true;
                }
            }
        }

        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(motion.x, jumpPower, motion.z);
        this.hasImpulse = true;

        float f = this.getYRot() * ((float) Math.PI / 180F);
        this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f) * forwardImpulse, 0.0D, Mth.cos(f) * forwardImpulse));
    }

    public boolean doPlayJumpSound() {
        return this.onGround() && !this.wantsToTongueAttack;
    }

    @Override
    public void travel(@NotNull Vec3 pTravelVector) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            if (this.isInLava()) {
                this.moveRelative(0.02F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
                return;
            }
        }
        super.travel(pTravelVector);
    }

    public int getJumpDelay() {
        return 10;
    }

    public void doTongueDamage(LivingEntity target) {
        if (target instanceof BlindsightEntity) {
            float knockbackStrength = 4.0F;
            target.knockback(knockbackStrength, Mth.sin(this.getYRot() * ((float) Math.PI / 180F)), -Mth.cos(this.getYRot() * ((float) Math.PI / 180F)));

            this.setTarget(null);
            this.setLastHurtByMob(null);

            ((BlindsightEntity) target).setTarget(null);
            target.setLastHurtByMob(null);
        } else if (this.doHurtTarget(target)) {
            float knockbackStrength = 2.5F;
            target.knockback(knockbackStrength, Mth.sin(this.getYRot() * ((float) Math.PI / 180F)), -Mth.cos(this.getYRot() * ((float) Math.PI / 180F)));
            target.addEffect(new MobEffectInstance(ModEffects.LUMINOUS.get(), 600));
        }
    }

    public boolean isAttacking() {
        return this.entityData.get(IS_ATTACKING);
    }

    public boolean isJumping() {
        return this.entityData.get(IS_JUMPING);
    }

    public boolean isResting() {
        return this.entityData.get(IS_RESTING);
    }

    public void setResting(boolean resting) {
        this.entityData.set(IS_RESTING, resting);
    }

    public boolean isWatchingLuminous() {
        return this.entityData.get(IS_WATCHING_LUMINOUS);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, @NotNull DamageSource source) {
        return false;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.getTarget() instanceof Player) {
            return null;
        }
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

    public SoundEvent getJumpSound() {
        return ModSounds.BLINDSIGHT_LEAP.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            if (this.entityData.get(IS_ATTACKING)) return PlayState.STOP;
            if (this.entityData.get(IS_JUMPING)) return event.setAndContinue(JUMP_LOOP);
            if (this.entityData.get(IS_RESTING)) {
                return event.setAndContinue(this.playRareIdle ? IDLE_RARE : IDLE);
            }
            return event.setAndContinue(IDLE);
        }));

        controllers.add(new AnimationController<>(this, "attackController", 0, event -> PlayState.STOP)
                .triggerableAnim("bite", BITE)
                .triggerableAnim("swallow", SWALLOW)
                .triggerableAnim("tongue_attack_telegraphed", TONGUE_ATTACK_TELEGRAPHED)
                .triggerableAnim("tongue_attack_immediate", TONGUE_ATTACK_IMMEDIATE)
                .triggerableAnim("alert", ALERT)
                .triggerableAnim("land", LAND));
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}