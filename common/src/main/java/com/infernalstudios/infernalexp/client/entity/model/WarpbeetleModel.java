package com.infernalstudios.infernalexp.client.entity.model;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.client.entity.animation.WarpbeetleAnimation;
import com.infernalstudios.infernalexp.entities.WarpbeetleEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class WarpbeetleModel extends IEHierarchicalModel<WarpbeetleEntity> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(IEConstants.MOD_ID, "warpbeetle"), "main");

    private final ModelPart root;

    public WarpbeetleModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot().addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition p_warpbeetle = root.addOrReplaceChild("warpbeetle", CubeListBuilder.create(), PartPose.offset(0F, -0.25F, 0F));

        PartDefinition p_body = p_warpbeetle.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5F, -5.75F, -9F, 10F, 6F, 17F), PartPose.offset(0F, -1F, 0F));

        PartDefinition p_head = p_body.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0F, -2F, -9F));

        PartDefinition p_head2 = p_head.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(43, 8).addBox(-3F, -1.75F, -4F, 6F, 4F, 4F).texOffs(0, 15).addBox(0F, -8.75F, -10F, 0F, 10F, 8F), PartPose.offset(0F, 0F, 0F));

        PartDefinition p_shells = p_body.addOrReplaceChild("shells", CubeListBuilder.create(), PartPose.offset(0F, -6F, -6F));

        PartDefinition p_left_shell = p_shells.addOrReplaceChild("left_shell", CubeListBuilder.create().texOffs(28, 29).addBox(-5F, -1.75F, 0F, 6F, 6F, 16F), PartPose.offset(5F, 0F, 0F));

        PartDefinition p_right_shell = p_shells.addOrReplaceChild("right_shell", CubeListBuilder.create().texOffs(0, 23).addBox(-1F, -1.75F, 0F, 6F, 6F, 16F), PartPose.offset(-5F, 0F, 0F));

        PartDefinition p_wings = p_body.addOrReplaceChild("wings", CubeListBuilder.create(), PartPose.offset(0F, -6F, -5F));

        PartDefinition p_left_wing = p_wings.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(41, 30).addBox(-3F, 0.24F, 0F, 6F, 0F, 15F), PartPose.offset(1F, 0F, 0F));

        PartDefinition p_right_wing = p_wings.addOrReplaceChild("right_wing", CubeListBuilder.create().mirror().texOffs(41, 30).addBox(-3F, 0.24F, 0F, 6F, 0F, 15F).mirror(false), PartPose.offset(-1F, 0F, 0F));

        PartDefinition p_legs = p_warpbeetle.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(0F, -2F, -1F));

        PartDefinition p_left_legs = p_legs.addOrReplaceChild("left_legs", CubeListBuilder.create(), PartPose.offset(4F, 0F, 0F));

        PartDefinition p_left_leg_3 = p_left_legs.addOrReplaceChild("left_leg_3", CubeListBuilder.create().texOffs(22, 29).addBox(0F, 0.25F, -1F, 5F, 0F, 6F), PartPose.offsetAndRotation(0F, 0F, 5F, 0.0F, 0.0F, 0.3927F));

        PartDefinition p_left_leg_1 = p_left_legs.addOrReplaceChild("left_leg_1", CubeListBuilder.create().texOffs(31, 6).addBox(0F, 0.25F, -5F, 5F, 0F, 6F), PartPose.offsetAndRotation(0F, 0F, -5F, 0.0F, 0.0F, 0.3927F));

        PartDefinition p_left_leg_2 = p_left_legs.addOrReplaceChild("left_leg_2", CubeListBuilder.create().texOffs(31, 0).addBox(0F, 0.25F, -1F, 5F, 0F, 6F), PartPose.offsetAndRotation(0F, 0F, -1F, 0.0F, 0.0F, 0.3927F));

        PartDefinition p_right_legs = p_legs.addOrReplaceChild("right_legs", CubeListBuilder.create(), PartPose.offset(-4F, 0F, 0F));

        PartDefinition p_right_leg_1 = p_right_legs.addOrReplaceChild("right_leg_1", CubeListBuilder.create().texOffs(22, 23).addBox(-5F, 0.25F, -5F, 5F, 0F, 6F), PartPose.offsetAndRotation(0F, 0F, -5F, 0.0F, 0.0F, -0.3927F));

        PartDefinition p_right_leg_2 = p_right_legs.addOrReplaceChild("right_leg_2", CubeListBuilder.create().texOffs(0, 6).addBox(-5F, 0.25F, -1F, 5F, 0F, 6F), PartPose.offsetAndRotation(0F, 0F, -1F, 0.0F, 0.0F, -0.3927F));

        PartDefinition p_right_leg_3 = p_right_legs.addOrReplaceChild("right_leg_3", CubeListBuilder.create().texOffs(0, 0).addBox(-5F, 0.25F, -1F, 5F, 0F, 6F), PartPose.offsetAndRotation(0F, 0F, 5F, 0.0F, 0.0F, -0.3927F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(WarpbeetleEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetPose();
        if (entity.isFlying() || (!entity.isPassenger() && !entity.onGround() && entity.fallDistance > 0.15F)) {
            this.animateAt(WarpbeetleAnimation.FLY, ageInTicks);
        } else if (!entity.isPassenger() && limbSwingAmount > 0.01F) {
            this.animateAt(WarpbeetleAnimation.WALK, ageInTicks);
        } else {
            this.animateAt(WarpbeetleAnimation.IDLE, ageInTicks);
        }

        if (entity.isDancing()) {
            this.animateAt(WarpbeetleAnimation.DANCE, ageInTicks);
        }

        this.animateOnce(entity.attackAnimationState, WarpbeetleAnimation.ATTACK, ageInTicks);
    }
}
