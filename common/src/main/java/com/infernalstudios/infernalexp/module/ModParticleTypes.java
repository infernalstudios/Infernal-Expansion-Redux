package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.mixin.accessor.SimpleParticleTypeAccessor;
import com.infernalstudios.infernalexp.registration.util.RegistrationProvider;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;

public class ModParticleTypes {
    /**
     * Map of all Particle Resource Locations to their Suppliers.
     */
    private static final RegistrationProvider<ParticleType<?>> PARTICLE_TYPE_REGISTRY = RegistrationProvider.get(Registries.PARTICLE_TYPE, IEConstants.MOD_ID);
    public static final SimpleParticleType GLOWSTONE_SPARKLE = register("glowstone_sparkle", SimpleParticleTypeAccessor.createSimpleParticleType(false));
    public static final SimpleParticleType GLOWSQUITO_WING = register("glowsquito_wing", SimpleParticleTypeAccessor.createSimpleParticleType(false));
    public static final SimpleParticleType TONGUE_WHIP_SLASH = register("tongue_whip_slash", SimpleParticleTypeAccessor.createSimpleParticleType(false));

    public static <T extends ParticleType<?>> T register(String name, T particle) {
        PARTICLE_TYPE_REGISTRY.register(name, () -> particle);
        return particle;
    }

    public static void load() {
    }
}