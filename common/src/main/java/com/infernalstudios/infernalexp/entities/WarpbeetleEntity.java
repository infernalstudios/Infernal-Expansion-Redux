package com.infernalstudios.infernalexp.entities;

import com.infernalstudios.infernalexp.module.ModEntityTypes;
import com.infernalstudios.infernalexp.module.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
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

import java.util.List;

public class WarpbeetleEntity extends Animal implements GeoEntity, FlyingAnimal {

    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(WarpbeetleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DANCING = SynchedEntityData.defineId(WarpbeetleEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation DANCE = RawAnimation.begin().thenLoop("dance");
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private BlockPos jukeboxPosition;

    public WarpbeetleEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        this.moveControl = new WarpbeetleMoveControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FLYING_SPEED, 0.4D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    public static boolean checkWarpbeetleSpawnRules(EntityType<WarpbeetleEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(Blocks.WARPED_NYLIUM) && isBrightEnoughToSpawn(level, pos);
    }

    @Override
    public @NotNull MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, false);
        this.entityData.define(DANCING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredient.of(Items.CRIMSON_FUNGUS), false));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));

        this.goalSelector.addGoal(5, new WarpbeetleWanderGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    public boolean isPushable() {
        return !this.isPassenger();
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown() && stack.isEmpty()) {
            if (this.isPassenger()) {
                this.stopRiding();
            } else if (!this.isVehicle()) {
                this.startRiding(player, true);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (stack.is(Items.CRIMSON_FUNGUS)) {
            if (this.getHealth() < this.getMaxHealth()) {
                this.heal(2.0F);
                this.usePlayerItem(player, hand, stack);
                this.gameEvent(GameEvent.EAT, this);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            } else if (this.isBaby()) {
                this.usePlayerItem(player, hand, stack);
                this.ageUp((int) (((float) -this.getAge() / 20) * 0.1F), true);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            } else if (!this.level().isClientSide && this.getAge() == 0 && this.canFallInLove()) {
                this.usePlayerItem(player, hand, stack);
                this.setInLove(player);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }

        return super.mobInteract(player, hand);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isDancing() ? null : ModSounds.WARPBEETLE_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return ModSounds.WARPBEETLE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.WARPBEETLE_DEATH.get();
    }

    @Override
    public void tick() {
        if (this.isPassenger()) {
            this.setYRot(0);
            this.setYHeadRot(0);
            this.yBodyRot = 0;
            this.setXRot(0);
        }
        super.tick();

        if (this.jukeboxPosition == null || !this.jukeboxPosition.closerToCenterThan(this.position(), 3.46D) || !this.level().getBlockState(this.jukeboxPosition).is(Blocks.JUKEBOX)) {
            this.setDancing(false);
            this.jukeboxPosition = null;
        }

    }

    @Override
    public void rideTick() {
        Entity vehicle = this.getVehicle();
        if (vehicle instanceof Player player) {
            this.setPos(player.getX(), player.getY() + 1.2D, player.getZ());
            this.setDeltaMovement(Vec3.ZERO);

            if (!player.onGround() && player.getDeltaMovement().y < -0.1) {
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 10, 0, false, false, false));
            }
        } else {
            super.rideTick();
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }

        if (source.getEntity() instanceof LivingEntity attacker) {
            AABB detectionBox = this.getBoundingBox().inflate(32.0D);
            List<EnderMan> endermen = this.level().getEntitiesOfClass(EnderMan.class, detectionBox);

            for (EnderMan enderman : endermen) {
                if (enderman.hasLineOfSight(this)) {
                    enderman.setTarget(attacker);
                }
            }
        }

        boolean result = super.hurt(source, amount);

        if (result && this.isAlive() && !this.level().isClientSide && !this.isPassenger()) {
            for (int i = 0; i < 64; ++i) {
                if (this.teleport()) {
                    return true;
                }
            }
        }
        return result;
    }

    protected boolean teleport() {
        if (!this.level().isClientSide() && this.isAlive()) {
            double d0 = this.getX() + (this.random.nextDouble() - 0.5D) * 16.0D;
            double d1 = this.getY() + (double) (this.random.nextInt(16) - 8);
            double d2 = this.getZ() + (this.random.nextDouble() - 0.5D) * 16.0D;
            return this.teleport(d0, d1, d2);
        } else {
            return false;
        }
    }

    private boolean teleport(double x, double y, double z) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(x, y, z);

        while (blockpos$mutableblockpos.getY() > this.level().getMinBuildHeight() && !this.level().getBlockState(blockpos$mutableblockpos).blocksMotion()) {
            blockpos$mutableblockpos.move(Direction.DOWN);
        }

        BlockState blockstate = this.level().getBlockState(blockpos$mutableblockpos);
        if (blockstate.blocksMotion()) {
            boolean flag = this.randomTeleport(x, (double) blockpos$mutableblockpos.getY() + 1.0D, z, true);
            if (flag) {
                this.level().gameEvent(GameEvent.TELEPORT, this.position(), GameEvent.Context.of(this));
                if (!this.isSilent()) {
                    this.level().playSound(null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
                    this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }
            return flag;
        } else {
            return false;
        }
    }

    @Override
    public void setRecordPlayingNearby(@NotNull BlockPos pos, boolean isPartying) {
        this.jukeboxPosition = pos;
        this.setDancing(isPartying);
    }

    public boolean isDancing() {
        return this.entityData.get(DANCING);
    }

    public void setDancing(boolean dancing) {
        this.entityData.set(DANCING, dancing);
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLYING, flying);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "base_controller", 0, event -> {
            if (this.isDancing()) return event.setAndContinue(DANCE);
            if (this.isFlying()) return event.setAndContinue(FLY);
            if (event.isMoving()) return event.setAndContinue(WALK);
            return event.setAndContinue(IDLE);
        }));
        controllers.add(new AnimationController<>(this, "attack_controller", 0, event -> PlayState.STOP)
                .triggerableAnim("attack", ATTACK));
    }

    @Override
    public void swing(@NotNull InteractionHand hand, boolean updateSelf) {
        super.swing(hand, updateSelf);

        if (this.level().isClientSide && hand == InteractionHand.MAIN_HAND) {
            this.triggerAnim("attack_controller", "attack");
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        FlyingPathNavigation flyingNavigation = new FlyingPathNavigation(this, level);
        flyingNavigation.setCanOpenDoors(false);
        flyingNavigation.setCanFloat(true);
        return flyingNavigation;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob parent) {
        return ModEntityTypes.WARPBEETLE.get().create(level);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.CRIMSON_FUNGUS);
    }

    static class WarpbeetleMoveControl extends MoveControl {
        private final WarpbeetleEntity beetle;

        public WarpbeetleMoveControl(WarpbeetleEntity beetle) {
            super(beetle);
            this.beetle = beetle;
        }

        @Override
        public void tick() {
            if (this.operation == Operation.MOVE_TO && this.beetle.isFlying()) {
                this.beetle.setNoGravity(true);
                Vec3 wanted = new Vec3(this.wantedX - this.beetle.getX(), this.wantedY - this.beetle.getY(), this.wantedZ - this.beetle.getZ());
                double dist = wanted.length();
                if (dist < 0.1D) {
                    this.beetle.setDeltaMovement(this.beetle.getDeltaMovement().scale(0.5D));
                } else {
                    this.beetle.setDeltaMovement(this.beetle.getDeltaMovement().add(wanted.scale(this.speedModifier * 0.1D / dist)));
                }
                Vec3 velocity = this.beetle.getDeltaMovement();
                this.beetle.setYRot(-((float) Math.atan2(velocity.x, velocity.z)) * (180F / (float) Math.PI));
                this.beetle.yBodyRot = this.beetle.getYRot();
            } else {
                this.beetle.setNoGravity(false);
                super.tick();
            }
        }
    }

    static class WarpbeetleWanderGoal extends WaterAvoidingRandomStrollGoal {
        private final WarpbeetleEntity beetle;

        public WarpbeetleWanderGoal(WarpbeetleEntity beetle) {
            super(beetle, 1.0D);
            this.beetle = beetle;
        }

        @Override
        public boolean canUse() {
            if (this.beetle.isPassenger()) return false;
            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse();
        }

        @Override
        public void start() {
            super.start();
        }

        @Override
        public void stop() {
            super.stop();
            this.beetle.setFlying(false);
        }

        @Nullable
        @Override
        protected Vec3 getPosition() {
            RandomSource random = this.beetle.getRandom();

            if (random.nextFloat() < 0.1F) {
                Vec3 airPos = findLowFlightPos();
                if (airPos != null) {
                    this.beetle.setFlying(true);
                    return airPos;
                }
            }

            this.beetle.setFlying(false);
            return super.getPosition();
        }

        private Vec3 findLowFlightPos() {
            Vec3 view = this.beetle.getViewVector(0.0F);
            Vec3 target = HoverRandomPos.getPos(this.beetle, 8, 4, view.x, view.z, ((float) Math.PI / 2F), 2, 1);

            if (target != null) {
                if (target.y > this.beetle.getY() + 3.0D) {
                    return new Vec3(target.x, this.beetle.getY() + 3.0D, target.z);
                }
            }
            return target;
        }
    }
}