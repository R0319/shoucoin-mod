package net.ryoma.shoucoin.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class ShouTotem extends Item {
    private final String tooltipKey;

    public ShouTotem(Settings settings, String tooltipKey) {
        super(settings);
        this.tooltipKey = tooltipKey;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable(tooltipKey).formatted(Formatting.GRAY));
    }
}
