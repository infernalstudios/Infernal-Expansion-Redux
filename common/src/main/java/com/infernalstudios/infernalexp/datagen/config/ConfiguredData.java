package com.infernalstudios.infernalexp.datagen.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.infernalstudios.infernalexp.module.ModBiomes;
import com.infernalstudios.infernalexp.platform.Services;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Credit for this system goes to [Lyof](https://github.com/Lyof429) and their mods
 * Do not reuse without crediting
 */
public class ConfiguredData {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public final ResourceLocation target;
    public Function<JsonElement, String> provider;
    public final Supplier<Boolean> enabled;

    public ConfiguredData(ResourceLocation target, Supplier<Boolean> enabled, Function<JsonElement, String> provider) {
        this.target = target;
        this.provider = provider;
        this.enabled = enabled;
    }

    public String apply(@Nullable String original) {
        return gson.fromJson(this.provider.apply(gson.fromJson(original == null ? "" : original, JsonElement.class)), JsonElement.class).toString();
    }


    public static List<ConfiguredData> INSTANCES = new LinkedList<>();

    public static @Nullable ConfiguredData get(ResourceLocation id) {
        return INSTANCES.stream().filter(data -> data.target.equals(id)).findAny().orElse(null);
    }

    protected static void register(ResourceLocation target, Supplier<Boolean> enabled, Function<JsonElement, String> provider) {
        INSTANCES.add(new ConfiguredData(target, enabled, provider));
    }


    public static void register() {
        register(ResourceLocation.tryBuild("minecraft", "dimension/the_nether.json"), () -> !Services.PLATFORM.isModLoaded("terrablender"), Common::changeNetherBiomeSource);
    }

    private static class Common {
        private static JsonElement getJson(String string) {
            return gson.fromJson(string, JsonElement.class);
        }

        public static String changeNetherBiomeSource(JsonElement json) {
            if (json == null) json = getJson("""
                    { "type": "minecraft:the_nether", "generator": { "type": "minecraft:noise", "biome_source": { "biomes": [
                      { "biome": "minecraft:nether_wastes", "parameters": { "continentalness": 0, "depth": 0, "erosion": 0,
                        "humidity": 0, "offset": 0, "temperature": 0, "weirdness": 0 } },
                      { "biome": "minecraft:soul_sand_valley", "parameters": { "continentalness": 0, "depth": 0,
                        "erosion": 0, "humidity": -0.5, "offset": 0, "temperature": 0, "weirdness": 0 } },
                      { "biome": "minecraft:crimson_forest", "parameters": { "continentalness": 0, "depth": 0, "erosion": 0,
                        "humidity": 0, "offset": 0, "temperature": 0.4, "weirdness": 0 } },
                      { "biome": "minecraft:warped_forest", "parameters": { "continentalness": 0, "depth": 0, "erosion": 0,
                        "humidity": 0.5, "offset": 0.375, "temperature": 0, "weirdness": 0 } },
                      { "biome": "minecraft:basalt_deltas", "parameters": { "continentalness": 0, "depth": 0, "erosion": 0,
                        "humidity": 0, "offset": 0.175, "temperature": -0.5, "weirdness": 0 } } ],
                    "type": "minecraft:multi_noise" }, "settings": "minecraft:nether" } }""");

            if (json.getAsJsonObject().get("generator").getAsJsonObject().get("biome_source").getAsJsonObject().get("type").getAsString().equals("minecraft:multi_noise")) {

                List<JsonElement> entries = json.getAsJsonObject().get("generator").getAsJsonObject().get("biome_source").getAsJsonObject().get("biomes").getAsJsonArray().asList();

                for (Map.Entry<ResourceKey<Biome>, Climate.ParameterPoint> entry : ModBiomes.getBiomeRegistry().entrySet()) {

                    JsonObject biome = new JsonObject();
                    biome.addProperty("biome", entry.getKey().location().toString());
                    JsonObject parameters = new JsonObject();
                    parameters.add("continentalness", ModBiomes.toJson(entry.getValue().continentalness()));
                    parameters.add("depth", ModBiomes.toJson(entry.getValue().depth()));
                    parameters.add("erosion", ModBiomes.toJson(entry.getValue().erosion()));
                    parameters.add("humidity", ModBiomes.toJson(entry.getValue().humidity()));
                    parameters.addProperty("offset", Climate.unquantizeCoord(entry.getValue().offset()));
                    parameters.add("temperature", ModBiomes.toJson(entry.getValue().temperature()));
                    parameters.add("weirdness", ModBiomes.toJson(entry.getValue().weirdness()));
                    biome.add("parameters", parameters);

                    entries.add(biome);
                }
            }

            return json.toString();
        }
    }
}
