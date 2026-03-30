package net.ryoma.shoucoin.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.ryoma.shoucoin.ShoucoinMod;

public class ModBlocks {
    public static final Block BANK_JOB_BLOCK = register(
            "bank_job_block",
            new BankJobBlock(AbstractBlock.Settings.copy(Blocks.GRAY_CONCRETE)
                    .nonOpaque()
                    .requiresTool()
                    .hardness(3.0f)
                    .resistance(6.0f))
    );

    public static final Block SHOP_BLOCK = register(
            "shop_block",
            new ShopBlock(AbstractBlock.Settings.copy(Blocks.GRAY_CONCRETE)
                    .nonOpaque()
                    .requiresTool()
                    .hardness(3.0f)
                    .resistance(6.0f))
    );

    public static final ATMBlock ATM_BLOCK = (ATMBlock) register(
            "atm",
            new ATMBlock(AbstractBlock.Settings.copy(Blocks.GRAY_CONCRETE)
                    .nonOpaque()
                    .requiresTool()
                    .hardness(3.0f)
                    .resistance(6.0f))
    );

    private static Block register(String name, Block block) {
        return Registry.register(
                Registries.BLOCK,
                Identifier.of(ShoucoinMod.MOD_ID,name),
                block
        );
    }

    // メインクラスのintialize()から呼ぶ用
    public static void registerBlocks() {
        ShoucoinMod.LOGGER.info("Registering Blocks for" + ShoucoinMod.MOD_ID);
    }
}
