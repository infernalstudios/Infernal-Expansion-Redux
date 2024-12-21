package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.world.carver.custom.GlowstoneRavineCarver;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

import java.util.HashMap;
import java.util.Map;

public class ModCarvers {
    /** Map of all Carver Resource Keys to their Carver. */
    private static final Map<ResourceKey<WorldCarver<?>>, WorldCarver<?>> CARVER_REGISTRY = new HashMap<>();

    public static ResourceKey<WorldCarver<?>> register(String name, WorldCarver<?> parameterPoint) {
        return register(ResourceKey.create(Registries.CARVER, IECommon.id(name)), parameterPoint);
    }

    public static ResourceKey<WorldCarver<?>> register(ResourceKey<WorldCarver<?>> id, WorldCarver<?> parameterPoint) {
        CARVER_REGISTRY.put(id, parameterPoint);
        return id;
    }

    public static Map<ResourceKey<WorldCarver<?>>, WorldCarver<?>> getCarverRegistry() {
        return CARVER_REGISTRY;
    }

    // Called in the mod initializer / constructor in order to make sure that items are registered
    public static void load() {}


    public static final ResourceKey<WorldCarver<?>> GLOWSTONE_RAVINE = register("glowstone_ravine",
            GlowstoneRavineCarver.INSTANCE
    );
}
