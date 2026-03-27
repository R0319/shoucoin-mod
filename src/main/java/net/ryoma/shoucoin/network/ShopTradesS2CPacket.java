package net.ryoma.shoucoin.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.ryoma.shoucoin.ShoucoinMod;

import java.util.ArrayList;
import java.util.List;

/**
 * サーバーがショップGUIを開いたときにクライアントへ交易一覧を送るパケット。
 * 各エントリは "buyItemId,buyCount,sellItemId,sellCount" 形式の文字列。
 */
public record ShopTradesS2CPacket(List<String> trades) implements CustomPayload {

    public static final Id<ShopTradesS2CPacket> ID =
            new Id<>(Identifier.of(ShoucoinMod.MOD_ID, "shop_trades"));

    public static final PacketCodec<RegistryByteBuf, ShopTradesS2CPacket> CODEC =
            PacketCodec.of(
                    (value, buf) -> {
                        buf.writeInt(value.trades().size());
                        for (String entry : value.trades()) {
                            buf.writeString(entry);
                        }
                    },
                    buf -> {
                        int size = buf.readInt();
                        List<String> list = new ArrayList<>(size);
                        for (int i = 0; i < size; i++) {
                            list.add(buf.readString());
                        }
                        return new ShopTradesS2CPacket(list);
                    }
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    /** "buyItemId,buyCount,sellItemId,sellCount" 形式の文字列を解析 */
    public record TradeData(String buyItemId, int buyCount, String sellItemId, int sellCount) {
        public static TradeData parse(String s) {
            String[] parts = s.split(",");
            return new TradeData(parts[0], Integer.parseInt(parts[1]), parts[2], Integer.parseInt(parts[3]));
        }
    }
}
