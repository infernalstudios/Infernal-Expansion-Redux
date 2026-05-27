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
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.isInvertedHealAndHarm() && !entity.fireImmune()) {
            entity.igniteForSeconds(3.0F);
            ((FireTypeAccess) entity).infernalexp$setFireType(ModFireTypes.GLOW_FIRE);
            entity.hurt(entity.damageSources().onFire(), 1.0F);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) { // Replaces isDurationEffectTick
        return duration % 20 == 0;
    }
}