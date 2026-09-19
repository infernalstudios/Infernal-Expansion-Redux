package com.infernalstudios.infernalexp.client.entity.model;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;

/**
 * Shared base for keyframe-animated models.
 */
public abstract class IEHierarchicalModel<T extends Entity> extends HierarchicalModel<T> {

    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

    /**
     * Resets every part back to its bind pose. Must be called before applying any animation.
     */
    protected void resetPose() {
        this.root().getAllParts().forEach(ModelPart::resetPose);
    }

    /**
     * Applies an animation at the given point in time.
     */
    protected void animateAt(AnimationDefinition animation, float elapsedTicks) {
        KeyframeAnimations.animate(this, animation, (long) (elapsedTicks / 20.0F * 1000.0F), 1.0F, ANIMATION_VECTOR_CACHE);
    }

    /**
     * Plays a one shot animation from an {@link AnimationState}, stopping the state once the animation has run
     * its full length.
     */
    protected void animateOnce(AnimationState state, AnimationDefinition animation, float ageInTicks) {
        state.updateTime(ageInTicks, 1.0F);
        state.ifStarted(started -> {
            long accumulated = started.getAccumulatedTime();

            if (accumulated >= (long) (animation.lengthInSeconds() * 1000.0F)) {
                state.stop();
            } else {
                KeyframeAnimations.animate(this, animation, accumulated, 1.0F, ANIMATION_VECTOR_CACHE);
            }
        });
    }
}
