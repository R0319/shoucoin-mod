package net.ryoma.shoucoin.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.ryoma.shoucoin.ShoucoinMod;
import net.ryoma.shoucoin.block.entity.ATMBlockEntity;

public class ModBlockEntities {
    public static final BlockEntityType<ATMBlockEntity> ATM_BLOCK_ENTITY =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(ShoucoinMod.MOD_ID, "atm"),
                    BlockEntityType.Builder.create(ATMBlockEntity::new, ModBlocks.ATM_BLOCK).build()
            );

    public static void registerBlockEntities() {
        ShoucoinMod.LOGGER.info("Registering Block Entities for " + ShoucoinMod.MOD_ID);
    }
}
