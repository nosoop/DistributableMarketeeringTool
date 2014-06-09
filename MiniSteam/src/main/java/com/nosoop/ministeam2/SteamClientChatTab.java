package com.nosoop.ministeam2;

import com.nosoop.ministeam2.util.LocalizationResources;
import com.nosoop.ministeam2.util.SteamIDUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.LinkedList;
import javax.swing.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.thomasc.steamkit.base.generated.steamlanguage.EChatEntryType;
import uk.co.thomasc.steamkit.types.steamid.SteamID;

/**
 * An individual chat session.
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class SteamClientChatTab extends javax.swing.JPanel {
    /**
     * The formatting string to use when adding a new chat message.
     */
    private static final String CHAT_MESSAGE_ENTRY_FMT = "%s: %s";
    /**
     * Formatting string for the location of the chatlog file to be saved.
     */
    private static final String CHATLOG_FILEPATH =
            "." + File.separator + "logs" + File.separator + "%s"
            + File.separator + "%s" + File.separator + "%s.log";
    /**
     * A quick hack to get the system-specific newline sequence.
     */
    private static final String NEWLINE = String.format("%n", new Object[0]);
    /**
     * Formatting string for the filename.
     */
    private static final String CHATLOG_FILENAME =
            "%1$tY-%1$tm-%1$td-%1$tH%1$tM%1$tS %2$s";
    /**
     * Formatting string for date / time.
     */
    private static final String DATE_TIME_FMT =
            "[%1$tm/%1$td/%1$tY %1$tI:%1$tM:%1$tS %1$Tp]";
    /**
     * Number of milliseconds that must pass before we fire off another message
     * notifying the other user that we are typing.
     *
     * Currently set to 5 seconds.
     */
    private static final int MILLISEC_INTERVAL_TYPING = 5000;
    /**
     * The last time a key was pressed in the current chat.
     */
    private long lastTimeKeyPressed = 0;
    /**
     * The SteamID of the person we are chatting with.
     */
    final SteamID chatter;
    /**
     * The parent frame of this tab.
     */
    private SteamClientChatFrame frame;
    /**
     * Another logging instance. Of course.
     */
    Logger logger = LoggerFactory.getLogger(
            SteamClientChatTab.class.getSimpleName());
    /**
     * Timer that runs to unset the "typing..." state.
     */
    private Timer userIsTypingTimer;
    /**
     * Stored user information.
     */
    private SteamClientMainForm.SteamFriendEntry userinfo;
    /**
     * Trade button status, to determine what action should be done when
     * clicked..
     */
    private TradeButtonState state;
    private int tradeid;
    /**
     * Chat logging.
     */
    private ChatLogger chatlogger;
    /**
     * The event listing.
     */
    private SteamChatEventList chatEvents;
    /**
     * The maximum number of events to keep visible in the chat window. Chat
     * logs keep everything.
     */
    private static final int CHAT_EVENT_CAPACITY = 100;

    /**
     * Creates new form SteamClientChatPanel
     */
    public SteamClientChatTab(SteamClientChatFrame frame, SteamID chatter,
            SteamClientMainForm.SteamFriendEntry userinfo) {
        this.chatter = chatter;
        this.frame = frame;
        this.userinfo = userinfo;
        initComponents();

        // Creates a timer that unsets typing state after 15 seconds.
        userIsTypingTimer = new Timer(15000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStatusLabel(false);
            }
        });

        this.state = TradeButtonState.IDLE;
        this.tradeid = frame.tradeRequest.TRADEID_INVALID;
        this.chatlogger = new ChatLogger();
        this.chatEvents = new SteamChatEventList();

        receiveUserStatus(userinfo);
    }
    
    /**
     * Called when this tab receives focus.
     */
    void receiveFocus() {
        messageEntryField.requestFocusInWindow();
    }

    /**
     * Receives a chat message event from the parent SteamClientChatFrame
     * instance.
     *
     * @param entryType The type of chat message as specified by SteamKit-Java.
     * @param message The string message, if applicable.
     */
    void receiveMessage(EChatEntryType entryType,
            String message) {
        if (entryType == EChatEntryType.ChatMsg) {
            // If a chat message was received, show it in the window.
            addChatEvent(new ChatEventMessage(userinfo.getUsername(),
                    message));

            // Clear typing message once a message is received.
            userIsTypingTimer.stop();
        } else if (entryType == EChatEntryType.Typing) {
            // If the user is busy typing, reset the timer that clears typing.
            userIsTypingTimer.stop();
            userIsTypingTimer.start();
        }
        updateStatusLabel(userIsTypingTimer.isRunning());
        logger.debug("Message received.");
    }

    /**
     * Receives a user status update event from the parent SteamClientChatFrame
     * instance.
     *
     * @param status An updated user status.
     */
    final void receiveUserStatus(SteamClientMainForm.SteamFriendEntry status) {
        // Their persona state has changed.
        if (userinfo.getPersonaState() != status.getPersonaState()) {
            addChatEvent(new ChatEvent(String.format(LocalizationResources
                    .getString("ChatEvent.Fmt.StatusChange"),
                    status.getUsername(), status.renderFriendStatus())));
        }
        
        // TODO add event for in-game-ness.
        
        // Their screen name has changed.
        if (!userinfo.getUsername().equals(status.getUsername())) {
            addChatEvent(new ChatEvent(
                    String.format(LocalizationResources
                    .getString("ChatEvent.Fmt.NameChange"),
                    userinfo.getUsername(), status.getUsername())));
        }

        userinfo = status;
        userNameLabel.setText(userinfo.getUsername());

        // If status is changed while typing, reflect the change.
        updateStatusLabel(userIsTypingTimer.isRunning());
    }
    
    void closeTrade() {
        addChatEvent(new ChatEvent("The trade has been closed."));
    }

    /**
     * Adds a user event to the list of current chat events, updating the text
     * box.
     *
     * @param event The event to be added.
     */
    private void addChatEvent(ChatEvent event) {
        chatEvents.add(event);
        chatlogger.writeEvent(event);

        // Rebuild the textarea text.
        StringBuilder chatBuffer = new StringBuilder();
        for (ChatEvent e : chatEvents) {
            // TODO custom formatting of chatbox text.
            chatBuffer.append(e.toString()).append(NEWLINE);
        }
        chatTextArea.setText(chatBuffer.toString());
        logger.debug("Chat field updated.");
    }

    /**
     * Appends a message showing whether or the user is currently typing.
     *
     * @param isTyping Whether or not to display the typing notification.
     */
    private void updateStatusLabel(boolean isTyping) {
        String status = String.format(isTyping
                ? LocalizationResources.getString("ChatEvent.Fmt.Label.Typing")
                : "%s", userinfo.renderUserStatus());

        userStatusLabel.setText(status);
    }

    /**
     * Updates the trade button, pushing a chat state if the change in state
     * calls for it.
     *
     * @param state
     * @param tradeid
     */
    void updateTradeButton(TradeButtonState state, int tradeid) {
        this.state = state;
        this.tradeid = tradeid;

        tradeButton.setEnabled(state.enabled);
        tradeButton.setText(LocalizationResources.getString(
                "ChatTab.TradeButtonState." + state.name()));

        switch (state) {
            case RECEIVED_REQUEST:
                String msg = String.format(LocalizationResources
                        .getString("ChatEvent.Fmt.TradeRequest"),
                        userinfo.getUsername());
                addChatEvent(new ChatEvent(msg));
                break;
        }
    }

    /**
     * Called when the tab is closed to close the log file and do other
     * maintenance work.
     */
    void cleanup() {
        if (chatlogger.pw != null) {
            chatlogger.pw.close();
        }
        frame.sendMessage(chatter, EChatEntryType.LeftConversation, "");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        userNameLabel = new javax.swing.JLabel();
        userStatusLabel = new javax.swing.JLabel();
        tradeButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatTextArea = new javax.swing.JTextArea();
        messageEntryField = new javax.swing.JTextField();

        userNameLabel.setText("[unknown]");

        userStatusLabel.setText("[unknown status]");

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/nosoop/ministeam2/UIStrings"); // NOI18N
        tradeButton.setText(bundle.getString("ChatTab.TradeButtonState.IDLE")); // NOI18N
        tradeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradeButtonActionPerformed(evt);
            }
        });

        chatTextArea.setEditable(false);
        chatTextArea.setColumns(20);
        chatTextArea.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        chatTextArea.setLineWrap(true);
        chatTextArea.setRows(5);
        chatTextArea.setWrapStyleWord(true);
        jScrollPane1.setViewportView(chatTextArea);

        messageEntryField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                messageEntryFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                messageEntryFieldKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(userNameLabel)
                            .addComponent(userStatusLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tradeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(messageEntryField, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(tradeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(userNameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(userStatusLabel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(messageEntryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void messageEntryFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_messageEntryFieldKeyPressed
        int key = evt.getKeyCode();
        String inputText = messageEntryField.getText();

        /**
         * Sends a message to the other player if the ENTER key is hit and there
         * is a non-zero length trimmed message.
         */
        if (key == KeyEvent.VK_ENTER && inputText.trim().length() > 0) {
            // Send the message.
            frame.sendMessage(chatter, EChatEntryType.ChatMsg, inputText);

            // Copy message to own chat.
            addChatEvent(new ChatEventMessage(
                    frame.getOwnPersonaName(), inputText));

            messageEntryField.setText("");

            // Reset state of typing message thingy.
            lastTimeKeyPressed = 0;
        }
    }//GEN-LAST:event_messageEntryFieldKeyPressed

    private void messageEntryFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_messageEntryFieldKeyTyped
        /**
         * Notify the other user of our typing activity.
         */
        if (evt.isActionKey()) {
            return;
        }
        
        if (System.currentTimeMillis() - lastTimeKeyPressed
                > MILLISEC_INTERVAL_TYPING) {
            frame.sendMessage(chatter, EChatEntryType.Typing, "");
            lastTimeKeyPressed = System.currentTimeMillis();
        }
    }//GEN-LAST:event_messageEntryFieldKeyTyped

    private void tradeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tradeButtonActionPerformed
        switch (state) {
            case IDLE:
                // Invite the user to a trade.
                frame.tradeRequest.send(chatter);
                updateTradeButton(TradeButtonState.CANCEL_SENT_REQUEST,
                        frame.tradeRequest.TRADEID_INVALID);
                break;
            case RECEIVED_REQUEST:
                // Accept the trade if applicable.
                frame.tradeRequest.accept(tradeid);
                break;
            case CANCEL_SENT_REQUEST:
                // Cancel trade if we sent one.
                frame.tradeRequest.cancel(chatter);
                updateTradeButton(TradeButtonState.IDLE,
                        frame.tradeRequest.TRADEID_INVALID);
                break;
        }
    }//GEN-LAST:event_tradeButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea chatTextArea;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField messageEntryField;
    private javax.swing.JButton tradeButton;
    private javax.swing.JLabel userNameLabel;
    private javax.swing.JLabel userStatusLabel;
    // End of variables declaration//GEN-END:variables

    public enum TradeButtonState {
        IDLE(true), DISABLED_WHILE_IN_TRADE(false), RECEIVED_REQUEST(true),
        CANCEL_SENT_REQUEST(true), IN_TRADE(false);
        boolean enabled;

        private TradeButtonState(boolean enabled) {
            this.enabled = enabled;
        }
    }

    /**
     * Class that writes chat log files.
     */
    private class ChatLogger {
        FileWriter fw;
        PrintWriter pw;

        ChatLogger() {
            // figure out where to write log file
            fw = null;
            pw = null;
        }

        /**
         * Creates and opens a log file if it does not exist.
         */
        void createLogFile() {
            if (fw != null && pw != null) {
                return;
            }

            try {
                // YYYY-mm-dd-HHMMSS <name>.log
                String fileName = String.format(CHATLOG_FILENAME, new Date(),
                        userinfo.getUsername());

                // ./logs/<account name>/<shortsteamid>/<filename>
                String filePath = String.format(CHATLOG_FILEPATH,
                        frame.getOwnUsername(),
                        SteamIDUtil.convertReadable(chatter), fileName);

                logger.info("Creating log file at {}.", filePath);

                File logFile = new File(filePath);
                logFile.getParentFile().mkdirs();

                fw = new FileWriter(logFile, true);
                pw = new PrintWriter(fw, true);
            } catch (IOException e) {
                logger.error("Error creating log file", e);
            }
        }

        /**
         * Writes an event to a file.
         */
        void writeEvent(ChatEvent event) {
            createLogFile();

            String dateTime = String.format(DATE_TIME_FMT,
                    new Date(event.timestamp));

            // TODO Custom formatting of file-written events.

            pw.printf("%s %s%n", dateTime, event.toString());
        }
    }

    /**
     * Describes user/chat events (person has changed status/name, sent
     * trade...), timestamped at the time of creation.
     *
     * ChatEvent instances are instantiated when the SteamClientChatTab instance
     * receives an appropriate event from the parent SteamClientChatFrame.
     */
    private static class ChatEvent {
        String message;
        long timestamp;

        ChatEvent(String eventMessage) {
            this.message = eventMessage;
            this.timestamp = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return "* " + message;
        }
    }

    /**
     * Describes user message events (e.g. "Person: Hi!").
     */
    private static class ChatEventMessage extends ChatEvent {
        String username;

        ChatEventMessage(String username, String message) {
            super(message);
            this.username = username;
        }

        @Override
        public String toString() {
            return String.format(CHAT_MESSAGE_ENTRY_FMT, username, message);
        }
    }

    /**
     * Subclass of LinkedList that holds a set number of SteamChatEvent
     * instances.
     */
    private class SteamChatEventList extends LinkedList<ChatEvent> {
        SteamChatEventList() {
        }

        @Override
        public boolean add(ChatEvent e) {
            boolean added = super.add(e);

            while (size() > CHAT_EVENT_CAPACITY) {
                super.poll();
            }

            return added;
        }
    }

}
