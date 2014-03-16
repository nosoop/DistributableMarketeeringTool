/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.ministeam.friends;

import uk.co.thomasc.steamkit.types.steamid.SteamID;

/**
 * A data structure that holds friend states to display on the main list screen.
 * Also stores the friend's steamid for use in sending messages.
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class FriendData implements Comparable<FriendData> {

    long steamid64;
    String name;
    String status;
    private SteamID steamid;

    public FriendData(SteamID steamid, String name, String status) {
        steamid64 = steamid.convertToLong();
        this.name = name;
        this.status = status;
        this.steamid = null;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public long getSteamid64() {
        return steamid64;
    }

    @Override
    public String toString() {
        return name;
    }

    public SteamID getSteamID() {
        if (steamid == null) {
            steamid = new SteamID(steamid64);
        }
        return steamid;
    }
    
    @Override
    public int hashCode() {
        return 59*Long.valueOf(steamid64).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FriendData other = (FriendData) obj;
        if (this.steamid64 != other.steamid64) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(FriendData t) {
        return name.compareTo(t.name);
    }
}
