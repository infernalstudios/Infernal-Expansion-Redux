package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.client.entity.model.GlowsquitoModel;
import com.infernalstudios.infernalexp.client.entity.model.VolineModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModModelLayers {
    private static final Map<ModelLayerLocation, Supplier<LayerDefinition>> LAYER_REGISTRY = new HashMap<>();

    public static void register(ModelLayerLocation location, Supplier<LayerDefinition> definitionSupplier) {
        LAYER_REGISTRY.put(location, definitionSupplier);
    }

    public static Map<ModelLayerLocation, Supplier<LayerDefinition>> getLayerRegistry() {
        return LAYER_REGISTRY;
    }

    public static void load() {
        register(VolineModel.LAYER_LOCATION, VolineModel::createBodyLayer);
        register(GlowsquitoModel.LAYER_LOCATION, GlowsquitoModel::createBodyLayer);
    }
}