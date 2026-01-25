package com.infernalstudios.infernalexp.entities;

import com.infernalstudios.infernalexp.entities.ai.glowsilkmoth.GlowsilkMothMoveControl;
import com.infernalstudios.infernalexp.entities.ai.LookAroundGoal;
import com.infernalstudios.infernalexp.entities.ai.glowsilkmoth.MothHoverGoal;
import com.infernalstudios.infernalexp.entities.ai.glowsilkmoth.MothRandomFlyGoal;
import com.infernalstudios.infernalexp.module.ModItems;
import com.infernalstudios.infernalexp.module.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GlowsilkMothEntity extends AmbientCreature implements FlyingAnimal, GeoEntity {
    private static final RawAnimation WOBBLE = RawAnimation.begin().thenLoop("wobble");
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GlowsilkMothEntity(EntityType<? extends GlowsilkMothEntity> type, Level worldIn) {
        super(type, worldIn);
        this.setNoGravity(true);
        this.moveControl = new GlowsilkMothMoveControl(this);
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

        return !(random.nextFloat() > 0.01F);
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
    public boolean fireImmune() {
        return true;
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
}