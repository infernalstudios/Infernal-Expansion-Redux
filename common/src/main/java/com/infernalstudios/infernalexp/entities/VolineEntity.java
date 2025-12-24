package com.infernalstudios.infernalexp.entities;

import com.infernalstudios.infernalexp.entities.ai.EatItemsGoal;
import com.infernalstudios.infernalexp.entities.ai.FindMagmaBlockGoal;
import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModItems;
import com.infernalstudios.infernalexp.module.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
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

public class VolineEntity extends Monster implements IBucketable, GeoEntity {
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

    public VolineEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.4D);
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

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FindMagmaBlockGoal(this, 1.0D, 16));
        this.goalSelector.addGoal(1, new EatItemsGoal(this));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.2D, Ingredient.of(Items.MAGMA_CREAM), false));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Player.class, 6.0F, 1.2D, 1.5D, entity -> entity.isHolding(Items.SNOWBALL)));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Piglin.class, 8.0F, 1.0D, 1.2D, entity -> !entity.hasEffect(MobEffects.FIRE_RESISTANCE)));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new PanicGoal(this, getAttributeValue(Attributes.MOVEMENT_SPEED) * 2.0D));
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true, entity -> entity.hasEffect(MobEffects.FIRE_RESISTANCE) && !entity.isHolding(Items.MAGMA_CREAM)));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, true, entity -> entity.hasEffect(MobEffects.FIRE_RESISTANCE)));
    }

    // GeckoLib Controllers
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movementController", 5, event -> {
            if (this.isGrown() && this.isSleeping()) {
                int timer = this.entityData.get(SLEEP_TIMER);
                if (timer > 11960) {
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
            // Server-side logic for shelter seeking
            if (this.isSeekingShelter()) {
                this.shelterSeekTime++;
                if (this.shelterSeekTime > 200) { // 10 seconds
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
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return super.getDimensions(pose).scale(this.getSizeFactor());
    }

    public float getSizeFactor() {
        if (this.isGrown()) {
            return this.isSleeping() ? 1.6F : 1.75F;
        }
        return 1.0F + (this.entityData.get(MAGMA_CREAM_EATEN) * 0.2F);
    }

    @Override
    public void refreshDimensions() {
        super.refreshDimensions();
        this.setPos(this.getX(), this.getY(), this.getZ());
    }

    public boolean wantsToEat(ItemStack stack) {
        return !this.isSleeping() && (stack.is(Items.MAGMA_CREAM));
    }

    public boolean isSeekingShelter() {
        return this.entityData.get(IS_SEEKING_SHELTER);
    }

    public void setSeekingShelter(boolean seeking) {
        this.entityData.set(IS_SEEKING_SHELTER, seeking);
    }

    public boolean isSleeping() {
        return this.entityData.get(IS_SLEEPING);
    }

    public boolean isGrown() {
        return this.entityData.get(IS_GROWN);
    }

    public void setGrown(boolean grown) {
        this.entityData.set(IS_GROWN, grown);
        this.refreshDimensions();
    }

    public void startSleeping(@NotNull BlockPos pos) {
        this.setSeekingShelter(false);
        this.entityData.set(IS_SLEEPING, true);
        this.entityData.set(SLEEP_TIMER, 12000);

        this.getNavigation().stop();
        Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(0.0D);
        this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
    }

    private void wakeUp() {
        this.entityData.set(IS_SLEEPING, false);
        this.entityData.set(MAGMA_CREAM_EATEN, 0); // TODO: does this even make sense
        Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(0.4D);
        this.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
    }

    @Override
    protected void jumpFromGround() {
        if (!this.isSleeping()) {
            super.jumpFromGround();
        }
    }

    public void ate(ItemStack stack) {
        if (stack.is(Items.MAGMA_CREAM)) {
            // Trigger Eating Animation
            this.triggerAnim("actionController", "eat");
            this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);

            if (!this.isGrown()) {
                int eaten = this.entityData.get(MAGMA_CREAM_EATEN) + 1;
                this.entityData.set(MAGMA_CREAM_EATEN, eaten);

                if (eaten >= 3) {
                    this.setGrown(true);
                    this.entityData.set(MAGMA_CREAM_EATEN, 0);
                    this.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED, 1.0F, 1.0F); // Transformation sound
                } else {
                    this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 6000, eaten - 1, false, false));
                }
            } else {
                this.setSeekingShelter(true);
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
            }

            this.setPersistenceRequired();
            this.setTarget(null);
        }
    }

    @Override
    public boolean isFromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean isFromBucket) {
        this.entityData.set(FROM_BUCKET, isFromBucket);
    }

    @Override
    public void copyToStack(ItemStack stack) {
        IBucketable.copyToStack(this, stack);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("MagmaCreamEaten", this.entityData.get(MAGMA_CREAM_EATEN));
        tag.putFloat("Size", this.getSizeFactor());
        tag.putBoolean("IsGrown", this.isGrown());
    }

    @Override
    public void copyFromAdditional(CompoundTag compound) {
        IBucketable.copyFromAdditional(this, compound);
        if (compound.contains("MagmaCreamEaten")) {
            this.entityData.set(MAGMA_CREAM_EATEN, compound.getInt("MagmaCreamEaten"));
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
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        return IBucketable.tryBucketEntity(player, hand, this).orElse(super.mobInteract(player, hand));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return null;
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
            if (this.level().getBlockState(pos).isAir()) {
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
        return level.getDifficulty() != Difficulty.PEACEFUL
                && !level.getBlockState(pos.below()).is(Blocks.NETHER_WART_BLOCK);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("MagmaCreamEaten", this.entityData.get(MAGMA_CREAM_EATEN));
        tag.putBoolean("IsSleeping", this.entityData.get(IS_SLEEPING));
        tag.putBoolean("FromBucket", this.isFromBucket());
        tag.putInt("SleepTimer", this.entityData.get(SLEEP_TIMER));
        tag.putBoolean("IsSeekingShelter", this.isSeekingShelter());
        tag.putBoolean("IsGrown", this.isGrown());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(MAGMA_CREAM_EATEN, tag.getInt("MagmaCreamEaten"));
        this.entityData.set(IS_SLEEPING, tag.getBoolean("IsSleeping"));
        this.setFromBucket(tag.getBoolean("FromBucket"));
        if (tag.contains("SleepTimer")) {
            this.entityData.set(SLEEP_TIMER, tag.getInt("SleepTimer"));
        }
        if (tag.contains("IsSeekingShelter")) {
            this.setSeekingShelter(tag.getBoolean("IsSeekingShelter"));
        }
        if (tag.contains("IsGrown")) {
            this.setGrown(tag.getBoolean("IsGrown"));
        }
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if (this.entityData.get(IS_SLEEPING)) return null;
        return this.getFirstPassenger() instanceof LivingEntity entity ? entity : null;
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity target) {
        return !this.isSleeping() && super.canAttack(target);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isSleeping()) {
            // Lock rotation and movement while sleeping
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
            this.yBodyRot = this.yBodyRotO;
            this.yHeadRot = this.yHeadRotO;
            this.setYRot(this.yRotO);
            this.setXRot(this.xRotO);

            // Handle timer
            int timer = this.entityData.get(SLEEP_TIMER);
            if (timer > 0) {
                this.entityData.set(SLEEP_TIMER, timer - 1);
                if (timer % 20 == 0) {
                    this.refreshDimensions();
                }
            } else {
                this.wakeUp();
            }
        }
    }
}