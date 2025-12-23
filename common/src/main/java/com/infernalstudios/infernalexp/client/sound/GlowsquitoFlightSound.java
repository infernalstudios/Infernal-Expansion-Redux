package com.infernalstudios.infernalexp.client.sound;

import com.infernalstudios.infernalexp.entities.GlowsquitoEntity;
import com.infernalstudios.infernalexp.module.ModSounds;
import net.minecraft.sounds.SoundSource;

public class GlowsquitoFlightSound extends LoopingSound<GlowsquitoEntity> {

    public GlowsquitoFlightSound(GlowsquitoEntity entity) {
        super(entity, ModSounds.GLOWSQUITO_LOOP.get(), SoundSource.NEUTRAL, entity.getRandom());
    }
}