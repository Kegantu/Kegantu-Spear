package me.kegantu.kegantu_spear.entity.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import me.kegantu.kegantu_spear.KegantuSpear;
import me.kegantu.kegantu_spear.entity.SpearEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
public class SpearEntityRenderer extends EntityRenderer<SpearEntity> {
	private final Identifier texture = new Identifier(KegantuSpear.MOD_ID, "textures/entity/spear.png");
	private final SpearEntityModel model;

	public SpearEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer modelLayer) {
		super(context);
		this.model = new SpearEntityModel(context.getPart(modelLayer));
	}

	public void render(SpearEntity spearEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.lerp(g, spearEntity.prevYaw, spearEntity.getYaw()) - 90.0F));
		matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.lerp(g, spearEntity.prevPitch, spearEntity.getPitch()) + 90.0F));
		VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumerProvider, this.model.getLayer(this.getTexture(spearEntity)), false, spearEntity.isEnchanted());
		this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
		matrixStack.pop();
		super.render(spearEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public Identifier getTexture(SpearEntity spearEntity) {
		return this.texture;
	}
}
