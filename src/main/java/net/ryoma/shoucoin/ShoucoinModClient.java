package net.ryoma.shoucoin;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.ryoma.shoucoin.block.ModBlocks;

public class ShoucoinModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BANK_JOB_BLOCK, RenderLayer.getCutout());
    }
}
