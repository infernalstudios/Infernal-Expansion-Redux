package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.module.ModEffects;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityParticleMixin {

    @WrapOperation(
            method = "tickEffects",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V")
    )
    private void infernalexp$hideLuminousSwirls(Level level, ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Operation<Void> original) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.hasEffect(ModEffects.LUMINOUS.get())) {
            if (entity.getActiveEffects().size() == 1) {
                return;
            }
        }

        original.call(level, particleData, x, y, z, xSpeed, ySpeed, zSpeed);
    }
}