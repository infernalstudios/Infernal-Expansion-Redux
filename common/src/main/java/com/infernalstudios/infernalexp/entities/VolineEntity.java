package com.infernalstudios.infernalexp.entities;

import com.infernalstudios.infernalexp.entities.ai.EatItemsGoal;
import com.infernalstudios.infernalexp.entities.ai.FindShelterGoal;
import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModEntityTypes;
import com.infernalstudios.infernalexp.module.ModItems;
import com.infernalstudios.infernalexp.module.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Objects;

public class VolineEntity extends Animal implements Enemy, IBucketable, GeoEntity {

    // NBT Tags
    private static final String TAG_MAGMA_CREAM_EATEN = "MagmaCreamEaten";
    private static final String TAG_IS_SLEEPING = "IsSleeping";
    private static final String TAG_FROM_BUCKET = "FromBucket";
    private static final String TAG_SLEEP_TIMER = "SleepTimer";
    private static final String TAG_IS_SEEKING_SHELTER = "IsSeekingShelter";
    private static final String TAG_IS_GROWN = "IsGrown";

    // Gameplay Constants
    private static final int MAX_MAGMA_CREAM_EATEN = 3;
    private static final int SLEEP_DURATION_TICKS = 1000;
    private static final int SHELTER_SEEK_TIMEOUT = 200;
    private static final double BASE_MOVEMENT_SPEED = 0.4D;
    private static final double GROWN_MOVEMENT_SPEED = 0.16D;
    private static final double SPEED_REDUCTION_PER_CREAM = 0.12D;
    private static final float SIZE_INCREMENT_PER_CREAM = 0.08F;

    // Synched Data
    public static final EntityDataAccessor<Integer> MAGMA_CREAM_EATEN = SynchedEntityData.defineId(VolineEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_SLEEPING = SynchedEntityData.defineId(VolineEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(VolineEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SLEEP_TIMER = SynchedEntityData.defineId(VolineEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_SEEKING_SHELTER = SynchedEntityData.defineId(VolineEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_GROWN = SynchedEntityData.defineId(VolineEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int shelterSeekTime = 0;

    // Animations
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation EAT = RawAnimation.begin().thenPlay("eat");
    private static final RawAnimation FALLING_ASLEEP = RawAnimation.begin().thenPlay("falling_asleep");
    private static final RawAnimation ASLEEP = RawAnimation.begin().thenLoop("asleep");

    public VolineEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, BASE_MOVEMENT_SPEED);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MAGMA_CREAM_EATEN, 0);
        this.entityData.define(IS_SLEEPING, false);
        this.entityData.define(FROM_BUCKET, false);
        this.entityData.define(SLEEP_TIMER, 0);
        this.entityData.define(IS_SEEKING_SHELTER, false);
        this.entityData.define(IS_GROWN, false);
    }

    public int getMagmaCreamEaten() {
        return this.entityData.get(MAGMA_CREAM_EATEN);
    }

    public void setMagmaCreamEaten(int amount) {
        this.entityData.set(MAGMA_CREAM_EATEN, amount);
    }

    public boolean isSleeping() {
        return this.entityData.get(IS_SLEEPING);
    }

    public void setIsSleeping(boolean sleeping) {
        this.entityData.set(IS_SLEEPING, sleeping);
    }

    @Override
    public boolean isFromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.isPersistenceRequired();
    }

    @Override
    protected float getSoundVolume() {
        if (this.isSleeping()) {
            return 2.0F;
        }
        return super.getSoundVolume();
    }

    @Override
    public boolean isPushedByFluid() {
        return !this.isSleeping() && super.isPushedByFluid();
    }

    @Override
    public void setFromBucket(boolean isFromBucket) {
        this.entityData.set(FROM_BUCKET, isFromBucket);
    }

    public int getSleepTimer() {
        return this.entityData.get(SLEEP_TIMER);
    }

    public void setSleepTimer(int timer) {
        this.entityData.set(SLEEP_TIMER, timer);
    }

    public boolean isSeekingShelter() {
        return this.entityData.get(IS_SEEKING_SHELTER);
    }

    public void setSeekingShelter(boolean seeking) {
        this.entityData.set(IS_SEEKING_SHELTER, seeking);
    }

    public boolean isGrown() {
        return this.entityData.get(IS_GROWN);
    }

    public void setGrown(boolean grown) {
        this.entityData.set(IS_GROWN, grown);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FindShelterGoal(this, 1.0D, 16));
        this.goalSelector.addGoal(1, new EatItemsGoal(this));

        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));

