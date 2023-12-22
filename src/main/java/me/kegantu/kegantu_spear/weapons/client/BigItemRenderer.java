package me.kegantu.kegantu_spear.weapons.client;

import me.kegantu.kegantu_spear.Interface.IKegantuSpearItem;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.profiler.Profiler;
import org.quiltmc.qsl.resource.loader.api.reloader.IdentifiableResourceReloader;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BigItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer, IdentifiableResourceReloader {

    private final Identifier id;
    private final Identifier itemId;
    private ItemRenderer itemRenderer;
    private BakedModel inventorySpearModel;
    private BakedModel worldSpearModel;
	private BakedModel worldUsingSpearModel;

    public BigItemRenderer(Identifier id){
		this.id = new Identifier(id.getNamespace(), id.getPath() + "_renderer");
		this.itemId = id;
    }

    @Override
    public void render(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.pop();
        matrices.push();
        if (mode != ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND && mode != ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND && mode != ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND && mode != ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND){
            itemRenderer.renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, this.inventorySpearModel);
        }
        else {
            boolean leftHanded;
            switch (mode){
                case FIRST_PERSON_LEFT_HAND, THIRD_PERSON_LEFT_HAND -> leftHanded = true;
                default -> leftHanded = false;
            }

			if (stack.getItem() instanceof IKegantuSpearItem){
				final MinecraftClient client = MinecraftClient.getInstance();
				if (client.player != null){
					if (client.player.isUsingItem()){
						if (mode == ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND || mode == ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND || mode == ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND || mode == ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND){
							itemRenderer.renderItem(stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, this.worldUsingSpearModel);
							return;
						}
					}
				}
			}



            itemRenderer.renderItem(stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, this.worldSpearModel);
        }

    }

	@Override
	public Identifier getQuiltId() {
		return this.id;
	}

	@Override
	public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
		return synchronizer.whenPrepared(Unit.INSTANCE).thenRunAsync(() -> {
			applyProfiler.startTick();
			applyProfiler.push("listener");
			final MinecraftClient client = MinecraftClient.getInstance();
			this.itemRenderer = client.getItemRenderer();
			this.inventorySpearModel = client.getBakedModelManager().getModel(new ModelIdentifier( this.itemId + "_gui", "inventory"));
			this.worldSpearModel = client.getBakedModelManager().getModel(new ModelIdentifier( this.itemId + "_handheld", "inventory"));
			this.worldUsingSpearModel = client.getBakedModelManager().getModel(new ModelIdentifier(this.itemId + "_throwing", "inventory"));
			applyProfiler.pop();
			applyProfiler.endTick();
		}, applyExecutor);
	}
}
