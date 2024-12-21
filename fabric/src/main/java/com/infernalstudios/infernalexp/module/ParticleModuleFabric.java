package com.infernalstudios.infernalexp.module;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ParticleModuleFabric {
    public static void registerParticles() {
        for (Map.Entry<ResourceLocation, SimpleParticleType> entry : ModParticles.getParticleRegistry().entrySet()) {
            // Register particle
            Registry.register(BuiltInRegistries.PARTICLE_TYPE, entry.getKey(), entry.getValue());
        }
    }
}
