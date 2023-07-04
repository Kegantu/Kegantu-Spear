package me.kegantu.kegantu_spear.mixin;

import me.kegantu.kegantu_spear.weapons.Halberd;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
	@ModifyVariable(method = "attack", at = @At(value = "STORE"), ordinal = 3)
	public boolean atttackEnemy(boolean value ,Entity target) {
		PlayerEntity player = MinecraftClient.getInstance().player;
		float cooldownProgress = player.getAttackCooldownProgress(0.5F);
		ItemStack itemStack = player.getMainHandStack();
		player.resetLastAttackedTicks();
		boolean bl = cooldownProgress > 0.9F;
		boolean bl2 = false;

		boolean bl3 = bl
			&& player.fallDistance > 0.0F
			&& !player.isOnGround()
			&& !player.isClimbing()
			&& !player.isTouchingWater()
			&& !player.hasStatusEffect(StatusEffects.BLINDNESS)
			&& !player.hasVehicle()
			&& target instanceof LivingEntity;
		bl3 = bl3 && !player.isSprinting();

		if (player.isSprinting() && bl) {
			player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, player.getSoundCategory(), 1.0F, 1.0F);
			bl2 = true;
		}

		double d = (player.horizontalSpeed - player.prevHorizontalSpeed);
		if (bl && !bl3 && !bl2 && player.isOnGround() && d < player.getMovementSpeed()){
			if (itemStack.getItem() instanceof SwordItem || itemStack.getItem() instanceof Halberd){
				return true;
			}
		}
		return value;
	}
}
