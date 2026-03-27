package net.ryoma.shoucoin.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.ryoma.shoucoin.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "tryUseTotem", at = @At("HEAD"), cancellable = true)
    private void onTryUseTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // まず全ての手でSHOU_TOTEMを優先検索
        // → メインハンドにバニラトーテムがあってもオフハンドのSHOU_TOTEMを見落とさない
        for (Hand hand : Hand.values()) {
            ItemStack stack = entity.getStackInHand(hand);
            if (stack.isOf(ModItems.SHOU_TOTEM)) {
                entity.setHealth(1.0f);
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));

                // デクリメント前にアニメーションを送信する
                // → クライアントがstatus 35を受け取った時点でまだSHOU_TOTEMを持っている状態になる
                if (entity.getWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.sendEntityStatus(entity, (byte) 35);
                }
                stack.decrement(1);

                entity.getWorld().playSound(
                        null,
                        entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.ITEM_TOTEM_USE,
                        entity.getSoundCategory(),
                        1.0f, 1.0f
                );

                cir.setReturnValue(true);
                return;
            }
        }

        // SHOU_TOTEMが無い場合のみバニラトーテムを無効化
        for (Hand hand : Hand.values()) {
            if (entity.getStackInHand(hand).isOf(Items.TOTEM_OF_UNDYING)) {
                cir.setReturnValue(false);
                return;
            }
        }
    }
}