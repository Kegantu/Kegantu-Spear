package me.kegantu.kegantu_spear;

import me.kegantu.kegantu_spear.entity.client.SpearEntityModel;
import me.kegantu.kegantu_spear.entity.client.SpearEntityRenderer;
import me.kegantu.kegantu_spear.weapons.client.SpearItemRenderer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.ItemConvertible;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

import static me.kegantu.kegantu_spear.KegantuSpear.SPEAR;
import static me.kegantu.kegantu_spear.KegantuSpear.SPEAR_ENTITY;

@ClientOnly
public class KegantuSpearClient implements ClientModInitializer {
	public static final EntityModelLayer MODEL_SPEAR_LAYER = new EntityModelLayer(new Identifier(KegantuSpear.MOD_ID, "spear"), "main");

	@Override
	public void onInitializeClient(ModContainer mod) {
		registerBigRenderer(SPEAR);
		EntityRendererRegistry.register(SPEAR_ENTITY, ctx -> new SpearEntityRenderer(ctx, MODEL_SPEAR_LAYER));
		EntityModelLayerRegistry.registerModelLayer(MODEL_SPEAR_LAYER, SpearEntityModel::getTexturedModelData);
	}

	private void registerBigRenderer(ItemConvertible item) {
		Identifier spearId = Registry.ITEM.getId(item.asItem());
		SpearItemRenderer bigItemRenderer = new SpearItemRenderer(spearId);
		ResourceLoader.get(ResourceType.CLIENT_RESOURCES).registerReloader(bigItemRenderer);
		BuiltinItemRendererRegistry.INSTANCE.register(item, bigItemRenderer);
		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
			out.accept(new ModelIdentifier(spearId + "_gui", "inventory"));
			out.accept(new ModelIdentifier(spearId + "_handheld", "inventory"));
			out.accept(new ModelIdentifier(spearId + "_throwing", "inventory"));
		});
	}
}
