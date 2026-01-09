package com.infernalstudios.infernalexp.entities;

import com.infernalstudios.infernalexp.entities.ai.RandomFlyGoal;
import com.infernalstudios.infernalexp.entities.ai.SuckGlowstoneGoal;
import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModEntityTypes;
import com.infernalstudios.infernalexp.module.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

public class GlowsquitoEntity extends Animal implements FlyingAnimal, GeoEntity {
    private static final EntityDataAccessor<Boolean> BRED = SynchedEntityData.defineId(GlowsquitoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final Ingredient TEMPTATION_ITEMS = Ingredient.of(ModBlocks.SHROOMLIGHT_TEAR.get().asItem());
    // Animations
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");
    private static final RawAnimation EAT = RawAnimation.begin().thenLoop("eat");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private SuckGlowstoneGoal eatGlowstoneGoal;
    private int hogTimer;

    public GlowsquitoEntity(EntityType<? extends Animal> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new GlowsquitoEntity.MoveHelperController(this);

        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 16.0F);
        this.setPathfindingMalus(BlockPathTypes.COCOA, -1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 32.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.FLYING_SPEED, 0.6D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.eatGlowstoneGoal = new SuckGlowstoneGoal(this);

        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 0.8D, true));

        this.goalSelector.addGoal(1, new GlowsquitoEntity.LookAroundGoal(this));
        this.goalSelector.addGoal(1, this.eatGlowstoneGoal);
        this.goalSelector.addGoal(2, new BreedGoal(this, 0.8d));
        this.goalSelector.addGoal(3, new TemptGoal(this, 0.8d, TEMPTATION_ITEMS, false));
        this.goalSelector.addGoal(8, new RandomFlyGoal(this));

        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
    }

    // --- Geckolib Implementation ---

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, event -> {
            if (this.hogTimer > 0) {
                return event.setAndContinue(EAT);
            }
            if (!this.onGround() || event.isMoving()) {
                return event.setAndContinue(FLY);
            }
            return event.setAndContinue(IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // -------------------------------

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level worldIn) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, worldIn) {
            @Override
            public boolean isStableDestination(@NotNull BlockPos pos) {
                return true;
            }

            @Override
            public void tick() {
                super.tick();
            }
        };
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(true);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    @Override
    public void travel(@NotNull Vec3 pTravelVector) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5F));
            } else {
                BlockPos ground = this.getBlockPosBelowThatAffectsMyMovement();
                float friction = 0.91F;
                if (this.onGround()) {
                    friction = this.level().getBlockState(ground).getBlock().getFriction() * 0.91F;
                }

                float f1 = 0.16277137F / (friction * friction * friction);
                friction = 0.91F;
                if (this.onGround()) {
                    friction = this.level().getBlockState(ground).getBlock().getFriction() * 0.91F;
                }

                this.moveRelative(this.onGround() ? 0.1F * f1 : 0.02F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(friction));
            }
        } else {
            super.travel(pTravelVector);
        }
    }

    @Override
    public boolean isFlying() {
        return !this.onGround();
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, @NotNull DamageSource source) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        // Vanilla animation logic removed in favor of registerControllers
    }

    @Override
    protected void customServerAiStep() {
        this.hogTimer = this.eatGlowstoneGoal.getEatAnimationTick();
        super.customServerAiStep();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            this.hogTimer = Math.max(0, this.hogTimer - 1);
        }
    }

    public @NotNull MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel world, @NotNull AgeableMob parent) {
        GlowsquitoEntity glowsquitoEntity = ModEntityTypes.GLOWSQUITO.get().create(world);
        if (glowsquitoEntity != null) {
            glowsquitoEntity.setBred(true);
        }
        return glowsquitoEntity;
    }

    public boolean isFood(@NotNull ItemStack stack) {
        return TEMPTATION_ITEMS.test(stack);
    }

    @Override
    public void ate() {
        super.ate();
        // TODO: add sucking sound
        // this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
        this.addEffect(new MobEffectInstance(MobEffects.GLOWING, 600));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BRED, false);
    }

    public boolean getBred() {
        return this.entityData.get(BRED);
    }

    public void setBred(boolean isBred) {
        this.entityData.set(BRED, isBred);
    }

    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Bred", this.getBred());
    }

    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setBred(compound.getBoolean("Bred"));
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 10) {
            this.hogTimer = 40;
        } else {
            super.handleEntityEvent(id);
        }
    }

    protected float getStandingEyeHeight(@NotNull Pose poseIn, @NotNull EntityDimensions sizeIn) {
        return this.isBaby() ? sizeIn.height * 0.35F : sizeIn.height * 0.72F;
    }

    @Override
    protected void doPush(@NotNull Entity entityIn) {
        super.doPush(entityIn);
        // TODO: add effect logic here
    }

    public boolean fireImmune() {
        return true;
    }

    @Override
    public int getExperienceReward() {
        return 1 + this.level().random.nextInt(4);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.GLOWSQUITO_DEATH.get();
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return ModSounds.GLOWSQUITO_HURT.get();
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entityIn) {
        if (!super.doHurtTarget(entityIn)) {
            return false;
        } else {
            if (entityIn instanceof LivingEntity livingEntity) {
                // TODO: apply effect on hit
                // livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 100));
            }
            return true;
        }
    }

    static class LookAroundGoal extends Goal {
        private final GlowsquitoEntity parentEntity;

        public LookAroundGoal(GlowsquitoEntity ghast) {
            this.parentEntity = ghast;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return true;
        }

        public void tick() {
            Vec3 vector3d = this.parentEntity.getDeltaMovement();
            this.parentEntity.setYRot(-((float) Mth.atan2(vector3d.x, vector3d.z)) * (180F / (float) Math.PI));
            this.parentEntity.yBodyRot = this.parentEntity.getYRot();
        }
    }

    static class MoveHelperController extends MoveControl {
        private final GlowsquitoEntity parentEntity;
        private int courseChangeCooldown;

        public MoveHelperController(GlowsquitoEntity ghast) {
            super(ghast);
            this.parentEntity = ghast;
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                if (this.courseChangeCooldown-- <= 0) {
                    this.courseChangeCooldown += this.parentEntity.getRandom().nextInt(5) + 2;
                    Vec3 vector3d = new Vec3(this.wantedX - this.parentEntity.getX(), this.wantedY - this.parentEntity.getY(), this.wantedZ - this.parentEntity.getZ());
                    double d0 = vector3d.length();
                    vector3d = vector3d.normalize();
                    if (this.canReach(vector3d, Mth.ceil(d0))) {
                        this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(vector3d.scale(0.1D)));
                    } else {
                        this.operation = MoveControl.Operation.WAIT;
                    }
                }
            }
        }

        private boolean canReach(Vec3 p_220673_1_, int p_220673_2_) {
            AABB axisalignedbb = this.parentEntity.getBoundingBox();
            for (int i = 1; i < p_220673_2_; ++i) {
                axisalignedbb = axisalignedbb.move(p_220673_1_);
                if (!this.parentEntity.level().noCollision(this.parentEntity, axisalignedbb)) {
                    return false;
                }
            }
            return true;
        }
    }
}