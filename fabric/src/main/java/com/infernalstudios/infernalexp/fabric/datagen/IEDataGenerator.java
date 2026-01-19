package com.infernalstudios.infernalexp.fabric.datagen;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.compat.NetherExpCompat;
import com.infernalstudios.infernalexp.config.IEConfig;
import com.infernalstudios.infernalexp.module.*;
import com.infernalstudios.infernalexp.registration.holders.BlockDataHolder;
import com.infernalstudios.infernalexp.registration.holders.EntityTypeDataHolder;
import com.infernalstudios.infernalexp.registration.holders.ItemDataHolder;
import com.infernalstudios.infernalexp.registration.holders.MobEffectDataHolder;
import com.infernalstudios.infernalexp.world.carver.ModConfiguredCarvers;
import com.infernalstudios.infernalexp.world.feature.ModConfiguredFeatures;
import com.infernalstudios.infernalexp.world.feature.ModPlacedFeatures;
import com.infernalstudios.infernalexp.world.structure.ModProcessorLists;
import com.infernalstudios.infernalexp.world.structure.ModStructurePools;
import com.infernalstudios.infernalexp.world.structure.ModStructures;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.infernalstudios.infernalexp.IEConstants.MOD_ID;

public class IEDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(IEWorldGenProvider::new);
        pack.addProvider(IEBlockTagProvider::new);
        pack.addProvider(IEItemTagProvider::new);
        pack.addProvider(IEBlockLootTableProvider::new);
        pack.addProvider(IEModelProvider::new);
        pack.addProvider(IELangProvider::new);
        pack.addProvider(IERecipeProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder builder) {
        builder.add(Registries.BIOME, ModBiomes::bootstrap);

        builder.add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap);
        builder.add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap);
        builder.add(Registries.CONFIGURED_CARVER, ModConfiguredCarvers::bootstrap);

        builder.add(Registries.PROCESSOR_LIST, ModProcessorLists::bootstrap);
        builder.add(Registries.TEMPLATE_POOL, ModStructurePools::bootstrap);
        builder.add(Registries.STRUCTURE, ModStructures::bootstrapStructures);
        builder.add(Registries.STRUCTURE_SET, ModStructures::bootstrapSets);
    }


    private static class IEWorldGenProvider extends FabricDynamicRegistryProvider {
        public IEWorldGenProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(HolderLookup.Provider registries, Entries entries) {
            entries.addAll(registries.lookupOrThrow(Registries.BIOME));
            entries.addAll(registries.lookupOrThrow(Registries.CONFIGURED_FEATURE));
            entries.addAll(registries.lookupOrThrow(Registries.PLACED_FEATURE));
            entries.addAll(registries.lookupOrThrow(Registries.CONFIGURED_CARVER));

            entries.addAll(registries.lookupOrThrow(Registries.PROCESSOR_LIST));
            entries.addAll(registries.lookupOrThrow(Registries.TEMPLATE_POOL));
            entries.addAll(registries.lookupOrThrow(Registries.STRUCTURE));
            entries.addAll(registries.lookupOrThrow(Registries.STRUCTURE_SET));
        }

        @Override
        public @NotNull String getName() {
            return "World Gen";
        }
    }

    private static class IERecipeProvider extends FabricRecipeProvider {
        public IERecipeProvider(FabricDataOutput output) {
            super(output);
        }

        private static String getName(ItemLike item) {
            return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
        }

        private static void offerStonecutting(Consumer<FinishedRecipe> exporter, ItemLike input, ItemLike output) {
            offerStonecutting(exporter, input, output, 1);
        }

        private static void offerStonecutting(Consumer<FinishedRecipe> exporter, ItemLike input, ItemLike output, int count) {
            SingleItemRecipeBuilder.stonecutting(Ingredient.of(input), RecipeCategory.BUILDING_BLOCKS, output, count)
                    .unlockedBy(getHasName(input), has(input))
                    .save(exporter, IECommon.makeID(getName(output) + "_from_" + getName(input) + "_stonecutting"));
        }

        private static void offerTilesRecipe(Consumer<FinishedRecipe> exporter, ItemLike result, int count,
                                             ItemLike a, ItemLike b) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result, count)
                    .pattern("AB")
                    .pattern("BA")
                    .define('A', a)
                    .define('B', b)
                    .unlockedBy(getHasName(a), has(a))
                    .unlockedBy(getHasName(b), has(b))
                    .group(getName(result))
                    .save(exporter, IECommon.makeID(getName(result)));
        }

        private static void offer2x2Recipe(Consumer<FinishedRecipe> exporter, ItemLike to, int count,
                                           ItemLike from) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, to, count)
                    .pattern("##")
                    .pattern("##")
                    .define('#', from)
                    .unlockedBy(getHasName(from), has(from))
                    .group(getName(to))
                    .save(exporter, IECommon.makeID(getName(to)));
        }

        private static void offer3x3Recipe(Consumer<FinishedRecipe> exporter, ItemLike to, int count,
                                           ItemLike from) {
            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, to, count)
                    .requires(from, 9)
                    .unlockedBy(getHasName(from), has(from))
                    .group(getName(to))
                    .save(exporter, IECommon.makeID(getName(to)));
        }

        private static void offerUnpackRecipe(Consumer<FinishedRecipe> exporter, ItemLike to, int count, ItemLike from) {
            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, to, count)
                    .requires(from)
                    .unlockedBy(getHasName(from), has(from))
                    .group(getName(to))
                    .save(exporter, IECommon.makeID(getName(to)));
        }

        @Override
        public void buildRecipes(Consumer<FinishedRecipe> exporter) {
            for (BlockDataHolder<?> block : ModBlocks.getBlockRegistry().values()) {
                if (block.getStairs() != null) {
                    stairBuilder(block.getStairs().get(), Ingredient.of(block.get()))
                            .group(getName(block.getStairs().get()))
                            .unlockedBy(getHasName(block.get()), has(block.get()))
                            .save(exporter, IECommon.makeID(getName(block.getStairs().get())));
                }
                if (block.getSlab() != null) {
                    slabBuilder(RecipeCategory.BUILDING_BLOCKS, block.getSlab().get(), Ingredient.of(block.get()))
                            .group(getName(block.getSlab().get()))
                            .unlockedBy(getHasName(block.get()), has(block.get()))
                            .save(exporter, IECommon.makeID(getName(block.getSlab().get())));
                }
                if (block.getWall() != null) {
                    wallBuilder(RecipeCategory.BUILDING_BLOCKS, block.getWall().get(), Ingredient.of(block.get()))
                            .group(getName(block.getWall().get()))
                            .unlockedBy(getHasName(block.get()), has(block.get()))
                            .save(exporter, IECommon.makeID(getName(block.getWall().get())));
                }
                if (block.getFence() != null) {
                    fenceBuilder(block.getFence().get(), Ingredient.of(block.get()))
                            .group(getName(block.getFence().get()))
                            .unlockedBy(getHasName(block.get()), has(block.get()))
                            .save(exporter, IECommon.makeID(getName(block.getFence().get())));
                }
                if (block.getFenceGate() != null) {
                    fenceGateBuilder(block.getFenceGate().get(), Ingredient.of(block.get()))
                            .group(getName(block.getFenceGate().get()))
                            .unlockedBy(getHasName(block.get()), has(block.get()))
                            .save(exporter, IECommon.makeID(getName(block.getFenceGate().get())));
                }
                if (block.getButton() != null) {
                    buttonBuilder(block.getButton().get(), Ingredient.of(block.get()))
                            .group(getName(block.getButton().get()))
                            .unlockedBy(getHasName(block.get()), has(block.get()))
                            .save(exporter, IECommon.makeID(getName(block.getButton().get())));
                }
                if (block.getPressurePlate() != null) {
                    pressurePlateBuilder(RecipeCategory.REDSTONE, block.getPressurePlate().get(), Ingredient.of(block.get()))
                            .group(getName(block.getPressurePlate().get()))
                            .unlockedBy(getHasName(block.get()), has(block.get()))
                            .save(exporter, IECommon.makeID(getName(block.getPressurePlate().get())));
                }
                if (block.getPaneBlock() != null) {
                    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, block.getPaneBlock().get(), 16)
                            .pattern("###")
                            .pattern("###")
                            .define('#', block.get())
                            .group(getName(block.getPaneBlock().get()))
                            .unlockedBy("has_glass", has(block.get()))
                            .save(exporter, IECommon.makeID(getName(block.getPaneBlock().get())));
                }
            }

            offerStonecutting(exporter, ModBlocks.SHIMMER_STONE.get(), ModBlocks.SHIMMER_STONE.getStairs().get());
            offerStonecutting(exporter, ModBlocks.SHIMMER_STONE.get(), ModBlocks.SHIMMER_STONE.getSlab().get(), 2);
            offerStonecutting(exporter, ModBlocks.SHIMMER_STONE.get(), ModBlocks.SHIMMER_STONE.getWall().get());
            offerStonecutting(exporter, ModBlocks.SHIMMER_STONE.get(), ModBlocks.SHIMMER_STONE_BRICKS.get());
            offerStonecutting(exporter, ModBlocks.SHIMMER_STONE.get(), ModBlocks.SHIMMER_STONE_BRICKS.getStairs().get());
            offerStonecutting(exporter, ModBlocks.SHIMMER_STONE.get(), ModBlocks.SHIMMER_STONE_BRICKS.getSlab().get(), 2);
            offerStonecutting(exporter, ModBlocks.SHIMMER_STONE.get(), ModBlocks.SHIMMER_STONE_BRICKS.getWall().get());
            offerStonecutting(exporter, ModBlocks.SHIMMER_STONE.get(), ModBlocks.CHISELED_SHIMMER_STONE_BRICKS.get());

            offerStonecutting(exporter, ModBlocks.COBBLED_BASALT.get(), ModBlocks.COBBLED_BASALT.getStairs().get());
            offerStonecutting(exporter, ModBlocks.COBBLED_BASALT.get(), ModBlocks.COBBLED_BASALT.getSlab().get(), 2);
            offerStonecutting(exporter, ModBlocks.COBBLED_BASALT.get(), ModBlocks.COBBLED_BASALT.getWall().get());
            offerStonecutting(exporter, ModBlocks.COBBLED_BASALT.get(), ModBlocks.BASALT_BRICKS.get());
            offerStonecutting(exporter, ModBlocks.COBBLED_BASALT.get(), ModBlocks.BASALT_BRICKS.getStairs().get());
            offerStonecutting(exporter, ModBlocks.COBBLED_BASALT.get(), ModBlocks.BASALT_BRICKS.getSlab().get(), 2);
            offerStonecutting(exporter, ModBlocks.COBBLED_BASALT.get(), ModBlocks.BASALT_BRICKS.getWall().get());
            offerStonecutting(exporter, ModBlocks.COBBLED_BASALT.get(), ModBlocks.CHISELED_BASALT_BRICKS.get());

            offerStonecutting(exporter, ModBlocks.SHIMMER_STONE_BRICKS.get(), ModBlocks.SHIMMER_STONE_BRICKS.getStairs().get());
            offerStonecutting(exporter, ModBlocks.SHIMMER_STONE_BRICKS.get(), ModBlocks.SHIMMER_STONE_BRICKS.getSlab().get(), 2);
            offerStonecutting(exporter, ModBlocks.SHIMMER_STONE_BRICKS.get(), ModBlocks.SHIMMER_STONE_BRICKS.getWall().get());
            offerStonecutting(exporter, ModBlocks.SHIMMER_STONE_BRICKS.get(), ModBlocks.CHISELED_SHIMMER_STONE_BRICKS.get());

            offerStonecutting(exporter, ModBlocks.BASALT_BRICKS.get(), ModBlocks.BASALT_BRICKS.getStairs().get());
            offerStonecutting(exporter, ModBlocks.BASALT_BRICKS.get(), ModBlocks.BASALT_BRICKS.getSlab().get(), 2);
            offerStonecutting(exporter, ModBlocks.BASALT_BRICKS.get(), ModBlocks.BASALT_BRICKS.getWall().get());
            offerStonecutting(exporter, ModBlocks.BASALT_BRICKS.get(), ModBlocks.CHISELED_BASALT_BRICKS.get());

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_SHIMMER_STONE_BRICKS.get())
                    .pattern("#")
                    .pattern("#")
                    .define('#', ModBlocks.SHIMMER_STONE_BRICKS.getSlab().get())
                    .unlockedBy(getHasName(ModBlocks.SHIMMER_STONE_BRICKS.getSlab().get()), has(ModBlocks.SHIMMER_STONE_BRICKS.getSlab().get()))
                    .save(exporter, IECommon.makeID("chiseled_shimmer_stone_bricks_from_slabs"));

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_BASALT_BRICKS.get())
                    .pattern("#")
                    .pattern("#")
                    .define('#', ModBlocks.BASALT_BRICKS.getSlab().get())
                    .unlockedBy(getHasName(ModBlocks.BASALT_BRICKS.getSlab().get()), has(ModBlocks.BASALT_BRICKS.getSlab().get()))
                    .save(exporter, IECommon.makeID("chiseled_basalt_bricks_from_slabs"));

            ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.GLOWSILK_BOW.get())
                    .pattern(" /#")
                    .pattern("/ #")
                    .pattern(" /#")
                    .define('#', ModItems.GLOWSILK_STRING.get())
                    .define('/', Items.STICK)
                    .unlockedBy("has_glowsilk_string", has(ModItems.GLOWSILK_STRING.get()))
                    .save(exporter, IECommon.makeID(getName(ModItems.GLOWSILK_BOW.get())));

            offer2x2Recipe(exporter, ModBlocks.SHIMMER_STONE_BRICKS.get(), 4, ModBlocks.SHIMMER_STONE.get());

            buttonBuilder(ModBlocks.DULLSTONE_BUTTON.get(), Ingredient.of(ModBlocks.POLISHED_DULLSTONE.get()))
                    .group(getName(ModBlocks.DULLSTONE_BUTTON.get()))
                    .unlockedBy(getHasName(ModBlocks.POLISHED_DULLSTONE.get()), has(ModBlocks.POLISHED_DULLSTONE.get()))
                    .save(exporter, IECommon.makeID(getName(ModBlocks.DULLSTONE_BUTTON.get())));
            pressurePlateBuilder(RecipeCategory.REDSTONE, ModBlocks.DULLSTONE_PRESSURE_PLATE.get(), Ingredient.of(ModBlocks.POLISHED_DULLSTONE.get()))
                    .group(getName(ModBlocks.DULLSTONE_PRESSURE_PLATE.get()))
                    .unlockedBy(getHasName(ModBlocks.POLISHED_DULLSTONE.get()), has(ModBlocks.POLISHED_DULLSTONE.get()))
                    .save(exporter, IECommon.makeID(getName(ModBlocks.DULLSTONE_PRESSURE_PLATE.get())));

            offer2x2Recipe(exporter, ModBlocks.POLISHED_GLOWSTONE.get(), 4, Blocks.GLOWSTONE);

            offerTilesRecipe(exporter, ModBlocks.DIMSTONE.get(), 1, ModItems.DULLROCKS.get(), Items.GLOWSTONE_DUST);
            offer2x2Recipe(exporter, ModBlocks.POLISHED_DIMSTONE.get(), 4, ModBlocks.DIMSTONE.get());

            offer2x2Recipe(exporter, ModBlocks.DULLSTONE.get(), 1, ModItems.DULLROCKS.get());
            offer2x2Recipe(exporter, ModBlocks.POLISHED_DULLSTONE.get(), 4, ModBlocks.DULLSTONE.get());

            offer2x2Recipe(exporter, ModBlocks.DULLTHORNS_BLOCK.get(), 1, ModBlocks.DULLTHORNS.get());

            offer3x3Recipe(exporter, ModBlocks.CRIMSON_FUNGUS_CAP.get(), 1, Items.CRIMSON_FUNGUS);
            offerUnpackRecipe(exporter, Items.CRIMSON_FUNGUS, 9, ModBlocks.CRIMSON_FUNGUS_CAP.get());
            offer3x3Recipe(exporter, ModBlocks.WARPED_FUNGUS_CAP.get(), 1, Items.WARPED_FUNGUS);
            offerUnpackRecipe(exporter, Items.WARPED_FUNGUS, 9, ModBlocks.WARPED_FUNGUS_CAP.get());

            oreSmelting(exporter, List.of(ModBlocks.BASALT_IRON_ORE.get()), RecipeCategory.MISC, Items.IRON_ORE,
                    5, 200, "basalt_iron_ore");
            oreBlasting(exporter, List.of(ModBlocks.BASALT_IRON_ORE.get()), RecipeCategory.MISC, Items.IRON_ORE,
                    5, 100, "basalt_iron_ore");

            offer3x3Recipe(exporter, Blocks.SHROOMLIGHT, 1, ModBlocks.SHROOMLIGHT_TEAR.get());
            if (NetherExpCompat.SHROOMNIGHT_TEAR != null) {
                offer3x3Recipe(exporter,
                        BuiltInRegistries.BLOCK.get(new ResourceLocation("netherexp", "shroomnight")),
                        1,
                        NetherExpCompat.SHROOMNIGHT_TEAR.get()
                );
            }
            offer2x2Recipe(exporter, ModBlocks.GLOWSILK_COCOON.get(), 1, ModItems.GLOWSILK_STRING.get());
            offerUnpackRecipe(exporter, ModItems.GLOWSILK_STRING.get(), 4, ModBlocks.GLOWSILK_COCOON.get());

            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LUMINOUS_PLANKS.get(), 4)
                    .requires(ModBlocks.LUMINOUS_STEM.get())
                    .unlockedBy(getHasName(ModBlocks.LUMINOUS_STEM.get()), has(ModBlocks.LUMINOUS_STEM.get()))
                    .group("luminous_planks")
                    .save(exporter, IECommon.makeID("luminous_planks_from_stem"));

            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LUMINOUS_PLANKS.get(), 4)
                    .requires(ModBlocks.STRIPPED_LUMINOUS_STEM.get())
                    .unlockedBy(getHasName(ModBlocks.STRIPPED_LUMINOUS_STEM.get()), has(ModBlocks.STRIPPED_LUMINOUS_STEM.get()))
                    .group("luminous_planks")
                    .save(exporter, IECommon.makeID("luminous_planks_from_stripped_stem"));

            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LUMINOUS_PLANKS.get(), 4)
                    .requires(ModBlocks.LUMINOUS_HYPHAE.get())
                    .unlockedBy(getHasName(ModBlocks.LUMINOUS_HYPHAE.get()), has(ModBlocks.LUMINOUS_HYPHAE.get()))
                    .group("luminous_planks")
                    .save(exporter, IECommon.makeID("luminous_planks_from_hyphae"));

            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LUMINOUS_PLANKS.get(), 4)
                    .requires(ModBlocks.STRIPPED_LUMINOUS_HYPHAE.get())
                    .unlockedBy(getHasName(ModBlocks.STRIPPED_LUMINOUS_HYPHAE.get()), has(ModBlocks.STRIPPED_LUMINOUS_HYPHAE.get()))
                    .group("luminous_planks")
                    .save(exporter, IECommon.makeID("luminous_planks_from_stripped_hyphae"));

            offer2x2Recipe(exporter, ModBlocks.LUMINOUS_HYPHAE.get(), 3, ModBlocks.LUMINOUS_STEM.get());
            offer2x2Recipe(exporter, ModBlocks.STRIPPED_LUMINOUS_HYPHAE.get(), 3, ModBlocks.STRIPPED_LUMINOUS_STEM.get());

            doorBuilder(ModBlocks.LUMINOUS_DOOR.get(), Ingredient.of(ModBlocks.LUMINOUS_PLANKS.get()))
                    .group("luminous_door")
                    .unlockedBy(getHasName(ModBlocks.LUMINOUS_PLANKS.get()), has(ModBlocks.LUMINOUS_PLANKS.get()))
                    .save(exporter, IECommon.makeID("luminous_door"));

            trapdoorBuilder(ModBlocks.LUMINOUS_TRAPDOOR.get(), Ingredient.of(ModBlocks.LUMINOUS_PLANKS.get()))
                    .group("luminous_trapdoor")
                    .unlockedBy(getHasName(ModBlocks.LUMINOUS_PLANKS.get()), has(ModBlocks.LUMINOUS_PLANKS.get()))
                    .save(exporter, IECommon.makeID("luminous_trapdoor"));

            offer3x3Recipe(exporter, ModBlocks.LUMINOUS_FUNGUS_CAP.get(), 1, ModBlocks.LUMINOUS_FUNGUS.get());
            offerUnpackRecipe(exporter, ModBlocks.LUMINOUS_FUNGUS.get(), 9, ModBlocks.LUMINOUS_FUNGUS_CAP.get());

            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.GLOWLIGHT_TORCH.get(), 4)
                    .pattern("A")
                    .pattern("B")
                    .define('A', ModItems.GLOWCOKE.get())
                    .define('B', Items.STICK)
                    .unlockedBy("has_glowcoke", has(ModItems.GLOWCOKE.get()))
                    .save(exporter, IECommon.makeID("glowlight_torch"));

            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.GLOWLIGHT_CAMPFIRE.get())
                    .pattern(" S ")
                    .pattern("SAS")
                    .pattern("LLL")
                    .define('S', Items.STICK)
                    .define('A', ModItems.GLOWCOKE.get())
                    .define('L', ItemTags.LOGS)
                    .unlockedBy("has_glowcoke", has(ModItems.GLOWCOKE.get()))
                    .save(exporter, IECommon.makeID("glowlight_campfire"));

            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.GLOWLIGHT_LANTERN.get())
                    .pattern("AAA")
                    .pattern("ABA")
                    .pattern("AAA")
                    .define('A', Items.IRON_NUGGET)
                    .define('B', ModItems.GLOWLIGHT_TORCH.get())
                    .unlockedBy("has_glowlight_torch", has(ModItems.GLOWLIGHT_TORCH.get()))
                    .save(exporter, IECommon.makeID("glowlight_lantern"));

            oreSmelting(exporter, List.of(ModBlocks.COBBLED_BASALT.get()), RecipeCategory.BUILDING_BLOCKS, Blocks.BASALT,
                    0.1f, 200, "basalt");
            offer2x2Recipe(exporter, ModBlocks.BASALT_BRICKS.get(), 4, Blocks.BASALT);

            stairBuilder(ModBlocks.BASALT_STAIRS.get(), Ingredient.of(Blocks.BASALT))
                    .group("basalt_stairs")
                    .unlockedBy("has_basalt", has(Blocks.BASALT))
                    .save(exporter, IECommon.makeID("basalt_stairs"));

            slabBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BASALT_SLAB.get(), Ingredient.of(Blocks.BASALT))
                    .group("basalt_slab")
                    .unlockedBy("has_basalt", has(Blocks.BASALT))
                    .save(exporter, IECommon.makeID("basalt_slab"));

            wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BASALT_WALL.get(), Ingredient.of(Blocks.BASALT))
                    .group("basalt_wall")
                    .unlockedBy("has_basalt", has(Blocks.BASALT))
                    .save(exporter, IECommon.makeID("basalt_wall"));

            offerStonecutting(exporter, Blocks.BASALT, ModBlocks.BASALT_STAIRS.get());
            offerStonecutting(exporter, Blocks.BASALT, ModBlocks.BASALT_SLAB.get());
            offerStonecutting(exporter, Blocks.BASALT, ModBlocks.BASALT_WALL.get());

            oreSmelting(exporter, List.of(ModBlocks.SHIMMER_SAND.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.GLOWLIGHT_GLASS.get(),
                    0.1f, 200, "glowlight_glass");

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.QUARTZ_GLASS.get())
                    .pattern(" Q ")
                    .pattern("QGQ")
                    .pattern(" Q ")
                    .define('Q', Items.QUARTZ)
                    .define('G', Items.GLASS)
                    .unlockedBy(getHasName(ModBlocks.QUARTZ_GLASS.get()), has(ModBlocks.QUARTZ_GLASS.get()))
                    .save(exporter, IECommon.makeID("quartz_glass"));
        }
    }

    private static class IELangProvider extends FabricLanguageProvider {
        protected IELangProvider(FabricDataOutput dataOutput) {
            super(dataOutput);
        }

        @Override
        public void generateTranslations(TranslationBuilder builder) {
            // Put manually added entries here
            builder.add(ModCreativeTabs.INFERNAL_EXPANSION_TAB.getResourceKey(), "Infernal Expansion");
            generateConfigTranslations(builder);

            // Tag Translations
            builder.add("tag.item.infernalexp.voline_food", "Voline Food");

            // Autumnity Compat
            builder.add("block.infernalexp.glowlight_jack_o_lantern",
                    "Glowlight Jack o'Lantern");
            builder.add("block.infernalexp.large_glowlight_jack_o_lantern_slice",
                    "Large Glowlight Jack o'Lantern Slice");

            // Geyser Tooltips
            builder.add("text.autoconfig.infernalexp.option.common.geyser.geyserSteamHeight.@Tooltip",
                    "Determines the maximum height of the steam particles produced by the geyser.");

            // Voline Tooltips
            builder.add("text.autoconfig.infernalexp.option.common.voline.volineTurnIntoGeyser.@Tooltip",
                    "If enabled, sleeping Volines will transform into Volatile Geysers when hit with a snowball.");

            builder.add("text.autoconfig.infernalexp.option.common.voline.volineSleepWhenFed.@Tooltip",
                    "If enabled, Volines will fall asleep after eating Magma Cream.");

            builder.add("text.autoconfig.infernalexp.option.common.voline.volineGetBig.@Tooltip",
                    "If enabled, Volines will grow in size when they eat Magma Cream.");

            builder.add("text.autoconfig.infernalexp.option.common.voline.volineMagmaCreamAmount.@Tooltip",
                    "Determines the amount of Magma Cream a Voline needs to eat to grow.");

            // Biome Translations
            builder.add("biome.infernalexp.glowstone_canyon", "Glowstone Canyon");

            // Music
            builder.add("item.infernalexp.music_disc_flush.desc", "LudoCrypt - Flush");

            // This handles all supplied block and item entries automatically
            for (BlockDataHolder<?> blockDataHolder : ModBlocks.getBlockRegistry().values()) {
                if (blockDataHolder.hasTranslation()) {
                    builder.add(blockDataHolder.get(), blockDataHolder.getTranslation());
                }

                if (blockDataHolder.isGlass()) {
                    builder.add(blockDataHolder.getPaneBlock().get(), blockDataHolder.getTranslation() + " Pane");
                }

                for (Map.Entry<BlockDataHolder.Model, BlockDataHolder<?>> blocksetEntry : blockDataHolder.getBlocksets().entrySet()) {
                    if (blockDataHolder.hasTranslation()) {
                        String translation = blockDataHolder.getTranslation();
                        if (translation.endsWith(" Bricks")) {
                            translation = translation.substring(0, translation.length() - 1);
                        } else if (translation.endsWith(" Planks")) {
                            translation = translation.substring(0, translation.length() - 7);
                        }
                        builder.add(blocksetEntry.getValue().get(), translation + " " + blocksetEntry.getKey().getLang());
                    }
                }
            }

            for (ItemDataHolder<?> itemDataHolder : ModItems.getItemRegistry().values()) {
                if (itemDataHolder.hasTranslation()) {
                    builder.add(itemDataHolder.get(), itemDataHolder.getTranslation());
                }
            }

            for (EntityTypeDataHolder<?> entityTypeDataHolder : ModEntityTypes.getEntityTypeRegistry().values()) {
                if (entityTypeDataHolder.hasTranslation()) {
                    builder.add(entityTypeDataHolder.get(), entityTypeDataHolder.getTranslation());
                }
            }

            for (Map.Entry<ResourceLocation, MobEffectDataHolder<?>> entry : ModEffects.getEffectRegistry().entrySet()) {
                if (entry.getValue().hasTranslation()) {
                    builder.add(entry.getValue().get(), entry.getValue().getTranslation());

                    if (entry.getValue().hasPotion()) {
                        String id = entry.getKey().getPath();

                        builder.add("item.minecraft.potion.effect." + id, "Potion of " + entry.getValue().getTranslation());
                        builder.add("item.minecraft.splash_potion.effect." + id, "Splash Potion of " + entry.getValue().getTranslation());
                        builder.add("item.minecraft.lingering_potion.effect." + id, "Lingering Potion of " + entry.getValue().getTranslation());
                        builder.add("item.minecraft.tipped_arrow.effect." + id, "Arrow of " + entry.getValue().getTranslation());
                    }
                }
            }
        }

        private void generateConfigTranslations(TranslationBuilder builder) {
            String baseKey = "text.autoconfig." + MOD_ID;

            builder.add(baseKey + ".title", "Infernal Expansion Config");

            for (Field field : IEConfig.class.getDeclaredFields()) {
                if (isValidConfigField(field)) {
                    String name = field.getName();
                    String categoryKey = baseKey + ".category." + name;
                    builder.add(categoryKey, toHumanReadable(name) + " Settings");

                    processConfigNested(builder, field.getType(), baseKey + ".option." + name);
                }
            }
        }

        private void processConfigNested(TranslationBuilder builder, Class<?> clazz, String parentKey) {
            for (Field field : clazz.getDeclaredFields()) {
                if (isValidConfigField(field)) {
                    String name = field.getName();
                    String key = parentKey + "." + name;
                    String humanReadable = toHumanReadable(name);

                    boolean isNestedCategory = field.getType().getName().contains("IEConfig$");

                    if (isNestedCategory) {
                        builder.add(key, humanReadable + " Content");
                        processConfigNested(builder, field.getType(), key);
                    } else {
                        builder.add(key, humanReadable);
                    }
                }
            }
        }

        private boolean isValidConfigField(Field field) {
            return !Modifier.isStatic(field.getModifiers()) && !field.isSynthetic();
        }

        private String toHumanReadable(String camelCase) {
            String[] words = StringUtils.splitByCharacterTypeCamelCase(camelCase);
            return Arrays.stream(words)
                    .map(StringUtils::capitalize)
                    .reduce((a, b) -> a + " " + b)
                    .orElse(camelCase);
        }

    }


    private static class IEBlockTagProvider extends FabricTagProvider.BlockTagProvider {
        public IEBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {
            for (Map.Entry<TagKey<Block>, List<BlockDataHolder<?>>> entry : BlockDataHolder.getBlockTags().entrySet()) {
                FabricTagProvider<Block>.FabricTagBuilder tagBuilder = getOrCreateTagBuilder(entry.getKey());

                entry.getValue().stream()
                        .map(BlockDataHolder::get)
                        .sorted(Comparator.comparing(BuiltInRegistries.BLOCK::getKey))
                        .forEach(tagBuilder::add);
            }

            getOrCreateTagBuilder(ModTags.Blocks.SHROOMLIGHT_TEARS_GROWABLE)
                    .add(Blocks.SHROOMLIGHT);

            getOrCreateTagBuilder(ModTags.Blocks.GLOW_FIRE_BASE_BLOCKS)
                    .add(Blocks.GLOWSTONE);
        }
    }

    private static class IEItemTagProvider extends FabricTagProvider.ItemTagProvider {
        public IEItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
            super(output, completableFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {
            for (Map.Entry<TagKey<Item>, List<ItemDataHolder<?>>> entry : ItemDataHolder.getItemTags().entrySet()) {
                FabricTagProvider<Item>.FabricTagBuilder tagBuilder = getOrCreateTagBuilder(entry.getKey());

                entry.getValue().forEach(b -> tagBuilder.add(b.get()));
            }

            getOrCreateTagBuilder(ModTags.Items.GLOWSQUITO_TEMPTATION_ITEMS)
                    .add(ModBlocks.SHROOMLIGHT_TEAR.get().asItem())
                    .addOptional(new ResourceLocation("netherexp", "shroomnight_tear"));

            getOrCreateTagBuilder(ModTags.Items.VOLINE_FOOD)
                    .add(Items.MAGMA_CREAM);
        }
    }

    private static class IEBlockLootTableProvider extends FabricBlockLootTableProvider {
        protected IEBlockLootTableProvider(FabricDataOutput dataOutput) {
            super(dataOutput);
        }

        @Override
        public void generate() {
            for (BlockDataHolder<?> blockDataHolder : ModBlocks.getBlockRegistry().values()) {
                for (BlockDataHolder<?> blocksetHolder : blockDataHolder.getBlocksets().values()) {
                    if (blocksetHolder.hasModel()) {
                        if (Objects.requireNonNull(blockDataHolder.getModel()) == BlockDataHolder.Model.SLAB) {
                            add(blockDataHolder.get(), createSlabItemTable(blockDataHolder.get()));
                        }
                    }
                }

                if (blockDataHolder.isGlass()) {
                    add(blockDataHolder.get(), createSilkTouchOnlyTable(blockDataHolder.get()));
                    add(blockDataHolder.getPaneBlock().get(), createSilkTouchOnlyTable(blockDataHolder.getPaneBlock().get()));
                } else if (blockDataHolder.hasModel()) {
                    switch (blockDataHolder.getModel()) {
                        case SLAB -> {
                            add(blockDataHolder.get(), createSlabItemTable(blockDataHolder.get()));
                            continue;
                        }
                        case DOOR -> {
                            add(blockDataHolder.get(), createDoorTable(blockDataHolder.get()));
                            continue;
                        }
                        case FLOWER_POT -> {
                            add(blockDataHolder.get(), createPotFlowerItemTable(((FlowerPotBlock) blockDataHolder.get()).getContent()));
                            continue;
                        }
                    }
                }

                if (blockDataHolder.hasDrop()) {
                    if (blockDataHolder.getDropCount() == null)
                        add(blockDataHolder.get(), createSilkTouchOnlyTable(blockDataHolder.getDrop().get()));
                    else
                        add(blockDataHolder.get(), createSingleItemTable(blockDataHolder.getDrop().get(), blockDataHolder.getDropCount()));
                }

                for (Map.Entry<BlockDataHolder.Model, BlockDataHolder<?>> entry : blockDataHolder.getBlocksets().entrySet()) {
                    switch (entry.getKey()) {
                        case SLAB -> add(entry.getValue().get(), createSlabItemTable(entry.getValue().get()));
                        case DOOR -> add(entry.getValue().get(), createDoorTable(entry.getValue().get()));
                        default -> add(entry.getValue().get(), createSingleItemTable(entry.getValue().get()));
                    }
                }
            }
        }
    }

    private static class IEModelProvider extends FabricModelProvider {
        public IEModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators generator) {
            for (BlockDataHolder<?> blockDataHolder : ModBlocks.getBlockRegistry().values()) {
                if (blockDataHolder.getBlocksets().isEmpty()) {

                    if (blockDataHolder.isGlass()) {
                        generator.createGlassBlocks(blockDataHolder.get(), blockDataHolder.getPaneBlock().get());
                    } else if (blockDataHolder.hasModel()) {
                        switch (blockDataHolder.getModel()) {
                            case CUBE -> generator.createTrivialCube(blockDataHolder.get());
                            case PILLAR -> {
                                var pillar = generator.woodProvider(blockDataHolder.get());
                                pillar.log(blockDataHolder.get());
                            }
                            case WOOD -> {
                                Block block = blockDataHolder.get();
                                String name = BuiltInRegistries.BLOCK.getKey(block).getPath();

                                if (name.contains("hyphae")) {
                                    Block stem = BuiltInRegistries.BLOCK.get(IECommon.makeID(name.replace("hyphae", "stem")));
                                    generator.woodProvider(stem).wood(block);
                                } else {
                                    var pillar = generator.woodProvider(block);
                                    pillar.wood(block);
                                }
                            }
                            case ROTATABLE -> generator.createRotatedVariantBlock(blockDataHolder.get());
                            case CROSS ->
                                    generator.createCrossBlockWithDefaultItem(blockDataHolder.get(), BlockModelGenerators.TintState.NOT_TINTED);
                            case DOOR -> generator.createDoor(blockDataHolder.get());
                            case TRAPDOOR -> generator.createTrapdoor(blockDataHolder.get());
                            case FLOWER_POT -> {
                                Block potted = blockDataHolder.get();
                                if (potted instanceof FlowerPotBlock pot) {
                                    TextureMapping textureMapping = TextureMapping.plant(pot.getContent());
                                    ResourceLocation modelLocation = ModelTemplates.FLOWER_POT_CROSS.create(potted, textureMapping, generator.modelOutput);
                                    generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(potted, modelLocation));
                                }
                            }
                        }
                    }
                } else {
                    BlockModelGenerators.BlockFamilyProvider familyProvider = generator.family(blockDataHolder.get());
                    for (Map.Entry<BlockDataHolder.Model, BlockDataHolder<?>> entry : blockDataHolder.getBlocksets().entrySet()) {
                        switch (entry.getKey()) {
                            case STAIRS -> familyProvider.stairs(entry.getValue().get());
                            case SLAB -> familyProvider.slab(entry.getValue().get());
                            case WALL -> familyProvider.wall(entry.getValue().get());
                            case PRESSURE_PLATE -> familyProvider.pressurePlate(entry.getValue().get());
                            case BUTTON -> familyProvider.button(entry.getValue().get());
                            case FENCE -> familyProvider.fence(entry.getValue().get());
                            case FENCE_GATE -> familyProvider.fenceGate(entry.getValue().get());
                        }
                    }
                }
            }
        }

        @Override
        public void generateItemModels(ItemModelGenerators generator) {
            for (ItemDataHolder<?> itemDataHolder : ModItems.getItemRegistry().values()) {
                if (itemDataHolder.hasModel()) {
                    generator.generateFlatItem(itemDataHolder.get(), itemDataHolder.getModel());
                }
            }
        }
    }
}