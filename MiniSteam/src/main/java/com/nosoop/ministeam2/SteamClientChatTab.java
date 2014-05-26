package com.nosoop.ministeam2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private static final int MSEC_INTERVAL_TYPING = 5000;
    /**
     * The last time a key was pressed in the current chat.
     */
    long lastTimeKeyPressed = 0;
    /**
     * The SteamID of the person we are chatting with.
     */
    SteamID chatter;
    /**
     * The parent frame of this tab.
     */
    SteamClientChatFrame frame;
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
    SteamClientMainForm.SteamFriendEntry userinfo;
    /**
     * Trade button status, to determine what action should be done when
     * clicked..
     */
    TradeButtonState state;
    int tradeid;
    /**
     * Chat logging.
     */
    ChatLogger chatlogger;
    /**
     * The event listing.
     */
    SteamChatEventList chatEvents;

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

        updateUserStatus(userinfo);
    }

    void receiveMessage(EChatEntryType entryType,
            String message) {
        if (entryType == EChatEntryType.ChatMsg) {
            // If a chat message was received, show it in the window.
            addChatEvent(new ChatEventMessage(userinfo.username,
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

    void addChatEvent(ChatEvent event) {
        chatEvents.add(event);
        chatlogger.writeEvent(event);

        StringBuilder chatBuffer = new StringBuilder();
        for (ChatEvent e : chatEvents) {
            chatBuffer.append(e.toString())
                    .append(String.format("%n", new Object[0]));
        }
        chatTextArea.setText(chatBuffer.toString());
        logger.debug("Chat field updated.");
    }

    /**
     * Appends a message showing whether or the user is currently typing.
     *
     * @param isTyping Whether or not to display the typing notification.
     */
    void updateStatusLabel(boolean isTyping) {
        String status = String.format(
                isTyping ? "%s (Typing...)" : "%s", userinfo.renderStatus());

        userStatusLabel.setText(status);
    }

    final void updateUserStatus(SteamClientMainForm.SteamFriendEntry status) {
        if (userinfo.state != status.state) {
            addChatEvent(new ChatEvent(String.format("%s is now %s.",
                    status.username, status.state.name())));
        }
        if (!userinfo.username.equals(status.username)) {
            addChatEvent(new ChatEvent(
                    String.format("%s changed their name to %s.",
                    userinfo.username, status.username)));
        }

        userinfo = status;
        userNameLabel.setText(userinfo.username);

        // If status is changed while typing, reflect the change.
        updateStatusLabel(userIsTypingTimer.isRunning());
    }

    void updateTradeButton(TradeButtonState state, int tradeid) {
        this.state = state;
        this.tradeid = tradeid;
        switch (state) {
            case IDLE:
                tradeButton.setEnabled(true);
                tradeButton.setText("Send Trade Request");
                break;
            case IN_TRADE:
                tradeButton.setEnabled(false);
                tradeButton.setText("In Trade");
                break;
            case DISABLED_WHILE_IN_TRADE:
                tradeButton.setEnabled(false);
                tradeButton.setText("Already Trading");
                break;
            case RECEIVED_REQUEST:
                tradeButton.setEnabled(true);
                tradeButton.setText("Accept Trade");
                break;
            case SENT_REQUEST:
                tradeButton.setEnabled(true);
                tradeButton.setText("Cancel Request");
                break;
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

        userNameLabel = new javax.swing.JLabel();
        userStatusLabel = new javax.swing.JLabel();
        tradeButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatTextArea = new javax.swing.JTextArea();
        messageEntryField = new javax.swing.JTextField();

        userNameLabel.setText("[unknown]");

        userStatusLabel.setText("[unknown status]");

        tradeButton.setText("Send Trade Request");
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
        if (key == java.awt.event.KeyEvent.VK_ENTER
                && inputText.trim().length() > 0) {
            // Send the message.
            frame.onSendingMessage(chatter, EChatEntryType.ChatMsg, inputText);

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
        if (System.currentTimeMillis() - lastTimeKeyPressed
                > MSEC_INTERVAL_TYPING) {
            frame.onSendingMessage(chatter, EChatEntryType.Typing, "");
            lastTimeKeyPressed = System.currentTimeMillis();
        }
    }//GEN-LAST:event_messageEntryFieldKeyTyped

    private void tradeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tradeButtonActionPerformed
        switch (state) {
            case IDLE:
                // Invite the user to a trade.
                frame.tradeRequest.send(chatter);
                updateTradeButton(TradeButtonState.SENT_REQUEST,
                        frame.tradeRequest.TRADEID_INVALID);
                break;
            case RECEIVED_REQUEST:
                // Accept the trade if applicable.
                frame.tradeRequest.accept(tradeid);
                break;
            case SENT_REQUEST:
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
        IDLE, DISABLED_WHILE_IN_TRADE, RECEIVED_REQUEST, SENT_REQUEST,
        IN_TRADE;
    }

    /**
     * Class that writes chat log files.
     */
    private class ChatLogger {
        FileWriter fw;
        PrintWriter pw;

        public ChatLogger() {
            // figure out where to write log file
            fw = null;
            pw = null;
        }

        void createLogFile() {
            if (fw != null && pw != null) {
                return;
            }

            try {
                String fileName = String.format(
                        CHATLOG_FILENAME, new Date(),
                        userinfo.username);

                String filePath = String.format(CHATLOG_FILEPATH,
                        frame.getOwnUsername(), chatter.convertToLong(),
                        fileName);

                logger.info("Creating log file at {}.", filePath);

                File logFile = new File(filePath);
                logFile.getParentFile().mkdirs();

                fw = new FileWriter(logFile, true);
                pw = new PrintWriter(fw, true);
            } catch (IOException e) {
                logger.error("Error creating log file", e);
            }
        }

        public void writeEvent(ChatEvent event) {
            createLogFile();

            String dateTime = String.format(DATE_TIME_FMT, 
                    new Date(event.timestamp));
            
            pw.printf("%s %s%n", dateTime, event.toString());
        }
    }

    /**
     * Describes user/chat events (person has changed status/name, sent
     * trade...), timestamped at the time of creation.
     */
    static class ChatEvent {
        String message;
        long timestamp;

        public ChatEvent(String eventMessage) {
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
    static class ChatEventMessage extends ChatEvent {
        String username;

        public ChatEventMessage(String username, String message) {
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
    static class SteamChatEventList extends LinkedList<ChatEvent> {
        int capacity;

        public SteamChatEventList() {
            capacity = 100;
        }

        @Override
        public boolean add(ChatEvent e) {
            boolean added = super.add(e);

            while (size() > capacity) {
                super.poll();
            }

            return added;
        }
    }

}
