package com.nosoop.ministeam2.util;

import java.util.regex.Pattern;
import uk.co.thomasc.steamkit.types.steamid.SteamID;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class SteamIDUtil {
    /**
     * Format for readable SteamIDs.
     */
    private final static String STEAMID_READABLE = "%s_%d_%d";
    /**
     * Pattern to test if a given long number is a SteamID64.
     */
    public final static Pattern STEAMID64_PATTERN = 
            Pattern.compile("765611(\\d){11}");

    public static String convertReadable(SteamID id) {
        return String.format(STEAMID_READABLE,
                getAccountTypeChar(id), getUniverse(id),
                // Convert account number.
                (int) (id.convertToLong()) >> 1);
    }

    public static char getAccountTypeChar(SteamID id) {
        switch (id.getAccountType()) {
            case Individual:
                return 'U';
            case Clan:
                return 'g';
            default:
                return 'I';
        }
    }

    public static int getUniverse(SteamID id) {
        switch (id.getAccountUniverse()) {
            case Invalid:
                return 0;
            case Public:
                return 1;
            case Beta:
                return 2;
            case Internal:
                return 3;
            case Dev:
                return 4;
            case RC:
                return 5;
            case Max:
                return 6;
            default:
                return 0;
        }
    }
}
