package net.ryoma.shoucoin.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.ryoma.shoucoin.screen.ATMScreen;
import net.minecraft.client.MinecraftClient;
import net.ryoma.shoucoin.network.PlayerListS2CPacket;

public class ModPacketsClient {

    public static void registerClientPackets() {
        // サーバーからの残高更新を受信
        ClientPlayNetworking.registerGlobalReceiver(BankUpdateS2CPacket.ID, (packet, context) -> {
            context.client().execute(() -> {
                MinecraftClient client = MinecraftClient.getInstance();

                if (client.currentScreen instanceof ATMScreen atmScreen) {
                    // メッセージタイプを変換
                    ATMScreen.MessageType type = switch (packet.messageType()) {
                        case "DEPOSIT"  -> ATMScreen.MessageType.SUCCESS_DEPOSIT;
                        case "WITHDRAW" -> ATMScreen.MessageType.SUCCESS_WITHDRAW;
                        case "TRANSFER" -> ATMScreen.MessageType.SUCCESS_TRANSFER;
                        default         -> ATMScreen.MessageType.ERROR;
                    };

                    atmScreen.updateBalance(packet.balance(), packet.message(), type);

                    // XP取得音を再生（エラー時は鳴らさない）
                    if (!packet.messageType().equals("ERROR")) {
                        client.player.playSound(
                                net.minecraft.sound.SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                                1.0f,
                                1.0f
                        );
                    }
                }
            });
        });

        // プレイヤー一覧を受信してATMScreenに渡す
        ClientPlayNetworking.registerGlobalReceiver(PlayerListS2CPacket.ID, (packet, context) -> {
            context.client().execute(() -> {
                if (MinecraftClient.getInstance().currentScreen instanceof ATMScreen atmScreen) {
                    atmScreen.updatePlayerList(packet.playerNames());
                }
            });
        });
    }
}