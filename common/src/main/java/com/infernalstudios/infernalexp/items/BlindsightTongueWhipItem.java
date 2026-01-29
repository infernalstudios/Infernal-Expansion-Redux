package com.infernalstudios.infernalexp.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlindsightTongueWhipItem extends Item {
    public static final int CHARGE_CAP_TICKS = 9;
    public static final int ATTACK_DURATION_TICKS = 10;
    public static final Map<Integer, Long> CLIENT_ATTACK_TIMES = new HashMap<>();

    public BlindsightTongueWhipItem(Properties properties) {
        super(properties);
    }

    public static void setAttacking(ItemStack stack, boolean attacking) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean("Attacking", attacking);
    }

    public static boolean isAttacking(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean("Attacking");
    }

    public static void setAttackStartTick(ItemStack stack, long tick) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putLong("AttackStartTick", tick);
    }

    public static long getAttackStartTick(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.getLong("AttackStartTick") : 0L;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 72000;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        setAttacking(itemstack, false);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public int getEnchantmentValue() {
        return 14;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            int useTime = this.getUseDuration(stack) - timeLeft;

            if (useTime < CHARGE_CAP_TICKS) {
                setAttacking(stack, false);
                return;
            }

            setAttacking(stack, true);

            long gameTime = level.getGameTime();
            setAttackStartTick(stack, gameTime);
            if (level.isClientSide) {
                CLIENT_ATTACK_TIMES.put(player.getId(), gameTime);
            }

            if (!level.isClientSide) {
                stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
                performWhipAttack(level, player);
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            player.awardStat(Stats.ITEM_USED.get(this));
        }
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if (isAttacking(stack)) {
            long gameTime = level.getGameTime();
            long startTick = getAttackStartTick(stack);

            if (gameTime - startTick > ATTACK_DURATION_TICKS + 3) {
                setAttacking(stack, false);
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    private void performWhipAttack(Level level, Player player) {
        double range = 4.0D;
        double width = 2.0D;

        Vec3 lookVec = player.getLookAngle();
        Vec3 playerPos = player.getEyePosition();
        Vec3 targetPos = playerPos.add(lookVec.scale(range));

        AABB attackBox = new AABB(playerPos, targetPos).inflate(width, width, width);
        List<LivingEntity> potentialTargets = level.getEntitiesOfClass(LivingEntity.class, attackBox);

        int knockbackLevel = EnchantmentHelper.getKnockbackBonus(player);

        float knockbackStrength = 2.5F + (float) knockbackLevel * 0.5F;

        for (LivingEntity target : potentialTargets) {
            if (target != player && player.hasLineOfSight(target)) {
                Vec3 dirToTarget = target.position().subtract(player.position()).normalize();

                if (lookVec.dot(dirToTarget) > 0.5) {
                    target.knockback(knockbackStrength, player.getX() - target.getX(), player.getZ() - target.getZ());
                    target.hurt(level.damageSources().playerAttack(player), 4.0F);
                }
            }
        }
    }
}