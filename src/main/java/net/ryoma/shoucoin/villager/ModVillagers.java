package net.ryoma.shoucoin.villager;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
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
import net.ryoma.shoucoin.item.ModItems;

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
        ShoucoinMod.LOGGER.info("POI: " + BANK_JOB_POI_KEY);
        ShoucoinMod.LOGGER.info("Profession: " + Registries.VILLAGER_PROFESSION.getId(BANK_VILLAGER));
        ShoucoinMod.LOGGER.info("States: " + ModBlocks.BANK_JOB_BLOCK.getStateManager().getStates());

        // レベル1の取引（基本素材）
        TradeOfferHelper.registerVillagerOffers(BANK_VILLAGER, 1, factories -> {

            // 銅の原石 → 1S
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.RAW_COPPER, 1),
                    new ItemStack(ModItems.SHOUCOIN, 1),
                    32, 5, 0.02f
            ));

            // クォーツ → 2S
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.QUARTZ, 1),
                    new ItemStack(ModItems.SHOUCOIN, 2),
                    32, 5, 0.02f
            ));

            // レッドストーン → 2S
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.REDSTONE, 1),
                    new ItemStack(ModItems.SHOUCOIN, 2),
                    32, 5, 0.02f
            ));

            // 鉄の原石 → 8S
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.RAW_IRON, 1),
                    new ItemStack(ModItems.SHOUCOIN, 8),
                    32, 5, 0.02f
            ));
        });

        // レベル2の取引（中級素材）
        TradeOfferHelper.registerVillagerOffers(BANK_VILLAGER, 2, factories -> {

            // 金の原石 → 12S
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.RAW_GOLD, 1),
                    new ItemStack(ModItems.SHOUCOIN, 12),
                    24, 10, 0.02f
            ));

            // 深層岩のダイヤモンド鉱石 → 50S (IRON_COIN x5)
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.DEEPSLATE_DIAMOND_ORE, 1),
                    new ItemStack(ModItems.IRON_COIN, 5),
                    16, 10, 0.02f
            ));

            // 深層岩のエメラルド鉱石 → 50S (IRON_COIN x5)
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.DEEPSLATE_EMERALD_ORE, 1),
                    new ItemStack(ModItems.IRON_COIN, 5),
                    16, 10, 0.02f
            ));
        });

        // レベル3の取引（希少素材）
        TradeOfferHelper.registerVillagerOffers(BANK_VILLAGER, 3, factories -> {

            // ネザライトの欠片 → 250S (IRON_COIN x25)
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.NETHERITE_SCRAP, 1),
                    new ItemStack(ModItems.IRON_COIN, 25),
                    12, 20, 0.02f
            ));
        });
    }
}