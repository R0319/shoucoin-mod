package net.ryoma.shoucoin;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;
import net.ryoma.shoucoin.block.ModBlocks;
import net.ryoma.shoucoin.item.ModItemGroups;
import net.ryoma.shoucoin.item.ModItems;
import net.ryoma.shoucoin.villager.ModVillagers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShoucoinMod implements ModInitializer {
	public static final String MOD_ID = "shoucoinmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		ModItems.register();
		ModBlocks.registerBlocks();
		ModItems.registerBlocks();
		ModItemGroups.register();
		ModVillagers.registerVillagers();
	}

}