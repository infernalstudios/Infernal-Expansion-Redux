package com.infernalstudios.infernalexp.module;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class ModBlockProperties {

    public static final BlockBehaviour.Properties SHIMMER_SAND = BlockBehaviour.Properties.copy(Blocks.SAND)
            .mapColor(MapColor.SAND);

    public static final BlockBehaviour.Properties GLIMMER_GRAVEL = BlockBehaviour.Properties.copy(Blocks.SAND)
            .mapColor(MapColor.SAND);

    public static final BlockBehaviour.Properties GLOWLIGHT_GLASS = BlockBehaviour.Properties.copy(Blocks.GLOWSTONE)
            .noOcclusion();

    public static final BlockBehaviour.Properties SHIMMER_STONE = BlockBehaviour.Properties.copy(Blocks.STONE)
            .mapColor(MapColor.COLOR_YELLOW);

    public static final BlockBehaviour.Properties GLOWSTONE = BlockBehaviour.Properties.copy(Blocks.GLOWSTONE);

    public static BlockBehaviour.Properties DIMSTONE() {
        return BlockBehaviour.Properties.copy(Blocks.GLOWSTONE)
                .strength(1)
                .lightLevel(a -> 6)
                .mapColor(MapColor.TERRACOTTA_BROWN)
                .requiresCorrectToolForDrops()
                .sound(ModSoundTypes.DIMSTONE.get());
    }

    public static BlockBehaviour.Properties DULLSTONE() {
        return BlockBehaviour.Properties.copy(Blocks.GLOWSTONE)
                .strength(1.7f)
                .lightLevel(a -> 0)
                .mapColor(MapColor.TERRACOTTA_GRAY)
                .requiresCorrectToolForDrops()
                .sound(ModSoundTypes.DULLSTONE.get());
    }

    public static BlockBehaviour.Properties DULLSTONE_BUTTON() {
        return BlockBehaviour.Properties.copy(Blocks.GLOWSTONE)
                .strength(1.7f)
                .noCollission()
                .lightLevel(a -> a.getValue(ButtonBlock.POWERED) ? 15 : 0)
                .mapColor(MapColor.TERRACOTTA_GRAY)
                .requiresCorrectToolForDrops()
                .sound(ModSoundTypes.DULLSTONE.get());
    }

    public static BlockBehaviour.Properties DULLSTONE_PLATE() {
        return BlockBehaviour.Properties.copy(Blocks.GLOWSTONE)
                .strength(1.7f)
                .noCollission()
                .lightLevel(a -> a.getValue(PressurePlateBlock.POWERED) ? 15 : 0)
                .mapColor(MapColor.TERRACOTTA_GRAY)
                .requiresCorrectToolForDrops()
                .sound(ModSoundTypes.DULLSTONE.get());
    }

    public static final BlockBehaviour.Properties GLOWSILK_COCOON = BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK)
            .strength(2f)
            .lightLevel(a -> 15)
            .mapColor(DyeColor.WHITE)
            .requiresCorrectToolForDrops();

    public static final BlockBehaviour.Properties DULLTHORNS = BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)
            .strength(1.1f)
            .mapColor(MapColor.TERRACOTTA_BROWN)
            .noCollission()
            .noOcclusion()
            .sound(SoundType.AZALEA_LEAVES);

    public static final BlockBehaviour.Properties DULLTHORNS_BLOCK = BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)
            .strength(0.8f)
            .mapColor(MapColor.TERRACOTTA_BROWN);

    public static final BlockBehaviour.Properties BASALT_SAND = BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_BLACK)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(0.95F, 4.2F)
            .sound(SoundType.SAND);

    public static final BlockBehaviour.Properties LUMINOUS_STEM = BlockBehaviour.Properties.copy(Blocks.CRIMSON_STEM)
            .mapColor(MapColor.COLOR_YELLOW);

    public static final BlockBehaviour.Properties LUMINOUS_PLANKS = BlockBehaviour.Properties.copy(Blocks.CRIMSON_PLANKS)
            .mapColor(MapColor.COLOR_YELLOW);

}