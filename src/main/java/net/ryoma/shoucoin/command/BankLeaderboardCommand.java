package net.ryoma.shoucoin.command;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.ryoma.shoucoin.data.BankDataManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BankLeaderboardCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("bank")
                            .then(CommandManager.literal("top")
                                    .executes(BankLeaderboardCommand::execute))
            );
        });
    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        BankDataManager bank = BankDataManager.get(source.getServer());

        // 残高を降順で最大10件取得
        List<Map.Entry<UUID, Integer>> top = bank.getAllBalances()
                .entrySet()
                .stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .limit(10)
                .toList();

        source.sendMessage(Text.literal("§6§l── 貯金ランキング TOP" + top.size() + " ──"));

        for (int i = 0; i < top.size(); i++) {
            UUID uuid = top.get(i).getKey();
            int balance = top.get(i).getValue();

            // プレイヤー名を取得（オフラインでも名前を取得）
            String name = source.getServer()
                    .getUserCache()
                    .getByUuid(uuid)
                    .map(profile -> profile.getName())
                    .orElse(uuid.toString());

            String formattedBalance = String.format("%,d", balance);
            String medal = switch (i) {
                default -> "§f" + (i + 1) + ". ";
            };

            source.sendMessage(Text.literal(medal + "§f" + name + " §7- §e" + formattedBalance + " SCoin"));
        }

        source.sendMessage(Text.literal("§6§l───────────────────"));
        return 1;
    }
}