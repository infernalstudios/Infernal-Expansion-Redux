package com.infernalstudios.infernalexp.items;

import com.infernalstudios.infernalexp.module.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class GlowsilkMothBottleItem extends Item {
    public GlowsilkMothBottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        }

        ItemStack itemStack = context.getItemInHand();
        BlockPos blockPos = context.getClickedPos().relative(context.getClickedFace());
        Player player = context.getPlayer();

        ModEntityTypes.GLOWSILK_MOTH.get().spawn((ServerLevel) level, itemStack, player, blockPos, MobSpawnType.BUCKET, true, false);

        level.gameEvent(player, GameEvent.ENTITY_PLACE, blockPos);
        level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.NEUTRAL, 1.0F, 1.0F);

        if (player != null && !player.getAbilities().instabuild) {
            player.setItemInHand(context.getHand(), new ItemStack(Items.GLASS_BOTTLE));
        }

        return InteractionResult.CONSUME;
    }
}