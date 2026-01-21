package com.infernalstudios.infernalexp.entities;

import com.infernalstudios.infernalexp.entities.ai.AvoidCampfiresGoal;
import com.infernalstudios.infernalexp.entities.ai.LookAroundGoal;
import com.infernalstudios.infernalexp.entities.ai.RandomFlyGoal;
import com.infernalstudios.infernalexp.entities.ai.SuckGlowstoneGoal;
import com.infernalstudios.infernalexp.module.ModEffects;
import com.infernalstudios.infernalexp.module.ModEntityTypes;
import com.infernalstudios.infernalexp.module.ModSounds;
import com.infernalstudios.infernalexp.module.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
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

public class GlowsquitoEntity extends Animal implements FlyingAnimal, GeoEntity {
    private static final EntityDataAccessor<Boolean> BRED = SynchedEntityData.defineId(GlowsquitoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> EATING = SynchedEntityData.defineId(GlowsquitoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHROOMLIGHT_POWERED = SynchedEntityData.defineId(GlowsquitoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHROOMNIGHT_POWERED = SynchedEntityData.defineId(GlowsquitoEntity.class, EntityDataSerializers.BOOLEAN);

    private static final Ingredient TEMPTATION_ITEMS = Ingredient.of(ModTags.Items.GLOWSQUITO_TEMPTATION_ITEMS);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation PERCHING = RawAnimation.begin().thenLoop("perching");
    private static final RawAnimation DRINKING = RawAnimation.begin().thenLoop("drinking");
    private static final RawAnimation DRINKING_ONCE = RawAnimation.begin().thenPlay("drinking");
    private static final RawAnimation FLYING_FLAPPING = RawAnimation.begin().thenLoop("flying_flapping");
    private static final RawAnimation FLYING_WOBBLING = RawAnimation.begin().thenLoop("flying_wobbling");
    private static final RawAnimation FLYING_TILTING = RawAnimation.begin().thenLoop("flying_tilting");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int shroomlightTimer;
    private int shroomnightTimer;

    public GlowsquitoEntity(EntityType<? extends Animal> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new GlowsquitoEntity.MoveHelperController(this);
        this.setPathfindingMalus(BlockPathTypes.LAVA, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 16.0F);
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FLYING_SPEED, 0.6D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    public static boolean checkGlowsquitoSpawnRules(EntityType<GlowsquitoEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.noCollision(entityType.getAABB(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.0D, true) {
            @Override
            protected double getAttackReachSqr(@NotNull LivingEntity attackTarget) {
                double standardReach = super.getAttackReachSqr(attackTarget);

                if (attackTarget instanceof Player) {
                    return standardReach * 0.5D;
                }

                return standardReach;
            }
        });
        this.goalSelector.addGoal(1, new AvoidCampfiresGoal(this, 8.0D, 1.2D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 0.8d));
        this.goalSelector.addGoal(3, new SuckGlowstoneGoal(this));
        this.goalSelector.addGoal(4, new TemptGoal(this, 0.8d, TEMPTATION_ITEMS, false));
        this.goalSelector.addGoal(5, new RandomFlyGoal(this));
        this.goalSelector.addGoal(8, new LookAroundGoal(this));

        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                (entity) -> entity.hasEffect(ModEffects.LUMINOUS.get()) && !(entity instanceof GlowsquitoEntity)));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "base_controller", 0, event -> {
            if (!this.isEating()) {
                return event.setAndContinue(FLYING_FLAPPING);
            }
            return event.setAndContinue(PERCHING);
        }));

        controllers.add(new AnimationController<>(this, "air_wobble_controller", 0, event -> {
            if (!this.isEating()) {
                return event.setAndContinue(FLYING_WOBBLING);
            }
            return PlayState.STOP;
        }));

        controllers.add(new AnimationController<>(this, "air_tilt_controller", 0, event -> {
            if (!this.isEating()) {
                return event.setAndContinue(FLYING_TILTING);
            }
            return PlayState.STOP;
        }));

        controllers.add(new AnimationController<>(this, "action_controller", 0, event -> {
            if (this.isEating()) {
                return event.setAndContinue(DRINKING);
            }
            return PlayState.STOP;
        }).triggerableAnim("attack_drink", DRINKING_ONCE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {
        return ModEntityTypes.GLOWSQUITO.get().create(serverLevel);
    }

    @Override
    public boolean isFood(@NotNull ItemStack stack) {
        return TEMPTATION_ITEMS.test(stack);
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return ModSounds.GLOWSQUITO_HURT.get();
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return ModSounds.GLOWSQUITO_DEATH.get();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BRED, false);
        this.entityData.define(EATING, false);
        this.entityData.define(SHROOMLIGHT_POWERED, false);
        this.entityData.define(SHROOMNIGHT_POWERED, false);
    }

    public boolean isEating() {
        return this.entityData.get(EATING);
    }

    public void setEating(boolean eating) {
        this.entityData.set(EATING, eating);
    }

    public boolean getBred() {
        return this.entityData.get(BRED);
    }

    public void setBred(boolean isBred) {
        this.entityData.set(BRED, isBred);
    }

    public boolean isShroomlightPowered() {
        return this.entityData.get(SHROOMLIGHT_POWERED);
    }

    public void setShroomlightPowered(boolean powered) {
        this.entityData.set(SHROOMLIGHT_POWERED, powered);
    }

    public void setShroomlightTimer(int time) {
        this.shroomlightTimer = time;
    }

    public boolean isShroomnightPowered() {
        return this.entityData.get(SHROOMNIGHT_POWERED);
    }

    public void setShroomnightPowered(boolean powered) {
        this.entityData.set(SHROOMNIGHT_POWERED, powered);
    }

    public void setShroomnightTimer(int time) {
        this.shroomnightTimer = time;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("ShroomlightPowered", this.isShroomlightPowered());
        compound.putInt("ShroomlightTimer", this.shroomlightTimer);
        compound.putBoolean("ShroomnightPowered", this.isShroomnightPowered());
        compound.putInt("ShroomnightTimer", this.shroomnightTimer);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setShroomlightPowered(compound.getBoolean("ShroomlightPowered"));
        this.shroomlightTimer = compound.getInt("ShroomlightTimer");
        this.setShroomnightPowered(compound.getBoolean("ShroomnightPowered"));
        this.shroomnightTimer = compound.getInt("ShroomnightTimer");
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            if (this.isShroomlightPowered()) {
                this.shroomlightTimer--;
                if (this.shroomlightTimer <= 0) {
                    this.setShroomlightPowered(false);
                }
            }

            if (this.isShroomnightPowered()) {
                this.shroomnightTimer--;
                if (this.shroomnightTimer <= 0) {
                    this.setShroomnightPowered(false);
                }
            }
        }
    }

    @Override
    public void ate() {
        this.playSound(ModSounds.GLOWSQUITO_HURT.get(), 1.0F, 1.0F);
        this.addEffect(new MobEffectInstance(ModEffects.LUMINOUS.get(), 1200));
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entityIn) {
        if (!super.doHurtTarget(entityIn)) {
            return false;
        } else {
            if (entityIn instanceof LivingEntity) {
                this.heal(1.0f);
                this.triggerAnim("action_controller", "attack_drink");
            }
            return true;
        }
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level worldIn) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, worldIn) {
            @Override
            public boolean isStableDestination(@NotNull BlockPos pos) {
                return true;
            }
        };
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(true);
        flyingpathnavigator.setCanPassDoors(false);
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
                float friction = 0.91F;

