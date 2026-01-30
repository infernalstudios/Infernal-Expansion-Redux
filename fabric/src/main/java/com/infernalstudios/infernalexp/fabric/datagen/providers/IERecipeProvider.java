package com.infernalstudios.infernalexp.fabric.datagen.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModItems;
import com.infernalstudios.infernalexp.registration.holders.BlockDataHolder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class IERecipeProvider extends FabricRecipeProvider {
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

    /**
     * Wraps a recipe exporter to append mod-loaded conditions for all 3 loaders.
     */
    private Consumer<FinishedRecipe> withConditions(Consumer<FinishedRecipe> exporter, String modId) {
        return recipe -> {
            JsonObject json = new JsonObject();
            recipe.serializeRecipeData(json);

            JsonObject forgeCondition = new JsonObject();
            forgeCondition.addProperty("type", "forge:mod_loaded");
            forgeCondition.addProperty("modid", modId);

            JsonObject neoforgeCondition = new JsonObject();
            neoforgeCondition.addProperty("type", "neoforge:mod_loaded");
            neoforgeCondition.addProperty("modid", modId);

            JsonObject fabricCondition = new JsonObject();
            fabricCondition.addProperty("condition", "fabric:all_mods_loaded");
            JsonArray values = new JsonArray();
            values.add(modId);
            fabricCondition.add("values", values);

            JsonArray forgeConditions = new JsonArray();
            forgeConditions.add(forgeCondition);
            JsonArray neoforgeConditions = new JsonArray();
            neoforgeConditions.add(neoforgeCondition);
            JsonArray fabricConditions = new JsonArray();
            fabricConditions.add(fabricCondition);

            exporter.accept(new FinishedRecipe() {
                @Override
                public void serializeRecipeData(@NotNull JsonObject json) {
                    recipe.serializeRecipeData(json);
                    json.add("forge:conditions", forgeConditions);
                    json.add("neoforge:conditions", neoforgeConditions);
                    json.add("fabric:load_conditions", fabricConditions);
                }

                @Override
                public @NotNull ResourceLocation getId() {
                    return recipe.getId();
                }

                @Override
                public @NotNull RecipeSerializer<?> getType() {
                    return recipe.getType();
                }

                @Override
                public JsonObject serializeAdvancement() {
                    return recipe.serializeAdvancement();
                }

                @Override
                public ResourceLocation getAdvancementId() {
                    return recipe.getAdvancementId();
                }
            });
        };
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

        offerStonecutting(exporter, ModBlocks.SHIMMER_STONE_BRICKS.get(), ModBlocks.SHIMMER_STONE_BRICKS.getStairs().get());
        offerStonecutting(exporter, ModBlocks.SHIMMER_STONE_BRICKS.get(), ModBlocks.SHIMMER_STONE_BRICKS.getSlab().get(), 2);
        offerStonecutting(exporter, ModBlocks.SHIMMER_STONE_BRICKS.get(), ModBlocks.SHIMMER_STONE_BRICKS.getWall().get());
        offerStonecutting(exporter, ModBlocks.SHIMMER_STONE_BRICKS.get(), ModBlocks.CHISELED_SHIMMER_STONE_BRICKS.get());

        offerStonecutting(exporter, ModBlocks.POLISHED_BASALT_BRICKS.get(), ModBlocks.POLISHED_BASALT_BRICKS.getStairs().get());
        offerStonecutting(exporter, ModBlocks.POLISHED_BASALT_BRICKS.get(), ModBlocks.POLISHED_BASALT_BRICKS.getSlab().get(), 2);
        offerStonecutting(exporter, ModBlocks.POLISHED_BASALT_BRICKS.get(), ModBlocks.POLISHED_BASALT_BRICKS.getWall().get());
        offerStonecutting(exporter, ModBlocks.POLISHED_BASALT_BRICKS.get(), ModBlocks.CHISELED_POLISHED_BASALT.get());

        offerStonecutting(exporter, Blocks.POLISHED_BASALT, ModBlocks.POLISHED_BASALT_BRICKS.get());
        offerStonecutting(exporter, Blocks.POLISHED_BASALT, ModBlocks.POLISHED_BASALT_BRICKS.getStairs().get());
        offerStonecutting(exporter, Blocks.POLISHED_BASALT, ModBlocks.POLISHED_BASALT_BRICKS.getSlab().get(), 2);
        offerStonecutting(exporter, Blocks.POLISHED_BASALT, ModBlocks.POLISHED_BASALT_BRICKS.getWall().get());
        offerStonecutting(exporter, Blocks.POLISHED_BASALT, ModBlocks.CHISELED_POLISHED_BASALT.get());

        slabBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_BASALT_SLAB.get(), Ingredient.of(Blocks.POLISHED_BASALT))
                .group(getName(ModBlocks.POLISHED_BASALT_SLAB.get()))
                .unlockedBy("has_polished_basalt", has(Blocks.POLISHED_BASALT))
                .save(exporter, IECommon.makeID("polished_basalt_slab"));

        stairBuilder(ModBlocks.POLISHED_BASALT_STAIRS.get(), Ingredient.of(Blocks.POLISHED_BASALT))
                .group(getName(ModBlocks.POLISHED_BASALT_STAIRS.get()))
                .unlockedBy("has_polished_basalt", has(Blocks.POLISHED_BASALT))
                .save(exporter, IECommon.makeID("polished_basalt_stairs"));

        wallBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_BASALT_WALL.get(), Ingredient.of(Blocks.POLISHED_BASALT))
                .group(getName(ModBlocks.POLISHED_BASALT_WALL.get()))
                .unlockedBy("has_polished_basalt", has(Blocks.POLISHED_BASALT))
                .save(exporter, IECommon.makeID("polished_basalt_wall"));

        offerStonecutting(exporter, Blocks.BASALT, ModBlocks.POLISHED_BASALT_SLAB.get(), 2);
        offerStonecutting(exporter, Blocks.POLISHED_BASALT, ModBlocks.POLISHED_BASALT_SLAB.get(), 2);

        offerStonecutting(exporter, Blocks.BASALT, ModBlocks.POLISHED_BASALT_STAIRS.get());
        offerStonecutting(exporter, Blocks.POLISHED_BASALT, ModBlocks.POLISHED_BASALT_STAIRS.get());

        offerStonecutting(exporter, Blocks.BASALT, ModBlocks.POLISHED_BASALT_WALL.get());
        offerStonecutting(exporter, Blocks.POLISHED_BASALT, ModBlocks.POLISHED_BASALT_WALL.get());

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_SHIMMER_STONE_BRICKS.get())
                .pattern("#")
                .pattern("#")
                .define('#', ModBlocks.SHIMMER_STONE_BRICKS.getSlab().get())
                .unlockedBy(getHasName(ModBlocks.SHIMMER_STONE_BRICKS.getSlab().get()), has(ModBlocks.SHIMMER_STONE_BRICKS.getSlab().get()))
                .save(exporter, IECommon.makeID("chiseled_shimmer_stone_bricks_from_slabs"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_POLISHED_BASALT.get())
                .pattern("#")
                .pattern("#")
                .define('#', ModBlocks.POLISHED_BASALT_SLAB.get())
                .unlockedBy(getHasName(Blocks.POLISHED_BASALT), has(ModBlocks.POLISHED_BASALT_SLAB.get()))
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

        oreSmelting(exporter, List.of(ModBlocks.BASALT_IRON_ORE.get()), RecipeCategory.MISC, Items.IRON_INGOT,
                1, 200, "basalt_iron_ore");
        oreBlasting(exporter, List.of(ModBlocks.BASALT_IRON_ORE.get()), RecipeCategory.MISC, Items.IRON_INGOT,
                1, 100, "basalt_iron_ore");

        offer3x3Recipe(exporter, Blocks.SHROOMLIGHT, 1, ModBlocks.SHROOMLIGHT_TEAR.get());

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

        signBuilder(ModBlocks.LUMINOUS_SIGN.get(), Ingredient.of(ModBlocks.LUMINOUS_PLANKS.get()))
                .unlockedBy(getHasName(ModBlocks.LUMINOUS_PLANKS.get()), has(ModBlocks.LUMINOUS_PLANKS.get()))
                .save(exporter, IECommon.makeID("luminous_sign"));

        hangingSign(exporter, ModBlocks.LUMINOUS_HANGING_SIGN.get(), ModBlocks.STRIPPED_LUMINOUS_STEM.get());

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

        oreSmelting(exporter, List.of(ModBlocks.POLISHED_BASALT_BRICKS.get()), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRACKED_POLISHED_BASALT_BRICKS.get(),
                0.1f, 200, "basalt");
        offer2x2Recipe(exporter, ModBlocks.POLISHED_BASALT_BRICKS.get(), 4, Blocks.POLISHED_BASALT);

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
        offerStonecutting(exporter, Blocks.BASALT, ModBlocks.BASALT_SLAB.get(), 2);
        offerStonecutting(exporter, Blocks.BASALT, ModBlocks.BASALT_WALL.get());

        offerStonecutting(exporter, Blocks.GLOWSTONE, ModBlocks.POLISHED_GLOWSTONE.get());
        offerStonecutting(exporter, ModBlocks.DIMSTONE.get(), ModBlocks.POLISHED_DIMSTONE.get());
        offerStonecutting(exporter, ModBlocks.DULLSTONE.get(), ModBlocks.POLISHED_DULLSTONE.get());

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

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.BLINDSIGHT_TONGUE_STEW.get())
                .requires(ModItems.BLINDSIGHT_TONGUE.get())
                .requires(Items.CRIMSON_FUNGUS)
                .requires(ModBlocks.LUMINOUS_FUNGUS.get())
                .requires(Items.WARPED_FUNGUS)
                .requires(Items.BOWL)
                .unlockedBy("has_blindsight_tongue", has(ModItems.BLINDSIGHT_TONGUE.get()))
                .save(exporter, IECommon.makeID("blindsight_tongue_stew"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.WAXED_GLOWSTONE.get())
                .requires(Blocks.GLOWSTONE)
                .requires(Items.HONEYCOMB)
                .unlockedBy("has_glowstone", has(Blocks.GLOWSTONE))
                .save(exporter, IECommon.makeID("waxed_glowstone"));

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BLINDSIGHT_TONGUE_WHIP.get())
                .pattern("  G")
                .pattern(" ST")
                .pattern("S T")
                .define('T', ModItems.BLINDSIGHT_TONGUE.get())
                .define('G', ModItems.GLOWSILK_STRING.get())
                .define('S', Items.STICK)
                .unlockedBy("has_blindsight_tongue", has(ModItems.BLINDSIGHT_TONGUE.get()))
                .save(exporter, IECommon.makeID("blindsight_tongue_whip"));
    }
}