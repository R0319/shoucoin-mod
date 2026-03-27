package net.ryoma.shoucoin.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.ryoma.shoucoin.ShoucoinMod;

public record DepositC2SPacket(int amount) implements CustomPayload {

    public static final Id<DepositC2SPacket> ID =
            new Id<>(Identifier.of(ShoucoinMod.MOD_ID, "deposit"));

    public static final PacketCodec<RegistryByteBuf, DepositC2SPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.INTEGER, DepositC2SPacket::amount,
                    DepositC2SPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
