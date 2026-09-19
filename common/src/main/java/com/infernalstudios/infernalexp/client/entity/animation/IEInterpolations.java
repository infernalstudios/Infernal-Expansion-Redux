package com.infernalstudios.infernalexp.client.entity.animation;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.util.Mth;

public final class IEInterpolations {

    public static final AnimationChannel.Interpolation EASE_IN_OUT_SINE = (cache, delta, keyframes, current, next, scale) -> {
        float eased = -(Mth.cos((float) Math.PI * delta) - 1.0F) / 2.0F;
        return keyframes[current].target().lerp(keyframes[next].target(), eased, cache).mul(scale);
    };

    private IEInterpolations() {
    }
}
