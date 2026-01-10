package com.infernalstudios.infernalexp.forge.module;

import com.infernalstudios.infernalexp.module.ModEntityTypes;
import com.infernalstudios.infernalexp.registration.holders.EntityTypeDataHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Map;

@Mod.EventBusSubscriber(modid = "infernalexp", bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityTypeModuleForge {

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