package me.kegantu.kegantu_spear.weapons;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import ladysnake.pickyourpoison.common.PickYourPoison;
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
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
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

import java.util.*;

public class Spear extends KegantuSpearItem {
	public Spear(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
		super(toolMaterial, attackDamage, attackSpeed, settings);
	}

	public static StatusEffectInstance getDartPoisonEffect(Item item) {
		if (item == PickYourPoison.COMATOSE_POISON_DART) {
			return new StatusEffectInstance(PickYourPoison.COMATOSE, 600);
		} else if (item == PickYourPoison.BATRACHOTOXIN_POISON_DART) {
			return new StatusEffectInstance(PickYourPoison.BATRACHOTOXIN, 160);
		} else if (item == PickYourPoison.NUMBNESS_POISON_DART) {
			return new StatusEffectInstance(PickYourPoison.NUMBNESS, 300);
		} else if (item == PickYourPoison.VULNERABILITY_POISON_DART) {
			return new StatusEffectInstance(PickYourPoison.VULNERABILITY, 300);
		} else if (item == PickYourPoison.TORPOR_POISON_DART) {
			return new StatusEffectInstance(PickYourPoison.TORPOR, 300);
		} else if (item == PickYourPoison.STIMULATION_POISON_DART) {
			return new StatusEffectInstance(PickYourPoison.STIMULATION, 600);
		} else if (item == PickYourPoison.BLINDNESS_POISON_DART) {
			return new StatusEffectInstance(StatusEffects.BLINDNESS, 200);
		}

		return null;
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (QuiltLoader.isModLoaded("pickyourpoison")) {
			NbtCompound nbtCompound = stack.getNbt();
			NbtElement element = nbtCompound.get("poisons");
			NbtList list = (NbtList) element;
			if (list != null){
				for (NbtElement nbtElement : list) {
					StatusEffectInstance statusEffectInstance = new StatusEffectInstance(Objects.requireNonNull(StatusEffectInstance.fromNbt((NbtCompound) nbtElement)));
					target.addStatusEffect(statusEffectInstance, attacker);
				}
			}
			/*if (EnchantmentHelper.getLevel(KegantuSpear.affectMultipleTargetsEnchantment, stack) > 0){
				PlayerEntity player = (PlayerEntity) attacker;
				if (!player.isOnGround()){
					return super.postHit(stack, target, attacker);
				}

				for(LivingEntity livingEntity : target.world.getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox().expand(1.0, 0.25, 1.0))) {
					if (livingEntity != attacker
						&& !attacker.isTeammate(livingEntity)
						&& (!(livingEntity instanceof ArmorStandEntity) || !((ArmorStandEntity)livingEntity).isMarker())
						&& attacker.squaredDistanceTo(livingEntity) < 9.0) {
						StatusEffectInstance statusEffect2 = new StatusEffectInstance(statusEffectInstance.getEffectType(), statusEffectInstance.getDuration() / 2);
						livingEntity.addStatusEffect(statusEffect2, attacker);
					}
				}
			}*/
		}
		return super.postHit(stack, target, attacker);
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		if (!(user instanceof PlayerEntity)) {
			return;
		}
		PlayerEntity playerEntity = (PlayerEntity) user;
		int i = this.getMaxUseTime(stack) - remainingUseTicks;
		if (i < 10) {
			return;
		}
		int j = EnchantmentHelper.getRiptide(stack);
		if (j > 0 && !playerEntity.isTouchingWaterOrRain()) {
			return;
		}
		if (!world.isClient) {
			stack.damage(1, playerEntity, p -> p.sendToolBreakStatus(user.getActiveHand()));
			if (j == 0) {
				SpearEntity spearEntity = createSpear(world, playerEntity, stack);
				spearEntity.setProperties(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0f, 2.5f + (float) j * 0.5f, 1.0f);
				NbtCompound nbtCompound = stack.getNbt();
				NbtElement element = nbtCompound.get("poisons");
				NbtList NBTlist = (NbtList) element;
				if (NBTlist != null){
					for (NbtElement nbtElement : NBTlist) {
						StatusEffectInstance potion = new StatusEffectInstance(Objects.requireNonNull(StatusEffectInstance.fromNbt((NbtCompound) nbtElement)));
						spearEntity.addEffect(potion);
					}
				}
				if (playerEntity.getAbilities().creativeMode) {
					spearEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
				}
				world.spawnEntity(spearEntity);
				world.playSoundFromEntity(null, spearEntity, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0f, 1.0f);
				if (!playerEntity.getAbilities().creativeMode) {
					playerEntity.getInventory().removeOne(stack);
				}
			}
		}
		playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
		if (j > 0) {
			float f = playerEntity.getYaw();
			float g = playerEntity.getPitch();
			float h = -MathHelper.sin(f * ((float) Math.PI / 180)) * MathHelper.cos(g * ((float) Math.PI / 180));
			float k = -MathHelper.sin(g * ((float) Math.PI / 180));
			float l = MathHelper.cos(f * ((float) Math.PI / 180)) * MathHelper.cos(g * ((float) Math.PI / 180));
			float m = MathHelper.sqrt(h * h + k * k + l * l);
			float n = 3.0f * ((1.0f + (float) j) / 4.0f);
			playerEntity.addVelocity(h *= n / m, k *= n / m, l *= n / m);
			playerEntity.startRiptideAttack(20);
			if (playerEntity.isOnGround()) {
				float o = 1.1999999f;
				playerEntity.move(MovementType.SELF, new Vec3d(0.0, 1.1999999284744263, 0.0));
			}
			SoundEvent soundEvent = j >= 3 ? SoundEvents.ITEM_TRIDENT_RIPTIDE_3 : (j == 2 ? SoundEvents.ITEM_TRIDENT_RIPTIDE_2 : SoundEvents.ITEM_TRIDENT_RIPTIDE_1);
			world.playSoundFromEntity(null, playerEntity, soundEvent, SoundCategory.PLAYERS, 1.0f, 1.0f);
		}
	}

	public @NotNull SpearEntity createSpear(World world, LivingEntity user, ItemStack stack) {
		SpearEntity spearEntity = Objects.requireNonNull(KegantuSpear.SPEAR_ENTITY.create(world));
		spearEntity.setSpearAttributes(world, user, stack);
		spearEntity.setOwner(user);
		spearEntity.setSpearStack(stack);
		spearEntity.setProperties(user, user.getPitch(), user.getYaw(), 0.0F, 2.5F, 1.0F);
		spearEntity.updatePosition(user.getX(), user.getEyeY() - 0.1, user.getZ());
		return spearEntity;
	}
}
