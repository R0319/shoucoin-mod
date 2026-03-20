package net.ryoma.shoucoin.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.ryoma.shoucoin.item.ModItems;
import net.ryoma.shoucoin.network.DepositC2SPacket;
import net.ryoma.shoucoin.network.TransferC2SPacket;
import net.ryoma.shoucoin.network.WithdrawC2SPacket;
import java.util.ArrayList;
import java.util.List;

public class ATMScreen extends HandledScreen<ATMScreenHandler> {

    // テキスト入力欄
    private TextFieldWidget amountField;  // 金額入力
    private TextFieldWidget targetField;  // 送金先プレイヤー名入力

    // 累積入金額の管理
    private int accumulatedDeposit = 0;
    private long lastDepositTime = 0;
    private static final long ACCUMULATE_WINDOW = 2000;

    // サーバーから受け取った残高（-1は未取得）
    private int currentBalance = -1;

    // サーバーから受け取ったメッセージ
    private String statusMessage = "";

    // メッセージの種類（色分けに使用）
    private MessageType messageType = MessageType.ERROR;

    // 選択中のコイン
    private String selectedCoin = "SHOUCOIN";

    // Tab補完用プレイヤー一覧
    private List<String> onlinePlayers = new ArrayList<>();
    private List<String> suggestions = new ArrayList<>();
    private int selectedSuggestion = -1; // 選択中のサジェストインデックス


    // コイン選択肢（表示名 → coinType文字列 → アイテム）
    private static final Object[][] COIN_OPTIONS = {
            {"SHOUCOIN",      ModItems.SHOUCOIN},
            {"IRON_COIN",     ModItems.IRON_COIN},
            {"GOLD_COIN",     ModItems.GOLD_COIN},
            {"DIAMOND_COIN",  ModItems.DIAMOND_COIN},
            {"EMERALD_COIN",  ModItems.EMERALD_COIN},
            {"NETHERITE_COIN",ModItems.NETHERITE_COIN}
    };

    // メッセージの種類を定義するenum
    public enum MessageType {
        SUCCESS_DEPOSIT,  // 入金成功 → 緑
        SUCCESS_WITHDRAW, // 出金成功 → 赤
        SUCCESS_TRANSFER, // 送金成功 → 緑
        ERROR             // エラー   → 赤
    }

