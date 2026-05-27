package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.api.FireType;
import net.minecraft.resources.ResourceLocation;

public class ModFireTypes {
    public static final FireType FIRE = FireType.register(ResourceLocation.withDefaultNamespace("fire"));
    public static final FireType SOUL_FIRE = FireType.register(ResourceLocation.withDefaultNamespace("soul_fire"));
    public static final FireType GLOW_FIRE = FireType.register(ResourceLocation.fromNamespaceAndPath(IEConstants.MOD_ID, "glowlight_fire"));

    public static void load() {
    }
}
