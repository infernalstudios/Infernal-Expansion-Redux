package com.infernalstudios.infernalexp.module;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.world.carver.ModConfiguredCarvers;
import com.infernalstudios.infernalexp.world.feature.ModPlacedFeatures;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.NetherPlacements;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.HashMap;
import java.util.Map;

public class ModBiomes {
    private static final Map<ResourceKey<Biome>, Climate.ParameterPoint> BIOME_REGISTRY = new HashMap<>();
    public static final ResourceKey<Biome> GLOWSTONE_CANYON = register("glowstone_canyon",
            Climate.parameters(0.7F, -0.2F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F)
    );
    public static final ResourceKey<Biome> DELTA_SHORES = ResourceKey.create(Registries.BIOME, IECommon.makeID("delta_shores"));
    /*
    public static final ResourceKey<Biome> DELTA_SHORES = register("delta_shores",
            Climate.parameters(-0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.195F)
    );
    */

    public static ResourceKey<Biome> register(String name, Climate.ParameterPoint parameterPoint) {
        return register(ResourceKey.create(Registries.BIOME, IECommon.makeID(name)), parameterPoint);
    }

    public static ResourceKey<Biome> register(ResourceKey<Biome> id, Climate.ParameterPoint parameterPoint) {
        BIOME_REGISTRY.put(id, parameterPoint);
        return id;
    }

    public static Map<ResourceKey<Biome>, Climate.ParameterPoint> getBiomeRegistry() {
        return BIOME_REGISTRY;
    }

    public static void load() {
    }

    public static void bootstrap(BootstapContext<Biome> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carvers = context.lookup(Registries.CONFIGURED_CARVER);

        context.register(GLOWSTONE_CANYON, createGlowstoneCanyon(placedFeatures, carvers));
        context.register(DELTA_SHORES, createDeltaShores(placedFeatures, carvers));
    }

