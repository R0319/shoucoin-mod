package net.ryoma.shoucoin.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.ryoma.shoucoin.ShoucoinMod;

import java.util.List;

public record PlayerListS2CPacket(List<String> playerNames) implements CustomPayload {

    public static final Id<PlayerListS2CPacket> ID =
            new Id<>(Identifier.of(ShoucoinMod.MOD_ID, "player_list"));

    public static final PacketCodec<RegistryByteBuf, PlayerListS2CPacket> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.STRING.collect(PacketCodecs.toList()),
                    PlayerListS2CPacket::playerNames,
                    PlayerListS2CPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}