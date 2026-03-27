package net.ryoma.shoucoin.block;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.ryoma.shoucoin.config.ShoucoinConfig;
import net.ryoma.shoucoin.network.ShopTradesS2CPacket;
import net.ryoma.shoucoin.screen.ShopScreenHandler;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ShopBlock extends HorizontalFacingBlock {

    public static final MapCodec<ShopBlock> CODEC = createCodec(ShopBlock::new);

    // SOUTH向きを基準に定義。非対称でも方角ごとに正しく回転する
    // 値を変える場合はここだけ編集すればOK
    private static final double X1 = 1.5, X2 = 14.5; // 左右幅
    private static final double Y1 = 0,   Y2 = 10;   // 高さ
    private static final double Z1 = 3.5, Z2 = 14.5; // 奥行き

    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);
    static {
        // Minecraftモデルのデフォルトと合わせてNORTHを基準にする
        SHAPES.put(Direction.NORTH, Block.createCuboidShape(X1,    Y1, Z1,    X2,    Y2, Z2));
        SHAPES.put(Direction.SOUTH, Block.createCuboidShape(16-X2, Y1, 16-Z2, 16-X1, Y2, 16-Z1));
        SHAPES.put(Direction.EAST,  Block.createCuboidShape(16-Z2, Y1, X1,    16-Z1, Y2, X2));
        SHAPES.put(Direction.WEST,  Block.createCuboidShape(Z1,    Y1, 16-X2, Z2,    Y2, 16-X1));
    }

    public ShopBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, net.minecraft.world.BlockView world,
                                      BlockPos pos, ShapeContext context) {
        return SHAPES.getOrDefault(state.get(FACING), SHAPES.get(Direction.SOUTH));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, net.minecraft.world.BlockView world,
                                        BlockPos pos, ShapeContext context) {
        return SHAPES.getOrDefault(state.get(FACING), SHAPES.get(Direction.SOUTH));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                (syncId, inventory, p) -> new ShopScreenHandler(syncId, inventory),
                Text.translatable("block.shoucoinmod.shop_block")
        ));

        // GUIを開くたびに最新のconfigを送信 → リロード後も即反映
        List<String> list = ShoucoinConfig.trades.stream()
                .map(t -> t.buyItem + "," + t.buyCount + "," + t.sellItem + "," + t.sellCount)
                .toList();
        ServerPlayNetworking.send((ServerPlayerEntity) player, new ShopTradesS2CPacket(list));

        return ActionResult.CONSUME;
    }
}
