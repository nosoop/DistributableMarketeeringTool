/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.ministeam;

import com.nosoop.ministeam.friends.FriendData;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import static uk.co.thomasc.steamkit.base.generated.steamlanguage.EChatEntryType.ChatMsg;
import static uk.co.thomasc.steamkit.base.generated.steamlanguage.EChatEntryType.Typing;
import uk.co.thomasc.steamkit.base.generated.steamlanguage.EFriendRelationship;
import uk.co.thomasc.steamkit.base.generated.steamlanguage.EPersonaState;
import uk.co.thomasc.steamkit.steam3.handlers.steamfriends.callbacks.FriendMsgCallback;
import uk.co.thomasc.steamkit.types.steamid.SteamID;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class SteamMainWindow extends javax.swing.JFrame {

    SteamChatWindow chatWindow = null;
    FrontendClient client;
    FriendData selectedFriend;
    
    Map<Long, FriendData> friendStatus = new HashMap<>();

    /**
     * Creates new form FriendsFrame
     */
    public SteamMainWindow(FrontendClient client) {
        initComponents();
        this.setLocationRelativeTo(null);
        
        chatWindow = new SteamChatWindow(client);

        this.client = client;
    }

    /**
     * Create methods to push data changes as necessary.
     */
    public void setPlayerName(String playerName) {
        playerNameLabel.setText(playerName);
    }

    public TableModel getFriendTableModel() {
        return friendTable.getModel();
    }

    public void setFriendTableModel(TableModel t) {
        friendTable.setModel(t);
    }
    
    /**
     * Changes the state of a friend.
     *
     * @param id
     * @param state
     */
    void updateFriendStatus(SteamID id) {
        if (id.isIndividualAccount()) {
            long sid = id.convertToLong();

            if (friendStatus.containsKey(sid)) {
                friendStatus.remove(sid);
            }

            String friendState;

            if (client.steamFriends.getFriendRelationship(id) != EFriendRelationship.Friend) {
                friendState = client.steamFriends.getFriendRelationship(id).name();
            } else {
                friendState = client.steamFriends.getFriendPersonaState(id).name();
            }

            // If they have nothing to do with you, ignore it.
            if (client.steamFriends.getFriendRelationship(id) != EFriendRelationship.None) {
                friendStatus.put(sid, new FriendData(id, client.steamFriends.getFriendPersonaName(id), friendState));
            }

            updateFriendsList();
        }
    }
    
    /**
     * Reloads the friend table.
     */
    synchronized void updateFriendsList() {

        DefaultTableModel t = (DefaultTableModel) this.getFriendTableModel();

        HashMap<Long, FriendData> duped = new HashMap(friendStatus);

        for (int r = 0; r < t.getRowCount(); r++) {
            FriendData of = (FriendData) t.getValueAt(r, 0);

            if (duped.containsKey(of.getSteamid64())) {
                FriendData nf = duped.get(of.getSteamid64());

                if (nf.getName().equals(of.getName()) && nf.getStatus().equals(of.getStatus())) {
                    // Well, nothing to do here if everything's the same.
                } else {
                    // Otherwise, replace the values.
                    t.setValueAt(nf, r, 0);
                    t.setValueAt(nf.getStatus(), r, 1);
                }

                // Well, we know this is in the list, so take it out.
                duped.remove(of.getSteamid64());
            }
        }

        // These are all the new entries waiting to be added.
        for (Long l : duped.keySet()) {
            FriendData f = duped.get(l);
            t.addRow(new Object[]{
                f, f.getStatus()
            });
        }

        // Trim off all the listings that were removed.
        for (int r = t.getRowCount() - 1; r >= 0; r--) {
            FriendData of = (FriendData) t.getValueAt(r, 0);

            if (!friendStatus.containsKey(of.getSteamid64())) {
                t.removeRow(r);
            }
        }

        setFriendTableModel(t);

        chatWindow.pushFriendStatuses(friendStatus.values());
    }
    
    public void receiveChatMessage(FriendMsgCallback callback) {
        FriendData friend = friendStatus.get(callback.getSender().convertToLong());
        switch (callback.getEntryType()) {
            case ChatMsg:
                chatWindow.pushChatMessage(
                        friend, callback.getMessage());
                break;
            case Typing:
                chatWindow.pushTypingState(friend);
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

        friendMenu = new javax.swing.JPopupMenu();
        sendMessageOption = new javax.swing.JMenuItem();
        sendTradeOption = new javax.swing.JMenuItem();
        removeFriendOption = new javax.swing.JMenuItem();
        pendingInviteMenu = new javax.swing.JPopupMenu();
        acceptInviteOption = new javax.swing.JMenuItem();
        ignoreInviteOption = new javax.swing.JMenuItem();
        personalMenu = new javax.swing.JPopupMenu();
        setNameOption = new javax.swing.JMenuItem();
        playerNameLabel = new javax.swing.JLabel();
        playerStatusBox = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        friendTable = new javax.swing.JTable();

        friendMenu.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                friendMenuPopupMenuWillBecomeVisible(evt);
            }
        });

        sendMessageOption.setText("Send Message");
        sendMessageOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendMessageOptionActionPerformed(evt);
            }
        });
        friendMenu.add(sendMessageOption);

        sendTradeOption.setText("Invite to Trade");
        sendTradeOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendTradeOptionActionPerformed(evt);
            }
        });
        friendMenu.add(sendTradeOption);

        removeFriendOption.setText("Remove Friend");
        removeFriendOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFriendOptionActionPerformed(evt);
            }
        });
        friendMenu.add(removeFriendOption);

        acceptInviteOption.setText("Accept Friend Invite");
        acceptInviteOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceptInviteOptionActionPerformed(evt);
            }
        });
        pendingInviteMenu.add(acceptInviteOption);

        ignoreInviteOption.setText("Ignore Friend Invite");
        ignoreInviteOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ignoreInviteOptionActionPerformed(evt);
            }
        });
        pendingInviteMenu.add(ignoreInviteOption);

        setNameOption.setText("Change Name...");
        setNameOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setNameOptionActionPerformed(evt);
            }
        });
        personalMenu.add(setNameOption);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Distributable Marketeering Tool");

        playerNameLabel.setText("$player_name");

        playerStatusBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Online", "Busy", "Away", "Snooze", "Looking to Play", "Looking to Trade", "Offline" }));
        playerStatusBox.setFocusable(false);
        playerStatusBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playerStatusBoxActionPerformed(evt);
            }
        });

        friendTable.setAutoCreateRowSorter(true);
        friendTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        friendTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        friendTable.setFillsViewportHeight(true);
        friendTable.setFocusable(false);
        friendTable.setShowHorizontalLines(false);
        friendTable.setShowVerticalLines(false);
        friendTable.getTableHeader().setReorderingAllowed(false);
        friendTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                friendTableMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                friendTableMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(friendTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(playerNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(playerStatusBox, 0, 251, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(playerNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(playerStatusBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void playerStatusBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playerStatusBoxActionPerformed
        // TODO Change hardcoded status indices to dynamic ones?
        switch (playerStatusBox.getSelectedIndex()) {
            case 0:
                client.steamFriends.setPersonaState(EPersonaState.Online);
                break;
            case 1:
                client.steamFriends.setPersonaState(EPersonaState.Busy);
                break;
            case 2:
                client.steamFriends.setPersonaState(EPersonaState.Away);
                break;
            case 3:
                client.steamFriends.setPersonaState(EPersonaState.Snooze);
                break;
            case 4:
                client.steamFriends.setPersonaState(EPersonaState.LookingToPlay);
                break;
            case 5:
                client.steamFriends.setPersonaState(EPersonaState.LookingToTrade);
                break;
            case 6:
                client.steamFriends.setPersonaState(EPersonaState.Offline);
                break;
            default:
                break;
        }
    }//GEN-LAST:event_playerStatusBoxActionPerformed

    private void friendTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_friendTableMouseClicked
        int targetRow = ((javax.swing.JTable) evt.getSource()).getSelectedRow();

        if (evt.getClickCount() == 2
                && evt.getButton() == MouseEvent.BUTTON1) {

            if (targetRow >= 0) {
                targetRow = ((javax.swing.JTable) evt.getSource()).convertRowIndexToModel(targetRow);
                
                FriendData user = (FriendData) ((javax.swing.JTable) evt.getSource()).getModel().getValueAt(targetRow, 0);
                
                chatWindow.addNewChat(user);
                chatWindow.setVisible(true);
            }
        }
    }//GEN-LAST:event_friendTableMouseClicked

    private void sendMessageOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendMessageOptionActionPerformed
        chatWindow.addNewChat(selectedFriend);
        chatWindow.setVisible(true);
        selectedFriend = null;
    }//GEN-LAST:event_sendMessageOptionActionPerformed

    private void friendTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_friendTableMouseReleased
        int r = friendTable.rowAtPoint(evt.getPoint());
        if (r >= 0 && r < friendTable.getRowCount()) {
            friendTable.setRowSelectionInterval(r, r);
        } else {
            friendTable.clearSelection();
        }

        if (evt.getButton() == MouseEvent.BUTTON3) {
            if (r >= 0) {
                selectedFriend = (FriendData) friendTable.getValueAt(r, 0);
                
                EFriendRelationship relationship = client.steamFriends.getFriendRelationship(selectedFriend.getSteamID());
                
                switch (relationship) {
                    case Friend:
                        friendMenu.show(friendTable, evt.getX(), evt.getY());
                        break;
                    case RequestRecipient:
                        pendingInviteMenu.show(friendTable, evt.getX(), evt.getY());
                        break;
                }
            } else {
                // Not a valid entry.  Show default menu?
            }
        }
    }//GEN-LAST:event_friendTableMouseReleased

    private void sendTradeOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendTradeOptionActionPerformed
        if (client.isReadyToTrade) {
            client.steamTrade.trade(selectedFriend.getSteamID());
        } else {
            // TODO Notify client that we are not ready to trade yet.
        }
        selectedFriend = null;
    }//GEN-LAST:event_sendTradeOptionActionPerformed

    private void acceptInviteOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acceptInviteOptionActionPerformed
        client.steamFriends.addFriend(selectedFriend.getSteamID());
        selectedFriend = null;
    }//GEN-LAST:event_acceptInviteOptionActionPerformed

    private void ignoreInviteOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ignoreInviteOptionActionPerformed
        // TODO Implement ignore.
    }//GEN-LAST:event_ignoreInviteOptionActionPerformed

    private void removeFriendOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFriendOptionActionPerformed
        client.steamFriends.removeFriend(selectedFriend.getSteamID());
        selectedFriend = null;
    }//GEN-LAST:event_removeFriendOptionActionPerformed

    private void friendMenuPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_friendMenuPopupMenuWillBecomeVisible
        if (!client.isReadyToTrade) {
            sendTradeOption.setEnabled(false);
        } else {
            sendTradeOption.setEnabled(true);
        }
    }//GEN-LAST:event_friendMenuPopupMenuWillBecomeVisible

    private void setNameOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setNameOptionActionPerformed
        // TODO Add handling code for setName.
    }//GEN-LAST:event_setNameOptionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem acceptInviteOption;
    private javax.swing.JPopupMenu friendMenu;
    private javax.swing.JTable friendTable;
    private javax.swing.JMenuItem ignoreInviteOption;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu pendingInviteMenu;
    private javax.swing.JPopupMenu personalMenu;
    private javax.swing.JLabel playerNameLabel;
    private javax.swing.JComboBox playerStatusBox;
    private javax.swing.JMenuItem removeFriendOption;
    private javax.swing.JMenuItem sendMessageOption;
    private javax.swing.JMenuItem sendTradeOption;
    private javax.swing.JMenuItem setNameOption;
    // End of variables declaration//GEN-END:variables
}
