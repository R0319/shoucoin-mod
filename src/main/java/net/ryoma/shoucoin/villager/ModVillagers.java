package net.ryoma.shoucoin.villager;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import net.ryoma.shoucoin.ShoucoinMod;
import net.ryoma.shoucoin.block.ModBlocks;
import net.ryoma.shoucoin.config.ShoucoinConfig;
import net.ryoma.shoucoin.item.ModItems;

import java.util.List;

public class ModVillagers {
    public static final RegistryKey<PointOfInterestType> BANK_JOB_POI_KEY = registerPoiKey("bank_job_poi");
    public static final PointOfInterestType BANK_JOB_POI = registerPOI("bank_job_poi", ModBlocks.BANK_JOB_BLOCK);
    public static final VillagerProfession BANK_VILLAGER = registerProfession("bank_villager", BANK_JOB_POI_KEY);

    private static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> type) {
        return Registry.register(Registries.VILLAGER_PROFESSION,
                Identifier.of(ShoucoinMod.MOD_ID, name),
                new VillagerProfession(name,
                        entry -> entry.matchesKey(type),
                        entry -> entry.matchesKey(type),
                        ImmutableSet.of(),
                        ImmutableSet.of(),
                        SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER
                )
        );
    }

    private static int ticketCount = 1;
    private static int villagerSearchDistance = 32;

    private static PointOfInterestType registerPOI(String name, Block block) {
        return PointOfInterestHelper.register(Identifier.of(ShoucoinMod.MOD_ID, name),
                1, 1,
                block
        );
    }

    private static RegistryKey<PointOfInterestType> registerPoiKey(String name) {
        return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE,
                Identifier.of(ShoucoinMod.MOD_ID, name));
    }

    public static void registerVillagers() {
        ShoucoinMod.LOGGER.info("Registering Villagers for" + ShoucoinMod.MOD_ID);

        // レベルごとにコンフィグから取引を登録
        for (int level = 1; level <= 5; level++) {
            final int lvl = level;
            List<ShoucoinConfig.TradeEntry> levelTrades = ShoucoinConfig.trades.stream()
                    .filter(t -> t.level == lvl)
                    .toList();

            if (levelTrades.isEmpty()) continue;

            TradeOfferHelper.registerVillagerOffers(BANK_VILLAGER, lvl, factories -> {
                for (ShoucoinConfig.TradeEntry entry : levelTrades) {
                    factories.add((entity, random) -> {
                        Item buyItem  = Registries.ITEM.get(Identifier.of(entry.buyItem));
                        Item sellItem = Registries.ITEM.get(Identifier.of(entry.sellItem));

                        return new TradeOffer(
                                new TradedItem(buyItem, entry.buyCount),
                                new ItemStack(sellItem, entry.sellCount),
                                entry.maxUses,
                                entry.villagerXp,
                                entry.priceMultiplier
                        );
                    });
                }
            });
        }
    }
}