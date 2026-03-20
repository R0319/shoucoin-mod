package net.ryoma.shoucoin.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BankDataManager extends PersistentState {

    private final Map<UUID, Integer> balances = new HashMap<>();

    // 残高取得（未登録なら0）
    public int getBalance(UUID uuid) {
        return balances.getOrDefault(uuid, 0);
    }

    // 残高セット
    public void setBalance(UUID uuid, int amount) {
        balances.put(uuid, amount);
        markDirty(); // ワールド保存時に書き込まれるようにする
    }

    // 入金
    public void deposit(UUID uuid, int amount) {
        setBalance(uuid, getBalance(uuid) + amount);
    }

    // 出金（残高不足ならfalse）
    public boolean withdraw(UUID uuid, int amount) {
        int current = getBalance(uuid);
        if (current < amount) return false;
        setBalance(uuid, current - amount);
        return true;
    }

    // NBTに書き込み（ワールドデータとして保存）
    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        NbtCompound balanceNbt = new NbtCompound();
        balances.forEach((uuid, balance) ->
                balanceNbt.putInt(uuid.toString(), balance)
        );
        nbt.put("balances", balanceNbt);
        return nbt;
    }

    // NBTから読み込み
    public static BankDataManager readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        BankDataManager manager = new BankDataManager();
        NbtCompound balanceNbt = nbt.getCompound("balances");
        balanceNbt.getKeys().forEach(key -> {
            UUID uuid = UUID.fromString(key);
            manager.balances.put(uuid, balanceNbt.getInt(key));
        });
        return manager;
    }

    // サーバーからManagerを取得するユーティリティ
    public static BankDataManager get(MinecraftServer server) {
        PersistentStateManager psm = server.getOverworld().getPersistentStateManager();
        return psm.getOrCreate(
                new PersistentState.Type<>(
                        BankDataManager::new,       // 新規作成時
                        BankDataManager::readNbt,   // ファイルから読み込み時
                        null
                ),
                "shoucoin_bank" // ワールドデータのファイル名 (.dat)
        );
    }

    // 残高順位表示の情報取得
    public Map<UUID, Integer> getAllBalances() {
        return Collections.unmodifiableMap(balances); // balancesはUUID→残高のMap
    }
}