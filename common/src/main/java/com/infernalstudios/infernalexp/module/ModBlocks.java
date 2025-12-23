package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.block.*;
import com.infernalstudios.infernalexp.mixin.accessor.*;
import com.infernalstudios.infernalexp.registration.holders.BlockDataHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ModBlocks {
    /** Map of all Block Resource Locations to their BlockDataHolders. */
    private static final Map<ResourceLocation, BlockDataHolder<?>> BLOCK_REGISTRY = new HashMap<>();

    public static BlockDataHolder<?> register(String name, BlockDataHolder<?> blockDataHolder) {
        ResourceLocation id = IECommon.makeID(name);
        BLOCK_REGISTRY.put(id, blockDataHolder);
        return blockDataHolder;
    }

    public static Map<ResourceLocation, BlockDataHolder<?>> getBlockRegistry() {
        return BLOCK_REGISTRY;
    }

    // Called in the mod initializer / constructor in order to make sure that items are registered
    public static void load() {}


    public static final BlockDataHolder<?> SHIMMER_SAND = register("shimmer_sand", BlockDataHolder.of(() ->
                    new SandBlock(0xffffaa, BlockBehaviour.Properties.copy(Blocks.SAND)))
            .withModel(BlockDataHolder.Model.ROTATABLE).withItem().dropsSelf()
            .withTags(BlockTags.MINEABLE_WITH_SHOVEL, ModTags.Blocks.GLOW_FIRE_BASE_BLOCKS, BlockTags.INFINIBURN_NETHER,
                    ModTags.Blocks.GLOWSTONE_CANYON_CARVER_REPLACEABLES)
            .withTranslation("Shimmer Sand")
    );

    public static final BlockDataHolder<?> SHIMMER_SHEET = register("shimmer_sheet", BlockDataHolder.of(() ->
                    new LayerBlock(BlockBehaviour.Properties.copy(Blocks.SAND)))
            .withItem().withTags(BlockTags.MINEABLE_WITH_SHOVEL)
            .withTranslation("Shimmer Sheet")
    );

    public static final BlockDataHolder<?> GLIMMER_GRAVEL = register("glimmer_gravel", BlockDataHolder.of(() ->
                    new GlimmerGravelBlock(BlockBehaviour.Properties.copy(Blocks.GRAVEL)))
            .withModel(BlockDataHolder.Model.ROTATABLE).withItem().dropsSelf()
            .withTags(BlockTags.MINEABLE_WITH_SHOVEL, ModTags.Blocks.GLOW_FIRE_BASE_BLOCKS)
            .withTranslation("Glimmer Gravel")
    );

    public static final BlockDataHolder<?> GLOWLIGHT_GLASS = register("glowlight_glass", BlockDataHolder.of(() ->
                    new GlassBlock(BlockBehaviour.Properties.copy(Blocks.GLOWSTONE).noOcclusion()))
            .glass().cutout().withItem()
            .withTranslation("Glowlight Glass")
    );

    public static final BlockBehaviour.Properties shimmerstone = BlockBehaviour.Properties.copy(Blocks.STONE)
            .mapColor(MapColor.COLOR_YELLOW);

    public static final BlockDataHolder<?> SHIMMER_STONE = register("shimmer_stone", BlockDataHolder.of(() ->
                    new Block(shimmerstone))
            .withModel(BlockDataHolder.Model.CUBE).withItem().dropsSelf()
            .withTags(BlockTags.MINEABLE_WITH_PICKAXE, ModTags.Blocks.GLOW_FIRE_BASE_BLOCKS,
                    ModTags.Blocks.GLOWSTONE_CANYON_CARVER_REPLACEABLES)
            .withTranslation("Shimmer Stone")
    );

    public static final BlockDataHolder<?> SHIMMER_STONE_BRICKS = register("shimmer_stone_bricks", BlockDataHolder.of(() ->
                    new Block(shimmerstone))
            .withStairs().withSlab()
            .withModel(BlockDataHolder.Model.CUBE).withItem().dropsSelf()
            .withTags(BlockTags.MINEABLE_WITH_PICKAXE, ModTags.Blocks.GLOW_FIRE_BASE_BLOCKS)
            .withTranslation("Shimmer Stone Bricks")
    );


    public static final BlockDataHolder<?> POLISHED_GLOWSTONE = register("polished_glowstone", BlockDataHolder.of(() ->
                    new Block(BlockBehaviour.Properties.copy(Blocks.GLOWSTONE)))
            .withModel(BlockDataHolder.Model.CUBE).withItem().dropsSelf()
            .withTags(BlockTags.MINEABLE_WITH_PICKAXE, ModTags.Blocks.GLOW_FIRE_BASE_BLOCKS)
            .withTranslation("Polished Glowstone")
    );

    private static final BlockBehaviour.Properties dimstone =
            BlockBehaviour.Properties.copy(Blocks.GLOWSTONE).strength(1).lightLevel(a -> 6)
                    .mapColor(MapColor.TERRACOTTA_BROWN).requiresCorrectToolForDrops();

    public static final BlockDataHolder<?> DIMSTONE = register("dimstone", BlockDataHolder.of(() ->
                    new Block(dimstone))
            .withItem().withTags(BlockTags.MINEABLE_WITH_PICKAXE, ModTags.Blocks.GLOW_FIRE_BASE_BLOCKS,
                    ModTags.Blocks.GLOWSTONE_CANYON_CARVER_REPLACEABLES)
            .withTranslation("Dimstone")
    );

    public static final BlockDataHolder<?> POLISHED_DIMSTONE = register("polished_dimstone", BlockDataHolder.of(() ->
                    new Block(dimstone))
            .withModel(BlockDataHolder.Model.CUBE).withItem().dropsSelf()
            .withTags(BlockTags.MINEABLE_WITH_PICKAXE, ModTags.Blocks.GLOW_FIRE_BASE_BLOCKS)
            .withTranslation("Polished Dimstone")
    );

    private static final BlockBehaviour.Properties dullstone =
            BlockBehaviour.Properties.copy(Blocks.GLOWSTONE).strength(1.7f).lightLevel(a -> 0)
                    .mapColor(MapColor.TERRACOTTA_GRAY).requiresCorrectToolForDrops().sound(SoundType.STONE);

    private static final BlockBehaviour.Properties dullstoneButton =
            BlockBehaviour.Properties.copy(Blocks.GLOWSTONE).strength(1.7f).noCollission()
                    .lightLevel(a -> a.getValue(ButtonBlock.POWERED) ? 15 : 0)
                    .mapColor(MapColor.TERRACOTTA_GRAY).requiresCorrectToolForDrops().sound(SoundType.STONE);

    private static final BlockBehaviour.Properties dullstonePlate =
            BlockBehaviour.Properties.copy(Blocks.GLOWSTONE).strength(1.7f).noCollission()
                    .lightLevel(a -> a.getValue(PressurePlateBlock.POWERED) ? 15 : 0)
                    .mapColor(MapColor.TERRACOTTA_GRAY).requiresCorrectToolForDrops().sound(SoundType.STONE);

    public static final BlockDataHolder<?> DULLSTONE = register("dullstone", BlockDataHolder.of(() ->
                    new Block(dullstone))
            .withModel(BlockDataHolder.Model.CUBE).withItem()
            .withTags(BlockTags.MINEABLE_WITH_PICKAXE, ModTags.Blocks.GLOW_FIRE_BASE_BLOCKS,
                    ModTags.Blocks.GLOWSTONE_CANYON_CARVER_REPLACEABLES)
            .withTranslation("Dullstone")
    );

    public static final BlockDataHolder<?> POLISHED_DULLSTONE = register("polished_dullstone", BlockDataHolder.of(() ->
                    new Block(dullstone))
            .withModel(BlockDataHolder.Model.CUBE).withItem().dropsSelf()
            .withTags(BlockTags.MINEABLE_WITH_PICKAXE, ModTags.Blocks.GLOW_FIRE_BASE_BLOCKS)
            .withTranslation("Polished Dullstone")
    );

    public static final BlockSetType dullstoneSet = new BlockSetType("dullstone");

    public static final BlockDataHolder<?> DULLSTONE_BUTTON = register("dullstone_button", BlockDataHolder.of(() ->
                    ButtonBlockAccessor.createButtonBlock(dullstoneButton,
                            dullstoneSet, 20, false))
            .withItem().dropsSelf().withTags(BlockTags.MINEABLE_WITH_PICKAXE)
            .withTranslation("Dullstone Button")
    );

    public static final BlockDataHolder<?> DULLSTONE_PRESSURE_PLATE = register("dullstone_pressure_plate", BlockDataHolder.of(() ->
                    PressurePlateBlockAccessor.createPressurePlateBlock(PressurePlateBlock.Sensitivity.MOBS, dullstonePlate,
                            dullstoneSet))
            .withItem().dropsSelf().withTags(BlockTags.MINEABLE_WITH_PICKAXE)
            .withTranslation("Dullstone Pressure Plate")
    );


    private static final BlockBehaviour.Properties cocoon = BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK)
            .strength(2f).lightLevel(a -> 15).mapColor(DyeColor.WHITE).requiresCorrectToolForDrops();

    public static final BlockDataHolder<?> GLOWSILK_COCOON = register("glowsilk_cocoon", BlockDataHolder.of(() ->
                    new RotatedPillarBlock(cocoon))
            .withModel(BlockDataHolder.Model.PILLAR).withItem().dropsSelf()
            .withTags(BlockTags.MINEABLE_WITH_HOE, BlockTags.NEEDS_IRON_TOOL)
            .withTranslation("Glowsilk Cocoon")
    );


    public static final BlockDataHolder<?> LUMINOUS_FUNGUS = register("luminous_fungus", BlockDataHolder.of(() ->
                    new LuminousFungusBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_FUNGUS).mapColor(DyeColor.YELLOW)
                            .lightLevel(a -> a.getValue(LuminousFungusBlock.LIT) ? 15 : 0).randomTicks()))
            .cutout().withItem().dropsSelf()
            .withTranslation("Luminous Fungus")
    );

    public static final BlockDataHolder<?> LUMINOUS_FUNGUS_CAP = register("luminous_fungus_cap", BlockDataHolder.of(() ->
                    new FungusCapBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_WART_BLOCK).mapColor(DyeColor.YELLOW)
                            .lightLevel(a -> 8)))
            .withItem().dropsSelf().withTags(BlockTags.MINEABLE_WITH_HOE)
            .withTranslation("Luminous Fungus Cap")
    );

    public static final BlockDataHolder<?> CRIMSON_FUNGUS_CAP = register("crimson_fungus_cap", BlockDataHolder.of(() ->
                    new FungusCapBlock(BlockBehaviour.Properties.copy(Blocks.NETHER_WART_BLOCK)))
            .withItem().dropsSelf().withTags(BlockTags.MINEABLE_WITH_HOE)
            .withTranslation("Crimson Fungus Cap")
    );

    public static final BlockDataHolder<?> WARPED_FUNGUS_CAP = register("warped_fungus_cap", BlockDataHolder.of(() ->
                    new FungusCapBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_WART_BLOCK)))
            .withItem().dropsSelf().withTags(BlockTags.MINEABLE_WITH_HOE, BlockTags.PIGLIN_REPELLENTS)
            .withTranslation("Warped Fungus Cap")
    );

    private static final BlockBehaviour.Properties dullthorns =
            BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(1.1f)
                    .mapColor(MapColor.TERRACOTTA_BROWN).noCollission().noOcclusion().sound(SoundType.AZALEA_LEAVES);

    private static final BlockBehaviour.Properties dullthornsBlock =
            BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(0.8f)
                    .mapColor(MapColor.TERRACOTTA_BROWN);

    public static final BlockDataHolder<?> DULLTHORNS = register("dullthorns", BlockDataHolder.of(() ->
                    new DullthornsBlock(dullthorns))
            .cutout().withItem().dropsSelf()
            .withTags(BlockTags.CLIMBABLE, BlockTags.MINEABLE_WITH_AXE)
            .withTranslation("Dullthorns")
    );

    public static final BlockDataHolder<?> DULLTHORNS_BLOCK = register("dullthorns_block", BlockDataHolder.of(() ->
                    new Block(dullthornsBlock) {
                        @Override
                        public void stepOn(@NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
                            super.stepOn(world, pos, state, entity);
                            DullthornsBlock.applyEffect(entity);
                        }
                    })
            .withItem().dropsSelf()
            .withModel(BlockDataHolder.Model.ROTATABLE)
            .withTags(BlockTags.MINEABLE_WITH_AXE)
            .withTranslation("Dullthorns Block")
    );


    public static final BlockDataHolder<?> GLOWLIGHT_FIRE = register("glowlight_fire", BlockDataHolder.of(() ->
                    new GlowlightFireBlock(BlockBehaviour.Properties.copy(Blocks.FIRE), 1))
            .cutout()
            .withTags(BlockTags.FIRE)
            .withTranslation("Glowlight Fire")
    );

    public static final BlockDataHolder<?> GLOWLIGHT_TORCH = register("glowlight_torch", BlockDataHolder.of(() ->
                    TorchBlockAccessor.createTorchBlock(BlockBehaviour.Properties.copy(Blocks.FIRE), ModParticleTypes.GLOWSTONE_SPARKLE))
            .cutout()
            .withTranslation("Glowlight Torch")
    );

    public static final BlockDataHolder<?> GLOWLIGHT_WALL_TORCH = register("glowlight_wall_torch", BlockDataHolder.of(() ->
                    WallTorchBlockAccessor.createWallTorchBlock(BlockBehaviour.Properties.copy(Blocks.FIRE), ModParticleTypes.GLOWSTONE_SPARKLE))
            .cutout()
    );

    public static final BlockDataHolder<?> GLOWLIGHT_CAMPFIRE = register("glowlight_campfire", BlockDataHolder.of(() ->
                    new CampfireBlock(false, 1, BlockBehaviour.Properties.copy(Blocks.CAMPFIRE)))
            .cutout()
            .withItem()
            .withTags(BlockTags.CAMPFIRES, BlockTags.MINEABLE_WITH_AXE)
            .withTranslation("Glowlight Campfire")
    );

    public static final BlockDataHolder<?> GLOWLIGHT_LANTERN = register("glowlight_lantern", BlockDataHolder.of(() ->
                    new LanternBlock(BlockBehaviour.Properties.copy(Blocks.LANTERN)))
            .cutout().withTags(BlockTags.MINEABLE_WITH_PICKAXE)
            .withItem()
            .withTranslation("Glowlight Lantern")
    );


    public static final BlockDataHolder<?> BASALT_IRON_ORE = register("basalt_iron_ore", BlockDataHolder.of(() ->
                    new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.BASALT).strength(1.6f, 4.4f)))
            .withItem().withModel(BlockDataHolder.Model.PILLAR)
            .withTags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.IRON_ORES, BlockTags.NEEDS_STONE_TOOL)
            .withTranslation("Basalt Iron Ore")
    );


    public static final BlockDataHolder<?> SHROOMLIGHT_TEAR = register("shroomlight_tear", BlockDataHolder.of(() ->
                    new ShroomlightTearBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).instabreak().noCollission(),
                            ModTags.Blocks.SHROOMLIGHT_TEARS_GROWABLE))
            .withItem().cutout().dropsSelf()
            .withTranslation("Shroomlight Tear")
    );


    public static final BlockDataHolder<?> PLANTED_QUARTZ = register("planted_quartz", BlockDataHolder.of(() ->
                    new SupportedBlock(BlockBehaviour.Properties.copy(Blocks.QUARTZ_BLOCK).instabreak().noCollission(), () -> Items.QUARTZ))
            .cutout().dropsOther(() -> Items.QUARTZ)
            .withTranslation("Planted Quartz")
    );

    public static final BlockDataHolder<?> BURIED_BONE = register("buried_bone", BlockDataHolder.of(() ->
                    new SupportedBlock(BlockBehaviour.Properties.copy(Blocks.BONE_BLOCK).instabreak().noCollission(), () -> Items.BONE))
            .cutout().dropsOther(() -> Items.BONE)
            .withTranslation("Buried Bone")
    );


    public static final BlockDataHolder<?> QUARTZ_GLASS = register("quartz_glass", BlockDataHolder.of(() ->
                    new GlassBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).explosionResistance(6).noOcclusion()))
            .glass().cutout().withItem()
            .withTranslation("Quartz Glass")
    );


    public static final BlockDataHolder<?> BASALT_STAIRS = register("basalt_stairs", BlockDataHolder.of(() ->
            StairBlockAccessor.createStairBlock(Blocks.BASALT.defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.BASALT))))
            .withItem().dropsSelf().withModel(BlockDataHolder.Model.STAIRS).withTags(BlockTags.STAIRS)
            .withTranslation("Basalt Stairs");

    public static final BlockDataHolder<?> BASALT_SLAB = register("basalt_slab", BlockDataHolder.of(() ->
            new SlabBlock(BlockBehaviour.Properties.copy(Blocks.BASALT))))
            .withItem().dropsSelf().withModel(BlockDataHolder.Model.SLAB).withTags(BlockTags.SLABS)
            .withTranslation("Basalt Slab");

    public static final BlockDataHolder<?> BASALT_WALL = register("basalt_wall", BlockDataHolder.of(() ->
            new WallBlock(BlockBehaviour.Properties.copy(Blocks.BASALT))))
            .withItem().dropsSelf().withModel(BlockDataHolder.Model.WALL).withTags(BlockTags.WALLS)
            .withTranslation("Basalt Wall");

    private static final BlockBehaviour.Properties basaltSandProperties = BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASEDRUM)
            .strength(0.95F, 4.2F).sound(SoundType.SAND);

    public static final BlockDataHolder<?> BASALT_SAND = register("basalt_sand", BlockDataHolder.of(() ->
                    new SandBlock(0x222222, basaltSandProperties))
            .withModel(BlockDataHolder.Model.ROTATABLE).withItem().dropsSelf()
            .withTags(BlockTags.MINEABLE_WITH_SHOVEL, BlockTags.SMELTS_TO_GLASS)
            .withTranslation("Basalt Sand")
    );

    public static final BlockDataHolder<?> BASALT_SAND_SHEET = register("basalt_sand_sheet", BlockDataHolder.of(() ->
                    new LayerBlock(basaltSandProperties))
            .withItem().withTags(BlockTags.MINEABLE_WITH_SHOVEL)
            .withTranslation("Basalt Sand Sheet")
    );

    public static final BlockDataHolder<?> COBBLED_BASALT = register("cobbled_basalt", BlockDataHolder.of(() ->
                    new Block(BlockBehaviour.Properties.copy(Blocks.BASALT).strength(1.1f, 3.5f)))
            .withStairs().withSlab()
            .withModel(BlockDataHolder.Model.CUBE).withItem().dropsSelf()
            .withTags(BlockTags.MINEABLE_WITH_PICKAXE)
            .withTranslation("Cobbled Basalt")
    );

    public static final BlockDataHolder<?> BASALT_BRICKS = register("basalt_bricks", BlockDataHolder.of(() ->
                    new Block(BlockBehaviour.Properties.copy(Blocks.BASALT)))
            .withItem().dropsSelf()
            .withStairs().withSlab().withWall()
            .withModel(BlockDataHolder.Model.PILLAR)
            .withTags(BlockTags.MINEABLE_WITH_PICKAXE)
            .withTranslation("Basalt Bricks")
    );

    public static final BlockDataHolder<?> VOLATILE_GEYSER = register("volatile_geyser", BlockDataHolder.of(() ->
            new VolatileGeyserBlock(BlockBehaviour.Properties.copy(Blocks.STONE))))
            .withItem().dropsSelf()
            .withTags(BlockTags.MINEABLE_WITH_PICKAXE)
            .withTranslation("Volatile Geyser");
}