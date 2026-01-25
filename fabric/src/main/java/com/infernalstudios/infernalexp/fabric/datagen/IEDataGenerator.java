package com.infernalstudios.infernalexp.fabric.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import net.minecraft.world.item.crafting.RecipeSerializer;
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

        private void createAutumnityRecipe(Consumer<FinishedRecipe> exporter, String resultPath, ResourceLocation inputA, ItemLike inputB) {
            ResourceLocation resultId = IECommon.makeID(resultPath);
            Consumer<FinishedRecipe> conditionalExporter = withConditions(exporter, "autumnity");

            conditionalExporter.accept(new FinishedRecipe() {
                @Override
                public void serializeRecipeData(@NotNull JsonObject json) {
                    json.addProperty("type", "minecraft:crafting_shaped");

                    JsonArray pattern = new JsonArray();
                    pattern.add("A");
                    pattern.add("B");
                    json.add("pattern", pattern);

                    JsonObject key = new JsonObject();
                    JsonObject keyA = new JsonObject();
                    keyA.addProperty("item", inputA.toString());
                    key.add("A", keyA);

                    JsonObject keyB = new JsonObject();
                    keyB.addProperty("item", BuiltInRegistries.ITEM.getKey(inputB.asItem()).toString());
                    key.add("B", keyB);
                    json.add("key", key);

                    JsonObject result = new JsonObject();
                    result.addProperty("item", resultId.toString());
                    json.add("result", result);
                }

                @Override
                public @NotNull ResourceLocation getId() {
                    return resultId;
                }

                @Override
                public @NotNull RecipeSerializer<?> getType() {
                    return RecipeSerializer.SHAPED_RECIPE;
                }

                @Override
                public JsonObject serializeAdvancement() {
                    return null;
                }

                @Override
                public ResourceLocation getAdvancementId() {
                    return null;
                }
            });
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

        /**
         * Generates a Create Crushing recipe with multi-loader conditions.
         */
        private void offerCreateCrushing(Consumer<FinishedRecipe> exporter, ItemLike input, int time, CrushingResult... results) {
            ResourceLocation id = IECommon.makeID("crushing/" + getName(input));
            Consumer<FinishedRecipe> conditionalExporter = withConditions(exporter, "create");

            conditionalExporter.accept(new FinishedRecipe() {
                @Override
                public void serializeRecipeData(@NotNull JsonObject json) {
                    json.addProperty("type", "create:crushing");

                    JsonArray ingredients = new JsonArray();
                    JsonObject ing = new JsonObject();
                    ing.addProperty("item", BuiltInRegistries.ITEM.getKey(input.asItem()).toString());
                    ingredients.add(ing);
                    json.add("ingredients", ingredients);

                    JsonArray resultsArray = new JsonArray();
                    for (CrushingResult result : results) {
                        JsonObject res = new JsonObject();
                        res.addProperty("item", result.itemId);

                        if (result.count > 1) res.addProperty("count", result.count);
                        if (result.chance < 1.0f) res.addProperty("chance", result.chance);
                        resultsArray.add(res);
                    }

                    json.add("results", resultsArray);
                    json.addProperty("processingTime", time);
                }

                @Override
                public @NotNull ResourceLocation getId() {
                    return id;
                }

                @Override
                public @NotNull RecipeSerializer<?> getType() {
                    return RecipeSerializer.SHAPED_RECIPE;
                }

                @Override
                public JsonObject serializeAdvancement() {
                    return null;
                }

                @Override
                public ResourceLocation getAdvancementId() {
                    return null;
                }
            });
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

            offerCompat3x3Recipe(
                    exporter,
                    new ResourceLocation("netherexp", "shroomnight"),
                    NetherExpCompat.SHROOMNIGHT_TEAR.get(),
                    "netherexp"
            );

            offerCreateCrushing(exporter, ModBlocks.DIMSTONE.get(), 150,
                    new CrushingResult(Items.GLOWSTONE_DUST, 1),
                    new CrushingResult(ModItems.DULLROCKS.get(), 1),
                    new CrushingResult(Items.GLOWSTONE_DUST, 1, 0.5f),
                    new CrushingResult(ModItems.DULLROCKS.get(), 1, 0.5f)
            );

            offerCreateCrushing(exporter, ModBlocks.DULLSTONE.get(), 150,
                    new CrushingResult(ModItems.DULLROCKS.get(), 3),
                    new CrushingResult(ModItems.DULLROCKS.get(), 1, 0.5f)
            );

            offerCreateCrushing(exporter, ModBlocks.SHIMMER_STONE.get(), 150,
                    new CrushingResult(ModBlocks.SHIMMER_SAND.get(), 2),
                    new CrushingResult(ModBlocks.SHIMMER_SAND.get(), 1, 0.25f)
            );

            offerCreateCrushing(exporter, ModBlocks.SHIMMER_SAND.get(), 150,
                    new CrushingResult(Items.GLOWSTONE_DUST, 2),
                    new CrushingResult(Items.GLOWSTONE_DUST, 1, 0.25f)
            );

            offerCreateCrushing(exporter, ModBlocks.BASALT_IRON_ORE.get(), 350,
                    new CrushingResult(new ResourceLocation("create", "crushed_raw_iron"), 2, 1.0f),
                    new CrushingResult(new ResourceLocation("create", "crushed_raw_iron"), 1, 0.25f),
                    new CrushingResult(new ResourceLocation("create", "experience_nugget"), 1, 0.75f),
                    new CrushingResult(Blocks.BASALT, 1, 0.125f)
            );

            createAutumnityRecipe(exporter, "glowlight_jack_o_lantern",
                    new ResourceLocation("minecraft", "carved_pumpkin"), ModItems.GLOWLIGHT_TORCH.get());

            createAutumnityRecipe(exporter, "large_glowlight_jack_o_lantern_slice",
                    new ResourceLocation("autumnity", "carved_large_pumpkin_slice"), ModItems.GLOWLIGHT_TORCH.get());

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
        }

        /**
         * Generates a 3x3 packing recipe (e.g. Nuggets to Ingot) for an item that might not exist at runtime.
         */
        private void offerCompat3x3Recipe(Consumer<FinishedRecipe> exporter, ResourceLocation outputId, ItemLike input, String conditionModId) {
            ResourceLocation recipeId = IECommon.makeID(outputId.getPath());
            Consumer<FinishedRecipe> conditionalExporter = withConditions(exporter, conditionModId);

            conditionalExporter.accept(new FinishedRecipe() {
                @Override
                public void serializeRecipeData(@NotNull JsonObject json) {
                    json.addProperty("type", "minecraft:crafting_shapeless");
                    json.addProperty("group", outputId.getPath());

                    JsonArray ingredients = new JsonArray();
                    JsonObject inputJson = new JsonObject();
                    inputJson.addProperty("item", BuiltInRegistries.ITEM.getKey(input.asItem()).toString());

                    for (int i = 0; i < 9; i++) {
                        ingredients.add(inputJson);
                    }
                    json.add("ingredients", ingredients);

                    JsonObject result = new JsonObject();
                    result.addProperty("item", outputId.toString());
                    result.addProperty("count", 1);
                    json.add("result", result);
                }

                @Override
                public @NotNull ResourceLocation getId() {
                    return recipeId;
                }

                @Override
                public @NotNull RecipeSerializer<?> getType() {
                    return RecipeSerializer.SHAPELESS_RECIPE;
                }

                @Override
                public JsonObject serializeAdvancement() {
                    return null;
                }

                @Override
                public ResourceLocation getAdvancementId() {
                    return null;
                }
            });
        }

        private record CrushingResult(String itemId, int count, float chance) {
            public CrushingResult(ResourceLocation id, int count, float chance) {
                this(id.toString(), count, chance);
            }

            public CrushingResult(ItemLike item, int count, float chance) {
                this(BuiltInRegistries.ITEM.getKey(item.asItem()).toString(), count, chance);
            }

            public CrushingResult(ItemLike item, int count) {
                this(item, count, 1.0f);
            }
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
            builder.add("tag.item.infernalexp.glowsquito_temptation_items", "Glowsquito Temptation Items");

            // Subtitles
            builder.add("subtitles.infernalexp.entity.voline.ambient", "Voline grunts");
            builder.add("subtitles.infernalexp.entity.voline.hurt", "Voline hurts");

            builder.add("subtitles.infernalexp.entity.glowsquito.hurt", "Glowsquito hurts");
            builder.add("subtitles.infernalexp.entity.glowsquito.death", "Glowsquito dies");
            builder.add("subtitles.infernalexp.entity.glowsquito.loop", "Glowsquito buzzes");
            builder.add("subtitles.infernalexp.entity.glowsquito.slurp", "Glowsquito slurps");

            builder.add("subtitles.infernalexp.entity.glowsilk_moth.ambient", "Glowsilk Moth flutters");
            builder.add("subtitles.infernalexp.entity.glowsilk_moth.hurt", "Glowsilk Moth hurts");
            builder.add("subtitles.infernalexp.entity.glowsilk_moth.death", "Glowsilk Moth dies");

            builder.add("subtitles.infernalexp.entity.blindsight.ambient", "Blindsight croaks");
            builder.add("subtitles.infernalexp.entity.blindsight.hurt", "Blindsight hurts");
            builder.add("subtitles.infernalexp.entity.blindsight.death", "Blindsight dies");
            builder.add("subtitles.infernalexp.entity.blindsight.leap", "Blindsight leaps");
            builder.add("subtitles.infernalexp.entity.blindsight.alert", "Blindsight roars");
            builder.add("subtitles.infernalexp.entity.blindsight.lick", "Blindsight licks");

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

            // Mob Interactions Tooltips
            builder.add("text.autoconfig.infernalexp.option.common.mobInteractions.glowsquitoBlockSucking.@Tooltip",
                    "Determines if Glowsquitos should drink from Glowstone and Shroomlight blocks.");


            // Miscellaneous Tooltips
            builder.add("text.autoconfig.infernalexp.option.common.miscellaneous.luminousFungusActivateDistance.@Tooltip",
                    "Determines the radius in blocks around Luminous Fungus that will cause them to light up.");

            builder.add("text.autoconfig.infernalexp.option.common.miscellaneous.glowsilkBowSpeed.@Tooltip",
                    "Determines the speed at which arrows are fired from the Glowsilk Bow.");


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