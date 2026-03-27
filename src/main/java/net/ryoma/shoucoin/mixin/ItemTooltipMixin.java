package net.ryoma.shoucoin.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
public class ItemTooltipMixin {

    @Inject(method = "appendTooltip", at = @At("TAIL"))
    private void addTotemTooltip(ItemStack stack, Item.TooltipContext context,
                                 List<Text> tooltip, TooltipType type, CallbackInfo ci) {
        if (stack.isOf(Items.TOTEM_OF_UNDYING)) {
            tooltip.add(Text.translatable("item.shoucoinmod.totem_of_undying.1_tooltip")
                    .formatted(Formatting.RED));
            tooltip.add(Text.translatable("item.shoucoinmod.totem_of_undying.2_tooltip")
                    .formatted(Formatting.RED));
        }
    }
}