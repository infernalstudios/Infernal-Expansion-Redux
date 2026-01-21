package com.infernalstudios.infernalexp.entities.ai.glowsilkmoth;

import com.infernalstudios.infernalexp.entities.GlowsilkMothEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class MothHoverGoal extends Goal {
    private final GlowsilkMothEntity moth;
    private int hoverTime;

    public MothHoverGoal(GlowsilkMothEntity moth) {
        this.moth = moth;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.moth.getNavigation().isInProgress() && this.moth.getRandom().nextFloat() < 0.015F;
    }

    @Override
    public boolean canContinueToUse() {
        return this.hoverTime > 0;
    }

    @Override
    public void start() {
        this.hoverTime = 10 + this.moth.getRandom().nextInt(20);

        this.moth.getNavigation().stop();

        this.moth.getMoveControl().setWantedPosition(
                this.moth.getX(),
                this.moth.getY(),
                this.moth.getZ(),
                0.0D
        );
    }

    @Override
    public void tick() {
        this.hoverTime--;
    }
}