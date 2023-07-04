package me.kegantu.kegantu_spear.entity;

import com.google.common.collect.Sets;
import me.kegantu.kegantu_spear.KegantuSpear;
import me.kegantu.kegantu_spear.mixin.TridentEntityAccessor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
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
				world.createExplosion(this, this.getX(), this.getY(), this.getZ(), 2, Explosion.DestructionType.DESTROY);
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
