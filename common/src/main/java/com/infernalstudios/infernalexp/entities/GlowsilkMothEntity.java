package com.infernalstudios.infernalexp.entities;

import com.infernalstudios.infernalexp.module.ModItems;
import com.infernalstudios.infernalexp.module.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

public class GlowsilkMothEntity extends AmbientCreature implements FlyingAnimal, GeoEntity {
    private static final RawAnimation WOBBLE = RawAnimation.begin().thenLoop("wobble");
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GlowsilkMothEntity(EntityType<? extends GlowsilkMothEntity> type, Level worldIn) {
        super(type, worldIn);
        this.setNoGravity(true);
        this.moveControl = new GlowsilkMothMoveControl(this);

        this.setPathfindingMalus(BlockPathTypes.LAVA, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.FLYING_SPEED, 0.7D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    public static boolean checkGlowsilkMothSpawnRules(EntityType<GlowsilkMothEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!level.getBlockState(pos.below()).isValidSpawn(level, pos.below(), entityType)) {
            return false;
        }

        if (random.nextFloat() > 0.01F) {
            return false;
        }

        return true;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MothHoverGoal(this));
        this.goalSelector.addGoal(2, new MothRandomFlyGoal(this));
        this.goalSelector.addGoal(3, new LookAroundGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "body_controller", 0, event ->
                event.setAndContinue(WOBBLE)
        ));

        controllers.add(new AnimationController<>(this, "wing_controller", 0, event ->
                event.setAndContinue(FLY)
        ));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 0.95F;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(@NotNull Entity entityIn) {
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.GLOWSILK_MOTH_AMBIENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.GLOWSILK_MOTH_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return ModSounds.GLOWSILK_MOTH_HURT.get();
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getItem() == Items.GLASS_BOTTLE) {
            player.playSound(SoundEvents.BOTTLE_FILL, 1.0F, 1.0F);
            ItemStack bottleStack = new ItemStack(ModItems.GLOWSILK_MOTH_BOTTLE.get());

            ItemStack result = IBucketable.ItemUtils.createFilledResult(itemStack, player, bottleStack, false);
            player.setItemInHand(hand, result);

            this.discard();
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level worldIn) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, worldIn) {
            @Override
            public boolean isStableDestination(@NotNull BlockPos pos) {
                return !this.level.isEmptyBlock(pos.below());
            }
        };
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    public boolean isFlying() {
        return !this.onGround();
    }

    @Override
    public @NotNull MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, @NotNull DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, @NotNull BlockState state, @NotNull BlockPos pos) {
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return !this.isInvulnerableTo(source) && super.hurt(source, amount);
    }

    @Override
    protected float getStandingEyeHeight(@NotNull Pose poseIn, EntityDimensions sizeIn) {
        return sizeIn.height * 0.5F;
    }

    static class GlowsilkMothMoveControl extends MoveControl {
        private final GlowsilkMothEntity moth;

        public GlowsilkMothMoveControl(GlowsilkMothEntity moth) {
            super(moth);
            this.moth = moth;
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 wanted = new Vec3(this.wantedX - this.moth.getX(), this.wantedY - this.moth.getY(), this.wantedZ - this.moth.getZ());
                double dist = wanted.length();

                if (this.moth.horizontalCollision && this.moth.level().getGameTime() % 20 == 0) {
                    this.moth.getNavigation().recomputePath();
                }

                if (dist < 0.5D) {
                    this.operation = MoveControl.Operation.WAIT;
                    this.moth.setDeltaMovement(this.moth.getDeltaMovement().scale(0.5D));
                    this.moth.getNavigation().stop();
                } else {
                    this.moth.setDeltaMovement(this.moth.getDeltaMovement().add(wanted.scale(this.speedModifier * 0.05D / dist)));

                    if (this.moth.getTarget() == null) {
                        Vec3 velocity = this.moth.getDeltaMovement();
                        this.moth.setYRot(-((float) Mth.atan2(velocity.x, velocity.z)) * (180F / (float) Math.PI));
                    } else {
                        double dx = this.moth.getTarget().getX() - this.moth.getX();
                        double dz = this.moth.getTarget().getZ() - this.moth.getZ();
                        this.moth.setYRot(-((float) Mth.atan2(dx, dz)) * (180F / (float) Math.PI));
                    }
                    this.moth.yBodyRot = this.moth.getYRot();
                }
            }
        }
    }

    static class MothRandomFlyGoal extends Goal {
        private final GlowsilkMothEntity moth;

        public MothRandomFlyGoal(GlowsilkMothEntity moth) {
            this.moth = moth;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return moth.getNavigation().isDone();
        }

        @Override
        public boolean canContinueToUse() {
            return moth.getNavigation().isInProgress();
        }

        @Override
        public void start() {
            RandomSource random = this.moth.getRandom();
            BlockPos randomPos = null;

            for (int i = 0; i < 10; i++) {
                BlockPos potentialPos = this.moth.blockPosition().offset(
                        random.nextInt(10) - 5,
                        random.nextInt(6) - 3,
                        random.nextInt(10) - 5
                );

                if (this.moth.level().isEmptyBlock(potentialPos)) {
                    BlockPathTypes nodeType = WalkNodeEvaluator.getBlockPathTypeStatic(this.moth.level(), potentialPos.mutable());
                    if (nodeType != BlockPathTypes.LAVA && nodeType != BlockPathTypes.DAMAGE_FIRE) {
                        randomPos = potentialPos;
                        break;
                    }
                }
            }

            if (randomPos != null) {
                this.moth.getNavigation().moveTo(randomPos.getX() + 0.5D, randomPos.getY() + 0.5D, randomPos.getZ() + 0.5D, 1.0D);
            }
        }
    }

    static class LookAroundGoal extends Goal {
        private final GlowsilkMothEntity moth;

        public LookAroundGoal(GlowsilkMothEntity moth) {
            this.moth = moth;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            if (this.moth.getTarget() == null) {
                Vec3 vector3d = this.moth.getDeltaMovement();

                if (vector3d.horizontalDistanceSqr() > 0.003D) {
                    float targetYaw = -((float) Mth.atan2(vector3d.x, vector3d.z)) * (180F / (float) Math.PI);
                    this.moth.setYRot(this.rotlerp(this.moth.getYRot(), targetYaw, 10.0F));
                    this.moth.yBodyRot = this.moth.getYRot();
                }
            }
        }

        private float rotlerp(float current, float target, float maxChange) {
            float f = Mth.wrapDegrees(target - current);
            if (f > maxChange) f = maxChange;
            if (f < -maxChange) f = -maxChange;
            return current + f;
        }
    }

    static class MothHoverGoal extends Goal {
        private final GlowsilkMothEntity moth;
        private int hoverTime;

        public MothHoverGoal(GlowsilkMothEntity moth) {
            this.moth = moth;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.moth.getNavigation().isInProgress() && this.moth.getRandom().nextFloat() < 0.015F;
        }

        @Override
        public boolean canContinueToUse() {
            return this.hoverTime > 0;
        }

        @Override
        public void start() {
            this.hoverTime = 10 + this.moth.getRandom().nextInt(20);

            this.moth.getNavigation().stop();

            this.moth.getMoveControl().setWantedPosition(
                    this.moth.getX(),
                    this.moth.getY(),
                    this.moth.getZ(),
                    0.0D
            );
        }

        @Override
        public void tick() {
            this.hoverTime--;
        }
    }
}