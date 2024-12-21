package com.infernalstudios.infernalexp.mixin.accessor;

import net.minecraft.core.particles.SimpleParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SimpleParticleType.class)
public interface SimpleParticleTypeAccessor {
    @Invoker("<init>")
    static SimpleParticleType createSimpleParticleType(boolean overrideLimit) {
        throw new UnsupportedOperationException();
    }
}
