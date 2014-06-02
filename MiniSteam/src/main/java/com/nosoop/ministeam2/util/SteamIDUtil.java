/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.ministeam2.util;

import bundled.steamtrade.org.json.JSONException;
import bundled.steamtrade.org.json.JSONObject;
import bundled.steamtrade.org.json.XML;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;
import uk.co.thomasc.steamkit.types.steamid.SteamID;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class SteamIDUtil {
    final static String STEAMID_READABLE = "%s_%d_%d";

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

    /**
     * Helper method that resolves the long-form SteamID value from a vanity URL
     * name.
     *
     * @param name The vanity name of the profile. ... .com/id/${name}
     * @return A long value referring to the SteamID of the profile, or -1 if
     * not found.
     * @throws IOException
     * @throws JSONException
     */
    public static long resolveSteamVanityURLToSteamID64(String name)
            throws IOException, JSONException {
        JSONObject profile = getCommunityProfile("id/" + name);

        if (profile.optJSONObject("profile") == null) {
            return -1;
        }

        return profile.getJSONObject("profile").getLong("steamID64");
    }

    /**
     * Helper method that resolves a screen name from a long-form SteamID value.
     *
     * @param name The long-format SteamID.
     * @return A String value representing the name of the user, or null if the
     * profile was not found.
     * @throws IOException
     * @throws JSONException
     */
    public static String resolveSteamID64ToUserName(long id) throws IOException,
            JSONException {
        JSONObject profile = getCommunityProfile("profiles/" + id);

        if (profile.optJSONObject("profile") == null) {
            return null;
        }

        return profile.getJSONObject("profile").getString("steamID");
    }

    /**
     * Returns a JSONObject containing data from the MXL community API.
     *
     * @param profileLocation A profile in the format id/${vanityName} or
     * profiles/${steamID64}.
     * @return
     * @throws IOException
     * @throws JSONException
     */
    private static JSONObject getCommunityProfile(String profileLocation)
            throws IOException, JSONException {
        String vanityPageLocation = String.format(
                "http://steamcommunity.com/%s/?xml=1&l=english", profileLocation);

        URL uri = new URL(vanityPageLocation);
        URLConnection conn = uri.openConnection();
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
        conn.setRequestProperty("User-Agent", "DistributableMarketeeringTool/1.0");

        InputStream is = conn.getInputStream();
        if (conn.getContentEncoding().contains("gzip")) {
            is = new GZIPInputStream(is);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append('\n');
        }

        return XML.toJSONObject(sb.toString());
    }
}
