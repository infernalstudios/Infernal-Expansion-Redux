package com.infernalstudios.infernalexp.forge.potion;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.mixin.PotionBrewingInvoker;
import com.infernalstudios.infernalexp.module.ModEffects;
import com.infernalstudios.infernalexp.registration.holders.MobEffectDataHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import java.util.Map;

@Mod.EventBusSubscriber(modid = "infernalexp", bus = Mod.EventBusSubscriber.Bus.MOD)
public class PotionRecipeRegistry {
    @SubscribeEvent
    public static void registerEffects(FMLLoadCompleteEvent event) {
        for (Map.Entry<ResourceLocation, MobEffectDataHolder<?>> entry : ModEffects.getEffectRegistry().entrySet()) {
            if (entry.getValue().hasPotion()) {
                String id = entry.getKey().getPath();

                Potion basep = BuiltInRegistries.POTION.get(entry.getKey());
                Potion longp = BuiltInRegistries.POTION.get(IECommon.makeID("long_" + id));
                Potion strongp = BuiltInRegistries.POTION.get(IECommon.makeID("strong_" + id));

                PotionBrewingInvoker.invokeAddMix(Potions.AWKWARD, entry.getValue().getPotionIngredient().get(), basep);
                PotionBrewingInvoker.invokeAddMix(basep, Items.REDSTONE, longp);
                PotionBrewingInvoker.invokeAddMix(basep, Items.GLOWSTONE_DUST, strongp);
            }
        }
    }
}