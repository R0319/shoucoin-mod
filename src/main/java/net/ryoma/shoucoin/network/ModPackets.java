package net.ryoma.shoucoin.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.ryoma.shoucoin.data.BankDataManager;
import net.ryoma.shoucoin.item.ModItems;
import net.ryoma.shoucoin.util.CoinValue;

import java.util.List;

public class ModPackets {
    public static void registerServerPackets() {

        // パケット型をサーバー・クライアント間で登録
        PayloadTypeRegistry.playC2S().register(DepositC2SPacket.ID, DepositC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(WithdrawC2SPacket.ID, WithdrawC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(TransferC2SPacket.ID, TransferC2SPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(BankUpdateS2CPacket.ID, BankUpdateS2CPacket.CODEC);

        // プレイヤー一覧パケットの登録
        PayloadTypeRegistry.playS2C().register(PlayerListS2CPacket.ID, PlayerListS2CPacket.CODEC);

        // 入金パケットの受信処理
        ServerPlayNetworking.registerGlobalReceiver(DepositC2SPacket.ID, (packet, context) -> {
            ServerPlayerEntity player = context.player();
            int amount = packet.amount();
            BankDataManager bank = BankDataManager.get(player.getServer());

            // インベントリの全コインの合計S換算値を確認
            int totalCoinValue = countTotalCoinValue(player);
            if (totalCoinValue < amount) {
                sendUpdate(player, bank.getBalance(player.getUuid()),
                        "コインが足りません", "ERROR", 0);
                return;
            }

            // インベントリからコインを消費して残高に加算
            removeCoinsByValue(player, amount);
            bank.deposit(player.getUuid(), amount);
            sendUpdate(player, bank.getBalance(player.getUuid()),
                    amount + "S 入金しました", "DEPOSIT", amount);
        });

        // 出金パケットの受信処理
        ServerPlayNetworking.registerGlobalReceiver(WithdrawC2SPacket.ID, (packet, context) -> {
            ServerPlayerEntity player = context.player();
            int amount = packet.amount();
            String coinType = packet.coinType();
            BankDataManager bank = BankDataManager.get(player.getServer());

            // 残高が足りているか確認
            if (!bank.withdraw(player.getUuid(), amount)) {
                sendUpdate(player, bank.getBalance(player.getUuid()),
                        "残高が足りません", "ERROR", 0);
                return;
            }

            // 選択コイン以下のレートを持つコインで段階的に出金
            int remaining = amount;
            boolean reachedSelected = false;

            // レート降順（大きい順）に処理
            int[][] coinRates = {
                    {100000, 0}, // NETHERITE_COIN
                    {10000,  1}, // EMERALD_COIN
                    {1000,   2}, // DIAMOND_COIN
                    {100,    3}, // GOLD_COIN
                    {10,     4}, // IRON_COIN
                    {1,      5}  // SHOUCOIN
            };
            Item[] coinItems = {
                    ModItems.NETHERITE_COIN,
                    ModItems.EMERALD_COIN,
                    ModItems.DIAMOND_COIN,
                    ModItems.GOLD_COIN,
                    ModItems.IRON_COIN,
                    ModItems.SHOUCOIN
            };

            // 選択コインのレートを取得
            Item selectedCoinItem = getCoinByName(coinType);
            int selectedRate = CoinValue.getValue(selectedCoinItem);

            for (int i = 0; i < coinRates.length; i++) {
                int rate = coinRates[i][0];
                Item coinItem = coinItems[i];

                // 選択コイン以下のレートになったら処理開始
                if (rate <= selectedRate) {
                    int coinCount = remaining / rate;
                    if (coinCount > 0) {
                        player.getInventory().offerOrDrop(new ItemStack(coinItem, coinCount));
                        remaining -= coinCount * rate;
                    }
                }

                if (remaining == 0) break;
            }

            sendUpdate(player, bank.getBalance(player.getUuid()),
                    amount + "S 出金しました", "WITHDRAW", amount);
        });

        // 送金パケットの受信処理
        ServerPlayNetworking.registerGlobalReceiver(TransferC2SPacket.ID, (packet, context) -> {
            ServerPlayerEntity sender = context.player();
            String targetName = packet.targetName();
            int amount = packet.amount();
            BankDataManager bank = BankDataManager.get(sender.getServer());

            // 送金先プレイヤーをオンラインから名前で検索
            ServerPlayerEntity target = sender.getServer()
                    .getPlayerManager().getPlayer(targetName);

            // 送金先が見つからない場合
            if (target == null) {
                sendUpdate(sender, bank.getBalance(sender.getUuid()),
                        "プレイヤーが見つかりません", "ERROR", 0);
                return;
            }

            // 自分自身への送金は不可
            if (target == sender) {
                sendUpdate(sender, bank.getBalance(sender.getUuid()),
                        "自分には送金できません", "ERROR", 0);
                return;
            }

            // 残高が足りているか確認
            if (!bank.withdraw(sender.getUuid(), amount)) {
                sendUpdate(sender, bank.getBalance(sender.getUuid()),
                        "残高が足りません", "ERROR", 0);
                return;
            }

            // 送金先の残高に加算
            bank.deposit(target.getUuid(), amount);

            // 送金者に完了通知
            sendUpdate(sender, bank.getBalance(sender.getUuid()),
                    targetName + "に" + amount + "S 送金しました", "TRANSFER", amount);

            // 受取人にも受信通知
            sendUpdate(target, bank.getBalance(target.getUuid()),
                    sender.getName().getString() + "から" + amount + "S 受け取りました", "TRANSFER", amount);
        });
    }

    private static Item getCoinByName(String name) {
        return switch (name) {
            case "IRON_COIN"      -> ModItems.IRON_COIN;
            case "GOLD_COIN"      -> ModItems.GOLD_COIN;
            case "DIAMOND_COIN"   -> ModItems.DIAMOND_COIN;
            case "EMERALD_COIN"   -> ModItems.EMERALD_COIN;
            case "NETHERITE_COIN" -> ModItems.NETHERITE_COIN;
            default               -> ModItems.SHOUCOIN; // デフォルトはShoucoin
        };
    }

    // 残高更新パケットをクライアントに送信するユーティリティ
    private static void sendUpdate(ServerPlayerEntity player, int balance,
                                   String message, String type, int amount) {
        ServerPlayNetworking.send(player, new BankUpdateS2CPacket(balance, message, type, amount));
    }

    // インベントリの全コインの合計S換算値を数える
    private static int countTotalCoinValue(ServerPlayerEntity player) {
        int total = 0;
        for (ItemStack stack : player.getInventory().main) {
            if (CoinValue.isCoin(stack.getItem())) {
                total += stack.getCount() * CoinValue.getValue(stack.getItem());
            }
        }
        return total;
    }

    // インベントリからコインをvalue分消費する（大きいコインから優先）
    private static void removeCoinsByValue(ServerPlayerEntity player, int value) {
        int remaining = value;

        // レート降順で処理（大きいコインから消費）
        Item[] coinItems = {
                ModItems.NETHERITE_COIN,
                ModItems.EMERALD_COIN,
                ModItems.DIAMOND_COIN,
                ModItems.GOLD_COIN,
                ModItems.IRON_COIN,
                ModItems.SHOUCOIN
        };

        for (Item coinItem : coinItems) {
            if (remaining <= 0) break;
            int rate = CoinValue.getValue(coinItem);

            for (ItemStack stack : player.getInventory().main) {
                if (remaining <= 0) break;
                if (stack.getItem() == coinItem) {
                    // このスタックから何枚消費できるか
                    int canUse = Math.min(stack.getCount(), remaining / rate);
                    if (canUse > 0) {
                        stack.decrement(canUse);
                        remaining -= canUse * rate;
                    }
                }
            }
        }
    }

    // オンラインプレイヤー一覧をクライアントに送信
    public static void sendPlayerList(ServerPlayerEntity player) {
        List<String> names = player.getServer().getPlayerManager().getPlayerList()
                .stream()
                .map(p -> p.getName().getString())
                .filter(name -> !name.equals(player.getName().getString())) // 自分を除く
                .toList();
        ServerPlayNetworking.send(player, new PlayerListS2CPacket(names));
    }
}