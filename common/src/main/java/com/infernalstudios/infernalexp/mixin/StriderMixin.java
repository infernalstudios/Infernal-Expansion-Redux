package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.entities.IBucketable;
import com.infernalstudios.infernalexp.module.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Strider.class)
public abstract class StriderMixin extends Animal implements IBucketable {

    @Unique
    private static final EntityDataAccessor<Boolean> infernalexp$FROM_BUCKET = SynchedEntityData.defineId(Strider.class, EntityDataSerializers.BOOLEAN);

    protected StriderMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "defineSynchedData", at = @At("HEAD"))
    private void IE_defineSynchedData(CallbackInfo ci) {
        this.entityData.define(infernalexp$FROM_BUCKET, false);
    }

    @Override
    public boolean infernalexp$isFromBucket() {
        return this.entityData.get(infernalexp$FROM_BUCKET);
    }

    @Override
    public void infernalexp$setFromBucket(boolean isFromBucket) {
        this.entityData.set(infernalexp$FROM_BUCKET, isFromBucket);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void IE_addAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        compound.putBoolean("FromBucket", this.infernalexp$isFromBucket());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void IE_readAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        this.infernalexp$setFromBucket(compound.getBoolean("FromBucket"));
    }

    @Inject(method = "mobInteract", at = @At("RETURN"), cancellable = true)
    private void IE_mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        InteractionResult result = cir.getReturnValue();
        if (this.isBaby()) {
            cir.setReturnValue(IBucketable.tryBucketEntity(player, hand, this).orElse(super.mobInteract(player, hand)));
        } else {
            cir.setReturnValue(result);
        }
    }

    @Inject(method = "finalizeSpawn", at = @At("HEAD"), cancellable = true)
    private void IE_finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag, CallbackInfoReturnable<SpawnGroupData> cir) {
        if (reason == MobSpawnType.BUCKET) {
            spawnData = new AgeableMob.AgeableMobGroupData(true);
            this.setAge(-24000);
            cir.setReturnValue(spawnData);
        }
    }

    @Override
    public void infernalexp$copyToStack(ItemStack stack) {
        IBucketable.copyDataToStack(this, stack);
        CompoundTag compound = stack.getOrCreateTag();
        compound.putInt("Age", this.getAge());
    }

    @Override
    public void infernalexp$copyFromAdditional(CompoundTag compound) {
        IBucketable.copyDataFromAdditional(this, compound);
        if (compound.contains("Age", 99)) {
            this.setAge(compound.getInt("Age"));
        }
    }

    @Override
    public SoundEvent infernalexp$getBucketedSound() {
        return SoundEvents.BUCKET_FILL_LAVA;
    }

    @Override
    public ItemStack infernalexp$getBucketItem() {
        return ModItems.STRIDER_BUCKET.get().getDefaultInstance();
    }
}