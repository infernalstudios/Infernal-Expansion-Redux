package com.infernalstudios.infernalexp.resources.config;

import com.google.gson.*;
import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.platform.Services;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Credit for this system goes to Lyof (<a href="https://github.com/Lyof429">...</a>) and their mods
 * Do not reuse without crediting
 */
public class ConfiguredData {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static List<ConfiguredData> INSTANCES = new LinkedList<>();
    public final ResourceLocation target;
    public final Supplier<Boolean> enabled;
    public Function<JsonElement, String> provider;

    public ConfiguredData(ResourceLocation target, Supplier<Boolean> enabled, Function<JsonElement, String> provider) {
        this.target = target;
        this.provider = provider;
        this.enabled = enabled;
    }

    public static @Nullable ConfiguredData get(ResourceLocation id) {
        return INSTANCES.stream().filter(data -> data.target.equals(id)).findAny().orElse(null);
    }

    protected static void register(ResourceLocation target, Supplier<Boolean> enabled, Function<JsonElement, String> provider) {
        INSTANCES.add(new ConfiguredData(target, enabled, provider));
    }

    public static void register() {
        register(ResourceLocation.tryBuild("minecraft", "loot_table/entities/magma_cube.json"), () -> true,
                Common::addVolineMagmaCreamDrop);

        register(ResourceLocation.tryBuild("infernalexp", "loot_table/blocks/glowlight_jack_o_lantern.json"), () -> Services.PLATFORM.isModLoaded("autumnity"),
                json -> Common.dropSelf(json, "infernalexp:glowlight_jack_o_lantern"));

        register(ResourceLocation.tryBuild("infernalexp", "loot_table/blocks/large_glowlight_jack_o_lantern_slice.json"), () -> Services.PLATFORM.isModLoaded("autumnity"),
                json -> Common.dropSelf(json, "infernalexp:large_glowlight_jack_o_lantern_slice"));

        register(ResourceLocation.tryBuild("infernalexp", "recipe/glowlight_jack_o_lantern.json"),
                () -> Services.PLATFORM.isModLoaded("autumnity"),
                json -> Common.createShaped(
                        "infernalexp:glowlight_jack_o_lantern",
                        1,
                        List.of("A", "B"),
                        Map.of("A", "minecraft:carved_pumpkin", "B", "infernalexp:glowlight_torch")
                ));

        register(ResourceLocation.tryBuild("infernalexp", "recipe/large_glowlight_jack_o_lantern_slice.json"),
                () -> Services.PLATFORM.isModLoaded("autumnity"),
                json -> Common.createShaped(
                        "infernalexp:large_glowlight_jack_o_lantern_slice",
                        1,
                        List.of("A", "B"),
                        Map.of("A", "autumnity:carved_large_pumpkin_slice", "B", "infernalexp:glowlight_torch")
                ));

        register(ResourceLocation.tryBuild("infernalexp", "loot_table/blocks/glowlight_brazier.json"), () -> Services.PLATFORM.isModLoaded("caverns_and_chasms"),
                json -> Common.dropSelf(json, "infernalexp:glowlight_brazier"));

        register(ResourceLocation.tryBuild("infernalexp", "recipe/glowlight_brazier.json"),
                () -> Services.PLATFORM.isModLoaded("caverns_and_chasms"),
                json -> Common.createShaped(
                        "infernalexp:glowlight_brazier",
                        1,
                        List.of(
                                "S#S",
                                " S "
                        ),
                        Map.of(
                                "S", "#c:ingots/silver",
                                "#", "infernalexp:glowcoke"
                        )
                ));

        register(ResourceLocation.tryBuild("infernalexp", "loot_table/blocks/dwarf_spruce_glowlight_torch.json"), () -> Services.PLATFORM.isModLoaded("environmental"),
                json -> Common.dropSelf(json, "environmental:dwarf_spruce"));

        register(ResourceLocation.tryBuild("infernalexp", "loot_table/blocks/dwarf_spruce_plant_glowlight_torch.json"), () -> Services.PLATFORM.isModLoaded("environmental"),
                json -> Common.dropSelf(json, "environmental:dwarf_spruce"));

        register(ResourceLocation.tryBuild("infernalexp", "recipe/crushing/dimstone.json"),
                () -> Services.PLATFORM.isModLoaded("create"),
                json -> {
                    if (json != null && !json.isJsonNull()) {
                        return json.toString();
                    }

                    return Common.createCrushing(150, "infernalexp:dimstone",
                            new Common.CrushOutput("minecraft:glowstone_dust", 1, 1.0f),
                            new Common.CrushOutput("infernalexp:dullrocks", 1, 1.0f),
                            new Common.CrushOutput("minecraft:glowstone_dust", 1, 0.5f),
                            new Common.CrushOutput("infernalexp:dullrocks", 1, 0.5f)
                    );
                });

        register(ResourceLocation.tryBuild("infernalexp", "recipe/crushing/dullstone.json"),
                () -> Services.PLATFORM.isModLoaded("create"),
                json -> Common.createCrushing(150, "infernalexp:dullstone",
                        new Common.CrushOutput("infernalexp:dullrocks", 3, 1.0f),
                        new Common.CrushOutput("infernalexp:dullrocks", 1, 0.5f)
                ));

        register(ResourceLocation.tryBuild("infernalexp", "recipe/crushing/shimmer_stone.json"),
                () -> Services.PLATFORM.isModLoaded("create"),
                json -> Common.createCrushing(150, "infernalexp:shimmer_stone",
                        new Common.CrushOutput("infernalexp:shimmer_sand", 2, 1.0f),
                        new Common.CrushOutput("infernalexp:shimmer_sand", 1, 0.25f)
                ));

        register(ResourceLocation.tryBuild("infernalexp", "recipe/crushing/shimmer_sand.json"),
                () -> Services.PLATFORM.isModLoaded("create"),
                json -> Common.createCrushing(150, "infernalexp:shimmer_sand",
                        new Common.CrushOutput("minecraft:glowstone_dust", 2, 1.0f),
                        new Common.CrushOutput("minecraft:glowstone_dust", 1, 0.25f)
                ));

        register(ResourceLocation.tryBuild("infernalexp", "recipe/crushing/basalt_iron_ore.json"),
                () -> Services.PLATFORM.isModLoaded("create"),
                json -> Common.createCrushing(350, "infernalexp:basalt_iron_ore",
                        new Common.CrushOutput("create:crushed_raw_iron", 2, 1.0f),
                        new Common.CrushOutput("create:crushed_raw_iron", 1, 0.25f),
                        new Common.CrushOutput("create:experience_nugget", 1, 0.75f),
                        new Common.CrushOutput("minecraft:basalt", 1, 0.125f)
                ));

        register(ResourceLocation.tryBuild("infernalexp", "recipe/shroomnight_from_tears.json"),
                () -> Services.PLATFORM.isModLoaded("netherexp"),
                json -> Common.createPacking3x3("netherexp:shroomnight", "infernalexp:shroomnight_tear"));

        register(ResourceLocation.tryBuild("infernalexp", "recipe/shroomblight_from_tears.json"),
                () -> Services.PLATFORM.isModLoaded("netherexp"),
                json -> Common.createPacking3x3("netherexp:shroomblight", "infernalexp:shroomblight_tear"));

        register(ResourceLocation.tryBuild("infernalexp", "recipe/shroombright_from_tears.json"),
                () -> Services.PLATFORM.isModLoaded("netherexp"),
                json -> Common.createPacking3x3("netherexp:shroombright", "infernalexp:shroombright_tear"));

        register(ResourceLocation.tryBuild("infernalexp", "loot_table/blocks/shroomlight_tear.json"),
                () -> Services.PLATFORM.isModLoaded("netherexp"),
                json -> Common.dropSporeOrSelf("infernalexp:shroomlight_tear", "netherexp:lightspores"));

        register(ResourceLocation.tryBuild("infernalexp", "loot_table/blocks/shroomnight_tear.json"),
                () -> Services.PLATFORM.isModLoaded("netherexp"),
                json -> Common.dropSporeOrSelf("infernalexp:shroomnight_tear", "netherexp:nightspores"));

        register(ResourceLocation.tryBuild("infernalexp", "loot_table/blocks/shroomblight_tear.json"),
                () -> (Services.PLATFORM.isModLoaded("netherexp") && Services.PLATFORM.isModLoaded("gardens_of_the_dead")),
                json -> Common.dropSporeOrSelf("infernalexp:shroomblight_tear", "netherexp:blightspores"));

        register(ResourceLocation.tryBuild("infernalexp", "loot_table/blocks/shroombright_tear.json"),
                () -> (Services.PLATFORM.isModLoaded("netherexp") && Services.PLATFORM.isModLoaded("cinderscapes")),
                json -> Common.dropSporeOrSelf("infernalexp:shroombright_tear", "netherexp:brightspores"));

        register(ResourceLocation.tryBuild("minecraft", "tags/block/mineable/pickaxe.json"),
                () -> Services.PLATFORM.isModLoaded("caverns_and_chasms"),
                json -> Common.appendToTag(json, "infernalexp:glowlight_brazier"));

        register(ResourceLocation.tryBuild("minecraft", "tags/block/mineable/hoe.json"),
                () -> Services.PLATFORM.isModLoaded("environmental"),
                json -> Common.appendToTag(json, "infernalexp:dwarf_spruce_glowlight_torch", "infernalexp:dwarf_spruce_plant_glowlight_torch"));

        register(ResourceLocation.tryBuild("caverns_and_chasms", "tags/block/braziers.json"),
                () -> Services.PLATFORM.isModLoaded("caverns_and_chasms"),
                json -> Common.appendToTag(json, "infernalexp:glowlight_brazier"));
    }

