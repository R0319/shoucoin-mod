package net.ryoma.shoucoin.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class ShopScreenHandler extends ScreenHandler {

    static final int INV_START_Y = 140;

    public ShopScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ModScreenHandlers.SHOP_SCREEN_HANDLER, syncId);

        // メインインベントリ（3行×9列）
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory,
                        col + row * 9 + 9,
                        8 + col * 18,
                        INV_START_Y + row * 18));
            }
        }

        // ホットバー（1行×9列）
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory,
                    col,
                    8 + col * 18,
                    INV_START_Y + 58));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
