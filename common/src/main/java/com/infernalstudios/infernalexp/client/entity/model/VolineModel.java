package com.infernalstudios.infernalexp.client.entity.model;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.entities.VolineEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class VolineModel<T extends VolineEntity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(IEConstants.MOD_ID, "voline"), "main");

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head; // Renamed from 'mouth' to match animation
    private final ModelPart frontRightLeg;
    private final ModelPart frontLeftLeg;
    private final ModelPart backRightLeg;
    private final ModelPart backLeftLeg;

    public VolineModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.frontRightLeg = root.getChild("frontrightleg");
        this.frontLeftLeg = root.getChild("frontleftleg");
        this.backRightLeg = root.getChild("backrightleg");
        this.backLeftLeg = root.getChild("backleftleg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 4.0F));
        PartDefinition head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -5.0F, -4.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        head.addOrReplaceChild("mouth_inside", CubeListBuilder.create().texOffs(0, 27).addBox(-4.0F, -4.0F, 1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(8, 27).addBox(0.0F, -4.0F, 1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(16, 27).addBox(0.0F, 0.0F, 1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(24, 27).addBox(-4.0F, 0.0F, 1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        body.addOrReplaceChild("mouth_inside_2", CubeListBuilder.create().texOffs(0, 27).addBox(-4.0F, -4.0F, 1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(8, 27).addBox(0.0F, -4.0F, 1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(16, 27).addBox(0.0F, 0.0F, 1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(24, 27).addBox(-4.0F, 0.0F, 1.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -4.0F, -1.5708F, 0.0F, 0.0F));

        partDefinition.addOrReplaceChild("frontrightleg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 22.0F, -2.0F));
        partDefinition.addOrReplaceChild("frontleftleg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 22.0F, -2.0F));
        partDefinition.addOrReplaceChild("backrightleg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 22.0F, 2.0F));
        partDefinition.addOrReplaceChild("backleftleg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 22.0F, 2.0F));

        return LayerDefinition.create(meshDefinition, 32, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animate(entity.idleAnimationState, VolineAnimation.idle, ageInTicks);
        this.animate(entity.walkAnimationState, VolineAnimation.walk, ageInTicks);
        this.animate(entity.eatAnimationState, VolineAnimation.eating, ageInTicks);
        this.animate(entity.sleepAnimationState, VolineAnimation.sleeping, ageInTicks);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack matrixStack, @NotNull VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}