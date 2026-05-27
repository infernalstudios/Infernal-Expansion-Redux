package com.infernalstudios.infernalexp.api;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FireType {
    private static final Map<ResourceLocation, FireType> FIRE_TYPES = new HashMap<>();

    private final ResourceLocation name;
    private final ResourceLocation block;
    private final ResourceLocation sprite0;
    private final ResourceLocation sprite1;

    private FireType(ResourceLocation name, ResourceLocation block, ResourceLocation sprite0, ResourceLocation sprite1) {
        this.name = name;
        this.block = block;
        this.sprite0 = sprite0;
        this.sprite1 = sprite1;

        FIRE_TYPES.put(name, this);
    }

    public static FireType register(ResourceLocation name) {
        return register(name, name, "block/" + name.getPath());
    }

    public static FireType register(ResourceLocation name, ResourceLocation block) {
        return register(name, block, "block/" + name.getPath());
    }

    public static FireType register(ResourceLocation name, ResourceLocation block, String spriteLocation) {
        return register(name, block, spriteLocation + "_0", spriteLocation + "_1");
    }

    public static FireType register(ResourceLocation name, ResourceLocation block, String spriteLocation0, String spriteLocation1) {
        if (FIRE_TYPES.containsKey(name))
            return null;
            //throw new IllegalStateException(name.toString() + " already exists in the FireType registry.");

        return new FireType(name, block, ResourceLocation.fromNamespaceAndPath(name.getNamespace(), spriteLocation0), ResourceLocation.fromNamespaceAndPath(name.getNamespace(), spriteLocation1));
    }

    public static FireType getOrDefault(ResourceLocation name, FireType defaultType) {
        return FIRE_TYPES.getOrDefault(name, defaultType);
    }

    public static Set<FireType> getFireTypes() {
        return new HashSet<>(FIRE_TYPES.values());
    }

    public ResourceLocation getName() {
        return this.name;
    }

    public ResourceLocation getBlock() {
        return this.block;
    }

    public Material getSprite0() {
        return MaterialCache.getOrCreate(this.sprite0);
    }

    public Material getSprite1() {
        return MaterialCache.getOrCreate(this.sprite1);
    }

    private static final class MaterialCache {
        private static final Map<ResourceLocation, Material> RENDER_MATERIALS = new HashMap<>();

        private static Material getOrCreate(ResourceLocation resourceLocation) {
            if (!RENDER_MATERIALS.containsKey(resourceLocation))
                RENDER_MATERIALS.put(resourceLocation, new Material(TextureAtlas.LOCATION_BLOCKS, resourceLocation));

            return RENDER_MATERIALS.get(resourceLocation);
        }
    }
}