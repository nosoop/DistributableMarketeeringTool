package com.nosoop.ministeam2.prefs;

import bundled.steamtrade.org.json.JSONException;
import bundled.steamtrade.org.json.JSONObject;
import bundled.steamtrade.org.json.JSONTokener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds all the settings for the application.
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class ClientSettings {
    /**
     * Hardcoded file instance to load preferences from.
     */
    final static File PREFS_FILE = new File("." + File.separator
            + "dmtprefs.json");
    /**
     * Logger instance.
     */
    private Logger logger = LoggerFactory.getLogger(ClientSettings.this.
            getClass().getSimpleName());
    /**
     * Property variables to be read across the entire application.
     */
    String dateTimeFormat, chatLogEntryFormat;

    /**
     * Private constructor.
     */
    private ClientSettings() {
        JSONObject preferenceJSON = new JSONObject();
        if (PREFS_FILE.exists()) {
            try (FileInputStream fis = new FileInputStream(PREFS_FILE)) {
                preferenceJSON = new JSONObject(new JSONTokener(fis));
            } catch (IOException e) {
                logger.error("I/O error on preferences file.", e);
            } catch (JSONException e) {
                logger.error("JSON parsing failure on preferences file.", e);
            }
        }
        
        dateTimeFormat = preferenceJSON.optString(
                "DateTimeFormat", "%1$tm/%1$td/%1$tY %1$tI:%1$tM:%1$tS %1$Tp");
        
        chatLogEntryFormat = preferenceJSON.optString(
                "ChatlogEntryFormat", "[${DATE}] ${EVTMSG}%n");
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public String getChatLogEntryFormat() {
        return chatLogEntryFormat;
    }
    
    /**
     * Singleton instance for preferences.
     */
    private static ClientSettings instance = null;

    /**
     * Returns the one existing copy of SteamClientPreferences.
     */
    public static ClientSettings getInstance() {
        return (instance != null
                ? instance : (instance = new ClientSettings()));
    }
}
