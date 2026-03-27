package net.ryoma.shoucoin.enchantment;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;

public class TunnelEnchantmentHandler {

    /** 再帰ブレーク防止フラグ（Tunnelで壊したブロックが再度Tunnelを発動しないようにする） */
    private static final ThreadLocal<Boolean> IS_TUNNELING = ThreadLocal.withInitial(() -> false);

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register(TunnelEnchantmentHandler::onBlockBroken);
    }

    private static void onBlockBroken(World world, PlayerEntity player,
                                      BlockPos pos, net.minecraft.block.BlockState state,
                                      net.minecraft.block.entity.BlockEntity blockEntity) {
        // 再帰防止 / クリエイティブ不要 / サーバーのみ
        if (IS_TUNNELING.get()) return;
        if (!(world instanceof ServerWorld serverWorld)) return;
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;

        // メインハンドのツールにトンネルエンチャントがあるか確認
        ItemStack tool = player.getMainHandStack();
        Optional<RegistryEntry.Reference<Enchantment>> optEntry = serverWorld
                .getRegistryManager()
                .get(RegistryKeys.ENCHANTMENT)
                .getEntry(ModEnchantments.TUNNEL);
        if (optEntry.isEmpty()) return;

        RegistryEntry<Enchantment> tunnelEntry = optEntry.get();
        if (EnchantmentHelper.getLevel(tunnelEntry, tool) <= 0) return;

        // プレイヤーの視線方向に対して垂直な面の3×3を採掘
        Vec3d lookVec = player.getRotationVec(1.0f);
        Direction facing = Direction.getFacing(lookVec.x, lookVec.y, lookVec.z);

        IS_TUNNELING.set(true);
        try {
            for (int u = -1; u <= 1; u++) {
                for (int v = -1; v <= 1; v++) {
                    if (u == 0 && v == 0) continue; // 中心は既に破壊済み
                    BlockPos target = getAdjacentPos(pos, facing, u, v);

                    net.minecraft.block.BlockState targetState = serverWorld.getBlockState(target);
                    // 空気・破壊不可ブロック（岩盤など）はスキップ
                    if (targetState.isAir()) continue;
                    if (targetState.getHardness(serverWorld, target) < 0) continue;

                    // プレイヤーのゲームモード・ツール耐久・ドロップを正しく処理
                    serverPlayer.interactionManager.tryBreakBlock(target);
                }
            }
        } finally {
            IS_TUNNELING.set(false);
        }
    }

    /**
     * 中心ブロックから視線方向に垂直な面のオフセット座標を返す
     * facing = NORTH/SOUTH → u=X軸, v=Y軸
     * facing = EAST/WEST   → u=Z軸, v=Y軸
     * facing = UP/DOWN     → u=X軸, v=Z軸
     */
    private static BlockPos getAdjacentPos(BlockPos center, Direction facing, int u, int v) {
        return switch (facing.getAxis()) {
            case Z -> center.add(u, v, 0); // NORTH/SOUTH
            case X -> center.add(0, v, u); // EAST/WEST
            case Y -> center.add(u, 0, v); // UP/DOWN
        };
    }
}
