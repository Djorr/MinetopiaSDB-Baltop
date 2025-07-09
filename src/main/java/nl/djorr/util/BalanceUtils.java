package nl.djorr.util;

import lombok.Setter;
import nl.minetopiasdb.api.banking.BankUtils;
import nl.minetopiasdb.api.banking.Bankaccount;
import nl.minetopiasdb.api.enums.BankAccountType;
import nl.minetopiasdb.api.playerdata.PlayerManager;
import nl.minetopiasdb.api.playerdata.objects.SDBPlayer;

import java.util.*;
import java.util.stream.Collectors;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Utility class voor het ophalen en sorteren van balances van alle spelers.
 *
 * @author Djorr
 */
public class BalanceUtils {

    private static Economy economy = null;

    public static void initEconomy() {
        if (economy == null) {
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                economy = rsp.getProvider();
            }
        }
    }

    /**
     * Haal alle bankaccounts op van een bepaald type.
     * @param types De gewenste BankAccountTypes
     * @return Lijst van bankaccounts
     */
    public static List<Bankaccount> getAccountsByType(BankAccountType... types) {
        return BankUtils.getInstance().getAccounts(types);
    }

    /**
     * Haal een mapping op van ALLE spelers (SDBPlayers) naar hun balances.
     * Dit gebruikt de SDBPlayerManager of vergelijkbare API van MinetopiaSDB.
     * Spelers zonder account krijgen saldo 0.
     *
     * @param types De gewenste BankAccountTypes
     * @return Map van UUID naar PlayerBalance
     */
    public static Map<UUID, PlayerBalance> getAllPlayerBalances(BankAccountType... types) {
        initEconomy();
        Map<UUID, PlayerBalance> result = new HashMap<>();
        // Verzamel alle unieke spelers (offline én online)
        Set<UUID> allUuids = new HashSet<>();
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getUniqueId() != null) {
                allUuids.add(offlinePlayer.getUniqueId());
            }
        }
        for (org.bukkit.entity.Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getUniqueId() != null) {
                allUuids.add(onlinePlayer.getUniqueId());
            }
        }
        // Voor elke unieke speler: haal Vault balance (personal) en SDB spaarrekening op
        for (UUID uuid : allUuids) {
            PlayerBalance balance = new PlayerBalance(uuid);
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            // Haal persoonlijke balance uit Vault
            if (economy != null && player != null) {
                balance.personal = economy.getBalance(player);
            }
            // Haal spaarrekening uit SDB
            List<Bankaccount> accounts = BankUtils.getInstance().getAccounts(uuid, BankAccountType.SAVINGS, BankAccountType.BUSINESS, BankAccountType.GOVERNMENT);
            for (Bankaccount account : accounts) {
                if (account.getType() == BankAccountType.SAVINGS) {
                    balance.savings += account.getBalance();
                } else if (account.getType() == BankAccountType.BUSINESS) {
                    balance.business += account.getBalance();
                } else if (account.getType() == BankAccountType.GOVERNMENT) {
                    balance.government += account.getBalance();
                }
            }
            result.put(uuid, balance);
        }
        return result;
    }

    /**
     * Sorteer een lijst van PlayerBalance op totaal saldo (aflopend).
     * @param balances De lijst van PlayerBalance
     * @return Gesorteerde lijst
     */
    public static List<PlayerBalance> sortBalances(Collection<PlayerBalance> balances) {
        return balances.stream()
                .sorted(Comparator.comparingDouble(PlayerBalance::getTotal).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Data class voor het bijhouden van balances per speler.
     */
    @Setter
    public static class PlayerBalance {
        public final UUID uuid;
        public double personal = 0.0;
        public double savings = 0.0;
        public double business = 0.0;
        public double government = 0.0;

        public PlayerBalance(UUID uuid) {
            this.uuid = uuid;
        }

        public double getTotal() {
            return personal + savings;
        }
    }
} 