package com.infernalstudios.infernalexp.compat;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.module.ModBiomes;
import com.infernalstudios.infernalexp.platform.Services;
import com.infernalstudios.infernalexp.world.surface.ModSurfaceRules;
import dev.worldgen.lithostitched.api.event.AddBiomeInjectorsEvent;
import dev.worldgen.lithostitched.api.event.AddWorldgenModifiersEvent;
import dev.worldgen.lithostitched.api.util.InjectionType;
import dev.worldgen.lithostitched.api.worldgen.biomeinjector.BiomeInjector;
import dev.worldgen.lithostitched.api.worldgen.biomeinjector.ParameterBuilder;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.DensityFunction;

public class LithostitchedCompat {
    public static final ResourceKey<DensityFunction> GLOWSTONE_CANYON_SELECTOR =
            ResourceKey.create(Registries.DENSITY_FUNCTION, IECommon.makeID("glowstone_canyon_selector"));

    public static void register() {
        AddBiomeInjectorsEvent.EVENT.register((registries, consumer) -> {
            Holder<DensityFunction> selector = registries.lookupOrThrow(Registries.DENSITY_FUNCTION).getOrThrow(GLOWSTONE_CANYON_SELECTOR);
            Holder<Biome> glowstoneCanyon = registries.lookupOrThrow(Registries.BIOME).getOrThrow(ModBiomes.GLOWSTONE_CANYON);

            consumer.accept(IECommon.makeID("glowstone_canyon"), BiomeInjector.builder(Level.NETHER)
                    .forcePlacement(glowstoneCanyon, ParameterBuilder.create().densityFunctionMin(selector, 0.35D)));
        });

        AddWorldgenModifiersEvent.EVENT.register((registries, consumer) -> {
            if (Services.PLATFORM.isModLoaded("terrablender")) return;

            consumer.accept(IECommon.makeID("nether_surface_rules"), WorldgenModifier.builder()
                    .addSurfaceRule(Level.NETHER, InjectionType.PREPEND, ModSurfaceRules.addNetherSurfaceRulesWithBedrock()));
        });
    }
}
