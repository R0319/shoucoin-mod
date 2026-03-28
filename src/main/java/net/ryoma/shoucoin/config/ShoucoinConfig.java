package net.ryoma.shoucoin.config;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ShoucoinConfig {

    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("shoucoinmod/shop_trades.json");

    public static List<TradeEntry> trades = new ArrayList<>();

    public static class TradeEntry {
        public String buyItem;   // 支払うアイテム (例: "minecraft:raw_copper")
        public int buyCount;     // 支払う個数
        public String sellItem;  // もらうアイテム (例: "shoucoinmod:shoucoin")
        public int sellCount;    // もらう個数
    }

    public static void load() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());

            if (!Files.exists(CONFIG_PATH)) {
                createDefault();
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                TradeEntry[] entries = gson.fromJson(reader, TradeEntry[].class);
                trades = entries != null ? new ArrayList<>(Arrays.asList(entries)) : new ArrayList<>();
            }

        } catch (Exception e) {
            System.err.println("[ShoucoinMod] コンフィグ読み込み失敗: " + e.getMessage());
            trades = new ArrayList<>();
        }
    }

    private static void createDefault() throws IOException {
        List<TradeEntry> defaults = new ArrayList<>();
        defaults.add(make("minecraft:raw_copper",            1, "shoucoinmod:shoucoin",   1));
        defaults.add(make("minecraft:raw_iron",              1, "shoucoinmod:shoucoin",   8));
        defaults.add(make("minecraft:raw_gold",              1, "shoucoinmod:shoucoin",  12));
        defaults.add(make("minecraft:redstone_ore",              1, "shoucoinmod:iron_coin",   2));
        defaults.add(make("minecraft:nether_gold_ore",                1, "shoucoinmod:iron_coin",  1));
        defaults.add(make("minecraft:nether_quartz_ore",                1, "shoucoinmod:iron_coin",  1));
        defaults.add(make("minecraft:diamond_ore", 1, "shoucoinmod:iron_coin",  5));
        defaults.add(make("minecraft:emerald_ore", 1, "shoucoinmod:iron_coin",  5));
        defaults.add(make("minecraft:deepslate_diamond_ore", 1, "shoucoinmod:iron_coin",  5));
        defaults.add(make("minecraft:deepslate_emerald_ore", 1, "shoucoinmod:iron_coin",  5));
        defaults.add(make("minecraft:netherite_scrap",       1, "shoucoinmod:iron_coin", 25));
        defaults.add(make("minecraft:totem_of_undying",       1, "shoucoinmod:emerald_coin", 2));
        defaults.add(make("shoucoinmod:emerald_coin",       5, "shoucoinmod:shou_totem", 1));


        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            gson.toJson(defaults, writer);
        }
        System.out.println("[ShoucoinMod] デフォルトコンフィグを生成しました: " + CONFIG_PATH);
    }

    private static TradeEntry make(String buyItem, int buyCount, String sellItem, int sellCount) {
        TradeEntry e = new TradeEntry();
        e.buyItem = buyItem;
        e.buyCount = buyCount;
        e.sellItem = sellItem;
        e.sellCount = sellCount;
        return e;
    }
}
