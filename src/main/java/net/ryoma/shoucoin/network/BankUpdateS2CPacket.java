package net.ryoma.shoucoin.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.ryoma.shoucoin.ShoucoinMod;

public record BankUpdateS2CPacket(int balance, String message) implements CustomPayload {

    public static final Id<BankUpdateS2CPacket> ID =
            new Id<>(Identifier.of(ShoucoinMod.MOD_ID, "bank_update"));

    public static final PacketCodec<RegistryByteBuf, BankUpdateS2CPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.INTEGER, BankUpdateS2CPacket::balance,
                    PacketCodecs.STRING, BankUpdateS2CPacket::message,
                    BankUpdateS2CPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}