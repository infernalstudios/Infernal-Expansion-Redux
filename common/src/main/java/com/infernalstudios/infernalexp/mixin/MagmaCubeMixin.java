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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MagmaCube.class)
public abstract class MagmaCubeMixin extends Slime implements IBucketable {
    @Unique
    private static final EntityDataAccessor<Boolean> infernalexp$FROM_BUCKET = SynchedEntityData.defineId(MagmaCube.class, EntityDataSerializers.BOOLEAN);

    public MagmaCubeMixin(EntityType<? extends Slime> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
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

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("FromBucket", this.infernalexp$isFromBucket());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.infernalexp$setFromBucket(compound.getBoolean("FromBucket"));
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (this.isTiny()) {
            return IBucketable.tryBucketEntity(player, hand, this).orElse(super.mobInteract(player, hand));
        } else {
            return super.mobInteract(player, hand);
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if (reason == MobSpawnType.BUCKET) {
            return spawnData;
        } else {
            this.setSize(0, false);
            return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        }
    }

    @Override
    public void infernalexp$copyToStack(ItemStack stack) {
        CompoundTag compound = stack.getOrCreateTag();
        IBucketable.copyDataToStack(this, stack);
        compound.putInt("Size", this.getSize());
    }

    @Override
    public void infernalexp$copyFromAdditional(CompoundTag compound) {
        IBucketable.copyDataFromAdditional(this, compound);
        if (compound.contains("Size", 99)) {
            this.setSize(compound.getInt("Size"), false);
        }
    }

    @Override
    public SoundEvent infernalexp$getBucketedSound() {
        return SoundEvents.BUCKET_FILL_LAVA;
    }

    @Override
    public ItemStack infernalexp$getBucketItem() {
        return ModItems.MAGMA_CUBE_BUCKET.get().getDefaultInstance();
    }
}