    public String apply(@Nullable String original) {
        try {
            JsonElement parsedOriginal = gson.fromJson(original == null ? "" : original, JsonElement.class);
            String modifiedString = this.provider.apply(parsedOriginal);

            return gson.fromJson(modifiedString, JsonElement.class).toString();

        } catch (JsonSyntaxException e) {
            IEConstants.LOG.error("Failed to parse JSON for target: {}", this.target, e);
            return original != null ? original : "";
        }
    }

    private static class Common {
        public static String appendToTag(JsonElement json, String... newValues) {
            JsonObject obj;
            if (json == null || !json.isJsonObject()) {
                obj = new JsonObject();
                obj.addProperty("replace", false);
                obj.add("values", new JsonArray());
            } else {
                obj = json.getAsJsonObject();
                if (!obj.has("values")) {
                    obj.add("values", new JsonArray());
                }
            }

            JsonArray values = obj.getAsJsonArray("values");
            for (String val : newValues) {
                values.add(val);
            }

            return gson.toJson(obj);
        }

        public static String addVolineMagmaCreamDrop(JsonElement json) {
            if (json != null && json.isJsonObject()) {
                JsonObject obj = json.getAsJsonObject();
                JsonArray pools = obj.getAsJsonArray("pools");

                if (pools != null) {
                    JsonObject newPool = new JsonObject();
                    newPool.addProperty("rolls", 1);

                    JsonArray entries = new JsonArray();
                    JsonObject entry = new JsonObject();
                    entry.addProperty("type", "minecraft:item");
                    entry.addProperty("name", "minecraft:magma_cream");
                    entries.add(entry);
                    newPool.add("entries", entries);

                    JsonArray conditions = new JsonArray();
                    JsonObject condition = new JsonObject();
                    condition.addProperty("condition", "minecraft:entity_properties");

                    condition.add("entity", new JsonPrimitive("attacker"));

                    JsonObject predicate = new JsonObject();
                    predicate.addProperty("type", "infernalexp:voline");
                    condition.add("predicate", predicate);

                    conditions.add(condition);
                    newPool.add("conditions", conditions);

                    pools.add(newPool);
                }
            }
            return json == null ? "" : json.toString();
        }

