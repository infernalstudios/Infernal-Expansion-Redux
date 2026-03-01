package com.infernalstudios.infernalexp.module;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

import java.util.function.Supplier;

public class ModSoundTypes {

    public static final Supplier<SoundType> DULLSTONE = () -> new DeferredSoundType(
            1.0F,
            1.0F,
            ModSounds.BLOCK_DULLSTONE_BREAK,
            ModSounds.BLOCK_DULLSTONE_STEP,
            ModSounds.BLOCK_DULLSTONE_PLACE,
            ModSounds.BLOCK_DULLSTONE_HIT,
            ModSounds.BLOCK_DULLSTONE_FALL
    );

    public static final Supplier<SoundType> DIMSTONE = () -> new DeferredSoundType(
            1.0F,
            1.0F,
            () -> SoundEvents.GLASS_BREAK,
            ModSounds.BLOCK_DULLSTONE_STEP,
            () -> SoundEvents.GLASS_PLACE,
            () -> SoundEvents.GLASS_HIT,
            () -> SoundEvents.GLASS_FALL
    );
}