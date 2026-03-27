package net.ryoma.shoucoin.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.ryoma.shoucoin.ShoucoinMod;

public record TransferC2SPacket(int amount, String targetName) implements CustomPayload {

    public static final Id<TransferC2SPacket> ID =
            new Id<>(Identifier.of(ShoucoinMod.MOD_ID, "transfer"));

    public static final PacketCodec<RegistryByteBuf, TransferC2SPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.INTEGER, TransferC2SPacket::amount,
                    PacketCodecs.STRING,  TransferC2SPacket::targetName,
                    TransferC2SPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