        public static String dropSelf(JsonElement json, String item) {
            return gson.fromJson("{\"type\":\"minecraft:block\",\"pools\":[{\"rolls\":1.0,\"bonus_rolls\":0.0,\"entries\":[{\"type\":\"minecraft:item\",\"name\":\"" + item + "\"}],\"conditions\":[{\"condition\":\"minecraft:survives_explosion\"}]}]}", JsonElement.class).toString();
        }

        public static String dropSporeOrSelf(String selfItem, String sporeItem) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "minecraft:block");

            JsonArray pools = new JsonArray();
            JsonObject pool = new JsonObject();
            pool.addProperty("rolls", 1.0);

            JsonArray entries = new JsonArray();
            JsonObject alternatives = new JsonObject();
            alternatives.addProperty("type", "minecraft:alternatives");

            JsonArray children = new JsonArray();

            JsonObject silkTouchOption = new JsonObject();
            silkTouchOption.addProperty("type", "minecraft:item");
            silkTouchOption.addProperty("name", selfItem);

            JsonArray conditions = new JsonArray();
            JsonObject matchTool = new JsonObject();
            matchTool.addProperty("condition", "minecraft:match_tool");
            JsonObject predicate = new JsonObject();
            JsonArray enchantments = new JsonArray();
            JsonObject silkTouch = new JsonObject();
            silkTouch.addProperty("enchantment", "minecraft:silk_touch");
            JsonObject levels = new JsonObject();
            levels.addProperty("min", 1);
            silkTouch.add("levels", levels);
            enchantments.add(silkTouch);
            predicate.add("enchantments", enchantments);
            matchTool.add("predicate", predicate);
            conditions.add(matchTool);

