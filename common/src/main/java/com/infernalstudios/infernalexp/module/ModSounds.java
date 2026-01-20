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
    public static final RegistryObject<SoundEvent> GLOWSQUITO_SLURP = register("entity.glowsquito.slurp");

    public static final RegistryObject<SoundEvent> GLOWSILK_MOTH_AMBIENT = register("entity.glowsilk_moth.ambient");
    public static final RegistryObject<SoundEvent> GLOWSILK_MOTH_HURT = register("entity.glowsilk_moth.hurt");
    public static final RegistryObject<SoundEvent> GLOWSILK_MOTH_DEATH = register("entity.glowsilk_moth.death");

    public static final RegistryObject<SoundEvent> BLINDSIGHT_AMBIENT = register("entity.blindsight.ambient");
    public static final RegistryObject<SoundEvent> BLINDSIGHT_HURT = register("entity.blindsight.hurt");
    public static final RegistryObject<SoundEvent> BLINDSIGHT_DEATH = register("entity.blindsight.death");
    public static final RegistryObject<SoundEvent> BLINDSIGHT_LEAP = register("entity.blindsight.leap");

    public static final RegistryObject<SoundEvent> AMBIENT_GLOWSTONE_CANYON_LOOP = register("ambient.glowstone_canyon.loop");
    public static final RegistryObject<SoundEvent> AMBIENT_GLOWSTONE_CANYON_ADDITIONS = register("ambient.glowstone_canyon.additions");
    public static final RegistryObject<SoundEvent> AMBIENT_GLOWSTONE_CANYON_MOOD = register("ambient.glowstone_canyon.mood");
    public static final RegistryObject<SoundEvent> MUSIC_NETHER_GLOWSTONE_CANYON = register("music.nether.glowstone_canyon");

    public static final RegistryObject<SoundEvent> RECORD_FLUSH = register("record.flush");

    public static final RegistryObject<SoundEvent> BLOCK_DULLSTONE_BREAK = register("block.dullstone.break");
    public static final RegistryObject<SoundEvent> BLOCK_DULLSTONE_STEP = register("block.dullstone.step");
    public static final RegistryObject<SoundEvent> BLOCK_DULLSTONE_PLACE = register("block.dullstone.place");
    public static final RegistryObject<SoundEvent> BLOCK_DULLSTONE_HIT = register("block.dullstone.hit");
    public static final RegistryObject<SoundEvent> BLOCK_DULLSTONE_FALL = register("block.dullstone.fall");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(IECommon.makeID(name)));
    }

    public static void load() {
    }
}