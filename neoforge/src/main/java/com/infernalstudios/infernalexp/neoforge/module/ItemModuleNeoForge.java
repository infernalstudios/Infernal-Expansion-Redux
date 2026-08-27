package com.infernalstudios.infernalexp.neoforge.module;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.module.ModItems;
import com.infernalstudios.infernalexp.registration.FuelRegistry;
import com.infernalstudios.infernalexp.registration.holders.ItemDataHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Map;

@EventBusSubscriber(modid = IEConstants.MOD_ID)
public class ItemModuleNeoForge {
    @SubscribeEvent
    public static void registerItems(RegisterEvent event) {
        for (Map.Entry<ResourceLocation, ItemDataHolder<?>> entry : ModItems.getItemRegistry().entrySet()) {
            // Register item
            event.register(Registries.ITEM, itemRegistryHelper ->
                    itemRegistryHelper.register(entry.getKey(), entry.getValue().get())
            );

            // Register Fuel
            if (entry.getValue().isFuel()) {
                FuelRegistry.register(entry.getValue().get(), entry.getValue().getFuelDuration());
            }
        }
    }

    @EventBusSubscriber(modid = IEConstants.MOD_ID)
    public static class FuelEventsNeoForge {
        @SubscribeEvent
        public static void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
            int burnTime = FuelRegistry.getCookTime(event.getItemStack().getItem());

            if (burnTime > 0) {
                event.setBurnTime(burnTime);
            }
        }
    }
}