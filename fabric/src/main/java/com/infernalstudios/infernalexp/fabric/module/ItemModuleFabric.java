package com.infernalstudios.infernalexp.fabric.module;

import com.infernalstudios.infernalexp.module.ModItems;
import com.infernalstudios.infernalexp.registration.holders.ItemDataHolder;
import net.fabricmc.fabric.api.registry.FuelRegistry;
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
                com.infernalstudios.infernalexp.registration.FuelRegistry.register(entry.getValue().get(), entry.getValue().getFuelDuration());
            }
        }

        for (var entry : com.infernalstudios.infernalexp.registration.FuelRegistry.getRegistry().object2IntEntrySet()) {
            FuelRegistry.INSTANCE.add(entry.getKey(), entry.getIntValue());
        }
    }
}
