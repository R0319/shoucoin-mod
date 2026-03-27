package net.ryoma.shoucoin.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.ryoma.shoucoin.ShoucoinMod;

public record ShopBuyC2SPacket(int tradeIndex, int quantity) implements CustomPayload {

    public static final Id<ShopBuyC2SPacket> ID =
            new Id<>(Identifier.of(ShoucoinMod.MOD_ID, "shop_buy"));

    public static final PacketCodec<RegistryByteBuf, ShopBuyC2SPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.INTEGER, ShopBuyC2SPacket::tradeIndex,
                    PacketCodecs.INTEGER, ShopBuyC2SPacket::quantity,
                    ShopBuyC2SPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
