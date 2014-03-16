/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.ministeam.trade;

import com.nosoop.steamtrade.inventory.TradeInternalItem;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class TradeOurDisplayItem extends TradeDisplayItem {

    List<TradeInternalItem> itemAddList;
    
    public TradeOurDisplayItem(int classid, String displayName) {
        super(classid, displayName);
        
        itemAddList = new ArrayList<>();
    }
    
    public void addItemToList(TradeInternalItem item) {
        itemAddList.add(item);
    }
    
    public List<TradeInternalItem> getItemList() {
        return itemAddList;
    }
}
