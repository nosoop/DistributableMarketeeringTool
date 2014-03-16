/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.ministeam.trade;

/**
 * 
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class TradeDisplayItem implements Comparable<TradeDisplayItem> {

    int classid;
    String displayName;
    int count;

    public TradeDisplayItem(int classid, String displayName) {
        this.classid = classid;
        this.displayName = displayName;

        this.count = 0;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public void incrementCount(int factor) {
        count += factor;
    }

    public int getCount() {
        return count;
    }

    public int getClassid() {
        return classid;
    }

    /**
     * Compares display items by name.
     *
     * @param t TradeDisplayItem to compare.
     * @return
     */
    @Override
    public int compareTo(TradeDisplayItem t) {
        return displayName.compareTo(t.displayName);
    }
}
