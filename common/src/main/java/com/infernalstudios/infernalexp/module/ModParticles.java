package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.mixin.accessor.SimpleParticleTypeAccessor;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ModParticles {
    /** Map of all Particle Ids to their Particle. */
    private static final Map<ResourceLocation, SimpleParticleType> PARTICLE_REGISTRY = new HashMap<>();

    public static SimpleParticleType register(String name, SimpleParticleType particle) {
        return register(IECommon.id(name), particle);
    }

    public static SimpleParticleType register(ResourceLocation id, SimpleParticleType particle) {
        PARTICLE_REGISTRY.put(id, particle);
        return particle;
    }

    public static Map<ResourceLocation, SimpleParticleType> getParticleRegistry() {
        return PARTICLE_REGISTRY;
    }

    // Called in the mod initializer / constructor in order to make sure that items are registered
    public static void load() {}

    public static final SimpleParticleType GLOWSTONE_SPARKLE = register("glowstone_sparkle", SimpleParticleTypeAccessor.createSimpleParticleType(false));

    /*public static final DefaultParticleType ZZZ = Registry.register(Registries.PARTICLE_TYPE, Phantasm.makeID("zzz"),
            FabricParticleTypes.simple());*/
}
