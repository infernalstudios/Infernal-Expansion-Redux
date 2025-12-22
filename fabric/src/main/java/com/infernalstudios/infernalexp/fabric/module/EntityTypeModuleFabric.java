package com.infernalstudios.infernalexp.fabric.module;

import com.infernalstudios.infernalexp.module.ModEntityTypes;
import com.infernalstudios.infernalexp.registration.holders.EntityTypeDataHolder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.Map;

public class EntityTypeModuleFabric {

    public static void registerEntities() {
        for (Map.Entry<ResourceLocation, EntityTypeDataHolder<? extends Entity>> entry : ModEntityTypes.getEntityTypeRegistry().entrySet()) {

            // Register entity type
            Registry.register(BuiltInRegistries.ENTITY_TYPE, entry.getKey(), entry.getValue().get());

            // Register entity attributes, if present
            if (entry.getValue().hasAttributes()) {
                registerAttributeHelper(entry.getValue());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends LivingEntity> void registerAttributeHelper(EntityTypeDataHolder<?> holder) {
        EntityTypeDataHolder<T> typedHolder = (EntityTypeDataHolder<T>) holder;

        AttributeSupplier.Builder attributesBuilder = typedHolder.getAttributesSupplier().get();
        FabricDefaultAttributeRegistry.register(typedHolder.get(), attributesBuilder);
    }
}