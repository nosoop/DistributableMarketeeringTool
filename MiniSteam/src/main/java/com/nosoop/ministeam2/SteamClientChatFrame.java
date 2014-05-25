package com.nosoop.ministeam2;

import com.nosoop.ministeam2.SteamClientChatTab.TradeButtonState;
import com.nosoop.ministeam2.SteamClientMainForm.SteamKitClient;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.thomasc.steamkit.base.generated.steamlanguage.EChatEntryType;
import uk.co.thomasc.steamkit.steam3.handlers.steamfriends.callbacks.PersonaStateCallback;
import uk.co.thomasc.steamkit.steam3.handlers.steamtrading.callbacks.SessionStartCallback;
import uk.co.thomasc.steamkit.steam3.handlers.steamtrading.callbacks.TradeProposedCallback;
import uk.co.thomasc.steamkit.types.steamid.SteamID;

/**
 * A window holding Steam chat tabs, passing chat information to the appropriate
 * tab.
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class SteamClientChatFrame extends javax.swing.JFrame {
    /**
     * The client instance this window is attached to.
     */
    private SteamKitClient client;
    /**
     * A map containing all the users we are currently chatting with.
     */
    private Map<SteamID, SteamClientChatTab> currentUsers;
    /**
     * A logging instance.
     */
    Logger logger = LoggerFactory.getLogger(
            SteamClientChatFrame.class.getSimpleName());

    /**
     * Creates new form SteamClientChatFrame
     */
    public SteamClientChatFrame(SteamKitClient client) {
        this.client = client;
        this.currentUsers = new HashMap<>();
        initComponents();
    }

    void addNewChatTab(SteamID user) {
        if (currentUsers.containsKey(user))
            return;
        
        String tabName = client.steamFriends.getFriendPersonaName(user);
        SteamClientChatTab tab = new SteamClientChatTab(this, user);

        chatTabbedPane.addTab(tabName, tab);
        currentUsers.put(user, tab);

        UserInfo info = new UserInfo();
        info.username = tabName;
        info.status = client.steamFriends.getFriendPersonaState(user).
                toString();
        tab.updateUserInfo(info);
    }

    /**
     * Pass the message to the specific tab, creating a new one if the user does
     * not have their own.
     *
     * @param sender
     * @param entryType
     * @param message
     */
    void onReceivedChatMessage(SteamID sender,
            EChatEntryType entryType, String message) {
        SteamClientChatTab tabHandlingMessage;

        logger.debug("Message received. Searching for tab.");

        if (!currentUsers.containsKey(sender)) {
            addNewChatTab(sender);
            logger.debug("Tab added.");
        }

        tabHandlingMessage = currentUsers.get(sender);
        tabHandlingMessage.receiveMessage(entryType, message);

        logger.debug("Message fired at tab.");

        if (entryType == EChatEntryType.ChatMsg) {
            this.setVisible(true);
        }
    }

    // TODO Maybe make this an event listener?
    void onSendingMessage(SteamID target, EChatEntryType entryType,
            String message) {
        client.steamFriends.sendChatMessage(target, entryType, message);
    }

    /**
     * Relay an updated persona state to the chat tab that needs it, if
     * applicable.
     *
     * @param callback
     */
    void onPersonaState(PersonaStateCallback callback) {
        if (currentUsers.containsKey(callback.getFriendID())) {
            UserInfo info = new UserInfo();
            info.username = callback.getName();
            info.status = callback.getState().name();

            SteamClientChatTab updateTab =
                    currentUsers.get(callback.getFriendID());

            updateTab.updateUserInfo(info);

            int tabNumber = chatTabbedPane.indexOfComponent(updateTab);

            // TODO Better way to rename tab than by removing and reinserting?
            chatTabbedPane.removeTabAt(tabNumber);
            chatTabbedPane.insertTab(info.username, null, updateTab, null,
                    tabNumber);
        }
    }
    
    void onTradeProposal(TradeProposedCallback callback) {
        SteamID proposer = callback.getOtherClient();
        addNewChatTab(proposer);
        
        SteamClientChatTab tabToUpdate = currentUsers.get(proposer);
        tabToUpdate.updateTradeButton(
                SteamClientChatTab.TradeButtonState.RECEIVED_REQUEST, 
                callback.getTradeID());
    }
    
    void onSessionStart(SessionStartCallback callback) {
        for (Map.Entry<SteamID,SteamClientChatTab> entry : 
                currentUsers.entrySet()) {
            TradeButtonState buttonState = 
                    entry.getKey().equals(callback.getOtherClient()) ?
                    TradeButtonState.IN_TRADE : 
                    TradeButtonState.DISABLED_WHILE_IN_TRADE;
            entry.getValue().updateTradeButton(buttonState, 0);
        }
    }
    
    void onTradeClosed() {
        // TODO behavior when another trade is being accepted.
        for (Map.Entry<SteamID, SteamClientChatTab> entry :
                currentUsers.entrySet()) {
            entry.getValue().updateTradeButton(TradeButtonState.IDLE, 0);
        }
    }
    
    void sendTradeRequest(SteamID target) {
        client.steamTrade.trade(target);
    }
    
    void acceptTradeRequest(int tradeid) {
        client.steamTrade.respondToTrade(tradeid, true);
    }
    
    void cancelTradeRequest(SteamID target) {
        client.steamTrade.cancelTrade(target);
    }
    
    String getOwnPersonaName() {
        return client.steamFriends.getPersonaName();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chatTabbedPane = new javax.swing.JTabbedPane();

        chatTabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chatTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chatTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane chatTabbedPane;
    // End of variables declaration//GEN-END:variables
    /**
     * A struct containing data to update the chat window with.
     */
    public static class UserInfo {
        String username, status;
    }

}
