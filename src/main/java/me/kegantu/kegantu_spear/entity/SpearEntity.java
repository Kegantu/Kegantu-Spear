package me.kegantu.kegantu_spear.entity;

import com.google.common.collect.Sets;
import me.kegantu.kegantu_spear.KegantuSpear;
import me.kegantu.kegantu_spear.mixin.TridentEntityAccessor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.Set;

public class SpearEntity extends TridentEntity {
	private final Set<StatusEffectInstance> effects = Sets.newHashSet();

	public SpearEntity(EntityType<? extends TridentEntity> entityType, World world) {
		super(entityType, world);
	}

	public void setSpearAttributes(World world, LivingEntity owner, ItemStack stack) {
		this.setSpearStack(stack.copy());
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		if (!world.isClient()){
			if (EnchantmentHelper.getLevel(KegantuSpear.BOOM, getSpearStack()) > 0){
				//world.createExplosion(this, this.getX(), this.getY(), this.getZ(), 250, Explosion.DestructionType.DESTROY);
				Entity entity = this.getOwner();
				AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.world, entityHitResult.getEntity().getX(), entityHitResult.getEntity().getY(), entityHitResult.getEntity().getZ());
				if (entity instanceof LivingEntity) {
					areaEffectCloudEntity.setOwner((LivingEntity)entity);
				}

				areaEffectCloudEntity.setRadius(3.0F);
				areaEffectCloudEntity.setRadiusOnUse(-0.5F);
				areaEffectCloudEntity.setWaitTime(10);
				areaEffectCloudEntity.setDuration(1200);
				areaEffectCloudEntity.setRadiusGrowth(areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());

				for(StatusEffectInstance statusEffectInstance : effects) {
					areaEffectCloudEntity.addEffect(new StatusEffectInstance(statusEffectInstance));
					areaEffectCloudEntity.setPotion(new Potion(statusEffectInstance));
				}

				NbtCompound nbtCompound = getSpearStack().getNbt();
				if (nbtCompound != null && nbtCompound.contains("CustomPotionColor", NbtElement.NUMBER_TYPE)) {
					areaEffectCloudEntity.setColor(nbtCompound.getInt("CustomPotionColor"));
				}

				this.world.spawnEntity(areaEffectCloudEntity);
			}
		}
	}

	@Override
	protected void onHit(LivingEntity target) {
		super.onHit(target);
		Entity entity = this.getEffectCause();

		if (!this.effects.isEmpty()){
			for (StatusEffectInstance statusEffectInstance : this.effects) {
				target.addStatusEffect(statusEffectInstance, entity);
			}
		}
	}

	public ItemStack getSpearStack() {
		return ((TridentEntityAccessor) this).impaled$getTridentStack();
	}

	public void setSpearStack(ItemStack tridentStack) {
		((TridentEntityAccessor) this).impaled$setTridentStack(tridentStack);
	}

	public void addEffect(StatusEffectInstance effect) {
		this.effects.add(effect);
	}
}
