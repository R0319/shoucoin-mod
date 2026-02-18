package net.ryoma.shoucoin.item;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.ryoma.shoucoin.ShoucoinMod;

public class ModItems {
    public static final Item COPPER_COIN = registerItem("copper_coin", new Item(new Item.Settings().registryKey(
            RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ShoucoinMod.MOD_ID,"copper_coin")))));
    public static final Item IRON_COIN = registerItem("iron_coin", new Item(new Item.Settings().registryKey(
            RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ShoucoinMod.MOD_ID,"iron_coin")))));
    public static final Item GOLD_COIN = registerItem("gold_coin", new Item(new Item.Settings().registryKey(
            RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ShoucoinMod.MOD_ID,"gold_coin")))));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(ShoucoinMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        ShoucoinMod.LOGGER.info("Registering ModItems for " + ShoucoinMod.MOD_ID);
    }
}
