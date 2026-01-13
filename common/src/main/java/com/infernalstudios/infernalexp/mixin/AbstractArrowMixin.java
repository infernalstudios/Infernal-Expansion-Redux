package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.api.AbstractArrowEntityAccess;
import com.infernalstudios.infernalexp.module.ModEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
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

    @Inject(at = @At("RETURN"), method = "defineSynchedData")
    private void infernalexp$registerData(CallbackInfo ci) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;
        arrow.getEntityData().define(infernalexp$LUMINOUS, false);
        arrow.getEntityData().define(infernalexp$GLOW, false);
    }

    @Inject(at = @At("RETURN"), method = "addAdditionalSaveData")
    private void infernalexp$writeAdditionalData(CompoundTag compound, CallbackInfo ci) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;
        compound.putBoolean("Luminous", arrow.getEntityData().get(infernalexp$LUMINOUS));
        compound.putBoolean("Glow", arrow.getEntityData().get(infernalexp$GLOW));
    }

    @Inject(at = @At("RETURN"), method = "readAdditionalSaveData")
    private void infernalexp$readAdditionalData(CompoundTag compound, CallbackInfo ci) {
        infernalexp$setLuminous(compound.getBoolean("Luminous"));
        infernalexp$setGlow(compound.getBoolean("Glow"));
    }

    @Inject(method = "onHitEntity", at = @At("TAIL"))
    private void infernalexp$onHitEntity(EntityHitResult result, CallbackInfo ci) {
        if (result.getEntity() instanceof LivingEntity livingEntity) {
            if (this.infernalexp$getLuminous() || this.infernalexp$getGlow()) {
                livingEntity.addEffect(new MobEffectInstance(ModEffects.LUMINOUS.get(), 600));
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
    public boolean infernalexp$getGlow() {
        return ((AbstractArrow) (Object) this).getEntityData().get(infernalexp$GLOW);
    }

    @Override
    public void infernalexp$setGlow(boolean shouldGlow) {
        ((AbstractArrow) (Object) this).getEntityData().set(infernalexp$GLOW, shouldGlow);
    }
}