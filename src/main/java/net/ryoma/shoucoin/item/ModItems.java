package net.ryoma.shoucoin.item;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.ryoma.shoucoin.ShoucoinMod;
import net.ryoma.shoucoin.block.ModBlocks;

import java.util.List;

public class ModItems {

    public static final Item SHOUCOIN = registerItem("shoucoin",
            new CoinItem(new Item.Settings(), "item.shoucoinmod.shoucoin.tooltip"));

    public static final Item IRON_COIN = registerItem("iron_coin",
            new CoinItem(new Item.Settings(), "item.shoucoinmod.iron_coin.tooltip"));

    public static final Item GOLD_COIN = registerItem("gold_coin",
            new CoinItem(new Item.Settings(), "item.shoucoinmod.gold_coin.tooltip"));

    public static final Item DIAMOND_COIN = registerItem("diamond_coin",
            new CoinItem(new Item.Settings(), "item.shoucoinmod.diamond_coin.tooltip"));

    public static final Item NETHERITE_COIN = registerItem("netherite_coin",
            new CoinItem(new Item.Settings(), "item.shoucoinmod.netherite_coin.tooltip"));

    public static final Item EMERALD_COIN = registerItem("emerald_coin",
            new CoinItem(new Item.Settings(), "item.shoucoinmod.emerald_coin.tooltip"));

    public static final Item PORTABLE_CRAFTING_TABLE =
            registerItem("portable_crafting_table",
                    new PortableCraftingTableItem(
                            new Item.Settings()
                                    .maxCount(1)
                    )
            );

    //アイテム登録のためのヘルパーメソッド
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(ShoucoinMod.MOD_ID, name), item);
    }

    //アイテムとしてブロック登録
    public static void registerBlocks() {
        Registry.register(
                Registries.ITEM,
                Identifier.of(ShoucoinMod.MOD_ID,"bank_job_block"),
                new BlockItem(ModBlocks.BANK_JOB_BLOCK, new Item.Settings())
        );

        Registry.register(
                Registries.ITEM,
                Identifier.of(ShoucoinMod.MOD_ID, "atm"),
                new BlockItem(ModBlocks.ATM_BLOCK, new Item.Settings()) {
                    @Override
                    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
                        tooltip.add(Text.translatable("item.shoucoinmod.atm.tooltip").formatted(Formatting.GRAY));
                    }
                }
        );
    }



    public static void register() {
        // ModItemsクラスの初期化を強制するためのメソッド
    }
}
