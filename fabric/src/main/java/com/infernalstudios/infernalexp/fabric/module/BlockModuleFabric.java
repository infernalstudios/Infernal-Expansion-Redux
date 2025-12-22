package com.infernalstudios.infernalexp.fabric.module;

import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.registration.FlammabilityRegistry;
import com.infernalstudios.infernalexp.registration.FuelRegistry;
import com.infernalstudios.infernalexp.registration.StrippableRegistry;
import com.infernalstudios.infernalexp.registration.holders.BlockDataHolder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Map;

public class BlockModuleFabric {
    public static void registerBlocks() {
        for (Map.Entry<ResourceLocation, BlockDataHolder<?>> entry : ModBlocks.getBlockRegistry().entrySet()) {
            // Register block
            Registry.register(BuiltInRegistries.BLOCK, entry.getKey(), entry.getValue().get());

            // Register the block items
            if (entry.getValue().hasItem()) {
                Registry.register(BuiltInRegistries.ITEM, entry.getKey(), entry.getValue().getBlockItem().get());

                // Register Block Item Fuel
                if (entry.getValue().isFuel()) {
                    FuelRegistry.register(entry.getValue().getBlockItem().get(), entry.getValue().getFuelDuration());
                }
            }

            // Register the pane
            if (entry.getValue().isGlass()) {
                Registry.register(BuiltInRegistries.BLOCK,
                        new ResourceLocation(entry.getKey().getNamespace(), entry.getKey().getPath() + "_pane"),
                        entry.getValue().getPaneBlock().get());

                Registry.register(BuiltInRegistries.ITEM,
                        new ResourceLocation(entry.getKey().getNamespace(), entry.getKey().getPath() + "_pane"),
                        entry.getValue().getPaneBlock().getBlockItem().get());
            }

            // Register Blockset Blocks and Items
            for (Map.Entry<BlockDataHolder.Model, BlockDataHolder<?>> blocksetEntry : entry.getValue().getBlocksets().entrySet()) {
                Registry.register(BuiltInRegistries.BLOCK,
                        new ResourceLocation(entry.getKey().getNamespace(), entry.getKey().getPath() + "_" + blocksetEntry.getKey().suffix()),
                        blocksetEntry.getValue().get()
                );

                // Register the block item
                if (entry.getValue().hasItem()) {
                    Registry.register(BuiltInRegistries.ITEM,
                            new ResourceLocation(entry.getKey().getNamespace(), entry.getKey().getPath() + "_" + blocksetEntry.getKey().suffix()),
                            blocksetEntry.getValue().getBlockItem().get()
                    );
                }
            }

            // Register Block Flammabilities
            for (Map.Entry<Block, FlammabilityRegistry.Entry> flammability : entry.getValue().getFlammabilities().entrySet()) {
                FlammabilityRegistry.getRegistry(flammability.getKey()).register(entry.getValue().get(), flammability.getValue());
            }

            // Register Block Stripping
            if (entry.getValue().hasStrippingResult()) {
                StrippableRegistry.register(entry.getValue().get(), entry.getValue().getStrippingResult());
            }
        }
    }
}
