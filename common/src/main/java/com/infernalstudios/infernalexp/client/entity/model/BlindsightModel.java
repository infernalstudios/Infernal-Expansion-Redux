package com.infernalstudios.infernalexp.client.entity.model;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.client.entity.animation.BlindsightAnimation;
import com.infernalstudios.infernalexp.entities.BlindsightEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BlindsightModel extends IEHierarchicalModel<BlindsightEntity> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(IEConstants.MOD_ID, "blindsight"), "main");

    private final ModelPart root;

    public BlindsightModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot().addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition p_all = root.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0F, -5F, 1F));

        PartDefinition p_body = p_all.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0F, 5F, -3F));

        PartDefinition p_head2 = p_body.addOrReplaceChild("head2", CubeListBuilder.create(), PartPose.offset(0F, -0.7F, 8F));

        PartDefinition p_head = p_head2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 19).addBox(-8F, -2F, 3F, 16F, 2F, 12F).texOffs(0, 58).addBox(-8F, -1.9F, 3F, 16F, 1F, 12F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0F, -1.3F, -15F, -0.0873F, 0.0F, 0.0F));

        PartDefinition p_mouth = p_head.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(0, 0).addBox(-8F, -6F, -12F, 16F, 7F, 12F).texOffs(0, 73).addBox(-8F, -0.9F, -12F, 16F, 1F, 12F, new CubeDeformation(-0.05F)), PartPose.offset(0F, -2F, 15F));

        PartDefinition p_hairs = p_mouth.addOrReplaceChild("hairs", CubeListBuilder.create(), PartPose.offset(0F, -3F, -8F));

        PartDefinition p_lefthairs = p_hairs.addOrReplaceChild("lefthairs", CubeListBuilder.create(), PartPose.offsetAndRotation(8F, 0F, 0F, 0.0F, 0.0436F, 0.0F));

        PartDefinition p_tongue = p_head.addOrReplaceChild("tongue", CubeListBuilder.create().texOffs(0, 34).addBox(-3F, -2.0474F, -11.5671F, 6F, 4F, 11F), PartPose.offsetAndRotation(0F, -2F, 14.9F, -0.0873F, 0.0F, 0.0F));

        PartDefinition p_bothhands = p_body.addOrReplaceChild("bothhands", CubeListBuilder.create(), PartPose.offset(-5F, -2F, -1F));

        PartDefinition p_lefthand = p_bothhands.addOrReplaceChild("lefthand", CubeListBuilder.create().texOffs(0, 50).addBox(-2F, 0F, -2F, 4F, 2F, 4F), PartPose.offset(10F, 0F, 0F));

        PartDefinition p_righthand = p_bothhands.addOrReplaceChild("righthand", CubeListBuilder.create().mirror().texOffs(0, 50).addBox(-2F, 0F, -2F, 4F, 2F, 4F).mirror(false), PartPose.offset(0F, 0F, 0F));

        PartDefinition p_bothlegs = p_body.addOrReplaceChild("bothlegs", CubeListBuilder.create(), PartPose.offset(0F, -1F, 7F));

        PartDefinition p_leftleg = p_bothlegs.addOrReplaceChild("leftleg", CubeListBuilder.create().texOffs(35, 34).addBox(-3F, 1F, -5F, 7F, 1F, 7F), PartPose.offset(5F, -1F, 0F));

        PartDefinition p_rightleg = p_bothlegs.addOrReplaceChild("rightleg", CubeListBuilder.create().mirror().texOffs(35, 34).addBox(-4F, 1F, -5F, 7F, 1F, 7F).mirror(false), PartPose.offset(-5F, -1F, 0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(BlindsightEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetPose();
        if (!entity.isAttacking()) {
            if (entity.isJumping()) {
                this.animateAt(BlindsightAnimation.JUMP_LOOP, ageInTicks);
            } else if (entity.isResting() && entity.playRareIdle) {
                this.animateAt(BlindsightAnimation.IDLE_RARE, ageInTicks);
            } else {
                this.animateAt(BlindsightAnimation.IDLE, ageInTicks);
            }
        }

        this.animateOnce(entity.biteAnimationState, BlindsightAnimation.BITE, ageInTicks);
        this.animateOnce(entity.swallowAnimationState, BlindsightAnimation.SWALLOW, ageInTicks);
        this.animateOnce(entity.tongueTelegraphedAnimationState, BlindsightAnimation.TONGUE_ATTACK_TELEGRAPHED, ageInTicks);
        this.animateOnce(entity.tongueImmediateAnimationState, BlindsightAnimation.TONGUE_ATTACK_IMMEDIATE, ageInTicks);
        this.animateOnce(entity.alertAnimationState, BlindsightAnimation.LUMINOUS_PLAYER_ALERT, ageInTicks);
        this.animateOnce(entity.landAnimationState, BlindsightAnimation.LAND, ageInTicks);
    }
}
