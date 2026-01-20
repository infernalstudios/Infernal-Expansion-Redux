package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.api.AbstractArrowEntityAccess;
import com.infernalstudios.infernalexp.module.ModParticleTypes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Arrow.class)
public abstract class ArrowMixin {

    @Inject(at = @At("HEAD"), method = "makeParticle")
    private void infernalexp$spawnCustomParticles(int particleCount, CallbackInfo ci) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;

        if (arrow instanceof AbstractArrowEntityAccess access) {
            if (access.infernalexp$getLuminous() || access.infernalexp$getGlow()) {
                if (arrow.level().isClientSide && arrow.level().getRandom().nextFloat() < 0.3F) {
                    arrow.level().addParticle(
                            ModParticleTypes.GLOWSTONE_SPARKLE,
                            arrow.getRandomX(0.5D),
                            arrow.getRandomY(),
                            arrow.getRandomZ(0.5D),
                            0.0D, 0.0D, 0.0D
                    );
                }
            }
        }
    }
}