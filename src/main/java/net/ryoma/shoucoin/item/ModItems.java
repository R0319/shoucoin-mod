package net.ryoma.shoucoin.item;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.ryoma.shoucoin.ShoucoinMod;
import net.ryoma.shoucoin.block.ModBlocks;

public class ModItems {

    //アイテム登録
    public static final Item SHOUCOIN = registerItem("shoucoin", new Item(new Item.Settings()));
    public static final Item IRON_COIN = registerItem("iron_coin", new Item(new Item.Settings()));
    public static final Item GOLD_COIN = registerItem("gold_coin", new Item(new Item.Settings()));
    public static final Item DIAMOND_COIN = registerItem("diamond_coin", new Item(new Item.Settings()));
    public static final Item NETHERITE_COIN = registerItem("netherite_coin", new Item(new Item.Settings()));
    public static final Item EMERALD_COIN = registerItem("emerald_coin", new Item(new Item.Settings()));
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
                new BlockItem(ModBlocks.ATM_BLOCK, new Item.Settings())
        );
    }



    public static void register() {
        // ModItemsクラスの初期化を強制するためのメソッド
    }
}
