package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.IECommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantments {
    public static final ResourceKey<Enchantment> DISARMING = ResourceKey.create(Registries.ENCHANTMENT, IECommon.makeID("disarming"));
    public static final ResourceKey<Enchantment> LEAPING = ResourceKey.create(Registries.ENCHANTMENT, IECommon.makeID("leaping"));
    public static final ResourceKey<Enchantment> ILLUMINATING = ResourceKey.create(Registries.ENCHANTMENT, IECommon.makeID("illuminating"));
    public static final ResourceKey<Enchantment> LASHING = ResourceKey.create(Registries.ENCHANTMENT, IECommon.makeID("lashing"));

    public static void load() {
    }
}