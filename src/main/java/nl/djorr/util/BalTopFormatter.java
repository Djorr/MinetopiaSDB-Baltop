package nl.djorr.util;

import nl.djorr.util.BalanceUtils.PlayerBalance;
import java.text.SimpleDateFormat;
import java.util.*;

public class BalTopFormatter {
    public static class Output {
        public final List<String> lines;
        public Output(List<String> lines) { this.lines = lines; }
    }

    public static Output format(List<PlayerBalance> sorted, int page) {
        int ENTRIES_PER_PAGE = 8;
        int totalPages = (int) Math.ceil(sorted.size() / (double) ENTRIES_PER_PAGE);
        if (totalPages == 0) totalPages = 1;
        int safePage = Math.min(Math.max(page, 1), totalPages);
        int start = (safePage - 1) * ENTRIES_PER_PAGE;
        int end = Math.min(start + ENTRIES_PER_PAGE, sorted.size());
        double serverTotalPersonal = sorted.stream().mapToDouble(pb -> pb.personal).sum();
        double serverTotalSavings = sorted.stream().mapToDouble(pb -> pb.savings).sum();
        double serverTotalBusiness = sorted.stream().mapToDouble(pb -> pb.business).sum();
        double serverTotalGovernment = sorted.stream().mapToDouble(pb -> pb.government).sum();
        List<String> lines = new ArrayList<>();
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
        lines.add("§8§m----------------------------------------");
        lines.add("§b§lTop balances §7[Page" + safePage + "/" + totalPages + "] §7(" + date + ")");
        lines.add("§7Totaal: §f" + formatMoney(serverTotalPersonal + serverTotalSavings + serverTotalBusiness + serverTotalGovernment));
        lines.add("§6[Persoonlijk]: §e" + formatMoney(serverTotalPersonal) + " §7| §2[Spaar]: §a" + formatMoney(serverTotalSavings));
        lines.add("§5[Zakelijk]: §d" + formatMoney(serverTotalBusiness) + " §7| §4[Overheid]: §c" + formatMoney(serverTotalGovernment));
        lines.add(" ");
        for (int i = start; i < end; i++) {
            PlayerBalance pb = sorted.get(i);
            String name = getPlayerName(pb.uuid);
            lines.add("§6" + (i + 1) + ". §e" + name + " §7- §f" + formatMoney(pb.getTotal()) +
                    " §8[§e" + formatMoney(pb.personal) + " §7/ §2" + formatMoney(pb.savings) + "§8]");
        }
        if (safePage < totalPages) {
            lines.add("§7Typ §b/sdbbaltop " + (safePage + 1) + " §7voor de volgende pagina.");
        }
        lines.add("§8§m----------------------------------------");
        return new Output(lines);
    }

    private static String getPlayerName(UUID uuid) {
        org.bukkit.OfflinePlayer player = org.bukkit.Bukkit.getOfflinePlayer(uuid);
        return player != null && player.getName() != null ? player.getName() : uuid.toString();
    }

    private static String formatMoney(double amount) {
        return String.format("€%,.2f", amount);
    }
} 