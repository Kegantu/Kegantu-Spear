package me.kegantu.kegantu_spear.weapons.craft;

import ladysnake.pickyourpoison.common.PickYourPoison;
import ladysnake.pickyourpoison.common.entity.PoisonDartFrogEntity;
import ladysnake.pickyourpoison.common.item.PoisonDartFrogBowlItem;
import me.kegantu.kegantu_spear.KegantuSpear;
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

	private static final Ingredient SPEAR = Ingredient.ofItems(KegantuSpear.SPEAR);

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
							if (EnchantmentHelper.getLevel(KegantuSpear.MORE_POISONS, inventory.getStack(j)) == 0){
								return false;
							}

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
		ItemStack spearStack = new ItemStack(KegantuSpear.SPEAR);
		NbtCompound compound = spearStack.getOrCreateNbt();
		NbtList nbtList = new NbtList();
		NbtList enchantmentsNBTList = new NbtList();
		Set<Item> poisonFrogsSet = new HashSet<>();

		for (int i = 0; i < 9; ++i) {
			ItemStack frogStack = inventory.getStack(i);
			if (!frogStack.isEmpty()) {
				if (frogStack.getItem() instanceof PoisonDartFrogBowlItem poisonFrogItem){
					poisonFrogsSet.add(poisonFrogItem);
				}

				if (frogStack.getItem() instanceof Spear){
					enchantmentsNBTList = frogStack.getEnchantments();
				}
			}
		}
		List<Item> poisonFrogsList = new ArrayList<>(poisonFrogsSet);

		for (Item poisonFrogItem : poisonFrogsList) {
			if (poisonFrogsList.indexOf(poisonFrogItem) >= 3){
				continue;
			}

			NbtCompound nbtCompound = new NbtCompound();
			StatusEffectInstance statusEffectInstance = new StatusEffectInstance(PoisonDartFrogEntity.getFrogPoisonEffect(getFrogType(poisonFrogItem)));
			statusEffectInstance.writeNbt(nbtCompound);
			nbtList.add(nbtCompound);
		}

		compound.put("poisons", nbtList);
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.fromNbt(enchantmentsNBTList);

		for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
			spearStack.addEnchantment(enchantment.getKey(), enchantment.getValue());
		}


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
