package com.infernalstudios.infernalexp.effect;

import com.infernalstudios.infernalexp.api.FireTypeAccess;
import com.infernalstudios.infernalexp.module.ModFireTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class LuminousEffect extends MobEffect {
    public LuminousEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.isInvertedHealAndHarm() && !entity.fireImmune()) {
            entity.setSecondsOnFire(3);
            ((FireTypeAccess) entity).infernalexp$setFireType(ModFireTypes.GLOW_FIRE);
            entity.hurt(entity.damageSources().onFire(), 1.0F);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}