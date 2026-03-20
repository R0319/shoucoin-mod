package net.ryoma.shoucoin.config;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ShoucoinConfig {

    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("shoucoinmod/villager_trades.json");

    public static List<TradeEntry> trades = new ArrayList<>();

    // 取引1件分のデータ
    public static class TradeEntry {
        public String buyItem;       // 支払うアイテム (例: "minecraft:raw_copper")
        public int buyCount;         // 支払う個数
        public String sellItem;      // もらうアイテム (例: "shoucoinmod:shoucoin")
        public int sellCount;        // もらう個数
        public int maxUses;          // 最大取引回数
        public int villagerXp;       // 村人に入るXP
        public float priceMultiplier;// 値段倍率
        public int level;            // 村人レベル (1〜5)
    }

    // コンフィグ読み込み（なければデフォルト生成）
    public static void load() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());

            if (!Files.exists(CONFIG_PATH)) {
                createDefault();
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                TradeEntry[] entries = gson.fromJson(reader, TradeEntry[].class);
                trades = entries != null ? Arrays.asList(entries) : new ArrayList<>();
            }

        } catch (Exception e) {
            System.err.println("[ShoucoinMod] コンフィグ読み込み失敗: " + e.getMessage());
            trades = new ArrayList<>();
        }
    }

    // デフォルトコンフィグを生成
    private static void createDefault() throws IOException {
        List<TradeEntry> defaults = new ArrayList<>();

        // レベル1
        defaults.add(make("minecraft:raw_copper",    1, "shoucoinmod:shoucoin",  1,  32, 5, 0.02f, 1));
        defaults.add(make("minecraft:quartz",        1, "shoucoinmod:shoucoin",  2,  32, 5, 0.02f, 1));
        defaults.add(make("minecraft:redstone",      1, "shoucoinmod:shoucoin",  2,  32, 5, 0.02f, 1));
        defaults.add(make("minecraft:raw_iron",      1, "shoucoinmod:shoucoin",  8,  32, 5, 0.02f, 1));
        // レベル2
        defaults.add(make("minecraft:raw_gold",               1, "shoucoinmod:shoucoin",   12, 24, 10, 0.02f, 2));
        defaults.add(make("minecraft:deepslate_diamond_ore",  1, "shoucoinmod:iron_coin",   5, 16, 10, 0.02f, 2));
        defaults.add(make("minecraft:deepslate_emerald_ore",  1, "shoucoinmod:iron_coin",   5, 16, 10, 0.02f, 2));
        // レベル3
        defaults.add(make("minecraft:netherite_scrap", 1, "shoucoinmod:iron_coin", 25, 12, 20, 0.02f, 3));

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            gson.toJson(defaults, writer);
        }

        System.out.println("[ShoucoinMod] デフォルトコンフィグを生成しました: " + CONFIG_PATH);
    }

    private static TradeEntry make(String buyItem, int buyCount, String sellItem, int sellCount,
                                   int maxUses, int villagerXp, float priceMultiplier, int level) {
        TradeEntry e = new TradeEntry();
        e.buyItem = buyItem;
        e.buyCount = buyCount;
        e.sellItem = sellItem;
        e.sellCount = sellCount;
        e.maxUses = maxUses;
        e.villagerXp = villagerXp;
        e.priceMultiplier = priceMultiplier;
        e.level = level;
        return e;
    }
}