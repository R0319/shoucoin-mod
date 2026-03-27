package net.ryoma.shoucoin.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtLong;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BankDataManager extends PersistentState {

    private final Map<UUID, Long> balances = new HashMap<>();

    // 残高取得（未登録なら0）
    public long getBalance(UUID uuid) {
        return balances.getOrDefault(uuid, 0L);
    }

    // 残高セット
    public void setBalance(UUID uuid, long amount) {
        balances.put(uuid, amount);
        markDirty(); // ワールド保存時に書き込まれるようにする
    }

    // 入金（オーバーフロー防止のため上限チェック付き）
    public void deposit(UUID uuid, long amount) {
        long newBalance = getBalance(uuid) + amount;
        setBalance(uuid, Math.min(newBalance, Long.MAX_VALUE / 2));
    }

    // 出金（残高不足ならfalse）
    public boolean withdraw(UUID uuid, long amount) {
        long current = getBalance(uuid);
        if (current < amount) return false;
        setBalance(uuid, current - amount);
        return true;
    }

    // NBTに書き込み（ワールドデータとして保存）
    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        NbtCompound balanceNbt = new NbtCompound();
        balances.forEach((uuid, balance) ->
                balanceNbt.putLong(uuid.toString(), balance)
        );
        nbt.put("balances", balanceNbt);
        return nbt;
    }

    // NBTから読み込み
    // 後方互換性: 旧バージョンでintとして保存されたデータも正しく読み込む
    public static BankDataManager readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        BankDataManager manager = new BankDataManager();
        NbtCompound balanceNbt = nbt.getCompound("balances");
        balanceNbt.getKeys().forEach(key -> {
            UUID uuid = UUID.fromString(key);
            NbtElement element = balanceNbt.get(key);
            long balance;
            if (element instanceof NbtLong) {
                // 新フォーマット: long
                balance = balanceNbt.getLong(key);
            } else {
                // 旧フォーマット: int → longに移行
                balance = balanceNbt.getInt(key);
            }
            manager.balances.put(uuid, balance);
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
    public Map<UUID, Long> getAllBalances() {
        return Collections.unmodifiableMap(balances);
    }
}
