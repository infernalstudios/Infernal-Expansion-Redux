package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.registration.util.RegistrationProvider;
import com.infernalstudios.infernalexp.registration.util.RegistryObject;
import com.infernalstudios.infernalexp.world.surface.ChanceConditionSource;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class ModSurfaceRuleConditions {

    public static final RegistrationProvider<MapCodec<? extends SurfaceRules.ConditionSource>> MATERIAL_CONDITION_REGISTRY =
            RegistrationProvider.get(Registries.MATERIAL_CONDITION, IEConstants.MOD_ID);

    public static final RegistryObject<MapCodec<ChanceConditionSource>> CHANCE =
            MATERIAL_CONDITION_REGISTRY.register("chance", ChanceConditionSource.CODEC::codec);

    public static void load() {
    }

    public static SurfaceRules.ConditionSource chance(String name, float probability) {
        return new ChanceConditionSource(name, probability);
    }
}