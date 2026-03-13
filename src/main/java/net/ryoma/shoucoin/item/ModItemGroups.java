package net.ryoma.shoucoin.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.ryoma.shoucoin.ShoucoinMod;
import net.ryoma.shoucoin.block.ModBlocks;

public class ModItemGroups {

    public static final RegistryKey<ItemGroup> SHOUCOIN_GROUP_KEY =
            RegistryKey.of(RegistryKeys.ITEM_GROUP,
                    Identifier.of(ShoucoinMod.MOD_ID, "shoucoin_group"));

    public static final ItemGroup SHOUCOIN_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.GOLD_COIN))
            .displayName(Text.translatable("itemGroup.shoucoinmod"))
            .entries((context, entries) -> {
                entries.add(ModItems.SHOUCOIN);
                entries.add(ModItems.IRON_COIN);
                entries.add(ModItems.GOLD_COIN);
                entries.add(ModItems.DIAMOND_COIN);
                entries.add(ModItems.EMERALD_COIN);
                entries.add(ModItems.NETHERITE_COIN);
                entries.add(ModItems.PORTABLE_CRAFTING_TABLE);

                entries.add(ModBlocks.BANK_JOB_BLOCK);
            })
            .build();

    public static void register() {
        Registry.register(Registries.ITEM_GROUP, SHOUCOIN_GROUP_KEY, SHOUCOIN_GROUP);
    }
}