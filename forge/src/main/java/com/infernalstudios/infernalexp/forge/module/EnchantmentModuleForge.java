package com.infernalstudios.infernalexp.forge.module;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.module.ModEnchantments;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = IEConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EnchantmentModuleForge {

    @SubscribeEvent
    public static void registerEnchantments(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.ENCHANTMENTS, helper -> {
            for (ModEnchantments.EnchantmentRegistryObj obj : ModEnchantments.getEnchantmentRegistry().values()) {
                Enchantment instance = obj.getFactory().get();
                helper.register(obj.getId(), instance);
                obj.setRegisteredInstance(instance);
            }
        });
    }
}