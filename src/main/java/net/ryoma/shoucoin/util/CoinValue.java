package net.ryoma.shoucoin.util;

import net.minecraft.item.Item;
import net.ryoma.shoucoin.item.ModItems;

import java.util.LinkedHashMap;
import java.util.Map;

public class CoinValue {

    // コインアイテム → 換算レート（S）
    public static final Map<Item, Integer> COIN_RATES = new LinkedHashMap<>();

    static {
        COIN_RATES.put(ModItems.SHOUCOIN, 1);
        COIN_RATES.put(ModItems.IRON_COIN, 10);
        COIN_RATES.put(ModItems.GOLD_COIN, 100);
        COIN_RATES.put(ModItems.DIAMOND_COIN, 1000);
        COIN_RATES.put(ModItems.EMERALD_COIN, 10000);
        COIN_RATES.put(ModItems.NETHERITE_COIN, 100000);

        // 将来追加する場合はここに追記
    }

    // アイテムがコインかどうか
    public static boolean isCoin(Item item) {
        return COIN_RATES.containsKey(item);
    }

    // コインのS換算値を取得
    public static int getValue(Item item) {
        return COIN_RATES.getOrDefault(item, 0);
    }
}