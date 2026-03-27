package net.ryoma.shoucoin.command;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.ryoma.shoucoin.config.ShoucoinConfig;

public class ReloadConfigCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(
                        CommandManager.literal("shoucoin")
                                .requires(source -> source.hasPermissionLevel(2))
                                .then(CommandManager.literal("reload")
                                        .executes(ReloadConfigCommand::execute))
                )
        );
    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        ShoucoinConfig.load();
        context.getSource().sendMessage(Text.literal("§a[ShoucoinMod] コンフィグを再読み込みしました"));
        return 1;
    }
}
