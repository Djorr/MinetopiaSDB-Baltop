package nl.djorr;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import nl.djorr.command.BalTopCommand;

/**
 * MinetopiaSDB | BalTop Addon
 *
 * Plugin entrypoint. Registreert commando's en initialiseert resources.
 *
 * @author Djorr
 * @version 1.0.0
 * @website https://discord.rubixdevelopment.nl/
 */
public class MinetopiaSDBBalTop extends JavaPlugin {

    /**
     * Wordt aangeroepen wanneer de plugin wordt ingeschakeld.
     */
    @Override
    public void onEnable() {
        // Registreer commando's en initialiseer resources
        getLogger().info("MinetopiaSDB | BalTop Addon is ingeschakeld.");
        this.getCommand("sdbbaltop").setExecutor(new BalTopCommand(this));

        new Metrics(this, 26433);
    }

    /**
     * Wordt aangeroepen wanneer de plugin wordt uitgeschakeld.
     */
    @Override
    public void onDisable() {
        getLogger().info("MinetopiaSDB | BalTop Addon is uitgeschakeld.");
    }
} 