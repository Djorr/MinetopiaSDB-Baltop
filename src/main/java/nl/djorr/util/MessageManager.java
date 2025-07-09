package nl.djorr.util;

import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * MessageManager verzorgt het ophalen en versturen van meertalige berichten.
 * Ondersteunt Nederlands en Engels via resource bundles.
 *
 * @author Djorr
 */
public class MessageManager {

    private static final String BASE_NAME = "messages";

    /**
     * Haal een bericht op in de gewenste taal.
     * @param key De sleutel van het bericht
     * @param locale De gewenste taal (bijv. Locale.ENGLISH of new Locale("nl"))
     * @param args Optionele parameters voor het bericht
     * @return Het geformatteerde bericht
     */
    public static String getMessage(String key, Locale locale, Object... args) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
            String pattern = bundle.getString(key);
            return MessageFormat.format(pattern, args);
        } catch (MissingResourceException e) {
            return "[Missing message: " + key + "]";
        }
    }

    /**
     * Stuur een bericht naar een CommandSender in de gewenste taal.
     * @param sender De ontvanger
     * @param key De sleutel van het bericht
     * @param locale De gewenste taal
     * @param args Optionele parameters
     */
    public static void sendMessage(CommandSender sender, String key, Locale locale, Object... args) {
        sender.sendMessage(getMessage(key, locale, args));
    }
} 