    public ATMScreen(ATMScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 176;
        backgroundHeight = 222;

        // 親クラスのタイトル描画を無効化
        titleX = -9999;
        titleY = -9999;
        playerInventoryTitleX = -9999;
        playerInventoryTitleY = -9999;
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        // 金額入力欄（最大6桁）
        amountField = new TextFieldWidget(
                textRenderer, x + 10, y + 50, 80, 16, Text.literal("金額"));
        amountField.setMaxLength(8);
        addDrawableChild(amountField);

        // 入金ボタン
        addDrawableChild(ButtonWidget.builder(Text.literal("入金"), btn -> onDeposit())
                .dimensions(x + 100, y + 48, 50, 20).build());

        // 出金ボタン
        addDrawableChild(ButtonWidget.builder(Text.literal("出金"), btn -> onWithdraw())
                .dimensions(x + 100, y + 72, 50, 20).build());

        // 送金先プレイヤー名入力欄（最大16文字）
        targetField = new TextFieldWidget(
                textRenderer, x + 10, y + 100, 80, 16, Text.literal("プレイヤー名"));
        targetField.setMaxLength(16);
        addDrawableChild(targetField);

        // 送金ボタン
        addDrawableChild(ButtonWidget.builder(Text.literal("送金"), btn -> onTransfer())
                .dimensions(x + 100, y + 98, 50, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        // タイトル
        context.drawText(textRenderer, "ATM", x + 10, y + 10, 0x404040, false);

        // 残高表示
        String balanceText = currentBalance >= 0
                ? "残高: " + String.format("%,d", currentBalance) + " SCoin   "
                : "残高: -- S";
        context.drawText(textRenderer, balanceText, x + 10, y + 25, 0x404040, false);

        // ラベル
        context.drawText(textRenderer, "金額:", x + 10, y + 40, 0x404040, false);
        context.drawText(textRenderer, "送金先:", x + 10, y + 90, 0x404040, false);
        context.drawText(textRenderer, "インベントリ", x + 8, y + 130, 0x404040, false);

        // ステータスメッセージ
        if (!statusMessage.isEmpty()) {
            int color = switch (messageType) {
                case SUCCESS_DEPOSIT, SUCCESS_TRANSFER -> 0x00AA00;
                case SUCCESS_WITHDRAW, ERROR           -> 0xFF4444;
            };
            context.drawText(textRenderer, statusMessage, x + 10, y + 122, color, false);
        }

        // 右側のコインアイコン選択UIを描画
        drawCoinSelector(context, x, y, mouseX, mouseY);

        // マウスオーバー時のツールチップを描画
        drawMouseoverTooltip(context, mouseX, mouseY);

        // サジェスト表示
        drawSuggestions(context, x, y);

        // 入力内容に合わせてサジェストを更新
        if (targetField.isFocused()) {
            updateSuggestions();
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        // テクスチャを描画
        context.drawTexture(
                Identifier.of("shoucoinmod", "textures/gui/atm_gui.png"),
                x, y,
                0, 0,
                backgroundWidth, backgroundHeight,
                256, 256
        );

        // --- テクスチャが機能しない場合の確認用（コメントアウト中）---
        // 上段：ATM操作エリア
        // context.fill(x, y, x + backgroundWidth, y + 130, 0xFFC6C6C6);
        // 下段：インベントリエリア
        // context.fill(x, y + 132, x + backgroundWidth, y + backgroundHeight, 0xFFD0D0D0);
        // メインインベントリスロット（3行×9列）
        // for (int row = 0; row < 3; row++) {
        //     for (int col = 0; col < 9; col++) {
        //         int sx = x + 8 + col * 18;
        //         int sy = y + 140 + row * 18;
        //         context.fill(sx, sy, sx + 16, sy + 16, 0xFF8B8B8B);
        //     }
        // }
        // ホットバースロット
        // for (int col = 0; col < 9; col++) {
        //     int sx = x + 8 + col * 18;
        //     int sy = y + 198;
        //     context.fill(sx, sy, sx + 16, sy + 16, 0xFF8B8B8B);
        // }
        // ------------------------------------------------------------

        // コイン選択パネル背景（左側）
        int px = x - 24;

        // context.fill(px, y, px + 22, y + COIN_OPTIONS.length * 22 + 4, 0xFF555555);
        // context.fill(px + 1, y + 1, px + 21, y + COIN_OPTIONS.length * 22 + 3, 0xFF888888);

        // テクスチャで描画
        context.drawTexture(
                Identifier.of("shoucoinmod", "textures/gui/atm_gui_icons.png"),
                px, y,    // 描画位置
                0, 0,     // UV開始位置
                24, 114,  // 描画サイズ（18px × 108px）
                256, 256  // テクスチャ全体のサイズ
        );
    }

    // 右側のコインアイコン選択UIを描画
    private void drawCoinSelector(DrawContext context, int x, int y, int mouseX, int mouseY) {
        int px = x - 21;

        for (int i = 0; i < COIN_OPTIONS.length; i++) {
            String coinType = (String) COIN_OPTIONS[i][0];
            Item coinItem = (Item) COIN_OPTIONS[i][1];
            int iconY = y + 4 + i * 18; // 外枠1px + スロット間1px

            boolean isSelected = coinType.equals(selectedCoin);
            boolean isHovered = mouseX >= px + 1 && mouseX <= px + 17
                    && mouseY >= iconY && mouseY <= iconY + 16;

            // 選択中はハイライト
            if (isSelected) {
                context.fill(px + 1, iconY, px + 17, iconY + 16, 0xAA6A9955);
            } else if (isHovered) {
                context.fill(px + 1, iconY, px + 17, iconY + 16, 0x55FFFFFF);
            }

            // コインアイコンを描画
            context.drawItem(new ItemStack(coinItem), px + 1, iconY);
        }
    }

    // 入金ボタン押下時
    private void onDeposit() {
        int amount = parseAmount();
        if (amount <= 0) return;
        ClientPlayNetworking.send(new DepositC2SPacket(amount));
    }

    // 出金ボタン押下時：選択中のコインタイプを一緒に送信
    private void onWithdraw() {
        int amount = parseAmount();
        if (amount <= 0) return;
        ClientPlayNetworking.send(new WithdrawC2SPacket(amount, selectedCoin));
    }

    // 送金ボタン押下時
    private void onTransfer() {
        int amount = parseAmount();
        String target = targetField.getText().trim();
        if (amount <= 0 || target.isEmpty()) return;
        ClientPlayNetworking.send(new TransferC2SPacket(amount, target));
    }

    // コインアイコンのクリック処理
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        int px = x - 21;

        // コインアイコンがクリックされたか確認
        for (int i = 0; i < COIN_OPTIONS.length; i++) {
            int iconY = y + 4 + i * 18;
            if (mouseX >= px + 2 && mouseX <= px + 17
                    && mouseY >= iconY && mouseY <= iconY + 18) {
                selectedCoin = (String) COIN_OPTIONS[i][0];
                return true;
            }
        }

        // サジェストのクリック処理
        if (!suggestions.isEmpty() && targetField.isFocused()) {
            int sx = x + 10;
            int sy = y + 116;

            for (int i = 0; i < suggestions.size(); i++) {
                if (mouseX >= sx && mouseX <= sx + 80 &&
                        mouseY >= sy + i * 12 && mouseY <= sy + i * 12 + 12) {
                    targetField.setText(suggestions.get(i));
                    suggestions.clear();
                    selectedSuggestion = -1;
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    // サーバーから残高・メッセージ・メッセージ種類を受け取って表示を更新
    public void updateBalance(int balance, String message, MessageType type) {
        this.currentBalance = balance;
        this.messageType = type;

        if (type == MessageType.SUCCESS_DEPOSIT) {
            long now = System.currentTimeMillis();

            if (now - lastDepositTime <= ACCUMULATE_WINDOW) {
                try {
                    int amount = Integer.parseInt(message.replace("S 入金しました", "").trim());
                    accumulatedDeposit += amount;
                } catch (NumberFormatException e) {
                    accumulatedDeposit = 0;
                }
            } else {
                try {
                    int amount = Integer.parseInt(message.replace("S 入金しました", "").trim());
                    accumulatedDeposit = amount;
                } catch (NumberFormatException e) {
                    accumulatedDeposit = 0;
                }
            }

            lastDepositTime = now;
            this.statusMessage = String.format("%,d", accumulatedDeposit) + "S 入金しました";
        } else {
            accumulatedDeposit = 0;
            lastDepositTime = 0;
            this.statusMessage = message;
        }
    }

    // 金額入力欄のテキストを整数に変換（不正な入力は0を返す）
    private int parseAmount() {
        try {
            return Integer.parseInt(amountField.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // サーバーからプレイヤー一覧を受け取る
    public void updatePlayerList(List<String> players) {
        this.onlinePlayers = players;
    }

    private void drawSuggestions(DrawContext context, int x, int y) {
        if (suggestions.isEmpty() || !targetField.isFocused()) return;

        int sx = x + 10;
        int sy = y + 116; // 送金先入力欄の下

        for (int i = 0; i < suggestions.size(); i++) {
            boolean isSelected = i == selectedSuggestion;
            int bgColor = isSelected ? 0xFF6A9955 : 0xFF555555;

            context.fill(sx, sy + i * 12, sx + 80, sy + i * 12 + 12, bgColor);
            context.drawText(textRenderer, suggestions.get(i),
                    sx + 2, sy + i * 12 + 2, 0xFFFFFF, false);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Tab キー（keyCode=258）
        if (keyCode == 258 && targetField.isFocused()) {
            updateSuggestions();

            if (!suggestions.isEmpty()) {
                // 次のサジェストを選択
                selectedSuggestion = (selectedSuggestion + 1) % suggestions.size();
                targetField.setText(suggestions.get(selectedSuggestion));
                return true;
            }
        }

        // Escキーでサジェストを閉じる
        if (keyCode == 256) {
            suggestions.clear();
            selectedSuggestion = -1;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // 入力内容に合わせてサジェストを絞り込む
    private void updateSuggestions() {
        String input = targetField.getText().trim().toLowerCase();
        if (input.isEmpty()) {
            suggestions = new ArrayList<>(onlinePlayers);
        } else {
            suggestions = onlinePlayers.stream()
                    .filter(name -> name.toLowerCase().startsWith(input))
                    .collect(java.util.stream.Collectors.toList());
        }
        // 選択インデックスをリセット
        if (selectedSuggestion >= suggestions.size()) {
            selectedSuggestion = -1;
        }
    }
}