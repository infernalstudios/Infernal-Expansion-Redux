package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.entities.GlowsquitoEntity;
import com.infernalstudios.infernalexp.entities.VolineEntity;
import com.infernalstudios.infernalexp.registration.holders.EntityTypeDataHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;

import java.util.HashMap;
import java.util.Map;

public class ModEntityTypes {

    private static final Map<ResourceLocation, EntityTypeDataHolder<? extends Entity>> ENTITY_TYPE_REGISTRY = new HashMap<>();

    public static <T extends Entity> EntityTypeDataHolder<T> register(String name, EntityTypeDataHolder<T> entityTypeDataHolder) {
        ResourceLocation id = IECommon.makeID(name);
        ENTITY_TYPE_REGISTRY.put(id, entityTypeDataHolder);
        return entityTypeDataHolder;
    }

    public static Map<ResourceLocation, EntityTypeDataHolder<? extends Entity>> getEntityTypeRegistry() {
        return ENTITY_TYPE_REGISTRY;
    }

    public static final EntityTypeDataHolder<VolineEntity> VOLINE = register("voline", EntityTypeDataHolder.of(() ->
                    EntityTypeDataHolder.Builder.of(VolineEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 0.75F)
                            .fireImmune()
                            .build())
            .attributes(VolineEntity::createAttributes)
            .withTranslation("Voline"));

    public static final EntityTypeDataHolder<GlowsquitoEntity> GLOWSQUITO = register("glowsquito", EntityTypeDataHolder.of(() ->
                    EntityTypeDataHolder.Builder.of(GlowsquitoEntity::new, MobCategory.MONSTER)
                            .sized(0.8f, 0.5f)
                            .build())
            .attributes(GlowsquitoEntity::createAttributes)
            .withTranslation("Glowsquito"));

    public static void load() {}
}
