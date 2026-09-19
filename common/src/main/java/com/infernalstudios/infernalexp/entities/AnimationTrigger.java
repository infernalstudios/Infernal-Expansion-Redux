package com.infernalstudios.infernalexp.entities;

public final class AnimationTrigger {

    public static final int NONE = 0;

    private static final int ANIMATION_MASK = 0xFF;
    private static final int TIME_MASK = 0xFFFFFF;
    private static final int TIME_SHIFT = 8;

    private AnimationTrigger() {
    }

    public static int pack(int animation, long gameTime) {
        return ((int) (gameTime & TIME_MASK) << TIME_SHIFT) | (animation & ANIMATION_MASK);
    }

    public static int animation(int packed) {
        return packed & ANIMATION_MASK;
    }

    public static int elapsedTicks(int packed, long gameTime) {
        return (int) ((gameTime - (packed >>> TIME_SHIFT)) & TIME_MASK);
    }
}
