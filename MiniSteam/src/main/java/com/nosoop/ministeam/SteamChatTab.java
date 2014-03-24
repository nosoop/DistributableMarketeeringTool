/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.ministeam;

import com.nosoop.ministeam.friends.FriendData;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.thomasc.steamkit.base.generated.steamlanguage.EChatEntryType;
import uk.co.thomasc.steamkit.types.steamid.SteamID;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class SteamChatTab extends javax.swing.JPanel {

    FrontendClient client;
    FriendData friend;
    
    long lastTimeKeyPressed = 0;
    
    private Timer userIsTypingTimer;
    private boolean userIsTyping;
    
    PrintWriter chatlog;
    
    static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
    
    final Logger LOGGER =
            LoggerFactory.getLogger(SteamChatTab.class.getSimpleName());
    

    /**
     * Creates new form ChatTab2
     */
    public SteamChatTab(FrontendClient client, FriendData friend) {
        this.chatlog = null;
        initComponents();
        this.messageTextPane.setEditable(false);

        this.client = client;
        updateFriendStatus(friend);
        
        userIsTyping = false;
        
        userIsTypingTimer = new Timer(15000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userIsTyping = false;
                updateFriendStatus();
            }
        });
        
        userIsTypingTimer.setRepeats(false);
    }
    
    public void notifyFriendTypingState() {
        if (userIsTypingTimer.isRunning()) {
            userIsTypingTimer.stop();
        }
        userIsTypingTimer.start();
        userIsTyping = true;
        updateFriendStatus();
    }
    
    private void updateFriendStatus() {
        String state = String.format("%s%s", friend.getStatus(),
                userIsTyping? " (Typing...)" : "");
        
        playerStatusLabel.setText(state);
    }
    
    public final void updateFriendStatus(FriendData friend) {
        this.friend = friend;

        playerNameLabel.setText(friend.getName());
        updateFriendStatus();
    }
    
    public void addMessage(boolean self, String text) {
        // If a chat log hasn't been created, make one.
        if (chatlog == null) {
            try {
                String logFolder = String.format(".\\log\\%d",
                        client.steamClient.getSteamId().convertToLong());
                
                // Generate filename, stripping out invalid characters.
                String filename = String.format("%s\\%s - %d - %s.txt",
                        logFolder,
                        friend.getSteamID().render().replaceAll(":", "_"),
                        System.currentTimeMillis(),
                        friend.getName().replaceAll("[\\\\/:\"*?<>|]+", "_"));
                
                if (!(new File(logFolder)).exists()) {
                    (new File(logFolder)).mkdirs();
                }
                
                (new File(filename)).createNewFile();
                
                // Create the PrintWriter for the chatlog.
                chatlog = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
            } catch (IOException e) {
                LOGGER.error("Error writing to log file", e);
            }
        }
        
        // Figure out if the message should be listed as self or the other.
        String name = self? client.getOwnSteamName() : friend.getName();
        String datetime = DATE_FMT.format(new Date(System.currentTimeMillis()));
        
        // Display message.
        messageTextPane.setText(String.format("%s%s: %s\n",
                    messageTextPane.getText(), name, text));
        
        // Write message to chatlog, flushing so it is viewable immediately.
        chatlog.println(String.format("[%s] %s: %s", datetime, name, text));
        chatlog.flush();
        
        // Scroll the chat down.
        jScrollPane1.scrollRectToVisible(
                new java.awt.Rectangle(0, messageTextPane.getBounds(null).height, 1, 1));
        
        // Remove the (Typing...) status when the other person has put a message.
        if (!self && userIsTyping) {
            userIsTyping = false;
            updateFriendStatus();
        }
    }
    
    void onRemoval() {
        // Close file handle to the chatlog when the tab is removed.
        chatlog.close();
    }
    
    @Override
    public void setVisible(boolean visible) {
        // Makes the text entry field take focus on tab change.
        super.setVisible(visible);
        messageEntryField.requestFocusInWindow();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        playerNameLabel = new javax.swing.JLabel();
        tradeButton = new javax.swing.JButton();
        playerStatusLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        messageTextPane = new javax.swing.JTextPane();
        messageEntryField = new javax.swing.JTextField();

        playerNameLabel.setText("[other player name]");

        tradeButton.setText("Send Trade Request");
        tradeButton.setFocusable(false);
        tradeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradeButtonActionPerformed(evt);
            }
        });

        playerStatusLabel.setText("[other player status]");

        messageTextPane.setFocusable(false);
        jScrollPane1.setViewportView(messageTextPane);

        messageEntryField.setFocusCycleRoot(true);
        messageEntryField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                messageEntryFieldKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(playerStatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                            .addComponent(playerNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tradeButton))
                    .addComponent(jScrollPane1)
                    .addComponent(messageEntryField))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tradeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(playerNameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(playerStatusLabel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(messageEntryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tradeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tradeButtonActionPerformed
        // TODO Change button action when receiving a trade.
        client.steamTrade.trade(friend.getSteamID());
    }//GEN-LAST:event_tradeButtonActionPerformed

    private void messageEntryFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_messageEntryFieldKeyPressed
        int key = evt.getKeyCode();

        String inputText = messageEntryField.getText();
        
        if (System.currentTimeMillis() - lastTimeKeyPressed > 5000) {
            client.steamFriends.sendChatMessage(friend.getSteamID(), EChatEntryType.Typing, "");
            lastTimeKeyPressed = System.currentTimeMillis();
        }

        if (key == java.awt.event.KeyEvent.VK_ENTER && inputText.length() > 0) {
            client.steamFriends.sendChatMessage(
                    new SteamID(friend.getSteamid64()), EChatEntryType.ChatMsg,
                    inputText);

            messageEntryField.setText("");

            addMessage(true, inputText);
            
            // Reset state of typing message thingy.
            lastTimeKeyPressed = 0;
        }
    }//GEN-LAST:event_messageEntryFieldKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField messageEntryField;
    private javax.swing.JTextPane messageTextPane;
    private javax.swing.JLabel playerNameLabel;
    private javax.swing.JLabel playerStatusLabel;
    private javax.swing.JButton tradeButton;
    // End of variables declaration//GEN-END:variables
}
