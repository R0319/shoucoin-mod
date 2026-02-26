package net.ryoma.shoucoin.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.ryoma.shoucoin.screen.PortableCraftingScreenHandler;

public class PortableCraftingTableItem extends Item {

    public PortableCraftingTableItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {

        if (!world.isClient()) {

            NamedScreenHandlerFactory factory =
                    new SimpleNamedScreenHandlerFactory(
                            (syncId, inventory, p) ->
                                    new PortableCraftingScreenHandler(syncId, inventory),
                            Text.translatable("PortableCraftingTableItemText.shoucoinmod")
                    );

            player.openHandledScreen(factory);
        }

        return ActionResult.SUCCESS;
    }
}