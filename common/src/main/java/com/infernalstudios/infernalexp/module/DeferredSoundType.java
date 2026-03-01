package com.infernalstudios.infernalexp.module;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DeferredSoundType extends SoundType {
    private final Supplier<SoundEvent> breakSound;
    private final Supplier<SoundEvent> stepSound;
    private final Supplier<SoundEvent> placeSound;
    private final Supplier<SoundEvent> hitSound;
    private final Supplier<SoundEvent> fallSound;

    public DeferredSoundType(float volume, float pitch, Supplier<SoundEvent> breakSound, Supplier<SoundEvent> stepSound, Supplier<SoundEvent> placeSound, Supplier<SoundEvent> hitSound, Supplier<SoundEvent> fallSound) {
        super(volume, pitch, SoundEvents.STONE_BREAK, SoundEvents.STONE_STEP, SoundEvents.STONE_PLACE, SoundEvents.STONE_HIT, SoundEvents.STONE_FALL);
        this.breakSound = breakSound;
        this.stepSound = stepSound;
        this.placeSound = placeSound;
        this.hitSound = hitSound;
        this.fallSound = fallSound;
    }

    @Override
    public @NotNull SoundEvent getBreakSound() {
        return this.breakSound.get();
    }

    @Override
    public @NotNull SoundEvent getStepSound() {
        return this.stepSound.get();
    }

    @Override
    public @NotNull SoundEvent getPlaceSound() {
        return this.placeSound.get();
    }

    @Override
    public @NotNull SoundEvent getHitSound() {
        return this.hitSound.get();
    }

    @Override
    public @NotNull SoundEvent getFallSound() {
        return this.fallSound.get();
    }
}