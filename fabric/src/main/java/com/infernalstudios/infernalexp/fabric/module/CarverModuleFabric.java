package com.infernalstudios.infernalexp.fabric.module;

import com.infernalstudios.infernalexp.module.ModCarvers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

import java.util.Map;

public class CarverModuleFabric {
    public static void registerCarvers() {
        for (Map.Entry<ResourceKey<WorldCarver<?>>, WorldCarver<?>> entry : ModCarvers.getCarverRegistry().entrySet()) {
            // Register carver
            Registry.register(BuiltInRegistries.CARVER, entry.getKey(), entry.getValue());
        }
    }
}
