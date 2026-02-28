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
    public static final Item COPPER_COIN = registerItem("copper_coin", new Item(new Item.Settings().registryKey(
            RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ShoucoinMod.MOD_ID, "copper_coin")))));

    //鉄コインの登録
    public static final Item IRON_COIN = registerItem("iron_coin", new Item(new Item.Settings().registryKey(
            RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ShoucoinMod.MOD_ID, "iron_coin")))));


    //金コインの登録
    public static final Item GOLD_COIN = registerItem("gold_coin", new Item(new Item.Settings().registryKey(
            RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ShoucoinMod.MOD_ID, "gold_coin")))));

    //ダイヤモンドコインの登録
    public static final Item DIAMOND_COIN = registerItem("diamond_coin", new Item(new Item.Settings().registryKey(
            RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ShoucoinMod.MOD_ID, "diamond_coin")))));

    //ネザライトコインの登録
    public static final Item NETHERITE_COIN = registerItem("netherite_coin", new Item(new Item.Settings().registryKey(
            RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ShoucoinMod.MOD_ID, "netherite_coin")))));

    //エメラルドコインの登録
    public static final Item EMERALD_COIN = registerItem("emerald_coin", new Item(new Item.Settings().registryKey(
            RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ShoucoinMod.MOD_ID, "emerald_coin")))));

    public static final Item PORTABLE_CRAFTING_TABLE =
            registerItem("portable_crafting_table",
                    //ポータブルクラフティングテーブル、Itemクラスを継承しているクラスに処理を導入
                    new PortableCraftingTableItem(
                            new Item.Settings().registryKey(
                                    RegistryKey.of(
                                            RegistryKeys.ITEM,
                                            Identifier.of(ShoucoinMod.MOD_ID, "portable_crafting_table")
                                    )
                            ).maxCount(1)
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
