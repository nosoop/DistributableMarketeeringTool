package com.nosoop.ministeam2;

import com.nosoop.ministeam2.util.LocalizationResources;
import bundled.steamtrade.org.json.JSONException;
import bundled.steamtrade.org.json.JSONObject;
import com.nosoop.inputdialog.CallbackInputFrame.DialogCallback;
import com.nosoop.steamtrade.*;
import com.nosoop.steamtrade.inventory.AssetBuilder;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.event.MouseEvent;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.*;
import java.security.spec.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.GZIPInputStream;
import javax.crypto.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sourceforge.iharder.base64.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static uk.co.thomasc.steamkit.base.generated.steamlanguage.EEconTradeResponse.*;
import uk.co.thomasc.steamkit.base.generated.steamlanguage.*;
import uk.co.thomasc.steamkit.steam3.handlers.steamfriends.SteamFriends;
import uk.co.thomasc.steamkit.steam3.handlers.steamfriends.callbacks.*;
import uk.co.thomasc.steamkit.steam3.handlers.steamfriends.types.Friend;
import uk.co.thomasc.steamkit.steam3.handlers.steamgamecoordinator.SteamGameCoordinator;
import uk.co.thomasc.steamkit.steam3.handlers.steamtrading.SteamTrading;
import uk.co.thomasc.steamkit.steam3.handlers.steamtrading.callbacks.*;
import uk.co.thomasc.steamkit.steam3.handlers.steamuser.SteamUser;
import uk.co.thomasc.steamkit.steam3.handlers.steamuser.callbacks.*;
import uk.co.thomasc.steamkit.steam3.handlers.steamuser.types.*;
import uk.co.thomasc.steamkit.steam3.steamclient.SteamClient;
import uk.co.thomasc.steamkit.steam3.steamclient.callbackmgr.*;
import uk.co.thomasc.steamkit.steam3.steamclient.callbacks.*;
import uk.co.thomasc.steamkit.steam3.webapi.WebAPI;
import uk.co.thomasc.steamkit.types.keyvalue.KeyValue;
import uk.co.thomasc.steamkit.types.steamid.SteamID;
import uk.co.thomasc.steamkit.util.KeyDictionary;
import uk.co.thomasc.steamkit.util.WebHelpers;
import uk.co.thomasc.steamkit.util.cSharp.events.ActionT;
import uk.co.thomasc.steamkit.util.crypto.CryptoHelper;
import uk.co.thomasc.steamkit.util.crypto.RSACrypto;

