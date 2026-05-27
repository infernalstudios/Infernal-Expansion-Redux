package com.infernalstudios.infernalexp.fabric.module;

import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.registration.StrippableRegistry;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

public class BlockModuleFabric {
    public static void registerBlocks() {
        ModBlocks.registerBlocks(
                (id, block) -> Registry.register(BuiltInRegistries.BLOCK, id, block),
                (id, item) -> Registry.register(BuiltInRegistries.ITEM, id, item)
        );

        for (Map.Entry<Block, Block> entry : StrippableRegistry.getRegistry().entrySet()) {
            StrippableBlockRegistry.register(entry.getKey(), entry.getValue());
        }

        OxidizableBlocksRegistry.registerWaxableBlockPair(Blocks.GLOWSTONE, ModBlocks.WAXED_GLOWSTONE.get());
    }
}