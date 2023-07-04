package me.kegantu.kegantu_spear.weapons.toolMaterial;

import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class SpearMaterial implements ToolMaterial {

    public static final SpearMaterial INSTANCE = new SpearMaterial();
    @Override
    public int getDurability() {
        return 400;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 2.5f;
    }

    @Override
    public float getAttackDamage() {
        return 0;
    }

    @Override
    public int getMiningLevel() {
        return 1;
    }

    @Override
    public int getEnchantability() {
        return 18;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(Items.IRON_INGOT);
    }
}
