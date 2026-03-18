package net.ryoma.shoucoin.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.ryoma.shoucoin.ShoucoinMod;

public record WithdrawC2SPacket(int amount) implements CustomPayload {

    public static final Id<WithdrawC2SPacket> ID =
            new Id<>(Identifier.of(ShoucoinMod.MOD_ID, "withdraw"));

    public static final PacketCodec<RegistryByteBuf, WithdrawC2SPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.INTEGER, WithdrawC2SPacket::amount,
                    WithdrawC2SPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}