        this.goalSelector.addGoal(2, new TemptGoal(this, 1.2D, Ingredient.of(Items.MAGMA_CREAM), false) {
            @Override
            public boolean canUse() {
                return !VolineEntity.this.isSeekingShelter() && !VolineEntity.this.isSleeping() && super.canUse();
            }
        });

        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Player.class, 6.0F, 1.2D, 1.5D, entity -> entity.isHolding(Items.SNOWBALL)));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Piglin.class, 8.0F, 1.0D, 1.2D, entity -> !entity.hasEffect(MobEffects.FIRE_RESISTANCE)));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.2D, true));

        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D) {
            @Override
            public boolean canUse() {
                return !VolineEntity.this.isSleeping() && super.canUse();
            }
        });

        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F) {
            @Override
            public boolean canUse() {
                return !VolineEntity.this.isSleeping() && super.canUse();
            }
        });

        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return !VolineEntity.this.isSleeping() && super.canUse();
            }
        });

        this.goalSelector.addGoal(9, new PanicGoal(this, getAttributeValue(Attributes.MOVEMENT_SPEED) * 2.0D));
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true, entity -> entity.hasEffect(MobEffects.FIRE_RESISTANCE) && !entity.isHolding(Items.MAGMA_CREAM)));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, MagmaCube.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, true, entity -> entity.hasEffect(MobEffects.FIRE_RESISTANCE)));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movementController", 5, event -> {
            if (this.isGrown() && this.isSleeping()) {
                if (this.getSleepTimer() > 960) {
                    return event.setAndContinue(FALLING_ASLEEP);
                }
                return event.setAndContinue(ASLEEP);
            }

            if (event.isMoving()) {
                return event.setAndContinue(WALK);
            }
            return event.setAndContinue(IDLE);
        }));

        controllers.add(new AnimationController<>(this, "actionController", 5, event -> PlayState.STOP)
                .triggerableAnim("eat", EAT));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            if (this.isSeekingShelter()) {
                this.shelterSeekTime++;
                if (this.shelterSeekTime > SHELTER_SEEK_TIMEOUT) {
                    this.startSleeping(this.blockPosition());
                    this.shelterSeekTime = 0;
                }
            } else {
                this.shelterSeekTime = 0;
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        if (MAGMA_CREAM_EATEN.equals(key) || IS_GROWN.equals(key)) {
            this.refreshSpeed();
        }
        super.onSyncedDataUpdated(key);
    }

    public void refreshSpeed() {
        double newSpeed;
        if (this.isSleeping()) {
            newSpeed = 0.0D;
        } else if (this.isGrown()) {
            newSpeed = GROWN_MOVEMENT_SPEED;
        } else {
            int eaten = this.getMagmaCreamEaten();
            newSpeed = BASE_MOVEMENT_SPEED - (eaten * SPEED_REDUCTION_PER_CREAM);
        }

        Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(newSpeed);
    }

    public float getSizeFactor() {
        if (this.isGrown()) {
            return 1.0F;
        }
        return 1.0F + (this.getMagmaCreamEaten() * SIZE_INCREMENT_PER_CREAM);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob parent) {
        VolineEntity baby = ModEntityTypes.VOLINE.get().create(level);
        if (baby != null) {
            baby.setGrown(false);
            baby.setMagmaCreamEaten(0);
        }
        return baby;
    }

    @Override
    public boolean isFood(@NotNull ItemStack stack) {
        return this.isGrown() && stack.is(ItemTags.PIGLIN_LOVED);
    }

    public boolean wantsToEat(ItemStack stack) {
        if (this.isSleeping()) return false;
        return stack.is(Items.MAGMA_CREAM) || stack.is(ItemTags.PIGLIN_LOVED);
    }

    private void lockToGrid() {
        this.setDeltaMovement(0, this.getDeltaMovement().y, 0);

        float snappedRot = Math.round(this.getYRot() / 90.0F) * 90.0F;
        this.setYRot(snappedRot);
        this.yBodyRot = snappedRot;
        this.yHeadRot = snappedRot;
    }

    public void startSleeping(@NotNull BlockPos pos) {
        this.setSeekingShelter(false);
        this.setIsSleeping(true);
        this.setSleepTimer(SLEEP_DURATION_TICKS);

        this.getNavigation().stop();
        this.refreshSpeed();

        this.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
        this.lockToGrid();
    }

    private void wakeUp() {
        this.setIsSleeping(false);
        this.setMagmaCreamEaten(0);
        this.refreshSpeed();
        this.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
    }

    @Override
    public @NotNull SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        if (level.getBlockState(pos.below()).is(Blocks.NETHER_WART_BLOCK) ||
                level.getBlockState(pos.below()).is(Blocks.NETHERRACK) ||
                level.getBlockState(pos.below()).is(Blocks.MAGMA_BLOCK) ||
                level.getBlockState(pos.below()).is(Blocks.BLACKSTONE)) {
            return 10.0F;
        }
        return 0.0F;
    }

    @Override
    protected void jumpFromGround() {
        if (!this.isSleeping()) {
            super.jumpFromGround();
        }
    }

    public void playEatingAnimation() {
        this.triggerAnim("actionController", "eat");
    }

    public void ate(ItemStack stack) {
        if (stack.is(Items.MAGMA_CREAM)) {
            int eaten = this.getMagmaCreamEaten() + 1;
            this.setMagmaCreamEaten(eaten);

            if (eaten >= MAX_MAGMA_CREAM_EATEN) {
                if (!this.isGrown()) {
                    this.setGrown(true);
                    this.setMagmaCreamEaten(0);
                } else {
                    this.setSeekingShelter(true);
                    this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
                }
            }
        }
        else if (stack.is(ItemTags.PIGLIN_LOVED)) {
            if (this.isGrown()) {
                if (this.canFallInLove()) {
                    this.setInLove(null);
                }
                this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
            } else {
                this.playSound(SoundEvents.GENERIC_EAT, 0.5F, 1.2F);
            }
        }

        this.refreshSpeed();
        this.setPersistenceRequired();
        this.setTarget(null);
    }

    @Override
    public float getVoicePitch() {
        if (!this.isGrown()) {
            return (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F;
        }
        return super.getVoicePitch();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isSleeping()) {
            this.lockToGrid();
            this.setXRot(this.xRotO);

            int timer = this.getSleepTimer();
            if (timer > 0) {
                this.setSleepTimer(timer - 1);
            } else {
                this.wakeUp();
            }
        }
    }

    @Override
    public void copyToStack(ItemStack stack) {
        IBucketable.copyToStack(this, stack);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(TAG_MAGMA_CREAM_EATEN, this.getMagmaCreamEaten());
        tag.putFloat("Size", this.getSizeFactor());
        tag.putBoolean(TAG_IS_GROWN, this.isGrown());
    }

    @Override
    public void copyFromAdditional(CompoundTag compound) {
        IBucketable.copyFromAdditional(this, compound);
        if (compound.contains(TAG_MAGMA_CREAM_EATEN)) {
            this.setMagmaCreamEaten(compound.getInt(TAG_MAGMA_CREAM_EATEN));
        }
    }

    @Override
    public ItemStack getBucketItem() {
        return new ItemStack(ModItems.VOLINE_BUCKET.get());
    }

    @Override
    public SoundEvent getBucketedSound() {
        return SoundEvents.BUCKET_FILL_LAVA;
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (!this.isGrown()) {
            return IBucketable.tryBucketEntity(player, hand, this).orElse(super.mobInteract(player, hand));
        }

        return super.mobInteract(player, hand);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return SoundEvents.FOX_SLEEP;
        }
        return ModSounds.VOLINE_AMBIENT.get();
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource source) {
        return ModSounds.VOLINE_HURT.get();
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return ModSounds.VOLINE_HURT.get();
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState block) {
        this.playSound(SoundEvents.PIG_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean isSnowball = source.getDirectEntity() instanceof Snowball;
        if (isSnowball) {
            amount += 3;
        }
        if (this.isSleeping()) {
            if (source.getDirectEntity() instanceof Snowball) {
                this.transformToGeyser();
                return false;
            }
            if (source != this.damageSources().fellOutOfWorld()) {
                return false;
            }
        }
        return super.hurt(source, amount);
    }

    private void transformToGeyser() {
        if (!this.level().isClientSide) {
            BlockPos pos = this.blockPosition();
            ServerLevel serverLevel = (ServerLevel) this.level();

            serverLevel.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, this.getSoundSource(), 1.0F, 1.0F);

            serverLevel.sendParticles(ParticleTypes.FLASH, this.getX(), this.getY() + 0.5, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
            serverLevel.sendParticles(ParticleTypes.CLOUD, this.getX(), this.getY() + 0.5, this.getZ(), 15, 0.5, 0.5, 0.5, 0.1);
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 5, 0.3, 0.5, 0.3, 0.05);

            if (this.level().getBlockState(pos).canBeReplaced()) {
                this.level().setBlock(pos, ModBlocks.VOLATILE_GEYSER.get().defaultBlockState(), 3);
            }
            this.discard();
        }
    }

    public static boolean checkVolineSpawnRules(
            EntityType<VolineEntity> voline,
            ServerLevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        return !level.getBlockState(pos.below()).is(Blocks.NETHER_WART_BLOCK);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(TAG_MAGMA_CREAM_EATEN, this.getMagmaCreamEaten());
        tag.putBoolean(TAG_IS_SLEEPING, this.isSleeping());
        tag.putBoolean(TAG_FROM_BUCKET, this.isFromBucket());
        tag.putInt(TAG_SLEEP_TIMER, this.getSleepTimer());
        tag.putBoolean(TAG_IS_SEEKING_SHELTER, this.isSeekingShelter());
        tag.putBoolean(TAG_IS_GROWN, this.isGrown());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setMagmaCreamEaten(tag.getInt(TAG_MAGMA_CREAM_EATEN));
        this.setIsSleeping(tag.getBoolean(TAG_IS_SLEEPING));
        this.setFromBucket(tag.getBoolean(TAG_FROM_BUCKET));

        if (tag.contains(TAG_SLEEP_TIMER)) {
            this.setSleepTimer(tag.getInt(TAG_SLEEP_TIMER));
        }
        if (tag.contains(TAG_IS_SEEKING_SHELTER)) {
            this.setSeekingShelter(tag.getBoolean(TAG_IS_SEEKING_SHELTER));
        }
        if (tag.contains(TAG_IS_GROWN)) {
            this.setGrown(tag.getBoolean(TAG_IS_GROWN));
        }
        this.refreshSpeed();
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if (this.isSleeping()) return null;
        return this.getFirstPassenger() instanceof LivingEntity entity ? entity : null;
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity target) {
        if (this.level().getDifficulty() == net.minecraft.world.Difficulty.PEACEFUL) {
            return false;
        }

        return !this.isSleeping() && super.canAttack(target);
    }
}