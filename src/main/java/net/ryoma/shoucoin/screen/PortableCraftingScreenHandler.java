package net.ryoma.shoucoin.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;

public class PortableCraftingScreenHandler extends CraftingScreenHandler {

    public PortableCraftingScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(syncId, playerInventory,
                ScreenHandlerContext.create(playerInventory.player.getEntityWorld(), BlockPos.ORIGIN));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}