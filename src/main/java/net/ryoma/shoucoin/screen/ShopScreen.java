package net.ryoma.shoucoin.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.ryoma.shoucoin.network.ShopBuyC2SPacket;
import net.ryoma.shoucoin.network.ShopTradesS2CPacket;

import java.util.ArrayList;
import java.util.List;

public class ShopScreen extends HandledScreen<ShopScreenHandler> {

    private static final int ROWS_PER_PAGE = 6;
    private static final int ROW_HEIGHT = 17;
    private static final int TRADE_START_Y = 16;

    /** 前回受信した交易リストを保持（再オープン時に即表示するためのキャッシュ） */
    private static List<ShopTradesS2CPacket.TradeData> cachedTrades = new ArrayList<>();
    /** 前回表示していたページ番号を保持 */
    private static int cachedPage = 0;

    private List<ShopTradesS2CPacket.TradeData> trades = new ArrayList<>(cachedTrades);
    private int currentPage = cachedPage;

    private final List<ButtonWidget> buyButtons = new ArrayList<>();
    private ButtonWidget prevButton;
    private ButtonWidget nextButton;
    private ButtonWidget bulkToggleButton;

    /** true のとき一括購入モード（最大可能数を一度に購入） */
    private boolean bulkMode = false;

    private String statusMessage = "";
    private boolean statusSuccess = true;

    public ShopScreen(ShopScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 176;
        backgroundHeight = 222;
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

        buyButtons.clear();
        for (int i = 0; i < ROWS_PER_PAGE; i++) {
            final int row = i;
            ButtonWidget btn = ButtonWidget.builder(Text.literal("購入"), b -> onBuy(row))
                    .dimensions(x + 136, y + TRADE_START_Y + row * ROW_HEIGHT, 32, 15)
                    .build();
            buyButtons.add(btn);
            addDrawableChild(btn);
        }

        prevButton = ButtonWidget.builder(Text.literal("◀"), b -> {
            currentPage--;
            updateButtons();
        }).dimensions(x + 50, y + 122, 20, 14).build();

        nextButton = ButtonWidget.builder(Text.literal("▶"), b -> {
            currentPage++;
            updateButtons();
        }).dimensions(x + 106, y + 122, 20, 14).build();

        // 一括購入トグルボタン（右端に配置）
        bulkToggleButton = ButtonWidget.builder(Text.literal("一括: OFF"), b -> {
            bulkMode = !bulkMode;
            b.setMessage(Text.literal(bulkMode ? "一括: ON" : "一括: OFF"));
        }).dimensions(x + 128, y + 122, 40, 14).build();

        addDrawableChild(prevButton);
        addDrawableChild(nextButton);
        addDrawableChild(bulkToggleButton);
        updateButtons();
    }

    private void updateButtons() {
        prevButton.active = currentPage > 0;
        nextButton.active = (currentPage + 1) * ROWS_PER_PAGE < trades.size();
        for (int i = 0; i < ROWS_PER_PAGE; i++) {
            buyButtons.get(i).visible = (currentPage * ROWS_PER_PAGE + i) < trades.size();
        }
    }

    private void onBuy(int row) {
        int tradeIdx = currentPage * ROWS_PER_PAGE + row;
        if (tradeIdx >= trades.size()) return;

        int quantity;
        if (Screen.hasShiftDown()) {
            // Shift押し: 売りアイテムが1スタック（64個）分になる回数
            ShopTradesS2CPacket.TradeData trade = trades.get(tradeIdx);
            quantity = (int) Math.ceil(64.0 / trade.sellCount());
        } else if (bulkMode) {
            // 一括モード: サーバー側で在庫上限まで実行（大きな数を渡す）
            quantity = 9999;
        } else {
            quantity = 1;
        }

        ClientPlayNetworking.send(new ShopBuyC2SPacket(tradeIdx, quantity));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawText(textRenderer, "ショップ", x + 8, y + 6, 0x404040, false);

        for (int i = 0; i < ROWS_PER_PAGE; i++) {
            int tradeIdx = currentPage * ROWS_PER_PAGE + i;
            if (tradeIdx >= trades.size()) break;

            ShopTradesS2CPacket.TradeData trade = trades.get(tradeIdx);
            int ry = y + TRADE_START_Y + i * ROW_HEIGHT;

            context.drawItem(resolveItem(trade.buyItemId(), trade.buyCount()), x + 8, ry);
            context.drawText(textRenderer, "×" + trade.buyCount(), x + 26, ry + 4, 0x404040, false);
            context.drawText(textRenderer, "→", x + 68, ry + 4, 0x606060, false);
            context.drawItem(resolveItem(trade.sellItemId(), trade.sellCount()), x + 82, ry);
            context.drawText(textRenderer, "×" + trade.sellCount(), x + 100, ry + 4, 0x404040, false);
        }

        if (!trades.isEmpty()) {
            int total = (trades.size() + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE;
            context.drawText(textRenderer, (currentPage + 1) + "/" + total, x + 76, y + 126, 0x404040, false);
        }

        if (!statusMessage.isEmpty()) {
            context.drawText(textRenderer, statusMessage, x + 40, y + 6,
                    statusSuccess ? 0x00AA00 : 0xFF4444, false);
        }

        drawMouseoverTooltip(context, mouseX, mouseY);
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

//
//        // 背景
//        context.fill(x, y, x + backgroundWidth, y + backgroundHeight, 0xFFC6C6C6);
//        // 交易エリア
//        context.fill(x + 1, y + 13, x + backgroundWidth - 1, y + 135, 0xFFD8D8D8);
//        // 各交易行
//        for (int i = 0; i < ROWS_PER_PAGE; i++) {
//            int ry = y + TRADE_START_Y + i * ROW_HEIGHT;
//            context.fill(x + 6, ry - 1, x + 134, ry + 16, 0xFFBBBBBB);
//        }
//        // インベントリエリア
//        context.fill(x + 1, y + 136, x + backgroundWidth - 1, y + backgroundHeight - 1, 0xFFD0D0D0);
//        // インベントリスロット
//        for (int row = 0; row < 3; row++) {
//            for (int col = 0; col < 9; col++) {
//                int sx = x + 8 + col * 18;
//                int sy = y + ShopScreenHandler.INV_START_Y + row * 18;
//                context.fill(sx, sy, sx + 16, sy + 16, 0xFF8B8B8B);
//            }
//        }
//        for (int col = 0; col < 9; col++) {
//            int sx = x + 8 + col * 18;
//            int sy = y + ShopScreenHandler.INV_START_Y + 58;
//            context.fill(sx, sy, sx + 16, sy + 16, 0xFF8B8B8B);
//        }
    }

    public void setTrades(List<ShopTradesS2CPacket.TradeData> trades) {
        this.trades = trades;
        cachedTrades = trades;

        // コンフィグ変更でトレード数が減った場合に空ページへ飛ばないようクランプ
        int maxPage = trades.isEmpty() ? 0 : (trades.size() - 1) / ROWS_PER_PAGE;
        currentPage = Math.min(currentPage, maxPage);
        cachedPage = currentPage;

        if (!buyButtons.isEmpty()) updateButtons();
    }

    @Override
    public void removed() {
        super.removed();
        // 閉じた時点のページを保存し、次回オープン時に同じページを表示する
        cachedPage = currentPage;
    }

    public void showStatus(String message, boolean success) {
        this.statusMessage = message;
        this.statusSuccess = success;
    }

    private ItemStack resolveItem(String itemId, int count) {
        try {
            return new ItemStack(Registries.ITEM.get(Identifier.of(itemId)), count);
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }
}
