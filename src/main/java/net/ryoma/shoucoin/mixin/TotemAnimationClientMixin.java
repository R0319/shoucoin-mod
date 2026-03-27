package net.ryoma.shoucoin.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.ryoma.shoucoin.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayNetworkHandler.class)
public class TotemAnimationClientMixin {

    /**
     * getActiveTotemOfUndying は両手を TOTEM_OF_UNDYING で検索し、
     * 見つからなければ new ItemStack(TOTEM_OF_UNDYING) を返す実装になっている。
     * SHOU_TOTEM も対象に含めることでアニメーションに正しいテクスチャを使わせる。
     */
    @Inject(
        method = "getActiveTotemOfUndying",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void onGetActiveTotemOfUndying(PlayerEntity player,
                                                   CallbackInfoReturnable<ItemStack> cir) {
        for (Hand hand : Hand.values()) {
            ItemStack stack = player.getStackInHand(hand);
            if (stack.isOf(ModItems.SHOU_TOTEM)) {
                cir.setReturnValue(stack);
                return;
            }
        }
    }
}
