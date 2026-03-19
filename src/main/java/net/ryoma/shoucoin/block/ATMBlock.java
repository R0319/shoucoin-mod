package net.ryoma.shoucoin.block;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.ryoma.shoucoin.block.entity.ATMBlockEntity;
import net.ryoma.shoucoin.data.BankDataManager;
import net.ryoma.shoucoin.network.BankUpdateS2CPacket;
import net.ryoma.shoucoin.network.ModPackets;

public class ATMBlock extends BlockWithEntity {

    public static final MapCodec<ATMBlock> CODEC = createCodec(ATMBlock::new);

    public static final EnumProperty<DoubleBlockHalf> HALF =
            Properties.DOUBLE_BLOCK_HALF;
    public static final DirectionProperty FACING =
            Properties.HORIZONTAL_FACING;

    public ATMBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(HALF, DoubleBlockHalf.LOWER)
                .with(FACING, Direction.NORTH));
    }

    @Override
    public MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        if (state.get(HALF) == DoubleBlockHalf.LOWER) {
            return new ATMBlockEntity(pos, state);
        }
        return null;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        World world = ctx.getWorld();
        Direction facing = ctx.getHorizontalPlayerFacing().getOpposite();

        if (pos.getY() < world.getTopY() - 1 &&
                world.getBlockState(pos.up()).canReplace(ctx)) {
            return getDefaultState()
                    .with(HALF, DoubleBlockHalf.LOWER)
                    .with(FACING, facing);
        }
        return null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state,
                         LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(),
                getDefaultState()
                        .with(HALF, DoubleBlockHalf.UPPER)
                        .with(FACING, state.get(FACING)));
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        DoubleBlockHalf half = state.get(HALF);
        BlockPos otherPos = half == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
        BlockState otherState = world.getBlockState(otherPos);

        if (otherState.getBlock() == this && otherState.get(HALF) != half) {
            world.setBlockState(otherPos, Blocks.AIR.getDefaultState(),
                    Block.NOTIFY_ALL | Block.SKIP_DROPS);
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        BlockPos bePos = state.get(HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos;
        BlockEntity be = world.getBlockEntity(bePos);

        if (be instanceof ATMBlockEntity atmBE) {
            player.openHandledScreen(atmBE);

            // ↓ 開いた瞬間に残高をクライアントに送る
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            int balance = BankDataManager.get(serverPlayer.getServer())
                    .getBalance(serverPlayer.getUuid());
            ServerPlayNetworking.send(serverPlayer,
                    new BankUpdateS2CPacket(balance, "", "ERROR", 0));
        }

        if (be instanceof ATMBlockEntity atmBE) {
            player.openHandledScreen(atmBE);

            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

            // 残高を送信
            int balance = BankDataManager.get(serverPlayer.getServer())
                    .getBalance(serverPlayer.getUuid());
            ServerPlayNetworking.send(serverPlayer,
                    new BankUpdateS2CPacket(balance, "", "ERROR", 0));

            // プレイヤー一覧を送信 ← 追加
            ModPackets.sendPlayerList(serverPlayer);
        }

        return ActionResult.CONSUME;
    }
}