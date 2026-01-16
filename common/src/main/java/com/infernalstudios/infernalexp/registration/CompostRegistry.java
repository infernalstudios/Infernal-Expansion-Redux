package com.infernalstudios.infernalexp.registration;

import it.unimi.dsi.fastutil.objects.Object2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.world.level.ItemLike;

public class CompostRegistry {
    private static final Object2FloatMap<ItemLike> REGISTRY = new Object2FloatLinkedOpenHashMap<>();

    public static float getCompostChance(ItemLike item) {
        return REGISTRY.getOrDefault(item, 0.0f);
    }

    public static void register(ItemLike item, float chance) {
        REGISTRY.put(item, chance);
    }

    public static Object2FloatMap<ItemLike> getRegistry() {
        return REGISTRY;
    }
}