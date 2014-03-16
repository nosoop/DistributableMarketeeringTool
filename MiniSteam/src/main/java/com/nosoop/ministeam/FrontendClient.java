/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.ministeam;

import net.sourceforge.iharder.base64.Base64;
import com.live.poonso.poopydebug.DebugPrint;
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

    public FrontendClient() {
        DebugPrint.setDebug(true);

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

        DebugPrint.println("Connecting to the Steam network...");
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
                DebugPrint.printf("Received callback %s.\n",
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
                        DebugPrint.printf("Unable to connect to Steam: %s\n",
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
                    DebugPrint.println("Disconnected from Steam.  Retrying in one.");

                    /**
                     * Try to reconnect after a second.
                     */
                    clientExec.schedule(new Runnable() {
                        @Override
                        public void run() {
                            DebugPrint.println("Retrying connection.");
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
                        DebugPrint.println("Submitting authentication code " + authcode);

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
                        DebugPrint.println("Successfully signed in to Steam!");

                        // TODO Move login code.
                        loginDialog.onSuccessfulLogin();

                        // Spawn a thread for checking AFK.  Might want to manage.
                        Runnable inactiveChecker = new FrontendInactivityChecker(steamFriends);
                        clientExec.scheduleAtFixedRate(inactiveChecker, 0, 1, TimeUnit.SECONDS);

                        mainWindow.setVisible(true);
                        return;
                    }

                    System.out.println(callback.getResult());
                }
            });

            msg.handle(LoggedOffCallback.class, new ActionT<LoggedOffCallback>() {
                // Told to log off by Steam for some reason or other.
                @Override
                public void call(LoggedOffCallback callback) {
                    DebugPrint.printf("Logged off of Steam with result %s.\n", callback.getResult());
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
                    System.out.println(sessionId);

                    TradeUser u = new TradeUser();

                    // I guess we'll just have to do this manually.
                    // TODO Implement SteamWeb's steamMachineAuth cookie.
                    /**
                     * I'm thinking you can just generate the authkey from the
                     * long steamid and just grab the value.
                     */
                    u.addCookie("sessionid", sessionId, true);
                    
                    if (clientInfo.getAccountToken() != null) {
                        u.addCookie("steamMachineAuth" + steamUser.getSteamId().convertToLong(), clientInfo.getAccountToken(), true);
                    }
                    try {
                        u.login(clientInfo.getAccountUsername(), clientInfo.getAccountPassword());
                        DebugPrint.println("SteamWeb login authenticated.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    List<Cookie> cookies = u.getCookies();
                    for (Cookie c : cookies) {
                        //System.out.printf("%s = %s\n", c.getName(), c.getValue());
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
                    DebugPrint.printf("Trade request received from [%s].\n",
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
                        e.printStackTrace();

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
                    //System.out.println(callback.getResponse().name());
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
                        System.out.printf("Wallet balance: %d %s\n",
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
            DebugPrint.printf("Received callback %s (JobCallback)\n",
                    msg.getClass().getSimpleName());

            // Go update your SteamGuard file.  Or make a new one.
            msg.handle(UpdateMachineAuthCallback.class,
                    new ActionT<UpdateMachineAuthCallback>() {
                @Override
                public void call(UpdateMachineAuthCallback callback) {
                    DebugPrint.println("Creating authentication file...");
                    byte[] sentryHash = CryptoHelper.SHAHash(callback.getData());

                    try {
                        BufferedOutputStream fo = new BufferedOutputStream(
                                new FileOutputStream(sentryFile));
                        fo.write(callback.getData());
                        fo.flush();
                        fo.close();
                    } catch (FileNotFoundException ex) {
                    } catch (IOException ex) {
                    }

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
        DebugPrint.printf("Connected to Steam.  Logging in as user %s.\n", clientInfo.getAccountUsername());

        String fileName = String.format(SENTRY_BIN_FILENAME, clientInfo.getAccountUsername());

        sentryFile = new File(fileName);

        byte[] sentryHash = null;

        if (sentryFile.exists()) {
            DebugPrint.println("Using sentryfile " + sentryFile.getName() + " for sign-in...");

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
                ex.printStackTrace();
            }
        }

        LogOnDetails loginData = new LogOnDetails()
                .username(clientInfo.getAccountUsername())
                .password(clientInfo.getAccountPassword())
                .authCode(authcode);
        loginData.sentryFileHash = sentryHash;
        
        System.out.printf("%s%n%s%n", clientInfo.getAccountUsername(),
                clientInfo.getAccountPassword());

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
 * available. Polls just about every second in addition to processing.
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
class FrontendInactivityChecker implements Runnable {

    SteamFriends steamFriends;
    long timeLastActive;
    int lastX, lastY;
    /*
     * If user has been out for 300 seconds (5 minutes), change the user's
     * persona state.
     */
    final int SECONDS_UNTIL_AWAY = 300;
    // If user set themselves away, don't automatically set them online.
    boolean inactiveSetAFK;

    public FrontendInactivityChecker(SteamFriends steamFriends) {
        super();

        this.steamFriends = steamFriends;
        this.timeLastActive = System.currentTimeMillis();

        java.awt.Point m = getMousePoint();
        this.lastX = m.x;
        this.lastY = m.y;

        this.inactiveSetAFK = false;
    }

    @Override
    @SuppressWarnings({"CallToThreadDumpStack"})
    public void run() {
        java.awt.Point m = getMousePoint();

        int newX = m.x, newY = m.y;

        if (this.lastX == newX && this.lastY == newY) {
            // Check how long the mouse has been there.
            int secondsSinceAFK = (int) (System.currentTimeMillis() - timeLastActive) / 1000;

            if (secondsSinceAFK > SECONDS_UNTIL_AWAY
                    && steamFriends.getPersonaState() == EPersonaState.Online) {
                steamFriends.setPersonaState(EPersonaState.Away);
                inactiveSetAFK = true;
            }
        } else { // Mouse was moved.
            lastX = newX;
            lastY = newY;
            timeLastActive = System.currentTimeMillis();

            // If the AFK handler set them away and we're not away, online.
            if (inactiveSetAFK && steamFriends.getPersonaState() == EPersonaState.Away) {
                steamFriends.setPersonaState(EPersonaState.Online);
                inactiveSetAFK = false;
                //System.out.println("AFK unset.");
            }
        }
    }

    // Unility to get the Point object from the mouse.
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