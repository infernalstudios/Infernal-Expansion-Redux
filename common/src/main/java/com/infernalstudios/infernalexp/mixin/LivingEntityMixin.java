package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.module.ModEffects;
import com.infernalstudios.infernalexp.module.ModParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable {
    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Shadow
    public abstract RandomSource getRandom();

    @Shadow
    public abstract boolean randomTeleport(double $$0, double $$1, double $$2, boolean $$3);

    @Shadow
    public abstract boolean hasEffect(MobEffect $$0);

    @Inject(method = "tick", at = @At("TAIL"))
    private void infernalexp$tick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!entity.level().isClientSide && entity.hasEffect(ModEffects.LUMINOUS.get())) {
            var effectInstance = entity.getEffect(ModEffects.LUMINOUS.get());

            if (effectInstance != null) {
                if ((effectInstance.getDuration() % 10) == 0 && effectInstance.isVisible()) {

                    ((ServerLevel) entity.level()).sendParticles(
                            ModParticleTypes.GLOWSTONE_SPARKLE,
                            entity.getX(),
                            entity.getRandomY(),
                            entity.getZ(),
                            1,
                            entity.getBbWidth() / 4.0,
                            0.1,
                            entity.getBbWidth() / 4.0,
                            0.05
                    );
                }
            }
        }
    }

    @Inject(method = "hurt", at = @At("TAIL"))
    public void teleportWarped(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() || !this.hasEffect(ModEffects.WARPED.get())) return;
        if (this.level().isClientSide()) return;

        for (int i = 0; i < 16; i++) {
            double x = this.getX() + (this.getRandom().nextDouble() - 0.5) * 16.0;
            double y = Mth.clamp(
                    this.getY() + (double) (this.getRandom().nextInt(16) - 8),
                    this.level().getMinBuildHeight(),
                    this.level().getMaxBuildHeight()
            );
            double z = this.getZ() + (this.getRandom().nextDouble() - 0.5) * 16.0;

            if (this.isPassenger())
                this.stopRiding();


            Vec3 pos = this.position();
            if (this.randomTeleport(x, y, z, true)) {
                this.level().gameEvent(GameEvent.TELEPORT, pos, GameEvent.Context.of((LivingEntity) (Object) this));
                SoundEvent sound = ((LivingEntity) (Object) this) instanceof Fox ? SoundEvents.FOX_TELEPORT : SoundEvents.CHORUS_FRUIT_TELEPORT;
                this.level().playSound(null, x, y, z, sound, SoundSource.PLAYERS, 1.0F, 1.0F);
                this.playSound(sound, 1.0F, 1.0F);
                break;
            }
        }
    }
}