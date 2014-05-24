/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.ministeam2;

import bundled.steamtrade.org.json.JSONException;
import com.nosoop.inputdialog.CallbackInputFrame.DialogCallback;
import com.nosoop.ministeam.FrontendTrade;
import com.nosoop.steamtrade.TradeListener;
import com.nosoop.steamtrade.TradeSession;
import com.nosoop.steamtrade.inventory.AssetBuilder;
import com.ryanspeets.tradeoffer.TradeUser;
import java.awt.EventQueue;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import javax.crypto.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sourceforge.iharder.base64.Base64;
import org.apache.http.cookie.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.thomasc.steamkit.base.generated.steamlanguage.EChatEntryType;
import static uk.co.thomasc.steamkit.base.generated.steamlanguage.EEconTradeResponse.*;
import uk.co.thomasc.steamkit.base.generated.steamlanguage.EFriendRelationship;
import uk.co.thomasc.steamkit.base.generated.steamlanguage.EPersonaState;
import uk.co.thomasc.steamkit.base.generated.steamlanguage.EResult;
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
import uk.co.thomasc.steamkit.types.steamid.SteamID;
import uk.co.thomasc.steamkit.util.cSharp.events.ActionT;
import uk.co.thomasc.steamkit.util.crypto.CryptoHelper;