                this.moveRelative(0.02F, pTravelVector);
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
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
        return false;
    }

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, @NotNull BlockState pState, @NotNull BlockPos pPos) {
    }

    static class MoveHelperController extends MoveControl {
        private final GlowsquitoEntity parentEntity;

        public MoveHelperController(GlowsquitoEntity mob) {
            super(mob);
            this.parentEntity = mob;
        }

        public void tick() {
            if (this.parentEntity.isEating()) {
                return;
            }

            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 wanted = new Vec3(this.wantedX - this.parentEntity.getX(), this.wantedY - this.parentEntity.getY(), this.wantedZ - this.parentEntity.getZ());
                double dist = wanted.length();

                if (this.parentEntity.horizontalCollision && this.parentEntity.level().getGameTime() % 20 == 0) {
                    this.parentEntity.getNavigation().recomputePath();
                }

                if (dist < 0.3D) {
                    this.operation = MoveControl.Operation.WAIT;
                    this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().scale(0.5D));
                    this.parentEntity.getNavigation().stop();
                } else {
                    this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(wanted.scale(this.speedModifier * 0.05D / dist)));

                    if (this.parentEntity.getTarget() == null) {
                        if (wanted.horizontalDistanceSqr() > 0.001D) {
                            float targetYaw = -((float) Mth.atan2(wanted.x, wanted.z)) * (180F / (float) Math.PI);
                            this.parentEntity.setYRot(this.rotlerp(this.parentEntity.getYRot(), targetYaw, 20.0F));
                        }
                    } else {
                        double d2 = this.parentEntity.getTarget().getX() - this.parentEntity.getX();
                        double d3 = this.parentEntity.getTarget().getZ() - this.parentEntity.getZ();
                        float targetYaw = -((float) Mth.atan2(d2, d3)) * (180F / (float) Math.PI);
                        this.parentEntity.setYRot(this.rotlerp(this.parentEntity.getYRot(), targetYaw, 20.0F));
                    }
                    this.parentEntity.yBodyRot = this.parentEntity.getYRot();
                }
            }
        }
    }
}