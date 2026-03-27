package net.ryoma.shoucoin.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.ryoma.shoucoin.ShoucoinMod;

// balance は long（残高はオーバーフロー対策でlong）、amount は int（1回の操作はmax8桁でint範囲内）
public record BankUpdateS2CPacket(long balance, String message, String messageType, int amount)
        implements CustomPayload {

    public static final Id<BankUpdateS2CPacket> ID =
            new Id<>(Identifier.of(ShoucoinMod.MOD_ID, "bank_update"));

    // long用カスタムコーデック（PacketCodecs.LONGはこのバージョンに存在しないため）
    // このバージョンのPacketCodec.ofはValueFirstEncoder: (値, バッファ) の順
    private static void encodeLong(Long val, RegistryByteBuf buf) { buf.writeLong(val); }
    private static Long decodeLong(RegistryByteBuf buf) { return buf.readLong(); }
    private static final PacketCodec<RegistryByteBuf, Long> LONG_CODEC =
            PacketCodec.of(BankUpdateS2CPacket::encodeLong, BankUpdateS2CPacket::decodeLong);

    public static final PacketCodec<RegistryByteBuf, BankUpdateS2CPacket> CODEC =
            PacketCodec.tuple(
                    LONG_CODEC,          BankUpdateS2CPacket::balance,
                    PacketCodecs.STRING, BankUpdateS2CPacket::message,
                    PacketCodecs.STRING, BankUpdateS2CPacket::messageType,
                    PacketCodecs.INTEGER, BankUpdateS2CPacket::amount,
                    BankUpdateS2CPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
