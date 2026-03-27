package net.ryoma.shoucoin;

import net.fabricmc.api.ModInitializer;
import net.ryoma.shoucoin.block.ModBlockEntities;
import net.ryoma.shoucoin.block.ModBlocks;
import net.ryoma.shoucoin.command.BankLeaderboardCommand;
import net.ryoma.shoucoin.command.ReloadConfigCommand;
import net.ryoma.shoucoin.config.ShoucoinConfig;
import net.ryoma.shoucoin.item.ModItemGroups;
import net.ryoma.shoucoin.item.ModItems;
import net.ryoma.shoucoin.enchantment.TunnelEnchantmentHandler;
import net.ryoma.shoucoin.network.ModPackets;
import net.ryoma.shoucoin.screen.ModScreenHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShoucoinMod implements ModInitializer {
	public static final String MOD_ID = "shoucoinmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		ShoucoinConfig.load();
		ReloadConfigCommand.register();
		ModItems.register();
		ModBlocks.registerBlocks();
		ModBlockEntities.registerBlockEntities();
		ModScreenHandlers.registerScreenHandlers();
		ModPackets.registerServerPackets();
		BankLeaderboardCommand.register();

		ModItems.registerBlocks();
		ModItemGroups.register();
		TunnelEnchantmentHandler.register();
	}
}
