package com.nosoop.ministeam2;

import com.nosoop.ministeam2.SteamClientChatTab.TradeButtonState;
import com.nosoop.ministeam2.SteamClientMainForm.SteamFriendEntry;
import com.nosoop.ministeam2.SteamClientMainForm.SteamKitClient;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.thomasc.steamkit.base.generated.steamlanguage.EChatEntryType;
import uk.co.thomasc.steamkit.steam3.handlers.steamtrading.callbacks.*;
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
     * Refers to trade commands.
     */
    TradeRequestActions tradeRequest;

    /**
     * Creates new form SteamClientChatFrame
     */
    public SteamClientChatFrame(SteamKitClient client) {
        this.client = client;
        this.currentUsers = new HashMap<>();
        initComponents();

        tradeRequest = new TradeRequestActions();
    }

    /**
     * Adds a new SteamClientChatTab instance corresponding to the given SteamID
     * to this instance if needed.
     */
    void addNewChatTab(SteamID user) {
        if (currentUsers.containsKey(user)) {
            return;
        }

        String tabName = client.steamFriends.getFriendPersonaName(user);
        SteamClientChatTab tab = new SteamClientChatTab(this, user,
                client.getUserStatus(user));

        chatTabbedPane.addTab(tabName, tab);
        currentUsers.put(user, tab);
    }

    /**
     * Switches to a tab specified by SteamID.
     */
    void switchToChatTab(SteamID user) {
        for (Map.Entry<SteamID, SteamClientChatTab> entry
                : currentUsers.entrySet()) {
            if (entry.getKey().equals(user)) {
                chatTabbedPane.setSelectedComponent(entry.getValue());
                return;
            }
        }
    }

    /**
     * Pass the message to the specific tab, creating a new one if the user does
     * not have their own.
     */
    void onReceivedChatMessage(SteamID sender,
            EChatEntryType entryType, String message) {
        SteamClientChatTab tabHandlingMessage;

        logger.debug("Message received. Searching for tab.");

        if (!currentUsers.containsKey(sender)) {
            if (entryType != EChatEntryType.ChatMsg) {
                // and not anything else then drop
                return;
            }
            addNewChatTab(sender);
            logger.debug("Tab added.");
        }

        tabHandlingMessage = currentUsers.get(sender);
        tabHandlingMessage.receiveMessage(entryType, message);

        logger.debug("Message of type {} fired at tab.", entryType);

        if (entryType == EChatEntryType.ChatMsg) {
            this.setVisible(true);
        }
    }

    /**
     * Passes a message from a SteamClientChatTab instance to the attached
     * SteamKitClient.
     *
     * @param target The recipient of the chat message.
     * @param entryType The type of message to send.
     * @param message The textual representation of the message, if any.
     */
    void sendMessage(SteamID target, EChatEntryType entryType,
            String message) {
        client.steamFriends.sendChatMessage(target, entryType, message);
    }

    /**
     * Receives an updated friend status, passing it to a tab if needed.
     */
    void onUpdatedFriendStatus(SteamFriendEntry friendStatus) {
        if (currentUsers.containsKey(friendStatus.steamid)) {
            currentUsers.get(friendStatus.steamid)
                    .receiveUserStatus(friendStatus);
        }
    }

    /**
     * Relay a received trade request to the applicable chat tab, as long as we
     * aren't in a trade already.
     */
    void onTradeProposal(TradeProposedCallback callback) {
        // TODO Don't silently drop trade requests?
        if (!client.tradePoller.isInTrade()) {
            SteamID proposer = callback.getOtherClient();
            addNewChatTab(proposer);

            SteamClientChatTab tabToUpdate = currentUsers.get(proposer);

            tabToUpdate.updateTradeButton(
                    SteamClientChatTab.TradeButtonState.RECEIVED_REQUEST,
                    callback.getTradeID());

            this.setVisible(true);
        }
    }

    /**
     * Notifies all the chat tabs to disable their buttons after receiving a
     * trade session start callback, updating a specific tab if we are in a
     * trade with them specifically.
     */
    void onSessionStart(SessionStartCallback callback) {
        for (Map.Entry<SteamID, SteamClientChatTab> entry
                : currentUsers.entrySet()) {
            TradeButtonState buttonState =
                    entry.getKey().equals(callback.getOtherClient())
                    ? TradeButtonState.IN_TRADE
                    : TradeButtonState.DISABLED_WHILE_IN_TRADE;
            entry.getValue().updateTradeButton(buttonState, 0);
        }
    }

    /**
     * Notifies all the chat tabs to re-enable their trade buttons, as well as
     * adding a message to an existing chat tab specific to the trade being
     * closed.
     */
    void onTradeClosed(SteamID partner) {
        for (Map.Entry<SteamID, SteamClientChatTab> entry
                : currentUsers.entrySet()) {
            entry.getValue().updateTradeButton(TradeButtonState.IDLE, 0);

            if (entry.getKey().equals(partner)) {
                entry.getValue().closeTrade();
            }
        }
    }

    /**
     * Returns the name of our client.
     */
    String getOwnPersonaName() {
        return client.steamFriends.getPersonaName();
    }

    /**
     * Returns the username given in sign-in information stored by the client,
     * used for chat logging.
     */
    String getOwnUsername() {
        return client.clientInfo.username;
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

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
        });

        chatTabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        chatTabbedPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                chatTabbedPaneMouseReleased(evt);
            }
        });
        chatTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chatTabbedPaneStateChanged(evt);
            }
        });

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

    /**
     * Sets the title to the current chatting user on tab panel state change.
     */
    private void chatTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chatTabbedPaneStateChanged
        // TODO Custom formatting for title
        int tabIndex = chatTabbedPane.getSelectedIndex();
        if (tabIndex != -1) {
            // Set title by tab title.
            setTitle(chatTabbedPane.getTitleAt(tabIndex));

            // Pass the focus event to the tab so it puts the text box in focus.
            ((SteamClientChatTab) chatTabbedPane.getSelectedComponent())
                    .receiveFocus();
        }
    }//GEN-LAST:event_chatTabbedPaneStateChanged

    /**
     * Removes the tab. Performs cleanup by closing open logs, removing the chat
     * from the tab pane, removing the SteamID associated with the chat from the
     * list of users with tabs open, and attempts to force cleanup by setting
     * the tab reference to null.
     */
    private void chatTabbedPaneMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chatTabbedPaneMouseReleased
        SteamClientChatTab tab = (SteamClientChatTab) chatTabbedPane
                .getSelectedComponent();

        if (tab != null && evt.getButton() == MouseEvent.BUTTON2) {
            tab.cleanup();
            chatTabbedPane.remove(tab);
            currentUsers.remove(tab.chatter);

            tab = null;
            assert (tab == null);
        }

        setVisible(chatTabbedPane.getTabCount() != 0);
    }//GEN-LAST:event_chatTabbedPaneMouseReleased

    /**
     * Possibly close all the chat tabs when the window is hidden.
     */
    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        // TODO Create preference to hide window when close button is pressed.
        if (1 == 0) {
            return;
        }

        for (Component component : chatTabbedPane.getComponents()) {
            if (component instanceof SteamClientChatTab) {
                SteamClientChatTab tab = (SteamClientChatTab) component;
                tab.cleanup();
                chatTabbedPane.remove(tab);
                currentUsers.remove(tab.chatter);

                tab = null;
                assert (tab == null);
            }
        }
    }//GEN-LAST:event_formComponentHidden
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane chatTabbedPane;
    // End of variables declaration//GEN-END:variables

    /**
     * Wrapper class that handles passing trades between the SteamKitClient and
     * individual chat tab instances.
     */
    class TradeRequestActions {
        int TRADEID_INVALID = 0;

        /**
         * Sends a trade
         *
         * @param target
         */
        void send(SteamID target) {
            client.steamTrade.trade(target);
        }

        /**
         * Accepts a trade if not an invalid trade.
         *
         * @param tradeid
         */
        void accept(int tradeid) {
            if (tradeid == TRADEID_INVALID) {
                return;
            }
            client.steamTrade.respondToTrade(tradeid, true);
        }

        /**
         * Cancels a trade proposal.
         *
         * @param target
         */
        void cancel(SteamID target) {
            client.steamTrade.cancelTrade(target);
        }
    }

}
