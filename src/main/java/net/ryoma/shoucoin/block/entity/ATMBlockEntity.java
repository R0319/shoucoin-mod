package net.ryoma.shoucoin.block.entity;


import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.ryoma.shoucoin.block.ModBlockEntities;
import net.minecraft.text.Text;
import net.ryoma.shoucoin.screen.ATMScreenHandler;
import org.jetbrains.annotations.Nullable;

public class ATMBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    public ATMBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ATM_BLOCK_ENTITY, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("ATM");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ATMScreenHandler(syncId, playerInventory, this);
    }
}
