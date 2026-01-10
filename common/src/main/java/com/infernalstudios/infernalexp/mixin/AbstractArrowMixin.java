package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.api.AbstractArrowEntityAccess;
import com.infernalstudios.infernalexp.module.ModEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin implements AbstractArrowEntityAccess {

    @Unique
    private static final EntityDataAccessor<Boolean> infernalexp$GLOW = SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BOOLEAN);
    @Unique
    private static final EntityDataAccessor<Boolean> infernalexp$LUMINOUS = SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BOOLEAN);
    @Unique
    private static final EntityDataAccessor<Boolean> infernalexp$INFECTION = SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BOOLEAN);
    @Unique
    private static final EntityDataAccessor<Boolean> INFECTED_SOURCE = SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BOOLEAN);

    @Inject(at = @At("RETURN"), method = "defineSynchedData")
    private void infernalexp$registerData(CallbackInfo ci) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;
        arrow.getEntityData().define(infernalexp$LUMINOUS, false);
        arrow.getEntityData().define(infernalexp$INFECTION, false);
        arrow.getEntityData().define(infernalexp$GLOW, false);
        arrow.getEntityData().define(INFECTED_SOURCE, false);
    }

    @Inject(at = @At("RETURN"), method = "addAdditionalSaveData")
    private void infernalexp$writeAdditionalData(CompoundTag compound, CallbackInfo ci) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;
        compound.putBoolean("Luminous", arrow.getEntityData().get(infernalexp$LUMINOUS));
        compound.putBoolean("Infection", arrow.getEntityData().get(infernalexp$INFECTION));
        compound.putBoolean("Glow", arrow.getEntityData().get(infernalexp$GLOW));
        compound.putBoolean("InfectedSource", arrow.getEntityData().get(INFECTED_SOURCE));
    }

    @Inject(at = @At("RETURN"), method = "readAdditionalSaveData")
    private void infernalexp$readAdditionalData(CompoundTag compound, CallbackInfo ci) {
        infernalexp$setLuminous(compound.getBoolean("Luminous"));
        infernalexp$setInfection(compound.getBoolean("Infection"));
        infernalexp$setGlow(compound.getBoolean("Glow"));
        infernalexp$setInfectedSource(compound.getBoolean("InfectedSource"));
    }

    @Inject(at = @At("RETURN"), method = "setOwner")
    private void infernalexp$setShooter(Entity entityIn, CallbackInfo ci) {
        if (entityIn instanceof LivingEntity livingEntity) {
            if (livingEntity.hasEffect(ModEffects.INFECTION.get())) {
                this.infernalexp$setInfectedSource(true);
                this.infernalexp$setInfection(true);
            }
        }
    }

    @Inject(method = "onHitEntity", at = @At("TAIL"))
    private void infernalexp$onHitEntity(EntityHitResult result, CallbackInfo ci) {
        if (result.getEntity() instanceof LivingEntity livingEntity) {
            // Apply Infection if arrow has it
            if (this.infernalexp$getInfection()) {
                livingEntity.addEffect(new MobEffectInstance(ModEffects.INFECTION.get(), 600)); // 30 seconds
            }
            // Apply Luminous if arrow has it
            if (this.infernalexp$getLuminous() || this.infernalexp$getGlow()) {
                livingEntity.addEffect(new MobEffectInstance(ModEffects.LUMINOUS.get(), 600)); // 30 seconds
            }
        }
    }

    @Override
    public boolean infernalexp$getLuminous() {
        return ((AbstractArrow) (Object) this).getEntityData().get(infernalexp$LUMINOUS);
    }

    @Override
    public void infernalexp$setLuminous(boolean isLuminous) {
        ((AbstractArrow) (Object) this).getEntityData().set(infernalexp$LUMINOUS, isLuminous);
    }

    @Override
    public boolean infernalexp$getInfection() {
        return ((AbstractArrow) (Object) this).getEntityData().get(infernalexp$INFECTION);
    }

    @Override
    public void infernalexp$setInfection(boolean isInfection) {
        ((AbstractArrow) (Object) this).getEntityData().set(infernalexp$INFECTION, isInfection);
    }

    @Override
    public boolean infernalexp$getInfectedSource() {
        return ((AbstractArrow) (Object) this).getEntityData().get(INFECTED_SOURCE);
    }

    @Override
    public void infernalexp$setInfectedSource(boolean isInfectedSource) {
        ((AbstractArrow) (Object) this).getEntityData().set(INFECTED_SOURCE, isInfectedSource);
    }

    @Override
    public boolean infernalexp$getGlow() {
        return ((AbstractArrow) (Object) this).getEntityData().get(infernalexp$GLOW);
    }

    @Override
    public void infernalexp$setGlow(boolean shouldGlow) {
        ((AbstractArrow) (Object) this).getEntityData().set(infernalexp$GLOW, shouldGlow);
    }
}