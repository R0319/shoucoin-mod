package net.ryoma.shoucoin.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.ryoma.shoucoin.data.BankDataManager;
import net.ryoma.shoucoin.item.ModItems;

public class ModPackets {

    public static void registerServerPackets() {
        // パケット型の登録
        PayloadTypeRegistry.playC2S().register(DepositC2SPacket.ID, DepositC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(WithdrawC2SPacket.ID, WithdrawC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(TransferC2SPacket.ID, TransferC2SPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(BankUpdateS2CPacket.ID, BankUpdateS2CPacket.CODEC);

        // 入金受信
        ServerPlayNetworking.registerGlobalReceiver(DepositC2SPacket.ID, (packet, context) -> {
            ServerPlayerEntity player = context.player();
            int amount = packet.amount();

            // インベントリのShoucoinを確認・消費
            int coinCount = countCoins(player);
            if (coinCount < amount) {
                sendUpdate(player, -1, "Shoucoinが足りません");
                return;
            }

            removeCoins(player, amount);
            BankDataManager bank = BankDataManager.get(player.getServer());
            bank.deposit(player.getUuid(), amount);

            sendUpdate(player, bank.getBalance(player.getUuid()), amount + "S 入金しました");
        });

        // 出金受信
        ServerPlayNetworking.registerGlobalReceiver(WithdrawC2SPacket.ID, (packet, context) -> {
            ServerPlayerEntity player = context.player();
            int amount = packet.amount();
            BankDataManager bank = BankDataManager.get(player.getServer());

            if (!bank.withdraw(player.getUuid(), amount)) {
                sendUpdate(player, bank.getBalance(player.getUuid()), "残高が足りません");
                return;
            }

            player.getInventory().offerOrDrop(new ItemStack(ModItems.SHOUCOIN, amount));
            sendUpdate(player, bank.getBalance(player.getUuid()), amount + "S 出金しました");
        });

        // 送金受信
        ServerPlayNetworking.registerGlobalReceiver(TransferC2SPacket.ID, (packet, context) -> {
            ServerPlayerEntity sender = context.player();
            String targetName = packet.targetName();
            int amount = packet.amount();
            BankDataManager bank = BankDataManager.get(sender.getServer());

            ServerPlayerEntity target = sender.getServer()
                    .getPlayerManager().getPlayer(targetName);

            if (target == null) {
                sendUpdate(sender, bank.getBalance(sender.getUuid()), "プレイヤーが見つかりません");
                return;
            }
            if (target == sender) {
                sendUpdate(sender, bank.getBalance(sender.getUuid()), "自分には送金できません");
                return;
            }
            if (!bank.withdraw(sender.getUuid(), amount)) {
                sendUpdate(sender, bank.getBalance(sender.getUuid()), "残高が足りません");
                return;
            }

            bank.deposit(target.getUuid(), amount);

            // 送金者に通知
            sendUpdate(sender, bank.getBalance(sender.getUuid()),
                    targetName + "に" + amount + "S 送金しました");
            // 受取人にも通知
            sendUpdate(target, bank.getBalance(target.getUuid()),
                    sender.getName().getString() + "から" + amount + "S 受け取りました");
        });
    }

    // 残高更新パケットをクライアントに送る
    private static void sendUpdate(ServerPlayerEntity player, int balance, String message) {
        ServerPlayNetworking.send(player, new BankUpdateS2CPacket(balance, message));
    }

    // インベントリのShoucoin枚数を数える
    private static int countCoins(ServerPlayerEntity player) {
        int count = 0;
        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() == ModItems.SHOUCOIN) {
                count += stack.getCount();
            }
        }
        return count;
    }

    // インベントリからShoucoinをamount枚削除
    private static void removeCoins(ServerPlayerEntity player, int amount) {
        int remaining = amount;
        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() == ModItems.SHOUCOIN && remaining > 0) {
                int remove = Math.min(stack.getCount(), remaining);
                stack.decrement(remove);
                remaining -= remove;
            }
        }
    }
}