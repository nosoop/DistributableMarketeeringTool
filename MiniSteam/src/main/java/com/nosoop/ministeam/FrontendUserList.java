/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.ministeam;

import bundled.steamtrade.org.json.JSONArray;
import bundled.steamtrade.org.json.JSONException;
import bundled.steamtrade.org.json.JSONObject;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
@SuppressWarnings("StaticNonFinalUsedInInitialization")
public class FrontendUserList {

    final static File USERDATA_FILE = new File("users.json");
    private static Map<String, FrontendClientInfo> userStore = new TreeMap<>();
    static Logger logger = LoggerFactory.getLogger(FrontendUserList.class.getSimpleName());

    static {
        userStore.put("", new FrontendClientInfo("", "", null));

        try {
            if (USERDATA_FILE.exists()) {
                JSONObject data = new JSONObject(Util.readFile(USERDATA_FILE));

                JSONArray clients = data.getJSONArray("clients");

                for (int i = 0; i < clients.length(); i++) {
                    JSONObject client = clients.getJSONObject(i);

                    userStore.put(client.getString("username"),
                            new FrontendClientInfo(client.getString("username"),
                            client.optString("password", ""),
                            client.optString("machineauth", null)));
                }
            } else {
                logger.error("User credential storage file does not exist.");
            }
        } catch (JSONException ex) {
            logger.error("Error loading user storage.", ex);
        }
    }

    static Map<String, FrontendClientInfo> getUserList() {
        return userStore;
    }
}