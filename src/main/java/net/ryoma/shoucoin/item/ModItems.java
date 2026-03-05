package net.ryoma.shoucoin.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.ryoma.shoucoin.ShoucoinMod;

public class ModItems {

    //アイテム登録
    //銅コインの登録
    public static final Item COPPER_COIN = registerItem("copper_coin", new Item(new Item.Settings()));

    //鉄コインの登録
    public static final Item IRON_COIN = registerItem("iron_coin", new Item(new Item.Settings()));


    //金コインの登録
    public static final Item GOLD_COIN = registerItem("gold_coin", new Item(new Item.Settings()));

    //ダイヤモンドコインの登録
    public static final Item DIAMOND_COIN = registerItem("diamond_coin", new Item(new Item.Settings()));

    //ネザライトコインの登録
    public static final Item NETHERITE_COIN = registerItem("netherite_coin", new Item(new Item.Settings()));

    //エメラルドコインの登録
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

    public static void register() {
        // ModItemsクラスの初期化を強制するためのメソッド
    }
}
