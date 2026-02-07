package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.IECommon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModEnchantments {
    private static final Map<ResourceLocation, EnchantmentRegistryObj> ENCHANTMENT_REGISTRY = new HashMap<>();
    public static final Supplier<Enchantment> DISARMING = register("disarming", () ->
            new Enchantment(Enchantment.Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND}) {
                @Override
                public int getMaxLevel() {
                    return 3;
                }

                @Override
                public int getMinCost(int level) {
                    return 15 + (level - 1) * 9;
                }

                @Override
                public int getMaxCost(int level) {
                    return super.getMinCost(level) + 50;
                }
            });
    public static final Supplier<Enchantment> LEAPING = register("leaping", () ->
            new Enchantment(Enchantment.Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND}) {
                @Override
                public int getMaxLevel() {
                    return 1;
                }
            });
    public static final Supplier<Enchantment> ILLUMINATING = register("illuminating", () ->
            new Enchantment(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND}) {
                @Override
                public int getMaxLevel() {
                    return 2;
                }

                @Override
                protected boolean checkCompatibility(@NotNull Enchantment other) {
                    return super.checkCompatibility(other) && other != Enchantments.FIRE_ASPECT;
                }
            });
    public static final Supplier<Enchantment> LASHING = register("lashing", () ->
            new Enchantment(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND}) {
                @Override
                public int getMaxLevel() {
                    return 5;
                }
            });

    public static Supplier<Enchantment> register(String name, Supplier<Enchantment> enchantmentSupplier) {
        ResourceLocation id = IECommon.makeID(name);
        EnchantmentRegistryObj obj = new EnchantmentRegistryObj(id, enchantmentSupplier);
        ENCHANTMENT_REGISTRY.put(id, obj);
        return obj;
    }

    public static void load() {
    }

    public static Map<ResourceLocation, EnchantmentRegistryObj> getEnchantmentRegistry() {
        return ENCHANTMENT_REGISTRY;
    }

    public static class EnchantmentRegistryObj implements Supplier<Enchantment> {
        private final ResourceLocation id;
        private final Supplier<Enchantment> factory;
        private Enchantment registeredInstance = null;

        public EnchantmentRegistryObj(ResourceLocation id, Supplier<Enchantment> factory) {
            this.id = id;
            this.factory = factory;
        }

        @Override
        public Enchantment get() {
            if (registeredInstance != null) {
                return registeredInstance;
            }
            return factory.get();
        }

        public void setRegisteredInstance(Enchantment instance) {
            this.registeredInstance = instance;
        }

        public ResourceLocation getId() {
            return id;
        }

        public Supplier<Enchantment> getFactory() {
            return factory;
        }
    }
}