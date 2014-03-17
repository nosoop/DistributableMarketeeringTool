/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.ministeam;

import com.nosoop.ministeam.trade.TradeDisplayItem;
import com.nosoop.ministeam.trade.TradeOurDisplayItem;
import com.nosoop.steamtrade.inventory.*;
import com.nosoop.steamtrade.TradeListener;
import com.nosoop.steamtrade.status.TradeEvent;
import com.nosoop.steamtrade.status.TradeEvent.TradeAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A TradeListener that receives input from .
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class FrontendTrade extends TradeListener {

    FrontendClient client;
    SteamTradeWindow tradeWindow;
    Map<String, TradeOurDisplayItem> myInventoryItems;
    Map<String, TradeDisplayItem> otherOfferedItems, myOfferedItems;
    private final short MAX_ITEMS_IN_TRADE = 256;
    private TradeInternalItem ourTradeSlotsFilled[];
    String otherPlayerName;
    Logger logger;

    public FrontendTrade(FrontendClient client, String otherPlayerName) {
        super();
        
        this.logger = LoggerFactory.getLogger(FrontendTrade.class);

        this.client = client;
        this.tradeWindow = new SteamTradeWindow(this, otherPlayerName);

        this.otherPlayerName = otherPlayerName;

        myOfferedItems = new HashMap<>();
        otherOfferedItems = new HashMap<>();

        myInventoryItems = new HashMap<>();

        ourTradeSlotsFilled = new TradeInternalItem[MAX_ITEMS_IN_TRADE];
        for (int i = 0; i < ourTradeSlotsFilled.length; i++) {
            ourTradeSlotsFilled[i] = null;
        }

        logger.info("Trade session started.");
    }

    public final boolean tradePutFirstValidItem(TradeOurDisplayItem item) {
        List<TradeInternalItem> itemids = item.getItemList();
        for (TradeInternalItem itemid : itemids) {
            if (tradePutItem(itemid)) {

                String name = item.getDisplayName();
                if (myOfferedItems.containsKey(name)) {
                    myOfferedItems.get(name).incrementCount(1);
                } else {
                    TradeDisplayItem it = new TradeDisplayItem(
                            item.getClassid(), name);
                    it.incrementCount(1);
                    myOfferedItems.put(name, it);
                }
                tradeWindow.updateTradeCount(true, myOfferedItems.values());

                return true;
            }
        }
        return false;
    }

    public final boolean tradeRemoveFirstValidItem(TradeDisplayItem dispItem) {
        TradeOurDisplayItem myItem = myInventoryItems.get(dispItem.getDisplayName());
        List<TradeInternalItem> itemids = myItem.getItemList();

        for (TradeInternalItem itemid : itemids) {
            if (tradeRemoveItem(itemid)) {

                // Update our offer table if there is a successful removal.
                String name = dispItem.getDisplayName();
                if (myOfferedItems.containsKey(name)) {
                    myOfferedItems.get(name).incrementCount(-1);
                } else {
                    TradeDisplayItem it = new TradeDisplayItem(
                            dispItem.getClassid(), name);
                    it.incrementCount(-1);
                    myOfferedItems.put(name, it);
                }
                tradeWindow.updateTradeCount(true, myOfferedItems.values());

                return true;
            }
        }
        return false;
    }

    /**
     * Renders the name of an item for display in the trading window. This name
     * must be unique enough to differentiate the special items from the normal
     * ones. (For example, tell us if it's renamed, if it's gifted, if it has a
     * visible craft number, so on, as similarly named items will be grouped.)
     *
     * @param inventoryItem
     * @return
     */
    public String getItemName(TradeInternalItem inventoryItem) {
        String invName = inventoryItem.getDisplayName();
        
        logger.debug("Got display.");

        // Format item name for renamed items.
        if (inventoryItem.isRenamed()) {
            invName = String.format("%s (%s)",
                    inventoryItem.getDisplayName(), inventoryItem.getMarketName());
        }

        logger.debug("Checked if renamed.");
        
        // Format item name for gifted items.
        if (inventoryItem.wasGifted()) {
            invName = String.format("%s (gifted)", invName);
        }
        
        logger.debug("Check if gifted.");

        return invName;
    }

    public final boolean tradePutItem(TradeInternalItem item) {
        // Make sure the item isn't in the trade already.
        if (getSlotByItemID(item) == -1) {
            int slotToFill = getFirstFreeSlot();
            trade.getCmds().addItem(item, slotToFill);
            ourTradeSlotsFilled[slotToFill] = item;

            return true;
        }
        return false;
    }

    public final boolean tradeRemoveItem(TradeInternalItem item) {
        int slotToRemove;
        if ((slotToRemove = getSlotByItemID(item)) != -1) {
            trade.getCmds().removeItem(item);
            ourTradeSlotsFilled[slotToRemove] = null;

            return true;
        }
        return false;
    }

    public synchronized void loadInventory(AppContextPair appcontext) {
        /**
         * Author's note: We're lazy and never updating the count on this in the
         * duration of the trade. We'll just excuse it as saying it's the total
         * item count for the inventory, and when we add supprot for other
         * inventories, we have something to work with. This sets up our item
         * list that is pushed to the UI.
         */
        TradeInternalInventory inventory;

        // Clear displayed inventory items.
        myInventoryItems.clear();

        // If we don't have a copy of that inventory loaded yet, do that.
        if (!trade.getSelf().getInventories().hasInventory(appcontext)) {
            trade.loadOwnInventory(appcontext);
        }
        inventory = trade.getSelf().getInventories().getInventory(appcontext);

        // Take count of inventory items.
        for (final TradeInternalItem item : inventory.getItemList()) {
            String invName = getItemName(item);

            TradeOurDisplayItem displayItem;

            if (myInventoryItems.containsKey(invName)) {
                displayItem = myInventoryItems.remove(invName);
            } else {
                displayItem = new TradeOurDisplayItem(item.getClassid(), invName);
            }

            displayItem.incrementCount(1);
            displayItem.addItemToList(item);
            myInventoryItems.put(invName, displayItem);
        }

        tradeWindow.setOwnInventoryTable(myInventoryItems.values());
    }

    /**
     * Attempts to remove an item from the trade according to defindex.
     *
     * @param defIndex Number corresponding to item.
     * @return True if there was an item matching the defindex and was removed,
     * false if there was no item.
     */
    public final boolean tradeRemoveItemByDefIndex(int defIndex) {
        for (int i = 0; i < ourTradeSlotsFilled.length; i++) {
            if (ourTradeSlotsFilled[i] != null) {
                final TradeInternalItem item = ourTradeSlotsFilled[i];

                if (item.getDefIndex() == defIndex) {
                    trade.getCmds().removeItem(ourTradeSlotsFilled[i]);
                    ourTradeSlotsFilled[i] = null;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Finds the first open slot in a trade.
     *
     * @return The position of the first "empty" slot in the trade, -1 if there
     * are no empty slots.
     */
    public final int getFirstFreeSlot() {
        for (int i = 0; i < ourTradeSlotsFilled.length; i++) {
            if (ourTradeSlotsFilled[i] == null) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds an item currently in the trade based on the item's id.
     *
     * @param item Item to search for.
     * @return The item's position in the trade if it is in the trade, -1 if
     * not.
     */
    public final int getSlotByItemID(TradeInternalItem item) {
        for (int i = 0; i < ourTradeSlotsFilled.length; i++) {
            if (ourTradeSlotsFilled[i] == item) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onError(int eid, String message) {
        String estr;
        switch (eid) {
            case TradeStatusCodes.TRADE_CANCELLED:
                estr = "The trade has been canceled.";
                break;
            case TradeStatusCodes.STATUS_PARSE_ERROR:
                estr = "We have encountered an error.";
                break;
            case TradeStatusCodes.PARTNER_TIMED_OUT:
                estr = "Other user timed out.";
                break;
            case TradeStatusCodes.TRADE_FAILED:
                estr = "Trade failed.";
                break;
            default:
                estr = "Unknown error (eid:" + eid + ").";
        }

        JOptionPane.showMessageDialog(tradeWindow, estr);
    }

    @Override
    public void onWelcome() {
        //trade.sendMessage("[DMT] Hello!  This user is testing out an in-development third-party Steam client; I am not a bot.  Pardon any bugs.");
        //trade.sendMessage("[DMT] For more information, feel free to check out the group: http://steamcommunity.com/groups/dmt-client");
    }

    @Override
    public void onAfterInit() {
        /**
         * Author's note: We're lazy and never updating the count on this in the
         * duration of the trade. We'll just excuse it as saying it's the total
         * item count for the inventory, and when we add supprot for other
         * inventories, we have something to work with. This sets up our item
         * list that is pushed to the UI.
         */
        tradeWindow.loadInventorySet(trade.myAppContextData);
    }

    @Override
    public void onUserAddItem(TradeInternalItem inventoryItem) {
        logger.debug("Getting name.");
        String invName = getItemName(inventoryItem);

        if (otherOfferedItems.containsKey(invName)) {
            otherOfferedItems.get(invName).incrementCount(1);
        } else {
            TradeDisplayItem it = new TradeDisplayItem(
                    inventoryItem.getClassid(), invName);
            it.incrementCount(1);
            otherOfferedItems.put(invName, it);
        }
        tradeWindow.updateTradeCount(false, otherOfferedItems.values());
    }

    @Override
    public void onUserRemoveItem(TradeInternalItem inventoryItem) {
        String invName = getItemName(inventoryItem);

        TradeDisplayItem it = otherOfferedItems.remove(invName);
        it.incrementCount(-1);
        otherOfferedItems.put(invName, it);
        tradeWindow.updateTradeCount(false, otherOfferedItems.values());
    }

    @Override
    public void onMessage(String msg) {
        tradeWindow.addMessage(otherPlayerName, msg);
    }

    @Override
    public void onUserSetReadyState(boolean ready) {
        tradeWindow.otherOfferReadyCheckbox.setSelected(ready);

        if (trade.getSelf().isReady() && trade.getPartner().isReady()) {
            tradeWindow.completeTradeButton.setEnabled(true);
        }
    }

    @Override
    public void onUserAccept() {
    }

    @Override
    public void onNewVersion() {
    }

    @Override
    public void onTradeSuccess() {
        // Trade completed!
        // TODO Show a message dialog telling you again what you received?
        JOptionPane.showMessageDialog(tradeWindow, "Trade completed!");
    }

    @Override
    public void onTimer(int secondsSinceAction, int secondsSinceTrade) {
    }

    @Override
    public void onTradeClosed() {
        tradeWindow.dispose();
        client.onTradeClosed();
    }

    @Override
    public void onUnknownAction(TradeEvent event) {
        int action = event.action;

        logger.debug("Unknown action: {} -- {}", action, 
                event.getJSONObject());

        boolean isBot = !event.steamid.equals(String.valueOf(trade.getPartnerSteamId()));
        
        // TODO Complete implementation of currency.
        
        switch (event.action) {
            case TradeAction.CURRENCY_CHANGED:
                if (!isBot) {
                    /**
                     * If this is the other user and we don't have their
                     * inventory yet, then we will load it.
                     */
                    if (!trade.getPartner().getInventories().hasInventory(event.appid, event.contextid)) {
                        //trade.addForeignInventory(event.appid, event.contextid);
                    }

                    TradeInternalCurrency item = trade.getPartner().getInventories().getInventory(event.appid, event.contextid).getCurrency(event.assetid);

                    logger.debug("Name: {}", item.getDisplayName());
                    logger.debug("Market name: {}", item.getMarketName());

                    this.onUserAddItem(item);
                    logger.debug("Added item.");
                }
                break;
            default:
                break;
        }
        logger.debug("Added item.");
    }
}