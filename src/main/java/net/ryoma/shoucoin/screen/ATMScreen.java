package net.ryoma.shoucoin.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.ryoma.shoucoin.network.DepositC2SPacket;
import net.ryoma.shoucoin.network.TransferC2SPacket;
import net.ryoma.shoucoin.network.WithdrawC2SPacket;

public class ATMScreen extends HandledScreen<ATMScreenHandler> {

    private TextFieldWidget amountField;
    private TextFieldWidget targetField;

    public ATMScreen(ATMScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 200;
        backgroundHeight = 200;
        // ↓ 親クラスのタイトル描画位置を画面外に追いやって非表示にする
        titleX = -9999;
        titleY = -9999;
        // プレイヤーインベントリのラベルも非表示
        playerInventoryTitleX = -9999;
        playerInventoryTitleY = -9999;
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        // 金額入力欄
        amountField = new TextFieldWidget(
                textRenderer, x + 10, y + 50, 80, 16, Text.literal("金額")
        );
        amountField.setMaxLength(6);
        addDrawableChild(amountField);

        // 入金ボタン
        addDrawableChild(ButtonWidget.builder(Text.literal("入金"), btn -> onDeposit())
                .dimensions(x + 100, y + 48, 50, 20).build());

        // 出金ボタン
        addDrawableChild(ButtonWidget.builder(Text.literal("出金"), btn -> onWithdraw())
                .dimensions(x + 100, y + 72, 50, 20).build());

        // 送金先入力欄
        targetField = new TextFieldWidget(
                textRenderer, x + 10, y + 120, 80, 16, Text.literal("プレイヤー名")
        );
        targetField.setMaxLength(16);
        addDrawableChild(targetField);

        // 送金ボタン
        addDrawableChild(ButtonWidget.builder(Text.literal("送金"), btn -> onTransfer())
                .dimensions(x + 100, y + 118, 50, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);  // ← 親のrenderはそのまま呼ぶ

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        // "ATM"は手動で1回だけ描画
        context.drawText(textRenderer, "ATM", x + 10, y + 10, 0x404040, false);

        String balanceText = currentBalance >= 0 ? "残高: " + currentBalance + " S" : "残高: -- S";
        context.drawText(textRenderer, balanceText, x + 10, y + 30, 0x404040, false);
        context.drawText(textRenderer, "金額:", x + 10, y + 38, 0x404040, false);
        context.drawText(textRenderer, "送金先:", x + 10, y + 108, 0x404040, false);

        if (!statusMessage.isEmpty()) {
            context.drawText(textRenderer, statusMessage, x + 10, y + 170, 0xFF4444, false);
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.fill(x, y, x + backgroundWidth, y + backgroundHeight, 0xFFC6C6C6);
    }

    // パケット送信部分を更新
    private void onDeposit() {
        int amount = parseAmount();
        if (amount <= 0) return;
        ClientPlayNetworking.send(new DepositC2SPacket(amount));
    }

    private void onWithdraw() {
        int amount = parseAmount();
        if (amount <= 0) return;
        ClientPlayNetworking.send(new WithdrawC2SPacket(amount));
    }

    private void onTransfer() {
        int amount = parseAmount();
        String target = targetField.getText().trim();
        if (amount <= 0 || target.isEmpty()) return;
        ClientPlayNetworking.send(new TransferC2SPacket(amount, target));
    }

    // サーバーから残高・メッセージを受け取って表示を更新
    private int currentBalance = -1;  // -1は未取得
    private String statusMessage = "";

    public void updateBalance(int balance, String message) {
        this.currentBalance = balance;
        this.statusMessage = message;
    }

    private int parseAmount() {
        try {
            return Integer.parseInt(amountField.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}