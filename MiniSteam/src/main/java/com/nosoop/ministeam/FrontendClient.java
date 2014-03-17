/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.ministeam;

import net.sourceforge.iharder.base64.Base64;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JOptionPane;
import com.nosoop.steamtrade.TradeSession;
import com.nosoop.steamtrade.TradeListener;
import com.ryanspeets.tradeoffer.TradeUser;
import java.util.List;
import org.apache.http.cookie.Cookie;
import uk.co.thomasc.steamkit.base.generated.steamlanguage.EChatEntryType;
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
import uk.co.thomasc.steamkit.util.cSharp.events.ActionT;
import uk.co.thomasc.steamkit.util.crypto.CryptoHelper;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import bundled.steamtrade.org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class FrontendClient {

    public boolean isLoggedIn = false;
    //
    public long[] admins;
    //
    public SteamFriends steamFriends;
    public SteamClient steamClient;
    public SteamTrading steamTrade;
    public SteamGameCoordinator steamGC;
    public SteamUser steamUser;
    //
    boolean isRunning;
    public boolean isReadyToTrade;
    //
    String sessionId;
    String token;
    String authcode = "";
    //
    File sentryFile;
    //
    private FrontendClientInfo clientInfo;
    //
    SteamLoginDialog loginDialog;
    SteamMainWindow mainWindow; // = new SteamMainWindow(this);
    //
    final ScheduledExecutorService tradeExec, clientExec;
    TradePoller tradePoller;
    //
    CallbackMgr callbackManager;
    Logger logger = LoggerFactory.getLogger(FrontendClient.class.getSimpleName());

    public FrontendClient() {
        loginDialog = new SteamLoginDialog(null, false, FrontendClient.this);
        loginDialog.setVisible(true);
        loginDialog.setLoginStatus("Connecting to the Steam network.");
        loginDialog.setInputState(false);

        mainWindow = new SteamMainWindow(FrontendClient.this);

        steamClient = new SteamClient();
        steamTrade = steamClient.getHandler(SteamTrading.class);
        steamUser = steamClient.getHandler(SteamUser.class);
        steamFriends = steamClient.getHandler(SteamFriends.class);
        steamGC = steamClient.getHandler(SteamGameCoordinator.class);

        tradeExec = Executors.newSingleThreadScheduledExecutor();
        clientExec = Executors.newSingleThreadScheduledExecutor();

        isRunning = true;
        isReadyToTrade = false;


        callbackManager = new CallbackMgr();

        logger.info("Connecting to the Steam network...");
        steamClient.connect();

        // Schedule trade poller to poll every second when in a trade.
        tradePoller = new TradePoller();
        tradeExec.scheduleAtFixedRate(tradePoller, 0, 1, TimeUnit.SECONDS);

        // Schedule callbacks.
        clientExec.scheduleWithFixedDelay(new CallbackGetter(), 0, 100, TimeUnit.MILLISECONDS);
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
            msg.handle(ConnectedCallback.class, new ActionT<ConnectedCallback>() {
                // Connected to Steam.  It acknowledges your presence.
                @Override
                public void call(ConnectedCallback callback) {
                    if (callback.getResult() != EResult.OK) {
                        logger.info("Unable to connect to Steam: {}",
                                callback.getResult().getClass().getName());
                        isRunning = false;
                    } else {
                        loginDialog.setInputState(true);
                        loginDialog.setLoginStatus("Waiting to sign in...");
                    }
                }
            });

            msg.handle(DisconnectedCallback.class, new ActionT<DisconnectedCallback>() {
                // Disconnected form Steam.
                @Override
                public void call(DisconnectedCallback callback) {
                    logger.error("Disconnected from Steam.  Retrying in one.");

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

            msg.handle(LoggedOnCallback.class, new ActionT<LoggedOnCallback>() {
                // Logged on to Steam, or attempted to login and SteamGuard-blocked.
                @Override
                public void call(LoggedOnCallback callback) {
                    if (callback.getResult() == EResult.AccountLogonDenied) {
                        String dialogMessage = String.format("Account is SteamGuard protected.\nEnter the authentication code sent to the address at %s.", callback.getEmailDomain());

                        authcode = JOptionPane.showInputDialog(null, dialogMessage, "SteamGuard", JOptionPane.INFORMATION_MESSAGE);
                        logger.info("Submitting authentication code " + authcode);

                        // Force a disconnect because it won't try logging in on its own.
                        steamClient.disconnect();
                        return;
                    }

                    if (callback.getResult() == EResult.InvalidPassword) {
                        // Notified that user info is incorrect.
                        // TODO Figure out how to handle an invalid password.
                        loginDialog.setLoginStatus("Password is invalid.");
                        steamClient.disconnect();
                        steamClient.connect();
                    }

                    if (callback.getResult() == EResult.OK) {
                        logger.info("Successfully signed in to Steam!");

                        // TODO Move login code.
                        loginDialog.onSuccessfulLogin();

                        // Spawn a thread for checking AFK.  Might want to manage.
                        Runnable inactiveChecker = new FrontendInactivityChecker(steamFriends);
                        clientExec.scheduleAtFixedRate(inactiveChecker, 0, 1, TimeUnit.SECONDS);

                        mainWindow.setVisible(true);
                        return;
                    }

                    logger.debug(callback.getResult().name());
                }
            });

            msg.handle(LoggedOffCallback.class, new ActionT<LoggedOffCallback>() {
                // Told to log off by Steam for some reason or other.
                @Override
                public void call(LoggedOffCallback callback) {
                    logger.info("Logged off of Steam with result {}.", callback.getResult());
                    // LogonSessionReplaced:  Another instance of Steam is using the account.
                }
            });

            /**
             * Account info and state.
             */
            msg.handle(AccountInfoCallback.class, new ActionT<AccountInfoCallback>() {
                // Oh hey, you can do stuff with your account now.
                @Override
                public void call(AccountInfoCallback obj) {
                    steamFriends.setPersonaState(EPersonaState.Online);
                }
            });

            msg.handle(PersonaStateCallback.class, new ActionT<PersonaStateCallback>() {
                @Override
                public void call(PersonaStateCallback callback) {
                    // TODO Fix references.
                    if (callback.getFriendID().convertToLong() == steamUser.getSteamId().convertToLong()) {
                        mainWindow.setPlayerName(callback.getName());
                    } else {
                        mainWindow.updateFriendStatus(callback.getFriendID());
                    }
                }
            });

            /**
             * Steam friends and messaging service.
             */
            msg.handle(FriendsListCallback.class, new ActionT<FriendsListCallback>() {
                // Updated friends list.  Client has either added or removed a player.
                @Override
                public void call(FriendsListCallback callback) {

                    for (Friend friend : callback.getFriendList()) {
                        mainWindow.updateFriendStatus(friend.getSteamId());
                    }
                    mainWindow.updateFriendsList();
                }
            });

            msg.handle(FriendMsgCallback.class, new ActionT<FriendMsgCallback>() {
                // Another person is typing or sent a message.
                @Override
                public void call(FriendMsgCallback callback) {
                    mainWindow.receiveChatMessage(callback);
                }
            });

            /**
             * Steam web services.
             */
            msg.handle(LoginKeyCallback.class, new ActionT<LoginKeyCallback>() {
                @Override
                public void call(LoginKeyCallback callback) {
                    // Hopefully we only need to do this once.

                    sessionId = Base64.encodeBytes(String.valueOf(callback.getUniqueId()).getBytes());

                    TradeUser u = new TradeUser();

                    // I guess we'll just have to do this manually.
                    u.addCookie("sessionid", sessionId, true);

                    if (clientInfo.getAccountToken() != null) {
                        u.addCookie("steamMachineAuth" + steamUser.getSteamId().convertToLong(), clientInfo.getAccountToken(), true);
                    }
                    try {
                        u.login(clientInfo.getAccountUsername(), clientInfo.getAccountPassword());
                        logger.info("SteamWeb login authenticated.");
                    } catch (Exception e) {
                        logger.error("SteamWeb Login Failre", e);
                    }

                    List<Cookie> cookies = u.getCookies();
                    for (Cookie c : cookies) {
                        if (c.getName().equals("steamLogin")) {
                            token = c.getValue();
                        }
                    }

                    isReadyToTrade = true;

                    // Once authenticated, you can set other things.
                }
            });

            /**
             * Handlers for trade events.
             */
            msg.handle(TradeProposedCallback.class, new ActionT<TradeProposedCallback>() {
                // A trade session was requested by another client.
                @Override
                public void call(TradeProposedCallback callback) {
                    // TODO Not automatically accept trade.
                    logger.info("Trade request received from [{}].",
                            callback.getOtherClient().render());

                    if (!isReadyToTrade) {
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

            msg.handle(SessionStartCallback.class, new ActionT<SessionStartCallback>() {
                // A trade session was initialized.
                @Override
                public void call(SessionStartCallback callback) {

                    TradeListener listener = new FrontendTrade(FrontendClient.this,
                            steamFriends.getFriendPersonaName(callback.getOtherClient()));

                    try {
                        tradePoller.setCurrentTradeSession(new TradeSession(steamUser.getSteamId().convertToLong(), callback.getOtherClient().convertToLong(), sessionId, token, listener));
                    } catch (final Exception e) {
                        // Error during construction.
                        logger.error("Error during trade init.", e);

                        steamFriends.sendChatMessage(callback.getOtherClient(), EChatEntryType.ChatMsg, "Whoops!  Something went wrong.");
                        steamTrade.cancelTrade(callback.getOtherClient());

                        tradePoller.forceCancelTradeSession();
                    }
                }
            });

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

            /**
             * Handler for money stuff?
             */
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

            /**
             * The job handler. Passes the callback into
             * handleSteamJobMessage(...).
             */
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
            msg.handle(UpdateMachineAuthCallback.class,
                    new ActionT<UpdateMachineAuthCallback>() {
                @Override
                public void call(UpdateMachineAuthCallback callback) {
                    logger.info("Creating authentication file...");
                    byte[] sentryHash = CryptoHelper.SHAHash(callback.getData());

                    try (BufferedOutputStream fo = new BufferedOutputStream(
                            new FileOutputStream(sentryFile))) {

                        fo.write(callback.getData());
                        fo.flush();
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
        }
    }
    // Hardcoded sentry file name format.
    final String SENTRY_BIN_FILENAME = "sentry_%s.bin";

    void doLogin() {
        logger.info("Connected to Steam.  Logging in as user {}.", clientInfo.getAccountUsername());

        String fileName = String.format(SENTRY_BIN_FILENAME, clientInfo.getAccountUsername());

        sentryFile = new File(fileName);

        byte[] sentryHash = null;

        if (sentryFile.exists()) {
            logger.info("Using sentryfile {} for sign-in...", sentryFile.getName());

            try {
                FileInputStream fi = new FileInputStream(sentryFile);
                byte[] sentryData = new byte[(int) sentryFile.length()];
                fi.read(sentryData);

                sentryHash = CryptoHelper.SHAHash(sentryData);

                fi.close();
            } catch (FileNotFoundException ex) {
                // We already checked to see if the file is available, dipshit.
                throw new Error("It's pointless.  Just give up already.");
            } catch (IOException ex) {
                logger.error("Sentry file dun goofed.", ex);
            }
        }

        LogOnDetails loginData = new LogOnDetails()
                .username(clientInfo.getAccountUsername())
                .password(clientInfo.getAccountPassword())
                .authCode(authcode);
        loginData.sentryFileHash = sentryHash;

        steamUser.logOn(loginData);
    }

    void quit() {
        System.exit(0);
    }

    public void receiveLoginInformation(FrontendClientInfo info) {
        clientInfo = info;
    }

    public String getOwnSteamName() {
        return steamFriends.getFriendPersonaName(steamUser.getSteamId());
    }

    void setClientInfo(FrontendClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    /**
     * Things to do when an ongoing trade is closed.
     */
    public void onTradeClosed() {
        tradePoller.endCurrentTradeSession();
    }
}

/**
 * A cheap AFK checker thread that polls for mouse input to see if the user is
 * available. Polls every second.
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
class FrontendInactivityChecker implements Runnable {

    final int SECONDS_UNTIL_AWAY = 5 * 60; // 5 minutes
    final int SECONDS_UNTIL_SNOOZE = 60 * 60 * 2; // 2 hours
    // TODO Implement auto-snooze.
    SteamFriends steamFriends;
    long timeLastActive;
    int lastX, lastY;
    // If user set themselves away, don't automatically set them online.
    boolean autoSetAFK;
    Logger logger;

    public FrontendInactivityChecker(SteamFriends steamFriends) {
        super();

        this.steamFriends = steamFriends;
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