package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.client.entity.model.*;
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
        register(BlindsightModel.LAYER_LOCATION, BlindsightModel::createBodyLayer);
        register(GlowsilkMothModel.LAYER_LOCATION, GlowsilkMothModel::createBodyLayer);
        register(GlowsquitoModel.LAYER_LOCATION, GlowsquitoModel::createBodyLayer);
        register(VolineModel.LAYER_LOCATION, VolineModel::createBodyLayer);
        register(VolineBigModel.LAYER_LOCATION, VolineBigModel::createBodyLayer);
        register(WarpbeetleModel.LAYER_LOCATION, WarpbeetleModel::createBodyLayer);
    }
}