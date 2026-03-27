package net.ryoma.shoucoin.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.ryoma.shoucoin.ShoucoinMod;

public class ModEnchantments {

    /** 3×3採掘エンチャント */
    public static final RegistryKey<Enchantment> TUNNEL = RegistryKey.of(
            RegistryKeys.ENCHANTMENT,
            Identifier.of(ShoucoinMod.MOD_ID, "tunnel")
    );
}
