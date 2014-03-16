/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.ministeam;

import bundled.steamtrade.org.json.JSONArray;
import bundled.steamtrade.org.json.JSONException;
import bundled.steamtrade.org.json.JSONObject;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
@SuppressWarnings("StaticNonFinalUsedInInitialization")
public class FrontendUserList {
    
    final static File USERDATA_FILE = new File("users.json");
    
    private static Map<String, FrontendClientInfo> userStore = new TreeMap<>();
    
    static {
        //userList.put("", new UserProperty("", null));
        userStore.put("", new FrontendClientInfo("", "", null));
        
        try {
            JSONObject data = new JSONObject(Util.readFile(USERDATA_FILE));
            
            JSONArray clients = data.getJSONArray("clients");
            
            for (int i = 0; i < clients.length(); i++) {
                JSONObject client = clients.getJSONObject(i);
                
                userStore.put(client.getString("username"), 
                        new FrontendClientInfo(client.getString("username"),
                        client.optString("password", ""),
                        client.optString("authval", null))
                        );
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    static Map<String, FrontendClientInfo> getUserList() {
        return userStore;
    }
}