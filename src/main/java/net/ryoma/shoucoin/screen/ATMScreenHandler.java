package net.ryoma.shoucoin.screen;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.ryoma.shoucoin.block.entity.ATMBlockEntity;
import net.ryoma.shoucoin.data.BankDataManager;
import net.ryoma.shoucoin.network.BankUpdateS2CPacket;
import net.ryoma.shoucoin.util.CoinValue;

public class ATMScreenHandler extends ScreenHandler {

    private final ATMBlockEntity blockEntity;

    // インベントリスロットの開始Y座標
    private static final int INV_START_Y = 140;

    // サーバー側から開くときに呼ばれる
    public ATMScreenHandler(int syncId, PlayerInventory playerInventory, ATMBlockEntity blockEntity) {
        super(ModScreenHandlers.ATM_SCREEN_HANDLER, syncId);
        this.blockEntity = blockEntity;

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

    // クライアント側で同期用に呼ばれる（ネットワーク越し）
    public ATMScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, null);
    }

    // Ctrl+クリックでコインを入金
    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (actionType == SlotActionType.QUICK_MOVE && slotIndex >= 0 && slotIndex < slots.size()) {
            ItemStack stack = slots.get(slotIndex).getStack();

            // コインアイテムかどうか確認
            if (!stack.isEmpty() && CoinValue.isCoin(stack.getItem())) {
                if (!player.getWorld().isClient && player instanceof ServerPlayerEntity serverPlayer) {
                    int amount = stack.getCount() * CoinValue.getValue(stack.getItem());
                    stack.setCount(0); // インベントリから削除

                    BankDataManager bank = BankDataManager.get(serverPlayer.getServer());
                    bank.deposit(serverPlayer.getUuid(), amount);

                    // DEPOSITタイプで送信することでクライアント側でXP音が鳴る
                    ServerPlayNetworking.send(serverPlayer,
                            new BankUpdateS2CPacket(
                                    bank.getBalance(serverPlayer.getUuid()),
                                    amount + "S 入金しました",
                                    "DEPOSIT",
                                    amount
                            ));
                }
                return;
            }
        }
        super.onSlotClick(slotIndex, button, actionType, player);
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