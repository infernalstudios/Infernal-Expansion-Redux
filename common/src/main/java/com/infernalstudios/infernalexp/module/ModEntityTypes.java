package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.entities.*;
import com.infernalstudios.infernalexp.registration.holders.EntityTypeDataHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;

import java.util.HashMap;
import java.util.Map;

public class ModEntityTypes {

    private static final Map<ResourceLocation, EntityTypeDataHolder<? extends Entity>> ENTITY_TYPE_REGISTRY = new HashMap<>();
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
                            .fireImmune()
                            .build())
            .attributes(GlowsquitoEntity::createAttributes)
            .withTranslation("Glowsquito"));
    public static final EntityTypeDataHolder<GlowsilkArrowEntity> GLOWSILK_ARROW = register("glowsilk_arrow", EntityTypeDataHolder.of(() ->
                    EntityTypeDataHolder.Builder.<GlowsilkArrowEntity>of(
                                    GlowsilkArrowEntity::new,
                                    MobCategory.MISC
                            )
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(4)
                            .updateInterval(20)
                            .build())
            .withTranslation("Glowsilk Arrow"));
    public static final EntityTypeDataHolder<GlowsilkMothEntity> GLOWSILK_MOTH = register("glowsilk_moth", EntityTypeDataHolder.of(() ->
                    EntityTypeDataHolder.Builder.of(GlowsilkMothEntity::new, MobCategory.AMBIENT)
                            .sized(0.5F, 0.9F)
                            .fireImmune()
                            .build())
            .attributes(GlowsilkMothEntity::createAttributes)
            .withTranslation("Glowsilk Moth"));
    public static final EntityTypeDataHolder<BlindsightEntity> BLINDSIGHT = register("blindsight", EntityTypeDataHolder.of(() ->
                    EntityTypeDataHolder.Builder.of(BlindsightEntity::new, MobCategory.MONSTER)
                            .sized(1.0F, 0.6F)
                            .clientTrackingRange(8)
                            .fireImmune()
                            .build())
            .attributes(BlindsightEntity::createAttributes)
            .withTranslation("Blindsight"));
    public static final EntityTypeDataHolder<ThrowableMagmaCreamEntity> THROWABLE_MAGMA_CREAM = register("throwable_magma_cream", EntityTypeDataHolder.of(() ->
                    EntityTypeDataHolder.Builder.<ThrowableMagmaCreamEntity>of(ThrowableMagmaCreamEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build())
            .withTranslation("Throwable Magma Cream"));
    public static final EntityTypeDataHolder<ThrowableFireChargeEntity> THROWABLE_FIRE_CHARGE = register("throwable_fire_charge", EntityTypeDataHolder.of(() ->
                    EntityTypeDataHolder.Builder.<ThrowableFireChargeEntity>of(ThrowableFireChargeEntity::new, MobCategory.MISC)
                            .sized(0.3125F, 0.3125F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build())
            .withTranslation("Throwable Fire Charge"));

    public static <T extends Entity> EntityTypeDataHolder<T> register(String name, EntityTypeDataHolder<T> entityTypeDataHolder) {
        ResourceLocation id = IECommon.makeID(name);
        ENTITY_TYPE_REGISTRY.put(id, entityTypeDataHolder);
        return entityTypeDataHolder;
    }

    public static Map<ResourceLocation, EntityTypeDataHolder<? extends Entity>> getEntityTypeRegistry() {
        return ENTITY_TYPE_REGISTRY;
    }

    public static void load() {
    }
}