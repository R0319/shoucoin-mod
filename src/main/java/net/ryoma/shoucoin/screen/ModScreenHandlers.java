package net.ryoma.shoucoin.screen;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.ryoma.shoucoin.ShoucoinMod;

public class ModScreenHandlers {
    public static final ScreenHandlerType<ATMScreenHandler> ATM_SCREEN_HANDLER =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(ShoucoinMod.MOD_ID, "atm"),
                    new ScreenHandlerType<>(ATMScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
            );

    public static final ScreenHandlerType<ShopScreenHandler> SHOP_SCREEN_HANDLER =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(ShoucoinMod.MOD_ID, "shop"),
                    new ScreenHandlerType<>(ShopScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
            );

    public static void registerScreenHandlers() {
        ShoucoinMod.LOGGER.info("Registering Screen Handlers for " + ShoucoinMod.MOD_ID);
    }
}
