package me.kegantu.kegantu_spear.weapons;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import me.kegantu.kegantu_spear.KegantuSpear;
import me.kegantu.kegantu_spear.entity.SpearEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.QuiltLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class KegantuSpearItem extends ToolItem {
	private final int attackDamage;
	private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
	public KegantuSpearItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
		super(material, settings);
		this.attackDamage = attackDamage;
		ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(
			EntityAttributes.GENERIC_ATTACK_DAMAGE,
			new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.attackDamage, EntityAttributeModifier.Operation.ADDITION)
		);
		builder.put(
			EntityAttributes.GENERIC_ATTACK_SPEED,
			new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", attackSpeed, EntityAttributeModifier.Operation.ADDITION)
		);
		this.attributeModifiers = builder.build();
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damage(1, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		return super.postHit(stack, target, attacker);
	}

	@Override
	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
		if (state.getHardness(world, pos) != 0.0F) {
			stack.damage(2, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		}

		return super.postMine(stack, world, state, pos, miner);
	}

	@Override
	public boolean isSuitableFor(BlockState state) {
		return state.isOf(Blocks.COBWEB);
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return !miner.isCreative();
	}

	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		if (state.isOf(Blocks.COBWEB)) {
			return 15.0F;
		} else {
			Material material = state.getMaterial();
			return material != Material.PLANT && material != Material.REPLACEABLE_PLANT && !state.isIn(BlockTags.LEAVES) && material != Material.GOURD ? 1.0F : 1.5F;
		}
	}

	@Override
	public int getEnchantability() {
		return 1;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		if (itemStack.getDamage() >= itemStack.getMaxDamage() - 1) {
			return TypedActionResult.fail(itemStack);
		}
		user.setCurrentHand(hand);
		return TypedActionResult.consume(itemStack);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.SPEAR;
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 72000;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if (QuiltLoader.isModLoaded("pickyourpoison")) {
			buildTooltip(tooltip, 1.0f, stack);
		}
	}

	public void buildTooltip(List<Text> list, float durationMultiplier, ItemStack stack) {
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
