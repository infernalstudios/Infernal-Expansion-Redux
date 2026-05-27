package com.infernalstudios.infernalexp.entities;

import com.infernalstudios.infernalexp.api.AbstractArrowEntityAccess;
import com.infernalstudios.infernalexp.module.ModEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GlowsilkArrowEntity extends Arrow {

    public GlowsilkArrowEntity(EntityType<? extends Arrow> type, Level level) {
        super(type, level);
        if (this instanceof AbstractArrowEntityAccess access) {
            access.infernalexp$setGlow(true);
        }
    }

    public GlowsilkArrowEntity(Level level, LivingEntity shooter, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(level, shooter, pickupItemStack, firedFromWeapon);
        if (this instanceof AbstractArrowEntityAccess access) {
            access.infernalexp$setGlow(true);
        }
    }

    public GlowsilkArrowEntity(Level level) {
        super(ModEntityTypes.GLOWSILK_ARROW.get(), level);
        if (this instanceof AbstractArrowEntityAccess access) {
            access.infernalexp$setGlow(true);
        }
    }

    @Override
    public @NotNull EntityType<?> getType() {
        return ModEntityTypes.GLOWSILK_ARROW.get();
    }
}