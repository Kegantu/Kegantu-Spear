package me.kegantu.kegantu_spear.weapons;

import me.kegantu.kegantu_spear.KegantuSpear;
import me.kegantu.kegantu_spear.entity.SpearEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Halberd extends KegantuSpearItem {

	public Halberd(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
		super(material, attackDamage, attackSpeed, settings);
	}

	/*@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		PlayerEntity player = (PlayerEntity) attacker;
		float genericAttackDamage = (float) player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
		if (!player.isOnGround()){
			return super.postHit(stack, target, attacker);
		}

		float sweepingMultiplier = 1.0F + EnchantmentHelper.getSweepingMultiplier(attacker) * genericAttackDamage;

		for(LivingEntity livingEntity : target.world.getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox().expand(1.0, 0.25, 1.0))) {
			if (livingEntity != attacker
				&& !attacker.isTeammate(livingEntity)
				&& (!(livingEntity instanceof ArmorStandEntity) || !((ArmorStandEntity)livingEntity).isMarker())
				&& attacker.squaredDistanceTo(livingEntity) < 9.0) {
				livingEntity.takeKnockback(
					0.4F, MathHelper.sin(player.getYaw() * (float) (Math.PI / 180.0)), (double)(-MathHelper.cos(player.getYaw() * (float) (Math.PI / 180.0)))
				);
				livingEntity.damage(DamageSource.player(player), sweepingMultiplier);
			}
		}

		player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
		player.spawnSweepAttackParticles();
		return super.postHit(stack, target, attacker);
	}*/

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
				SpearEntity spearEntity = createHalberd(world, playerEntity, stack);
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

	public @NotNull SpearEntity createHalberd(World world, LivingEntity user, ItemStack stack) {
		SpearEntity spearEntity = Objects.requireNonNull(KegantuSpear.SPEAR_ENTITY.create(world));
		spearEntity.setSpearAttributes(world, user, stack);
		spearEntity.setOwner(user);
		spearEntity.setSpearStack(stack);
		spearEntity.setProperties(user, user.getPitch(), user.getYaw(), 0.0F, 2.5F, 1.0F);
		spearEntity.updatePosition(user.getX(), user.getEyeY() - 0.1, user.getZ());
		return spearEntity;
	}
}
