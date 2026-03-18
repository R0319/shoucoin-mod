package net.ryoma.shoucoin.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.ryoma.shoucoin.screen.ATMScreen;
import net.minecraft.client.MinecraftClient;

public class ModPacketsClient {

    public static void registerClientPackets() {
        // サーバーからの残高更新を受信
        ClientPlayNetworking.registerGlobalReceiver(BankUpdateS2CPacket.ID, (packet, context) -> {
            context.client().execute(() -> {
                // 現在ATMScreenが開いていたら残高とメッセージを更新
                if (MinecraftClient.getInstance().currentScreen instanceof ATMScreen atmScreen) {
                    atmScreen.updateBalance(packet.balance(), packet.message());
                }
            });
        });
    }
}