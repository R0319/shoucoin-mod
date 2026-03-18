package net.ryoma.shoucoin;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.ryoma.shoucoin.block.ModBlocks;
import net.ryoma.shoucoin.network.ModPacketsClient;
import net.ryoma.shoucoin.screen.ATMScreen;
import net.ryoma.shoucoin.screen.ModScreenHandlers;

public class ShoucoinModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BANK_JOB_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ATM_BLOCK, RenderLayer.getCutout());
        HandledScreens.register(ModScreenHandlers.ATM_SCREEN_HANDLER, ATMScreen::new);
        ModPacketsClient.registerClientPackets();
    }
}