/**
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
    Map<Long, SteamFriendEntry> friendList;
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

        loginDialog.setSteamConnectionState(
                SteamClientLoginDialog.ClientConnectivityState.CONNECTING);

        friendList = new HashMap<>();
        
        chatFrame = new SteamClientChatFrame(backend);
    }

    synchronized void updateFriendStatus(final SteamID userid) {
        if (userid.isIndividualAccount()) {
            long sid = userid.convertToLong();

            if (friendList.containsKey(sid)) {
                friendList.remove(sid);
            }

            String friendState;

            /**
             * If the user is your friend, get their activity state (Online,
             * Away...), otherwise, get your relationship (Invite pending...).
             */
            if (backend.steamFriends.getFriendRelationship(userid)
                    == EFriendRelationship.Friend) {
                friendState = backend.steamFriends.
                        getFriendPersonaState(userid).name();
            } else {
                friendState = backend.steamFriends.
                        getFriendRelationship(userid).name();
            }

            // Only put them in the friends list if you have a relationship.
            if (backend.steamFriends.getFriendRelationship(userid)
                    != EFriendRelationship.None) {
                SteamFriendEntry friend = new SteamFriendEntry();
                friend.steamid64 = sid;
                friend.username = backend.steamFriends.
                        getFriendPersonaName(userid);
                friend.status = friendState;

                friendList.put(sid, friend);
            }

            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
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

        // Fastest way to clear the table.
        friendTable.setNumRows(0);

        // TODO a way to update the table more efficently?
        for (Map.Entry<Long, SteamFriendEntry> keyValues : friendList.entrySet()) {
            SteamFriendEntry entry = keyValues.getValue();
            friendTable.addRow(new Object[]{entry.username, entry.status});
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

        labelPlayerName = new javax.swing.JLabel();
        comboboxUserStatus = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableUsers = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        labelPlayerName.setText("[unknown]");

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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox comboboxUserStatus;
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

            // Schedule callbacks.
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
                            // TODO Set not running.
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

                        if (callback.getResult() == EResult.InvalidPassword) {
                            // Notified that user info is incorrect.
                            // TODO Figure out how to handle an invalid password.
                            //loginDialog.setLoginStatus("Password is invalid.");
                            loginDialog.setSteamConnectionState(
                                    SteamClientLoginDialog.ClientConnectivityState.INCORRECT_LOGIN);
                            loginOnConnectedCallback = false;
                            steamClient.disconnect();
                            steamClient.connect();
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
                        
                        if (callback.getFriendID().convertToLong() ==
                                steamUser.getSteamId().convertToLong()) {
                            form.labelPlayerName.setText(callback.getName());
                            if (!form.comboboxUserStatus.getSelectedItem().equals(callback.getState())) {
                                form.comboboxUserStatus.setSelectedItem(callback.getState());
                            }
                        } else {
                            form.updateFriendStatus(callback.getFriendID());
                            // TODO use callback to update other stuff.
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
                        SteamClientMainForm.this.chatFrame.onReceivedMessage(
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
                         * login data and enable automatic client authentication
                         * for those that have it working.
                         */
                        // Hopefully we only need to do this once.
                        sessionId = Base64.encodeBytes(
                                String.valueOf(callback.getUniqueId()).
                                getBytes());

                        TradeUser u = new TradeUser();

                        // I guess we'll just have to do this manually.
                        u.addCookie("sessionid", sessionId, true);

                        if (!clientInfo.machineauthcookie.equals("")) {
                            u.addCookie("steamMachineAuth" + 
                                    steamUser.getSteamId().convertToLong(), 
                                    clientInfo.machineauthcookie, true);
                        }
                        try {
                            u.login(clientInfo.username, clientInfo.password);
                            logger.info("SteamWeb login authenticated.");
                        } catch (IOException | NoSuchAlgorithmException |
                                InvalidKeySpecException |
                                NoSuchPaddingException | InvalidKeyException |
                                IllegalBlockSizeException |
                                BadPaddingException e) {
                            logger.error("SteamWeb Login Failre", e);
                        }

                        List<Cookie> cookies = u.getCookies();
                        for (Cookie c : cookies) {
                            if (c.getName().equals("steamLogin")) {
                                token = c.getValue();
                            }
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
                        // TODO Not automatically accept trade.
                        logger.info("Trade request received from [{}].",
                                callback.getOtherClient().render());

                        if (!readyToTrade()) {
                            steamTrade.respondToTrade(callback.getTradeID(), false);
                            steamFriends.sendChatMessage(
                                    callback.getOtherClient(), EChatEntryType.ChatMsg,
                                    "[DMT] The client has not completely initialized.");
                        } else if (!tradePoller.isInTrade()) {
                            steamTrade.respondToTrade(callback.getTradeID(), true);
                        } else {
                            steamFriends.sendChatMessage(
                                    callback.getOtherClient(), EChatEntryType.ChatMsg,
                                    "Already in a trade!  Give me a bit.");
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

                        // TODO Clean up reference to FrontendTrade.
                        TradeListener listener = new FrontendTrade(null,
                                steamFriends.getFriendPersonaName(callback.getOtherClient()));

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

                        CallbackMgr.this.handleSteamJobMessage(jobCallback, JOBID);
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

        void login(SteamClientInfo userLogin) {
            logger.info("Connected to Steam.  Logging in as user {}.",
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

            public TradePoller() {
                t = null;
            }

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
            // TODO Implement auto-snooze.
            long timeLastActive;
            int lastX, lastY;
            // If user set themselves away, don't automatically set them online.
            boolean autoSetAFK;
            Logger logger;

            public FrontendInactivityChecker() {
                super();

                this.timeLastActive = System.currentTimeMillis();

                logger = LoggerFactory.getLogger(FrontendInactivityChecker.class.getSimpleName());

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

                int newX = m.x, newY = m.y;

                // If mouse was not moved in the last second...
                if (this.lastX == newX && this.lastY == newY) {
                    // Update seconds since we have been assumed AFK.
                    int secondsSinceAFK =
                            (int) (System.currentTimeMillis() - timeLastActive) / 1000;

                    if (secondsSinceAFK > SECONDS_UNTIL_SNOOZE
                            && steamFriends.getPersonaState() == EPersonaState.Away
                            && autoSetAFK) {
                        steamFriends.setPersonaState(EPersonaState.Snooze);
                    } else // If past AFK threshold and not away or snoozed, then set.
                    if (secondsSinceAFK > SECONDS_UNTIL_AWAY
                            && steamFriends.getPersonaState() != EPersonaState.Away
                            && steamFriends.getPersonaState() != EPersonaState.Snooze) {
                        steamFriends.setPersonaState(EPersonaState.Away);
                        autoSetAFK = true;
                    }
                } else { // Mouse was moved.
                    lastX = newX;
                    lastY = newY;
                    timeLastActive = System.currentTimeMillis();

                    // If the AFK handler set them away and we're not away, online.
                    if (autoSetAFK && steamFriends.getPersonaState() == EPersonaState.Away) {
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

    /**
     * Struct holding client login data.
     */
    public static class SteamClientInfo {
        String username, password, authcode, token, machineauthcookie;
        File sentryFile;
    }

    public static class SteamFriendEntry {
        long steamid64;
        String username;
        String status;

        @Override
        public String toString() {
            return username;
        }
    }

}