            silkTouchOption.add("conditions", conditions);
            children.add(silkTouchOption);

            JsonObject sporeOption = new JsonObject();
            sporeOption.addProperty("type", "minecraft:item");
            sporeOption.addProperty("name", sporeItem);
            children.add(sporeOption);

            alternatives.add("children", children);
            entries.add(alternatives);
            pool.add("entries", entries);
            pools.add(pool);
            json.add("pools", pools);

            return gson.toJson(json);
        }

        public static String createCrushing(int time, String inputItem, CrushOutput... outputs) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "create:crushing");

            JsonArray ingredients = new JsonArray();
            JsonObject ing = new JsonObject();
            ing.addProperty("item", inputItem);
            ingredients.add(ing);
            json.add("ingredients", ingredients);

            json.addProperty("processingTime", time);

            JsonArray results = new JsonArray();
            for (CrushOutput output : outputs) {
                JsonObject res = new JsonObject();
                res.addProperty("id", output.item);
                if (output.count > 1) res.addProperty("count", output.count);
                if (output.chance < 1.0f) res.addProperty("chance", output.chance);
                results.add(res);
            }
            json.add("results", results);

            return gson.toJson(json);
        }

        public static String createShaped(String resultItem, int count, List<String> pattern, Map<String, String> keys) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "minecraft:crafting_shaped");

            JsonArray patternArray = new JsonArray();
            for (String line : pattern) {
                patternArray.add(line);
            }
            json.add("pattern", patternArray);

            JsonObject keyObj = new JsonObject();
            for (Map.Entry<String, String> entry : keys.entrySet()) {
                JsonObject itemObj = new JsonObject();
                String value = entry.getValue();

                // tags
                if (value.startsWith("#")) {
                    itemObj.addProperty("tag", value.substring(1));
                } else {
                    // items
                    itemObj.addProperty("item", value);
                }

                keyObj.add(entry.getKey(), itemObj);
            }
            json.add("key", keyObj);

            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("id", resultItem);
            if (count > 1) resultObj.addProperty("count", count);
            json.add("result", resultObj);

            return gson.toJson(json);
        }

        public static String createPacking3x3(String resultItem, String inputItem) {
            return createShaped(resultItem, 1, List.of("###", "###", "###"), Map.of("#", inputItem));
        }

        record CrushOutput(String item, int count, float chance) {
        }
    }
}