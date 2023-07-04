package me.kegantu.kegantu_spear;

import me.kegantu.kegantu_spear.enchantments.AffectMultipleTargetsEnchantment;
import me.kegantu.kegantu_spear.enchantments.BoomEnchantment;
import me.kegantu.kegantu_spear.enchantments.MorePoisonsEnchantment;
import me.kegantu.kegantu_spear.entity.SpearEntity;
import me.kegantu.kegantu_spear.weapons.Halberd;
import me.kegantu.kegantu_spear.weapons.Spear;
import me.kegantu.kegantu_spear.weapons.craft.SpearCraftingRecipe;
import me.kegantu.kegantu_spear.weapons.toolMaterial.SpearMaterial;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KegantuSpear implements ModInitializer {
	public static final String MOD_ID = "kegantu_spear";

	public static final RecipeType<SpearCraftingRecipe> SPEAR_CRAFTING_RECIPE_RECIPE_TYPE = new RecipeType<>() {};

	public static final SpecialRecipeSerializer<SpearCraftingRecipe> SPEAR_CRAFTING_RECIPE = RecipeSerializer.register(
		"kegantu_spear:crafting_special_spear", new SpecialRecipeSerializer<>(SpearCraftingRecipe::new)
	);

	public static final Item SPEAR = new Spear(SpearMaterial.INSTANCE, 8, -2.2f, new QuiltItemSettings().rarity(Rarity.COMMON).group(ItemGroup.COMBAT));
	public static final Item HALBERD = new Halberd(SpearMaterial.INSTANCE, 9, -2.8f, new QuiltItemSettings().rarity(Rarity.EPIC).group(ItemGroup.COMBAT));

	public static final Enchantment AFFECT_MULTIPLE_TARGETS_ENCHANTMENT = new AffectMultipleTargetsEnchantment();

	public static final Enchantment BOOM = new BoomEnchantment();

	public static final Enchantment MORE_POISONS = new MorePoisonsEnchantment();

	public static final EntityType<SpearEntity> SPEAR_ENTITY = register("spear", createEntityType(SpearEntity::new));
	public static final Logger LOGGER = LoggerFactory.getLogger("Kegantu Spear");

	@Override
	public void onInitialize(ModContainer mod) {
		registerItems();
		registerEnchantments();
		Registry.register(Registry.RECIPE_TYPE, new Identifier(MOD_ID, "spear"), SPEAR_CRAFTING_RECIPE_RECIPE_TYPE);
	}

	public static void registerItems(){
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "spear"), SPEAR);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "halberd"), HALBERD);
	}

	public static void registerEnchantments(){
		Registry.register(Registry.ENCHANTMENT, new Identifier(MOD_ID, "affect_multiple_targets"), AFFECT_MULTIPLE_TARGETS_ENCHANTMENT);
		Registry.register(Registry.ENCHANTMENT, new Identifier(MOD_ID, "boom"), BOOM);
		Registry.register(Registry.ENCHANTMENT, new Identifier(MOD_ID, "more_poisons"), MORE_POISONS);
	}

	private static <T extends Entity> EntityType<T> register(String s, EntityType<T> bombEntityType) {
		return Registry.register(Registry.ENTITY_TYPE, MOD_ID + ":" + s, bombEntityType);
	}

	private static <T extends Entity> EntityType<T> createEntityType(EntityType.EntityFactory<T> factory) {
		return QuiltEntityTypeBuilder.create(SpawnGroup.MISC, factory).setDimensions(EntityDimensions.changing(0.5f, 0.5f)).maxBlockTrackingRange(4).trackingTickInterval(20).build();
	}
}
