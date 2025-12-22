package com.infernalstudios.infernalexp.forge.module;

import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.registration.FlammabilityRegistry;
import com.infernalstudios.infernalexp.registration.FuelRegistry;
import com.infernalstudios.infernalexp.registration.StrippableRegistry;
import com.infernalstudios.infernalexp.registration.holders.BlockDataHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Map;

@Mod.EventBusSubscriber(modid = "infernalexp", bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockModuleForge {
    @SubscribeEvent
    public static void registerBlocks(RegisterEvent event) {
        for (Map.Entry<ResourceLocation, BlockDataHolder<?>> entry : ModBlocks.getBlockRegistry().entrySet()) {
            // Register block
            event.register(Registries.BLOCK, blockRegistryHelper ->
                    blockRegistryHelper.register(entry.getKey(), entry.getValue().get())
            );

            // Register the block items
            if (entry.getValue().hasItem()) {
                event.register(Registries.ITEM, itemRegistryHelper ->
                        itemRegistryHelper.register(entry.getKey(), entry.getValue().getBlockItem().get())
                );

                // Register Block Item Fuel
                if (entry.getValue().isFuel()) {
                    FuelRegistry.register(entry.getValue().getBlockItem().get(), entry.getValue().getFuelDuration());
                }
            }

            // Register glass pane
            if (entry.getValue().isGlass()) {
                event.register(Registries.BLOCK, blockRegistryHelper ->
                        blockRegistryHelper.register(
                                new ResourceLocation(entry.getKey().getNamespace(), entry.getKey().getPath() + "_pane"),
                                entry.getValue().getPaneBlock().get())
                );

                event.register(Registries.ITEM, itemRegistryHelper ->
                        itemRegistryHelper.register(
                                new ResourceLocation(entry.getKey().getNamespace(), entry.getKey().getPath() + "_pane"),
                                entry.getValue().getPaneBlock().getBlockItem().get())
                );
            }

            // Register Blockset Blocks and Items
            for (Map.Entry<BlockDataHolder.Model, BlockDataHolder<?>> blocksetEntry : entry.getValue().getBlocksets().entrySet()) {
                event.register(Registries.BLOCK, blockRegisterHelper ->
                        blockRegisterHelper.register(
                                new ResourceLocation(entry.getKey().getNamespace(), entry.getKey().getPath() + "_" + blocksetEntry.getKey().suffix()),
                                blocksetEntry.getValue().get()
                        )
                );

                // Register the block item
                if (entry.getValue().hasItem()) {
                    event.register(Registries.ITEM, itemRegisterHelper ->
                            itemRegisterHelper.register(
                                    new ResourceLocation(entry.getKey().getNamespace(), entry.getKey().getPath() + "_" + blocksetEntry.getKey().suffix()),
                                    blocksetEntry.getValue().getBlockItem().get()
                            )
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
