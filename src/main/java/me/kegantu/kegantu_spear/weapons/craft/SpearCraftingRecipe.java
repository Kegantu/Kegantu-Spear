package me.kegantu.kegantu_spear.weapons.craft;

import ladysnake.pickyourpoison.common.PickYourPoison;
import ladysnake.pickyourpoison.common.entity.PoisonDartFrogEntity;
import ladysnake.pickyourpoison.common.item.PoisonDartFrogBowlItem;
import me.kegantu.kegantu_spear.KegantuSpear;
import me.kegantu.kegantu_spear.weapons.KegantuSpearItem;
import me.kegantu.kegantu_spear.weapons.Spear;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.*;

import static ladysnake.pickyourpoison.common.item.PoisonDartFrogBowlItem.getFrogType;

public class SpearCraftingRecipe extends SpecialCraftingRecipe {

	private static final Ingredient FROGS = Ingredient.ofItems(
		PickYourPoison.GREEN_POISON_DART_FROG_BOWL,
		PickYourPoison.GOLDEN_POISON_DART_FROG_BOWL,
		PickYourPoison.RED_POISON_DART_FROG_BOWL,
		PickYourPoison.ORANGE_POISON_DART_FROG_BOWL,
		PickYourPoison.BLUE_POISON_DART_FROG_BOWL,
		PickYourPoison.CRIMSON_POISON_DART_FROG_BOWL);

	private static final Ingredient SPEAR = Ingredient.ofItems(KegantuSpear.SPEAR, KegantuSpear.HALBERD);

	public SpearCraftingRecipe(Identifier id) {
		super(id);
	}

	@Override
	public boolean matches(CraftingInventory inventory, World world) {

		for (int i = 0; i < 9; ++i) {
			ItemStack itemStack = inventory.getStack(i);
			if (!itemStack.isEmpty()) {
				if (FROGS.test(itemStack)) {
					for (int j = 0; j < 9; j++) {
						if (SPEAR.test(inventory.getStack(j))){
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	@Override
	public ItemStack craft(CraftingInventory inventory) {
		ItemStack spearStack = ItemStack.EMPTY;
		NbtList nbtList = new NbtList();
		Set<Item> poisonFrogsSet = new HashSet<>();

		for (int i = 0; i < 9; ++i) {
			ItemStack inventoryStack = inventory.getStack(i);
			if (!inventoryStack.isEmpty()) {
				if (inventoryStack.getItem() instanceof PoisonDartFrogBowlItem poisonFrogItem){
					poisonFrogsSet.add(poisonFrogItem);
				}

				if (inventoryStack.getItem() instanceof KegantuSpearItem){
					spearStack = inventoryStack.copy();
				}
			}
		}
		List<Item> poisonFrogsList = new ArrayList<>(poisonFrogsSet);

		if (EnchantmentHelper.getLevel(KegantuSpear.MORE_POISONS, spearStack) <= 0){
			NbtCompound nbtCompound = new NbtCompound();
			StatusEffectInstance statusEffectInstance = new StatusEffectInstance(PoisonDartFrogEntity.getFrogPoisonEffect(getFrogType(poisonFrogsList.get(0))));
			statusEffectInstance.writeNbt(nbtCompound);
			nbtList.add(nbtCompound);
		}
		else if (EnchantmentHelper.getLevel(KegantuSpear.MORE_POISONS, spearStack) > 0){
			for (Item poisonFrogItem : poisonFrogsList) {
				if (poisonFrogsList.indexOf(poisonFrogItem) >= 3){
					continue;
				}

				NbtCompound nbtCompound = new NbtCompound();
				StatusEffectInstance statusEffectInstance = new StatusEffectInstance(PoisonDartFrogEntity.getFrogPoisonEffect(getFrogType(poisonFrogItem)));
				statusEffectInstance.writeNbt(nbtCompound);
				nbtList.add(nbtCompound);
			}
		}
		NbtCompound compound = spearStack.getOrCreateNbt();

		compound.put("poisons", nbtList);

		return spearStack;
	}

	@Override
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getOutput() {
		return new ItemStack(KegantuSpear.SPEAR);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return KegantuSpear.SPEAR_CRAFTING_RECIPE;
	}

	@Override
	public DefaultedList<ItemStack> getRemainder(CraftingInventory inventory) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);

		for(int i = 0; i < defaultedList.size(); ++i) {
			Item item = inventory.getStack(i).getItem();
			if (item instanceof PoisonDartFrogBowlItem) {
				defaultedList.set(i, new ItemStack(item));
			}
		}

		return defaultedList;
	}
}
