package net.ryoma.shoucoin.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.ryoma.shoucoin.block.entity.ATMBlockEntity;

public class ATMScreenHandler extends ScreenHandler {
    private final ATMBlockEntity blockEntity;

    // サーバー側から開くときによばれる
    public ATMScreenHandler(int syncId, PlayerInventory playerInventory, ATMBlockEntity blockEntity) {
        super(ModScreenHandlers.ATM_SCREEN_HANDLER, syncId);
        this.blockEntity = blockEntity;
    }

    // クライアント側で同期用に呼ばれる(ネットワーク越し)
    public ATMScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, null);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public ATMBlockEntity getBlockEntity() {
        return blockEntity;
    }
}
