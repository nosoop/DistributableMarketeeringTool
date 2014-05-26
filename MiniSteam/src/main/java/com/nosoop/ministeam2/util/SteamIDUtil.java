/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.ministeam2.util;

import uk.co.thomasc.steamkit.types.steamid.SteamID;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class SteamIDUtil {
    final static String STEAMID_READABLE = "%s_%d_%d";

    static String convertReadable(SteamID id) {
        return String.format(STEAMID_READABLE,
                getAccountTypeChar(id), getUniverse(id), id.getAccountID());
    }

    static char getAccountTypeChar(SteamID id) {
        switch (id.getAccountType()) {
            case Individual:
                return 'U';
            case Clan:
                return 'g';
            default:
                return 'I';
        }
    }

    static int getUniverse(SteamID id) {
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
