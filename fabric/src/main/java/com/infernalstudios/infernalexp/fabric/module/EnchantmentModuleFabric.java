package com.infernalstudios.infernalexp.fabric.module;

import com.infernalstudios.infernalexp.module.ModEnchantments;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentModuleFabric {

    public static void registerEnchantments() {
        for (ModEnchantments.EnchantmentRegistryObj obj : ModEnchantments.getEnchantmentRegistry().values()) {
            Enchantment enchantment = obj.getFactory().get();

            Registry.register(BuiltInRegistries.ENCHANTMENT, obj.getId(), enchantment);

            obj.setRegisteredInstance(enchantment);
        }
    }
}