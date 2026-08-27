package com.infernalstudios.infernalexp.neoforge.module;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.module.ModEntityTypes;
import com.infernalstudios.infernalexp.registration.holders.EntityTypeDataHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Map;

@EventBusSubscriber(modid = IEConstants.MOD_ID)
public class EntityTypeModuleNeoForge {

    @SubscribeEvent
    public static void registerEntityTypes(RegisterEvent event) {
        event.register(Registries.ENTITY_TYPE, helper -> {
            for (Map.Entry<ResourceLocation, EntityTypeDataHolder<? extends Entity>> entry : ModEntityTypes.getEntityTypeRegistry().entrySet()) {
                helper.register(entry.getKey(), entry.getValue().get());
            }
        });
    }

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        for (Map.Entry<ResourceLocation, EntityTypeDataHolder<? extends Entity>> entry : ModEntityTypes.getEntityTypeRegistry().entrySet()) {
            if (entry.getValue().hasAttributes()) {
                registerAttributesHelper(event, entry.getValue());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends LivingEntity> void registerAttributesHelper(EntityAttributeCreationEvent event, EntityTypeDataHolder<?> holder) {
        EntityTypeDataHolder<T> typedHolder = (EntityTypeDataHolder<T>) holder;

        AttributeSupplier.Builder builder = typedHolder.getAttributesSupplier().get();

        event.put(typedHolder.get(), builder.build());
    }
}