/**
 * The main window for the Distributable Marketeering Tool.
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class SteamClientMainForm extends javax.swing.JFrame {
    /**
     * Class for Steam connectivity.
     */
    SteamKitClient backend;
    /**
     * Child dialog for signing in to Steam.
     */
    SteamClientLoginDialog loginDialog;
    /**
     * Child dialog for sending and receiving Steam user messages.
     */
    SteamClientChatFrame chatFrame;
    /**
     * Holding spot for friend table entries.
     */
    Map<SteamID, SteamFriendEntry> friendList;
    Object[][] dataTable;

    /**
     * Creates new form SteamClientMainForm
     */
    public SteamClientMainForm() {
        /**
         * Initialize the sign-in form; data is passed to it while the client is
         * running.
         */
        loginDialog = new SteamClientLoginDialog(
                new DialogCallback<SteamClientInfo>() {
            @Override
            public void run(SteamClientInfo returnValue) {
                SteamClientMainForm.this.backend.login(returnValue);
            }
        });
        loginDialog.setVisible(true);

        /**
         * Initialize the Steam client.
         */
        backend = new SteamKitClient();

        /**
         * Show components.
         */
        initComponents();

        friendList = new HashMap<>();

        chatFrame = new SteamClientChatFrame(backend);
    }

    /**
     * Updates the status of a user, updating it through the visible friend list
     * and by passing the information to the chat window.
     *
     * @param userid
     */
    synchronized void updateFriendStatus(final SteamID userid) {
        if (userid.isIndividualAccount()) {
            if (friendList.containsKey(userid)) {
                friendList.remove(userid);
            }

            // Update applicable tab first in case of a removal.
            SteamFriendEntry newEntry = backend.getUserStatus(userid);
            chatFrame.onUpdatedFriendStatus(newEntry);

            // Only put them in the friends list if you have a relationship.
            if (backend.steamFriends.getFriendRelationship(userid)
                    != EFriendRelationship.None) {
                friendList.put(userid, newEntry);
            }

            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // Update friend table.
                    updateFriendTable();
                }
            });
        }
    }

    /**
     * Clears and rebuilds the viewable friend list.
     */
    private synchronized void updateFriendTable() {
        DefaultTableModel friendTable =
                ((DefaultTableModel) tableUsers.getModel());

        // Save the row to restore after clearing and filling.the table.
        int storedPosition = tableUsers.getSelectedRow();

        // Fastest way to clear the table.
        friendTable.setNumRows(0);

        // TODO a way to update the table more efficently?
        for (Map.Entry<SteamID, SteamFriendEntry> keyValues : friendList.entrySet()) {
            SteamFriendEntry entry = keyValues.getValue();

            // Display username in first cell, status in second.
            friendTable.addRow(new Object[]{entry, entry.renderUserStatus()});
        }

        // Restore row if there is a selection (row != -1)
        if (storedPosition > 0) {
            tableUsers.setRowSelectionInterval(storedPosition, storedPosition);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        friendPopupMenu = new javax.swing.JPopupMenu();
        friendChatOption = new javax.swing.JMenuItem();
        friendTradeOption = new javax.swing.JMenuItem();
        labelPlayerName = new javax.swing.JLabel();
        comboboxUserStatus = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableUsers = new javax.swing.JTable();

        friendChatOption.setText("Open chat window");
        friendPopupMenu.add(friendChatOption);

        friendTradeOption.setText("Invite to trade");
        friendPopupMenu.add(friendTradeOption);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/nosoop/ministeam2/UIStrings"); // NOI18N
        labelPlayerName.setText(bundle.getString("Steam.UndefinedName")); // NOI18N

        comboboxUserStatus.setModel(new javax.swing.DefaultComboBoxModel(EPersonaState.values()));
        comboboxUserStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboboxUserStatusActionPerformed(evt);
            }
        });

        tableUsers.setAutoCreateRowSorter(true);
        tableUsers.setModel(new javax.swing.table.DefaultTableModel(
            dataTable,
            new String [] {
                "Name", "Status"
            }
        ) {
            Class[] types = new Class [] {
                SteamFriendEntry.class
                , java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        tableUsers.setFillsViewportHeight(true);
        tableUsers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tableUsers.setShowHorizontalLines(false);
        tableUsers.setShowVerticalLines(false);
        tableUsers.getTableHeader().setReorderingAllowed(false);
        tableUsers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tableUsersMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableUsersMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableUsers);
        tableUsers.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelPlayerName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(comboboxUserStatus, 0, 251, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelPlayerName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboboxUserStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void comboboxUserStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboboxUserStatusActionPerformed
        backend.steamFriends.setPersonaState(
                (EPersonaState) comboboxUserStatus.getSelectedItem());
    }//GEN-LAST:event_comboboxUserStatusActionPerformed

    private void tableUsersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableUsersMouseClicked
        int targetRow = tableUsers.getSelectedRow();

        if (evt.getClickCount() == 2 && targetRow >= 0
                && evt.getButton() == MouseEvent.BUTTON1) {
            targetRow = tableUsers.convertRowIndexToModel(targetRow);
            SteamFriendEntry user = (SteamFriendEntry) tableUsers.getModel().getValueAt(targetRow, 0);

            chatFrame.addNewChatTab(user.steamid);
            chatFrame.switchToChatTab(user.steamid);

            chatFrame.setVisible(true);
        }
    }//GEN-LAST:event_tableUsersMouseClicked

    private void tableUsersMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableUsersMouseReleased
        int selectedRow = tableUsers.rowAtPoint(evt.getPoint());

        // Show exactly which row we're acting on.
        if (selectedRow >= 0 && selectedRow < tableUsers.getRowCount()) {
            tableUsers.setRowSelectionInterval(selectedRow, selectedRow);
        } else {
            tableUsers.clearSelection();
        }

        selectedRow = tableUsers.getSelectedRow();

        if (evt.getButton() == MouseEvent.BUTTON3 && selectedRow >= 0) {
            selectedRow = tableUsers.convertRowIndexToModel(selectedRow);
            SteamFriendEntry user = (SteamFriendEntry) tableUsers.getModel().getValueAt(selectedRow, 0);

            // Show menu depending on friend relationship
            switch (user.relationship) {
                case Friend:
                    friendPopupMenu.show(tableUsers, evt.getX(), evt.getY());
                    break;
            }
        }
    }//GEN-LAST:event_tableUsersMouseReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox comboboxUserStatus;
    private javax.swing.JMenuItem friendChatOption;
    private javax.swing.JPopupMenu friendPopupMenu;
    private javax.swing.JMenuItem friendTradeOption;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelPlayerName;
    private javax.swing.JTable tableUsers;
    // End of variables declaration//GEN-END:variables

    /**
     * Inner class that handles Steam client connectivity.
     */
    public class SteamKitClient {
        public SteamFriends steamFriends;
        public SteamClient steamClient;
        public SteamTrading steamTrade;
        public SteamGameCoordinator steamGC;
        public SteamUser steamUser;
        final ScheduledExecutorService tradeExec, clientExec;
        CallbackMgr callbackManager;
        TradePoller tradePoller;
        Logger logger = LoggerFactory.getLogger(
                SteamClientMainForm.class.getSimpleName());
        String sessionId, token;
        SteamClientInfo clientInfo;
        boolean loginOnConnectedCallback;

        public SteamKitClient() {
            steamClient = new SteamClient();
            steamTrade = steamClient.getHandler(SteamTrading.class);
            steamUser = steamClient.getHandler(SteamUser.class);
            steamFriends = steamClient.getHandler(SteamFriends.class);
            steamGC = steamClient.getHandler(SteamGameCoordinator.class);

            tradeExec = Executors.newSingleThreadScheduledExecutor();
            clientExec = Executors.newSingleThreadScheduledExecutor();

            callbackManager = new CallbackMgr();

            logger.info("Connecting to the Steam network...");
            steamClient.connect();

            // Schedule trade poller to poll every second when in a trade.
            tradePoller = new TradePoller();
            tradeExec.scheduleAtFixedRate(tradePoller, 0, 1, TimeUnit.SECONDS);

            // Schedule callback fetching to occur every 100 ms.
            clientExec.scheduleWithFixedDelay(
                    new CallbackGetter(), 0, 100, TimeUnit.MILLISECONDS);

            loginOnConnectedCallback = false;
        }

        /**
         * Class that groups all the callback handlers together.
         */
        private class CallbackMgr {
            /**
             * A collection of handlers for Steam callback messages.
             *
             * @param msg A callback message to be handled.
             */
            void handleSteamMessage(CallbackMsg msg) {
                // Print to output if not a common callback.
                if (!(msg instanceof PersonaStateCallback)
                        && !(msg instanceof SessionTokenCallback)) {
                    logger.info("Received callback {}.",
                            msg.getClass().getSimpleName());
                }

                /**
                 * Basic Steam connectivity.
                 */
                //<editor-fold defaultstate="collapsed" desc="ConnectedCallback">
                msg.handle(ConnectedCallback.class, new ActionT<ConnectedCallback>() {
                    // Connected to Steam.  It acknowledges your presence.
                    @Override
                    public void call(ConnectedCallback callback) {
                        if (callback.getResult() != EResult.OK) {
                            logger.info("Unable to connect to Steam: {}",
                                    callback.getResult().getClass().getName());

                            clientExec.schedule(new Runnable() {
                                @Override
                                public void run() {
                                    logger.info("Retrying connection.");
                                    steamClient.connect();
                                }
                            }, 30, TimeUnit.SECONDS);
                        } else if (loginOnConnectedCallback) {
                            // Sign in client if we disconnected after login.
                            // (See: LoggedOnCallback)
                            login(clientInfo);
                        } else {
                            loginDialog.setSteamConnectionState(
                                    SteamClientLoginDialog.ClientConnectivityState.CONNECTED);
                        }
                    }
                });
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="DisconnectedCallback">
                msg.handle(DisconnectedCallback.class, new ActionT<DisconnectedCallback>() {
                    // Disconnected form Steam.
                    @Override
                    public void call(DisconnectedCallback callback) {
                        logger.error("Disconnected from Steam.  Retrying in one.");

                        loginDialog.setSteamConnectionState(
                                SteamClientLoginDialog.ClientConnectivityState.DISCONNECTED);

                        /**
                         * Try to reconnect after a second.
                         */
                        clientExec.schedule(new Runnable() {
                            @Override
                            public void run() {
                                logger.info("Retrying connection.");
                                steamClient.connect();
                            }
                        }, 1, TimeUnit.SECONDS);
                    }
                });
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="LoggedOnCallback">
                msg.handle(LoggedOnCallback.class, new ActionT<LoggedOnCallback>() {
                    // Logged on to Steam, or attempted to login and SteamGuard-blocked.
                    @Override
                    public void call(LoggedOnCallback callback) {
                        logger.debug("Logon enum value: {}", callback.getResult().name());

                        if (callback.getResult() == EResult.AccountLogonDenied) {
                            String dialogMessage = String.format(
                                    "Account is SteamGuard protected.\n"
                                    + "Enter the authentication code sent to"
                                    + " the address at %s.",
                                    callback.getEmailDomain());

                            clientInfo.authcode = JOptionPane.showInputDialog(
                                    null, dialogMessage, "SteamGuard",
                                    JOptionPane.INFORMATION_MESSAGE);
                            logger.info("Disconnecting and submitting "
                                    + "authentication code {}.",
                                    clientInfo.authcode);

                            /**
                             * Force a disconnect because it won't try logging
                             * in on its own, then set a flag to communicate
                             * that we need to log in again once we receive the
                             * ConnectedCallback.
                             */
                            steamClient.disconnect();
                            loginOnConnectedCallback = true;
                            loginDialog.setSteamConnectionState(
                                    SteamClientLoginDialog.ClientConnectivityState.SIGNING_IN);
                            return;
                        }

                        if (callback.getResult() == EResult.InvalidPassword
                                || callback.getResult() == EResult.PasswordNotSet) {
                            // Notified that user info is incorrect.
                            // TODO Figure out how to handle an invalid password.
                            loginDialog.setSteamConnectionState(
                                    SteamClientLoginDialog.ClientConnectivityState.INCORRECT_LOGIN);
                            loginOnConnectedCallback = false;
                            steamClient.disconnect();
                        }

                        if (callback.getResult() == EResult.OK) {
                            logger.info("Successfully signed in to Steam!");

                            loginDialog.setSteamConnectionState(
                                    SteamClientLoginDialog.ClientConnectivityState.SIGNED_IN);

                            // Spawn a thread for checking AFK.  Might want to manage.
                            Runnable inactiveChecker =
                                    new FrontendInactivityChecker();
                            clientExec.scheduleAtFixedRate(inactiveChecker, 0,
                                    1, TimeUnit.SECONDS);

                            SteamClientMainForm.this.setVisible(true);
                        }
                    }
                });
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="LoggedOffCallback">
                msg.handle(LoggedOffCallback.class, new ActionT<LoggedOffCallback>() {
                    // Told to log off by Steam for some reason or other.
                    @Override
                    public void call(LoggedOffCallback callback) {
                        logger.info("Logged off of Steam with result {}.",
                                callback.getResult());
                        // LogonSessionReplaced:  Another instance of Steam is using the account.
                    }
                });
                //</editor-fold>

                /**
                 * Account info and state.
                 */
                //<editor-fold defaultstate="collapsed" desc="AccountInfoCallback">
                msg.handle(AccountInfoCallback.class, new ActionT<AccountInfoCallback>() {
                    // Oh hey, you can do stuff with your account now.
                    @Override
                    public void call(AccountInfoCallback obj) {
                        steamFriends.setPersonaState(EPersonaState.Online);
                    }
                });
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="PersonaStateCallback">
                msg.handle(PersonaStateCallback.class, new ActionT<PersonaStateCallback>() {
                    @Override
                    public void call(PersonaStateCallback callback) {
                        SteamClientMainForm form = SteamClientMainForm.this;

                        if (callback.getFriendID().convertToLong()
                                == steamUser.getSteamId().convertToLong()) {
                            form.labelPlayerName.setText(callback.getName());
                            if (!form.comboboxUserStatus.getSelectedItem().equals(callback.getState())) {
                                form.comboboxUserStatus.setSelectedItem(callback.getState());
                            }
                        } else {
                            form.updateFriendStatus(callback.getFriendID());
                        }
                    }
                });
                //</editor-fold>

                /**
                 * Steam friends and messaging service.
                 */
                //<editor-fold defaultstate="collapsed" desc="FriendsListCallback">
                msg.handle(FriendsListCallback.class, new ActionT<FriendsListCallback>() {
                    // Updated friends list.  Client has either added or removed a player?
                    @Override
                    public void call(FriendsListCallback callback) {
                        for (Friend friend : callback.getFriendList()) {
                            SteamClientMainForm.this.updateFriendStatus(
                                    friend.getSteamId());
                        }
                    }
                });
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="FriendMsgCallback">
                msg.handle(FriendMsgCallback.class, new ActionT<FriendMsgCallback>() {
                    // Another person is typing or sent a message.
                    @Override
                    public void call(FriendMsgCallback callback) {
                        //mainWindow.receiveChatMessage(callback);
                        SteamClientMainForm.this.chatFrame.onReceivedChatMessage(
                                callback.getSender(), callback.getEntryType(),
                                callback.getMessage());
                    }
                });
                //</editor-fold>

                /**
                 * Steam web services.
                 */
                //<editor-fold defaultstate="collapsed" desc="LoginKeyCallback">
                msg.handle(LoginKeyCallback.class, new ActionT<LoginKeyCallback>() {
                    @Override
                    public void call(LoginKeyCallback callback) {
                        /**
                         * TODO Implement support for storing the authorized
                         * login data.
                         */
                        // Hopefully we only need to do this once.
                        sessionId = Base64.encodeBytes(
                                String.valueOf(callback.getUniqueId()).
                                getBytes());

                        // Attempt to authenticate by API.
                        {
                            SteamLoginAuth auth = authenticate(callback);

                            if (auth.success) {
                                token = auth.token;
                                return;
                            }
                        }
                        logger.info("API sign-in failed. Using SteamWeb.");

                        // I guess we'll just have to do this manually.
                        Map<String, String> cookies = new HashMap<>();
                        cookies.put("sessionid", sessionId);

                        if (!clientInfo.machineauthcookie.equals("")) {
                            cookies.put("steamMachineAuth"
                                    + steamUser.getSteamId().convertToLong(),
                                    clientInfo.machineauthcookie);
                        }
                        try {
                            SteamLoginAuth auth =
                                    SteamWebLogin.login(clientInfo.username,
                                    clientInfo.password, cookies);
                            logger.info("SteamWeb login authenticated.");

                            if (auth.success) {
                                token = auth.token;
                            }
                        } catch (IOException | NoSuchAlgorithmException |
                                InvalidKeySpecException |
                                NoSuchPaddingException | InvalidKeyException |
                                IllegalBlockSizeException |
                                BadPaddingException | JSONException e) {
                            logger.error("SteamWeb Login Failre", e);
                        }

                        // Once authenticated, you can set other things.
                    }
                });
                //</editor-fold>

                /**
                 * Handlers for trade events.
                 */
                //<editor-fold defaultstate="collapsed" desc="TradeProposedCallback">
                msg.handle(TradeProposedCallback.class, new ActionT<TradeProposedCallback>() {
                    // A trade session was requested by another client.
                    @Override
                    public void call(TradeProposedCallback callback) {
                        logger.info("Trade request received from [{}].",
                                callback.getOtherClient().render());

                        if (readyToTrade() && !tradePoller.isInTrade()) {
                            chatFrame.onTradeProposal(callback);
                        }
                    }
                });
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="SessionStartCallback">
                msg.handle(SessionStartCallback.class, new ActionT<SessionStartCallback>() {
                    // A trade session was initialized.
                    @Override
                    public void call(SessionStartCallback callback) {

                        logger.debug("Opening up trade window.");

                        SteamClientTradeWindow sct =
                                new SteamClientTradeWindow(SteamKitClient.this);

                        TradeListener listener = sct.getTradeListener();

                        logger.debug("Trade window created.");

                        try {
                            List<AssetBuilder> assetBuilders = new ArrayList<>();
                            tradePoller.setCurrentTradeSession(
                                    new TradeSession(
                                    steamUser.getSteamId().convertToLong(),
                                    callback.getOtherClient().convertToLong(),
                                    sessionId, token, listener, assetBuilders));
                        } catch (final Exception e) {
                            // Error during construction.
                            logger.error("Error during trade init.", e);

                            steamFriends.sendChatMessage(
                                    callback.getOtherClient(),
                                    EChatEntryType.ChatMsg,
                                    "Whoops!  Something went wrong.");
                            steamTrade.cancelTrade(callback.getOtherClient());

                            tradePoller.forceCancelTradeSession();
                        }

                        logger.debug("Trade polling attached.");
                        chatFrame.onSessionStart(callback);
                    }
                });
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="TradeResultCallback">
                msg.handle(TradeResultCallback.class, new ActionT<TradeResultCallback>() {
                    // We have sent a trade request; the other client responded.
                    @Override
                    public void call(TradeResultCallback callback) {
                        switch (callback.getResponse()) {
                            case Accepted:
                                // The other client has accepted the trade.
                                break;
                            case TargetAlreadyTrading:
                                // The other client is already in a trade.
                                break;
                            case Declined:
                                // The other user declined the trade.
                                // (Clicked on the 'close' button.)
                                break;
                            case Cancel:
                                // The user cancelled their trade request.
                                break;
                            case Timeout:
                                break;
                            default:
                                // We assume the request timed out.
                                break;
                        }
                    }
                });
                //</editor-fold>

                /**
                 * Handler for money stuff?
                 */
                //<editor-fold defaultstate="collapsed" desc="WalletInfoCallback">
                msg.handle(WalletInfoCallback.class, new ActionT<WalletInfoCallback>() {
                    // TODO Wallet balance?
                    @Override
                    public void call(WalletInfoCallback callback) {
                        if (callback.isHasWallet()) {
                            logger.info("Wallet balance: {} {}",
                                    callback.getBalance(), callback.getCurrency().name());
                        }
                    }
                });
                //</editor-fold>

                /**
                 * The job handler. Passes the callback into
                 * handleSteamJobMessage(...).
                 */
                //<editor-fold defaultstate="collapsed" desc="JobCallback">
                msg.handle(JobCallback.class, new ActionT<JobCallback>() {
                    // You have a job to do~!
                    // Add a handler in handleSteamJobMessage(...) to handle it.
                    @Override
                    public void call(JobCallback job) {
                        CallbackMsg jobCallback = job.getCallback();
                        final long JOBID = job.getJobId().getValue();

                        handleSteamJobMessage(jobCallback, JOBID);
                    }
                });
                //</editor-fold>
            }

            /**
             * A collection of handlers for Steam job callback messages.
             *
             * @param msg The job callback message to be handled.
             * @param jobID The id number of the job. Used for auth.
             */
            void handleSteamJobMessage(CallbackMsg msg, final long jobID) {
                logger.info("Received callback {} (JobCallback)",
                        msg.getClass().getSimpleName());

                // Go update your SteamGuard file.  Or make a new one.
                //<editor-fold defaultstate="collapsed" desc="UpdateMachineAuthCallback">
                msg.handle(UpdateMachineAuthCallback.class,
                        new ActionT<UpdateMachineAuthCallback>() {
                    @Override
                    public void call(UpdateMachineAuthCallback callback) {
                        logger.info("Creating authentication file...");
                        byte[] sentryHash = CryptoHelper.SHAHash(callback.getData());

                        try (BufferedOutputStream fo = new BufferedOutputStream(
                                new FileOutputStream(clientInfo.sentryFile))) {

                            fo.write(callback.getData());
                            fo.flush();
                            logger.info("File successfully written at {}",
                                    clientInfo.sentryFile.getAbsolutePath());
                        } catch (FileNotFoundException ex) {
                        } catch (IOException ex) {
                        }

                        // Respond by saying you did what you were told.
                        MachineAuthDetails machineAuth = new MachineAuthDetails();
                        {
                            machineAuth.jobId = jobID;

                            machineAuth.fileName = callback.getFileName();

                            machineAuth.bytesWritten = callback.getBytesToWrite();
                            machineAuth.fileSize = callback.getData().length;
                            machineAuth.offset = callback.getOffset();

                            machineAuth.result = EResult.OK;
                            machineAuth.lastError = 0;

                            machineAuth.oneTimePassword = callback.getOneTimePassword();

                            machineAuth.sentryFileHash = sentryHash;
                        }

                        steamUser.sendMachineAuthResponse(machineAuth);
                    }
                });
                //</editor-fold>
            }
        }

        /**
         * Signs in the client, using applicable files.
         *
         * @param userLogin
         */
        void login(SteamClientInfo userLogin) {
            logger.info("Logging in as user {}.",
                    userLogin.username);

            byte[] sentryHash = null;

            if (userLogin.sentryFile != null && userLogin.sentryFile.exists()) {
                logger.info("Using sentryfile {} for sign-in...",
                        userLogin.sentryFile.getName());

                try (FileInputStream fi =
                        new FileInputStream(userLogin.sentryFile)) {
                    byte[] sentryData =
                            new byte[(int) userLogin.sentryFile.length()];
                    fi.read(sentryData);

                    sentryHash = CryptoHelper.SHAHash(sentryData);
                } catch (FileNotFoundException ex) {
                    // We already checked to see if the file is available, dipshit.
                    throw new Error(ex);
                } catch (IOException ex) {
                    logger.error("Sentry file dun goofed.", ex);
                }
            }

            LogOnDetails loginData = new LogOnDetails()
                    .username(userLogin.username)
                    .password(userLogin.password)
                    .authCode(userLogin.authcode != null
                    ? userLogin.authcode : "");
            loginData.sentryFileHash = sentryHash;

            steamUser.logOn(loginData);

            clientInfo = userLogin;
        }

        /**
         * Authenticate the login via Steam's internal API.
         *
         * @param callback
         * @return
         */
        SteamLoginAuth authenticate(LoginKeyCallback callback) {
            logger.info("Attempting API auth...");

            final WebAPI userAuth = new WebAPI("ISteamUserAuth", "");
            // generate an AES session key
            final byte[] sessionKey = CryptoHelper.GenerateRandomBlock(32);

            // rsa encrypt it with the public key for the universe we're on
            final RSACrypto rsa = new RSACrypto(KeyDictionary.getPublicKey(steamClient.getConnectedUniverse()));
            byte[] cryptedSessionKey = rsa.encrypt(sessionKey);

            final byte[] loginKey = new byte[20];
            System.arraycopy(callback.getLoginKey().getBytes(), 0, loginKey, 0, callback.getLoginKey().length());

            // aes encrypt the loginkey with our session key
            final byte[] cryptedLoginKey = CryptoHelper.SymmetricEncrypt(loginKey, sessionKey);

            KeyValue authResult;

            SteamLoginAuth result = new SteamLoginAuth();

            try {
                authResult = userAuth.authenticateUser(
                        String.valueOf(steamClient.getSteamId().convertToLong()),
                        WebHelpers.UrlEncode(cryptedSessionKey),
                        WebHelpers.UrlEncode(cryptedLoginKey), "POST");
            } catch (final Exception e) {
                result.success = false;
                return result;
            }

            result.token = authResult.get("token").asString();
            result.success = true;

            return result;
        }

        public SteamFriendEntry getUserStatus(SteamID user) {
            SteamFriendEntry friend = new SteamFriendEntry();
            friend.steamid = user;
            friend.username = backend.steamFriends.
                    getFriendPersonaName(user);
            friend.state = backend.steamFriends.
                    getFriendPersonaState(user);
            friend.relationship = backend.steamFriends.
                    getFriendRelationship(user);
            friend.game = backend.steamFriends.getFriendGamePlayedName(user);

            return friend;
        }

        /**
         * Runnable class that gets as many unhandled callbacks as possible and
         * handles them.
         */
        private class CallbackGetter implements Runnable {
            @Override
            public void run() {
                CallbackMsg msg;

                // 'null' when empty.
                while ((msg = steamClient.getCallback(true)) != null) {
                    callbackManager.handleSteamMessage(msg);
                }
            }
        }

        class TradePoller implements Runnable {
            TradeSession t;

            /**
             * Creates a trade polling instance.
             */
            TradePoller() {
                t = null;
            }

            /**
             * Poll-checking code - calls the trade update method.
             */
            @Override
            public void run() {
                if (isInTrade()) {
                    t.run();
                }
            }

            public void setCurrentTradeSession(TradeSession session) {
                t = session;
            }

            public void endCurrentTradeSession() {
                t = null;
                logger.info("Trade ended.");
                chatFrame.onTradeClosed();
            }

            public void forceCancelTradeSession() {
                if (isInTrade()) {
                    try {
                        t.getCmds().cancelTrade();
                    } catch (JSONException e) {
                    }
                }
                endCurrentTradeSession();
            }

            public boolean isInTrade() {
                return t != null;
            }
        }

        boolean readyToTrade() {
            return sessionId != null && token != null;
        }

        /**
         * A cheap AFK checker thread that polls for mouse input to see if the
         * user is available. Polls every second.
         *
         * @author nosoop < nosoop at users.noreply.github.com >
         */
        class FrontendInactivityChecker implements Runnable {
            final int SECONDS_UNTIL_AWAY = 5 * 60; // 5 minutes
            final int SECONDS_UNTIL_SNOOZE = 60 * 60 * 2; // 2 hours
            long timeLastActive;
            int lastX, lastY;
            // If user set themselves away, don't automatically set them online.
            boolean autoSetAFK;
            Logger logger;

            FrontendInactivityChecker() {
                super();

                this.timeLastActive = System.currentTimeMillis();

                logger = LoggerFactory.getLogger(
                        FrontendInactivityChecker.class.getSimpleName());

                java.awt.Point m = getMousePoint();
                this.lastX = m.x;
                this.lastY = m.y;

                this.autoSetAFK = false;
            }

            /**
             * Mouse checking runnable.
             */
            @Override
            @SuppressWarnings({"CallToThreadDumpStack"})
            public void run() {
                java.awt.Point m = getMousePoint();
                EPersonaState status = steamFriends.getPersonaState();

                int newX = m.x, newY = m.y;

                // If mouse was not moved in the last second...
                if (this.lastX == newX && this.lastY == newY) {
                    // Update seconds since we have been assumed AFK.
                    int secondsSinceAFK = (int) (System.currentTimeMillis() - timeLastActive) / 1000;

                    if (secondsSinceAFK > SECONDS_UNTIL_SNOOZE
                            && status == EPersonaState.Away && autoSetAFK) {
                        steamFriends.setPersonaState(EPersonaState.Snooze);
                    } else // If past AFK threshold and not away or snoozed, then set.
                    if (secondsSinceAFK > SECONDS_UNTIL_AWAY
                            && status != EPersonaState.Away
                            && status != EPersonaState.Snooze) {
                        steamFriends.setPersonaState(EPersonaState.Away);
                        autoSetAFK = true;
                    }
                } else { // Mouse was moved.
                    lastX = newX;
                    lastY = newY;
                    timeLastActive = System.currentTimeMillis();

                    // If the AFK handler set them away and we're not away, online.
                    if (autoSetAFK && (status == EPersonaState.Away
                            || status == EPersonaState.Snooze)) {
                        steamFriends.setPersonaState(EPersonaState.Online);
                        autoSetAFK = false;
                        logger.info("AFK unset.");
                    }
                }
            }

            /**
             * Gets the location of the mouse.
             *
             * @return A Point object representing the position of the mouse.
             */
            private java.awt.Point getMousePoint() {
                return java.awt.MouseInfo.getPointerInfo().getLocation();
            }
        }

    }

    public static class SteamWebLogin {
        /**
         * Signs into Steam using the supplied credentials.
         *
         * @param username The user to sign in as.
         * @param password The password linked to the username.
         * @param cookies Any cookies available to bypass SteamGuard.
         * @return
         * @throws IOException
         * @throws NoSuchAlgorithmException
         * @throws InvalidKeySpecException
         * @throws NoSuchPaddingException
         * @throws InvalidKeyException
         * @throws IllegalBlockSizeException
         * @throws BadPaddingException
         * @throws JSONException
         */
        public static SteamLoginAuth login(String username, String password,
                Map<String, String> cookies) throws IOException,
                NoSuchAlgorithmException, InvalidKeySpecException,
                NoSuchPaddingException, InvalidKeyException,
                IllegalBlockSizeException, BadPaddingException, JSONException {

            // Build cookie request property
            Map<String, String> reqp = new HashMap<>();
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> c : cookies.entrySet()) {
                sb.append(c.getKey()).append("=")
                        .append(URLEncoder.encode(c.getValue(), "UTF-8"))
                        .append("; ");
            }
            reqp.put("Cookie", sb.toString().trim());

            // Add posts.
            Map<String, String> post = new HashMap<>();
            post.put("username", username);

            String response = retrievePageText(
                    "https://steamcommunity.com/login/getrsakey", reqp, post);
            JSONObject rsaJSON = new JSONObject(response);


            // Validate
            if (!rsaJSON.getBoolean("success")) {
                SteamLoginAuth auth = new SteamLoginAuth();
                auth.success = false;
                return auth;
            }

            byte[] encodedPassword = cryptRSA(
                    rsaJSON.getString("publickey_exp"),
                    rsaJSON.getString("publickey_mod"),
                    password.getBytes("ASCII"));

            String encryptedPassword = Base64.encodeBytes(encodedPassword);

            JSONObject loginJSON = new JSONObject();
            String steamGuardText = "";
            String steamGuardId = "";
            do {
                boolean captcha = loginJSON.optBoolean("captcha_needed");
                boolean steamGuard = loginJSON.optBoolean("emailauth_needed");

                String time = rsaJSON.getString("timestamp");
                String capGID = loginJSON.optString("captcha_gid", null);

                post = new HashMap<>();
                post.put("password", encryptedPassword);
                post.put("username", username);

                // Captcha
                String capText = "";
                if (captcha) {
                    System.out.println("SteamWeb: Captcha is needed.");

                    try {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().browse(new URI("https://steamcommunity.com/public/captcha.php?gid=" + loginJSON.getString("captcha_gid")));
                        }
                        String promptMessage = String.format(
                                "SteamWeb login failed.  Please type the "
                                + "captcha at %s to continue.",
                                "https://steamcommunity.com/public/"
                                + "captcha.php?gid="
                                + loginJSON.getString("captcha_gid"));

                        capText = JOptionPane.showInputDialog(null,
                                promptMessage, "");
                    } catch (URISyntaxException ex) {
                        throw new Error(ex);
                    }
                }

                post.put("captchagid", captcha ? capGID : "-1");
                post.put("captcha_text", captcha ? capText : "");
                // Captcha end

                // SteamGuard
                if (steamGuard) {
                    String promptMessage = "SteamWeb login failed. Please "
                            + "enter the SteamGuard code sent to your e-mail.";
                    steamGuardText = JOptionPane.showInputDialog(null,
                            promptMessage, "");

                    steamGuardId = loginJSON.getString("emailsteamid");
                }

                post.put("emailauth", steamGuardText);
                post.put("emailsteamid", steamGuardId);
                // SteamGuard end

                post.put("rsatimestamp", time);

                response = retrievePageText(
                        "https://steamcommunity.com/login/dologin/", reqp, post);

                loginJSON = new JSONObject(response);

            } while (loginJSON.optBoolean("captcha_needed")
                    || loginJSON.optBoolean("emailauth_needed"));

            if (loginJSON.getBoolean("success")) {
                post = new HashMap<>();
                JSONObject tp = loginJSON.getJSONObject("transfer_parameters");
                for (String kv : (Set<String>) tp.keySet()) {
                    post.put(kv, tp.optString(kv));
                }

                retrievePageText(loginJSON.getString("transfer_url"),
                        reqp, post);

                SteamLoginAuth auth = new SteamLoginAuth();
                auth.success = true;

                String rawtoken = tp.getString("steamid") + "||"
                        + tp.getString("token");
                auth.token = URLEncoder.encode(rawtoken, "UTF-8");

                return auth;
            } else {
                System.out.println("SteamWeb Error: "
                        + loginJSON.optString("message"));
                SteamLoginAuth auth = new SteamLoginAuth();
                auth.success = false;
                return auth;
            }
        }

        private static byte[] cryptRSA(String exp, String mod, byte[] bytes)
                throws NoSuchAlgorithmException, InvalidKeySpecException,
                NoSuchPaddingException, IllegalBlockSizeException,
                InvalidKeyException, BadPaddingException {
            BigInteger m = new BigInteger(mod, 16);
            BigInteger e = new BigInteger(exp, 16);
            RSAPublicKeySpec spec = new RSAPublicKeySpec(m, e);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            PublicKey key = keyFactory.generatePublic(spec);

            Cipher rsa = Cipher.getInstance("RSA");
            rsa.init(Cipher.ENCRYPT_MODE, key);

            return rsa.doFinal(bytes);
        }

        /**
         * Stores the contents of a requested URL into a StringBuffer. This
         * method requests the URL via POST.
         *
         * @param url The URL to retrieve.
         * @param req Request properties to be sent.
         * @param post Post parameters to be sent.
         * @return StringBuffer containing response, default error JSON if
         * otherwise
         * @throws IOException
         */
        private static String retrievePageText(String url,
                Map<String, String> req, Map<String, String> post) throws
                IOException {
            BufferedReader feedReader;
            InputStreamReader feedStream;

            StringBuilder buf = new StringBuilder();
            URL siteUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) siteUrl.openConnection();

            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (req != null) {
                for (String key : req.keySet()) {
                    conn.setRequestProperty(key, req.get(key));
                }
            }

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (post != null) {
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                Set keys = post.keySet();
                Iterator<String> keyIter = keys.iterator();
                String content = "";
                for (int i = 0; keyIter.hasNext(); i++) {
                    String key = keyIter.next();
                    if (i != 0) {
                        content += "&";
                    }
                    content += key + "=" + URLEncoder.encode(post.get(key),
                            "UTF-8");
                }
                out.writeBytes(content);
                out.flush();
                out.close();
            }

            InputStream netStream;

            try {
                netStream = conn.getInputStream();
            } catch (java.io.IOException ee) {
                // Grab the JSON message from the error stream.
                netStream = conn.getErrorStream();
            }

            if (netStream != null) {
                if ("gzip".equals(conn.getContentEncoding())) {
                    netStream = new GZIPInputStream(netStream);
                }

                feedStream = new InputStreamReader(netStream);

                feedReader = new BufferedReader(feedStream);

                String line;
                while ((line = feedReader.readLine()) != null) {
                    buf.append(line).append('\n');
                }

                feedReader.close();
            }
            conn.disconnect();
            return buf.toString();
        }
    }

    /**
     * Container class holding client login data.
     */
    public static class SteamClientInfo {
        String username, password, authcode, token, machineauthcookie;
        File sentryFile;
    }

    /**
     * Container class containing friend data for use in the friends list.
     */
    public static class SteamFriendEntry {
        SteamID steamid;
        private String username, game;
        private EPersonaState state;
        private EFriendRelationship relationship;

        @Override
        public String toString() {
            return username;
        }

        /**
         * Returns the username held by this SteamFriendEntry instance.
         *
         * @return The username field.
         */
        String getUsername() {
            return username;
        }

        /**
         * Returns the status of the user.
         *
         * @return An EPersonaState value representing the current status of the
         * user.
         */
        EPersonaState getPersonaState() {
            return state;
        }

        /**
         * Returns the client's relationship with the user.
         *
         * @return An EFriendRelationship value representing the relationship
         * with the user.
         */
        EFriendRelationship getRelationship() {
            return relationship;
        }

        /**
         * Returns the status to be displayed in the friend table, among other
         * places.
         *
         * @return
         */
        String renderUserStatus() {
            return relationship == EFriendRelationship.Friend
                    ? renderFriendState() : relationship.name();
        }

        /**
         * Renders the state of the user, localizing if necessary.
         *
         * @return
         */
        String renderFriendState() {
            return LocalizationResources.getString("PersonaState." + state.name());
        }
    }

    /**
     * Container class holding the result of sign-in.
     */
    public static class SteamLoginAuth {
        boolean success;
        String token;
    }

}