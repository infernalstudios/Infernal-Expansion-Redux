package com.infernalstudios.infernalexp.items;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.entities.GlowsilkArrowEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GlowsilkBowItem extends BowItem {

    public GlowsilkBowItem(Properties properties) {
        super(properties);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player playerEntity) {
            ItemStack itemStack = playerEntity.getProjectile(stack);
            if (itemStack.isEmpty()) return;

            int ticksUsed = this.getUseDuration(stack, playerEntity) - timeLeft;
            if (ticksUsed < 0) return;

            float velocity = getPowerForTime(ticksUsed);
            if (velocity < 0.1D) return;

            List<ItemStack> projectiles = draw(stack, itemStack, playerEntity);
            if (level instanceof ServerLevel serverLevel && !projectiles.isEmpty()) {
                double speedMultiplier = IECommon.getConfig().common.miscellaneous.glowsilkBowSpeed;
                this.shoot(serverLevel, playerEntity, playerEntity.getUsedItemHand(), stack, projectiles, velocity * 6.0F * (float) speedMultiplier, 1.0F, velocity == 1.0F, null);
            }

            level.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + velocity * 0.5F);

            playerEntity.awardStat(Stats.ITEM_USED.get(this));
        }
    }

    @Override
    protected @NotNull Projectile createProjectile(@NotNull Level level, @NotNull LivingEntity shooter, @NotNull ItemStack weapon, @NotNull ItemStack ammo, boolean isCrit) {
        GlowsilkArrowEntity arrow = new GlowsilkArrowEntity(level, shooter, ammo.copyWithCount(1), weapon);
        arrow.setBaseDamage(arrow.getBaseDamage() / 2.0D);
        if (isCrit) {
            arrow.setCritArrow(true);
        }
        return arrow;
    }
}