    private static Biome createGlowstoneCanyon(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        MobSpawnSettings.Builder spawnSettings = new MobSpawnSettings.Builder();
        spawnSettings.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(ModEntityTypes.GLOWSILK_MOTH.get(), 1, 1, 1));
        spawnSettings.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntityTypes.GLOWSQUITO.get(), 3, 1, 5));
        spawnSettings.addMobCharge(ModEntityTypes.GLOWSILK_MOTH.get(), 0.1, 3.0);

        BiomeGenerationSettings.Builder generationSettings = new BiomeGenerationSettings.Builder(placedFeatures, carvers);
        generationSettings.addCarver(GenerationStep.Carving.AIR, ModConfiguredCarvers.GLOWSTONE_RAVINE);

        generationSettings.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, ModPlacedFeatures.GSC_BLACKSTONE_BLOBS);

        generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.HANGING_BROWN_MUSHROOM);
        generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.GLOWSTONE_SPIKE);
        generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.DEATH_PIT);

        generationSettings.addFeature(GenerationStep.Decoration.FLUID_SPRINGS, ModPlacedFeatures.GSC_SPRING_OPEN);
        generationSettings.addFeature(GenerationStep.Decoration.FLUID_SPRINGS, ModPlacedFeatures.GSC_SPRING_CLOSED);
        generationSettings.addFeature(GenerationStep.Decoration.FLUID_SPRINGS, OrePlacements.ORE_MAGMA);
        generationSettings.addFeature(GenerationStep.Decoration.FLUID_SPRINGS, ModPlacedFeatures.DULLTHORNS);
        generationSettings.addFeature(GenerationStep.Decoration.FLUID_SPRINGS, ModPlacedFeatures.GLOWLIGHT_FIRE);
        generationSettings.addFeature(GenerationStep.Decoration.FLUID_SPRINGS, ModPlacedFeatures.LUMINOUS_FUNGUS);

        generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, NetherPlacements.GLOWSTONE_EXTRA);
        generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, NetherPlacements.GLOWSTONE);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(2.0F)
                .downfall(0.0F)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(13408563)
                        .waterFogColor(10053120)
                        .fogColor(-2916568)
                        .skyColor(3197208)
                        .ambientParticle(new AmbientParticleSettings(ModParticleTypes.GLOWSTONE_SPARKLE, 0.002f))
                        .ambientLoopSound(Holder.direct(ModSounds.AMBIENT_GLOWSTONE_CANYON_LOOP.get()))
                        .ambientMoodSound(new AmbientMoodSettings(Holder.direct(ModSounds.AMBIENT_GLOWSTONE_CANYON_MOOD.get()), 6000, 8, 2.0D))
                        .ambientAdditionsSound(new AmbientAdditionsSettings(Holder.direct(ModSounds.AMBIENT_GLOWSTONE_CANYON_ADDITIONS.get()), 0.0111D))
                        .backgroundMusic(Musics.createGameMusic(Holder.direct(ModSounds.MUSIC_NETHER_GLOWSTONE_CANYON.get())))
                        .build())
                .mobSpawnSettings(spawnSettings.build())
                .generationSettings(generationSettings.build())
                .build();
    }

    private static Biome createDeltaShores(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        MobSpawnSettings.Builder spawnSettings = new MobSpawnSettings.Builder();
        spawnSettings.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(ModEntityTypes.GLOWSILK_MOTH.get(), 1, 1, 1));
        spawnSettings.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.STRIDER, 40, 1, 2));
        spawnSettings.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.GHAST, 20, 1, 1));
        spawnSettings.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.MAGMA_CUBE, 50, 2, 5));
        spawnSettings.addMobCharge(ModEntityTypes.GLOWSILK_MOTH.get(), 0.4, 1.0);

        BiomeGenerationSettings.Builder generationSettings = new BiomeGenerationSettings.Builder(placedFeatures, carvers);
        generationSettings.addCarver(GenerationStep.Carving.AIR, Carvers.NETHER_CAVE);

        generationSettings.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, NetherPlacements.BASALT_PILLAR);

        generationSettings.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, NetherPlacements.SMALL_BASALT_COLUMNS);

        generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, ModPlacedFeatures.ORE_BASALT_IRON_SHORES);
        generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.BASALT_BLOBS);
        generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.BLACKSTONE_BLOBS);
        generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.SPRING_DELTA);
        generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.PATCH_FIRE);
        generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.PATCH_SOUL_FIRE);
        generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.GLOWSTONE_EXTRA);
        generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.GLOWSTONE);
        generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, VegetationPlacements.BROWN_MUSHROOM_NETHER);
        generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, VegetationPlacements.RED_MUSHROOM_NETHER);
        generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_MAGMA);
        generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.SPRING_CLOSED_DOUBLE);
        generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_GOLD_DELTAS);
        generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_QUARTZ_DELTAS);
        generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_ANCIENT_DEBRIS_LARGE);
        generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_ANCIENT_DEBRIS_SMALL);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(2.0F)
                .downfall(0.0F)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(4159204)
                        .waterFogColor(329011)
                        .fogColor(6840176)
                        .skyColor(7254527)
                        .ambientParticle(new AmbientParticleSettings(ParticleTypes.WHITE_ASH, 0.118093334f))
                        .ambientLoopSound(SoundEvents.AMBIENT_BASALT_DELTAS_LOOP)
                        .ambientMoodSound(new AmbientMoodSettings((SoundEvents.AMBIENT_BASALT_DELTAS_MOOD), 6000, 8, 2.0D))
                        .ambientAdditionsSound(new AmbientAdditionsSettings((SoundEvents.AMBIENT_BASALT_DELTAS_ADDITIONS), 0.0111D))
                        .backgroundMusic(Musics.createGameMusic((SoundEvents.MUSIC_BIOME_BASALT_DELTAS)))
                        .build())
                .mobSpawnSettings(spawnSettings.build())
                .generationSettings(generationSettings.build())
                .build();
    }

    public static JsonElement toJson(Climate.Parameter value) {
        double min = Climate.unquantizeCoord(value.min());
        double max = Climate.unquantizeCoord(value.max());
        if (min == max) return new JsonPrimitive(min);

        JsonArray array = new JsonArray();
        array.add(min);
        array.add(max);
        return array;
    }
}