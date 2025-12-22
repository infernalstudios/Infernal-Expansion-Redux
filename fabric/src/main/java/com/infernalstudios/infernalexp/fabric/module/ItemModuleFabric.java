package com.infernalstudios.infernalexp.fabric.module;

import com.infernalstudios.infernalexp.module.ModItems;
import com.infernalstudios.infernalexp.registration.FuelRegistry;
import com.infernalstudios.infernalexp.registration.holders.ItemDataHolder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ItemModuleFabric {
    public static void registerItems() {
        for (Map.Entry<ResourceLocation, ItemDataHolder<?>> entry : ModItems.getItemRegistry().entrySet()) {
            // Register item
            Registry.register(BuiltInRegistries.ITEM, entry.getKey(), entry.getValue().get());

            // Register Fuel
            if (entry.getValue().isFuel()) {
                FuelRegistry.register(entry.getValue().get(), entry.getValue().getFuelDuration());
            }
        }
    }
}
