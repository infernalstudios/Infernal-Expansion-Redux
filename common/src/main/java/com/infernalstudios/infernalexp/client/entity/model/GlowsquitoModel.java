
package com.infernalstudios.infernalexp.client.entity.model;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.client.entity.animation.GlowsquitoAnimation;
import com.infernalstudios.infernalexp.entities.GlowsquitoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class GlowsquitoModel<T extends GlowsquitoEntity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(IEConstants.MOD_ID, "glowsquito"), "main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart stinger;
    private final ModelPart body;
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    private final ModelPart butt;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public GlowsquitoModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.stinger = root.getChild("stinger");
        this.body = root.getChild("body");
        this.leftWing = root.getChild("left_wing");
        this.rightWing = root.getChild("right_wing");
        this.butt = root.getChild("butt");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        partDefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(26, 19).addBox(-2.0F, -2.6F, -4.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 19.6F, -4.0F));
        partDefinition.addOrReplaceChild("stinger", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -6.5F, 1.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 19.0F, -7.0F, 0.3927F, 0.0F, 0.0F));
        partDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 17).addBox(-3.0F, -4.5F, -0.636F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 19.5F, -4.0F, -0.1745F, 0.0F, 0.0F));
        partDefinition.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(27, 9).addBox(0.0F, -0.01F, -0.6F, 5.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.3F, 14.7F, -3.0F, 0.6545F, 0.2182F, 0.0F));
        partDefinition.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(17, 0).addBox(-5.0F, -0.01F, -0.6F, 5.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.3F, 14.7F, -3.0F, 0.6545F, -0.2182F, 0.0F));
        partDefinition.addOrReplaceChild("butt", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -3.2218F, -1.6213F, 8.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 18.3218F, 2.1213F, -0.2618F, 0.0F, 0.0F));
        partDefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(2, 0).addBox(0.01F, 0.0F, -0.5F, 0.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 19.0F, -3.5F));
        partDefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(4, 0).addBox(-0.01F, 0.0F, -0.5F, 0.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 19.0F, -3.5F));
        partDefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(6, 0).addBox(0.01F, -1.5F, -0.5F, 0.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 21.0F, -1.5F));
        partDefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-0.01F, -1.5F, -0.5F, 0.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 21.0F, -1.5F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animate(entity.idleAnimationState, GlowsquitoAnimation.idle, ageInTicks);
        this.animate(entity.flyAnimationState, GlowsquitoAnimation.fly, ageInTicks);
        this.animate(entity.eatAnimationState, GlowsquitoAnimation.eat, ageInTicks);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack matrixStack, @NotNull VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}