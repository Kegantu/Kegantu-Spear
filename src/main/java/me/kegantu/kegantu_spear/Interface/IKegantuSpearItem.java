package me.kegantu.kegantu_spear.Interface;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface IKegantuSpearItem {

	default void buildTooltip(List<Text> list, float durationMultiplier, ItemStack stack) {
		NbtCompound nbtCompound = stack.getNbt();
		NbtElement element = nbtCompound.get("poisons");
		NbtList NBTlist = (NbtList) element;
		ArrayList<Pair<EntityAttribute, EntityAttributeModifier>> list3 = Lists.newArrayList();
		if (NBTlist == null) {
			list.add(Text.translatable("effect.none").formatted(Formatting.GRAY));
		} else {
			for (NbtElement NBTelement : NBTlist) {
				StatusEffectInstance effectInstance = new StatusEffectInstance(Objects.requireNonNull(StatusEffectInstance.fromNbt((NbtCompound) NBTelement)));

				MutableText mutableText = Text.translatable(effectInstance.getTranslationKey());
				StatusEffect statusEffect = effectInstance.getEffectType();
				Map<EntityAttribute, EntityAttributeModifier> map = statusEffect.getAttributeModifiers();
				if (!map.isEmpty()) {
					for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : map.entrySet()) {
						EntityAttributeModifier entityAttributeModifier = entry.getValue();
						EntityAttributeModifier entityAttributeModifier2 = new EntityAttributeModifier(entityAttributeModifier.getName(), statusEffect.adjustModifierAmount(effectInstance.getAmplifier(), entityAttributeModifier), entityAttributeModifier.getOperation());
						list3.add(new Pair<EntityAttribute, EntityAttributeModifier>(entry.getKey(), entityAttributeModifier2));
					}
				}
				if (effectInstance.getAmplifier() > 0) {
					mutableText = Text.translatable("potion.withAmplifier", mutableText, Text.translatable("potion.potency." + effectInstance.getAmplifier()));
				}
				if (effectInstance.getDuration() > 20) {
					mutableText = Text.translatable("potion.withDuration", mutableText, StatusEffectUtil.durationToString(effectInstance, durationMultiplier));
				}
				list.add(mutableText.formatted(statusEffect.getType().getFormatting()));
			}
		}
		if (!list3.isEmpty()) {
			list.add(Text.of(""));
			list.add(Text.translatable("potion.whenDrank").formatted(Formatting.DARK_PURPLE));
			for (Pair pair : list3) {
				EntityAttributeModifier entityAttributeModifier3 = (EntityAttributeModifier) pair.getRight();
				double d = entityAttributeModifier3.getValue();
				double e = entityAttributeModifier3.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE || entityAttributeModifier3.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL ? entityAttributeModifier3.getValue() * 100.0 : entityAttributeModifier3.getValue();
				if (d > 0.0) {
					list.add(Text.translatable("attribute.modifier.plus." + entityAttributeModifier3.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(e), Text.translatable(((EntityAttribute) pair.getLeft()).getTranslationKey())).formatted(Formatting.BLUE));
					continue;
				}
				if (!(d < 0.0)) continue;
				list.add(Text.translatable("attribute.modifier.take." + entityAttributeModifier3.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(e *= -1.0), Text.translatable(((EntityAttribute) pair.getLeft()).getTranslationKey())).formatted(Formatting.RED));
			}
		}
	}
}
