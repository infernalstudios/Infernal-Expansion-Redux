package com.infernalstudios.infernalexp.entities.ai;

import com.infernalstudios.infernalexp.entities.GlowsquitoEntity;
import com.infernalstudios.infernalexp.module.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.EnumSet;

public class SuckGlowstoneGoal extends Goal {
    private final GlowsquitoEntity mob;
    private final Level level;
    private int eatAnimationTick;
    private BlockPos targetPos = BlockPos.ZERO;

    public SuckGlowstoneGoal(GlowsquitoEntity mob) {
        this.mob = mob;
        this.level = mob.level();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (this.mob.getRandom().nextInt(50) != 0) return false;

        BlockPos pos = this.findGlowstone();
        if (pos != null) {
            this.targetPos = pos;
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.eatAnimationTick > 0 && this.targetPos != null && this.level.getBlockState(this.targetPos).is(Blocks.GLOWSTONE);
    }

    @Override
    public void start() {
        this.eatAnimationTick = this.adjustedTickDelay(40);
        this.mob.getNavigation().moveTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 1.0D);
        this.level.broadcastEntityEvent(this.mob, (byte) 10); // Syncs animation start
    }

    @Override
    public void stop() {
        this.eatAnimationTick = 0;
        this.targetPos = BlockPos.ZERO;
    }

    @Override
    public void tick() {
        this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);

        this.mob.getLookControl().setLookAt(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5);

        if (this.eatAnimationTick == 4) {
            if (this.level.getBlockState(targetPos).is(Blocks.GLOWSTONE)) {

                if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {

                    this.level.levelEvent(2001, targetPos, Block.getId(Blocks.GLOWSTONE.defaultBlockState()));

                    this.level.setBlock(targetPos, ModBlocks.DULLSTONE.get().defaultBlockState(), 3);

                    this.mob.ate();
                }
            }
        }
    }

    private BlockPos findGlowstone() {
        BlockPos mobPos = this.mob.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(mobPos.offset(-5, -2, -5), mobPos.offset(5, 2, 5))) {
            if (this.level.getBlockState(pos).is(Blocks.GLOWSTONE)) {
                return pos.immutable();
            }
        }
        return null;
    }

    public int getEatAnimationTick() {
        return this.eatAnimationTick;
    }
}