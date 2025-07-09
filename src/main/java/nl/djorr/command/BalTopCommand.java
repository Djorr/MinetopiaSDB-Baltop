package nl.djorr.command;

import nl.djorr.MinetopiaSDBBalTop;
import nl.djorr.util.BalanceUtils;
import nl.djorr.util.BalanceUtils.PlayerBalance;
import nl.djorr.util.MessageManager;
import nl.djorr.util.BalTopFormatter;
import nl.minetopiasdb.api.enums.BankAccountType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Command executor voor /sdbbaltop.
 * Verantwoordelijk voor het verwerken van het commando en het tonen van de top balances.
 *
 * @author Djorr
 */
public class BalTopCommand implements CommandExecutor {

    private final MinetopiaSDBBalTop plugin;
    private static final int ENTRIES_PER_PAGE = 10;

    public BalTopCommand(MinetopiaSDBBalTop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender.hasPermission("sdbbaltop.use") || sender.isOp())) {
            sender.sendMessage("§cJe hebt geen permissie om dit commando uit te voeren.");
            return true;
        }
        final int page = parsePageArg(args);
        sender.sendMessage("§eOrdering balances, please wait...");
        new BukkitRunnable() {
            @Override
            public void run() {
                Map<UUID, PlayerBalance> balances = BalanceUtils.getAllPlayerBalances();
                List<PlayerBalance> sorted = BalanceUtils.sortBalances(balances.values());
                BalTopFormatter.Output output = BalTopFormatter.format(sorted, page);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    for (String line : output.lines) {
                        sender.sendMessage(line);
                    }
                });
            }
        }.runTaskAsynchronously(plugin);
        return true;
    }

    private int parsePageArg(String[] args) {
        if (args.length > 0) {
            try {
                int page = Integer.parseInt(args[0]);
                return Math.max(page, 1);
            } catch (NumberFormatException e) {
                return 1;
            }
        }
        return 1;
    }

    /**
     * Bepaal de gewenste taal van de sender (default NL, anders EN).
     */
    private Locale getLocale(CommandSender sender) {
        if (sender instanceof Player) {
            // Hier kun je eventueel player preferences ophalen
            return new Locale("nl");
        }
        return Locale.ENGLISH;
    }

    /**
     * Parse het type argument naar BankAccountType(s).
     */
    private BankAccountType[] parseType(String arg) {
        switch (arg) {
            case "persoonlijk":
            case "personal":
                return new BankAccountType[]{BankAccountType.PERSONAL};
            case "spaarrekening":
            case "spaar":
            case "savings":
                return new BankAccountType[]{BankAccountType.SAVINGS};
            case "zakelijk":
            case "business":
                return new BankAccountType[]{BankAccountType.BUSINESS};
            case "overheid":
            case "government":
                return new BankAccountType[]{BankAccountType.GOVERNMENT};
            case "alles":
            case "all":
            default:
                return new BankAccountType[]{BankAccountType.PERSONAL, BankAccountType.SAVINGS};
        }
    }

    /**
     * Haal de naam van een speler op basis van UUID.
     */
    private String getPlayerName(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        return player != null && player.getName() != null ? player.getName() : uuid.toString();
    }

    /**
     * Format een bedrag als geld (met euroteken en 2 decimalen).
     */
    private String formatMoney(double amount) {
        return String.format("€%,.2f", amount);
    }
} 