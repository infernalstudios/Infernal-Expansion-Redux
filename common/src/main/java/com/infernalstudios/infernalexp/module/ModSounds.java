package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.registration.util.RegistrationProvider;
import com.infernalstudios.infernalexp.registration.util.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static final RegistrationProvider<SoundEvent> SOUNDS = RegistrationProvider.get(BuiltInRegistries.SOUND_EVENT, com.infernalstudios.infernalexp.IEConstants.MOD_ID);

    public static final RegistryObject<SoundEvent> VOLINE_AMBIENT = register("entity.voline.ambient");
    public static final RegistryObject<SoundEvent> VOLINE_HURT = register("entity.voline.hurt");

    public static final RegistryObject<SoundEvent> GLOWSQUITO_HURT = register("entity.glowsquito.hurt");
    public static final RegistryObject<SoundEvent> GLOWSQUITO_DEATH = register("entity.glowsquito.death");
    public static final RegistryObject<SoundEvent> GLOWSQUITO_LOOP = register("entity.glowsquito.loop");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(IECommon.makeID(name)));
    }

    public static void load() {
    }
}