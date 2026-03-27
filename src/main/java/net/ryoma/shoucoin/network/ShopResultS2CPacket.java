package net.ryoma.shoucoin.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.ryoma.shoucoin.ShoucoinMod;

public record ShopResultS2CPacket(String message, boolean success) implements CustomPayload {

    public static final Id<ShopResultS2CPacket> ID =
            new Id<>(Identifier.of(ShoucoinMod.MOD_ID, "shop_result"));

    public static final PacketCodec<RegistryByteBuf, ShopResultS2CPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.STRING, ShopResultS2CPacket::message,
                    PacketCodecs.BOOL,   ShopResultS2CPacket::success,
                    ShopResultS2CPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
