package com.infernalstudios.infernalexp.forge.compat.environmental;

import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModItems;
import com.infernalstudios.infernalexp.registration.holders.BlockDataHolder;
import com.teamabnormals.environmental.common.block.DwarfSpruceHeadBlock;
import com.teamabnormals.environmental.common.block.DwarfSprucePlantBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class EnvironmentalCompat {
    public static BlockDataHolder<?> DWARF_SPRUCE_GLOWLIGHT_TORCH;
    public static BlockDataHolder<?> DWARF_SPRUCE_PLANT_GLOWLIGHT_TORCH;

    public static void register() {
        DWARF_SPRUCE_GLOWLIGHT_TORCH = ModBlocks.register("dwarf_spruce_glowlight_torch", BlockDataHolder.of(() ->
                new DwarfSpruceHeadBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_LEAVES).lightLevel((state) -> 15), ModItems.GLOWLIGHT_TORCH::get)).cutout());

        DWARF_SPRUCE_PLANT_GLOWLIGHT_TORCH = ModBlocks.register("dwarf_spruce_plant_glowlight_torch", BlockDataHolder.of(() ->
                new DwarfSprucePlantBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_LEAVES).lightLevel((state) -> 15), ModItems.GLOWLIGHT_TORCH::get, (DwarfSpruceHeadBlock) DWARF_SPRUCE_GLOWLIGHT_TORCH.get()))).cutout();
    }
}