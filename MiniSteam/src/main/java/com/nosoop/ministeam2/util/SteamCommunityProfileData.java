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

/**
 * Contains data from the Community Data API. Deprecated, but hey, it's free.
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class SteamCommunityProfileData {
    public final long steamID64;
    public final String steamID;

    private SteamCommunityProfileData(JSONObject jsonInput) throws JSONException {
        JSONObject profile = jsonInput.getJSONObject("profile");

        steamID = profile.getString("steamID");
        steamID64 = profile.getLong("steamID64");
    }

    /**
     * Helper method that gets Steam Community data through a vanity URL name.
     *
     * @param name The vanity name of the profile. ... .com/id/${name}
     * @return A SteamCommunityData instance referring to the id, or null if
     * there is none.
     * @throws IOException
     * @throws JSONException
     */
    public static SteamCommunityProfileData getDataForVanityName(String name)
            throws IOException, JSONException {
        JSONObject profile = getCommunityProfile("id/" + name);

        if (profile.optJSONObject("profile") == null) {
            return null;
        }

        return new SteamCommunityProfileData(profile);
    }

    /**
     * Helper method that gets Steam Community data by long-form SteamID.
     *
     * @param name The long-format SteamID.
     * @return A SteamCommunityData instance referring to the id, or null if
     * there is none.
     * @throws IOException
     * @throws JSONException
     */
    public static SteamCommunityProfileData getDataForSteamID64(long id)
            throws IOException, JSONException {
        JSONObject profile = getCommunityProfile("profiles/" + id);

        if (profile.optJSONObject("profile") == null) {
            return null;
        }

        return new SteamCommunityProfileData(profile);
    }

    /**
     * Returns a JSONObject containing data from the XML community API.
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
