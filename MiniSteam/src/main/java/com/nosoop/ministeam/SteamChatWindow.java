/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.ministeam;

import com.nosoop.ministeam.friends.FriendData;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class SteamChatWindow extends javax.swing.JFrame {

    int currentTabIndex = -1;
    FrontendClient client;
    Map<FriendData, SteamChatTab> activeFriendChats;
    Logger logger;
    
    /**
     * Creates new form ChatWindow
     */
    public SteamChatWindow(FrontendClient client) {
        initComponents();
        this.setLocationRelativeTo(null);
        
        this.logger = LoggerFactory.getLogger(SteamChatWindow.class.getSimpleName());
        
        this.client = client;
        
        activeFriendChats = new HashMap<>();
    }

    public javax.swing.JTabbedPane getChatTabPanel() {
        return chatTabPanel;
    }
    
    /**
     * Opens or brings a user's chat tab into focus.
     * @param friend 
     */
    public SteamChatTab addNewChat(FriendData friend) {
        if (!activeFriendChats.containsKey(friend)) {
            SteamChatTab sc = new SteamChatTab(client, friend);
            activeFriendChats.put(friend, sc);
            
            chatTabPanel.addTab(friend.getName(), 
                    activeFriendChats.get(friend));
            
            return sc;
        } else {
            // I have no idea what I did here.
            for (Component c : chatTabPanel.getComponents()) {
                if (c instanceof SteamChatTab) {
                    SteamChatTab s = (SteamChatTab) c;

                    if (s.friend == friend) {
                        chatTabPanel.setSelectedComponent(c);
                        break;
                    }

                    return s;
                }
            }
        }
        // I sure hope I never see this.
        return null;
    }
    
    public void pushFriendStatuses(Collection<FriendData> friends) {
        for (FriendData friend : friends) {
            if (activeFriendChats.containsKey(friend)) {
                activeFriendChats.get(friend).updateFriendStatus(friend);
            }
        }
    }
    
    public void pushChatMessage(FriendData friend, String message) {
        if (activeFriendChats.containsKey(friend)) {
            activeFriendChats.get(friend).addMessage(false, message);
        } else {
            addNewChat(friend).addMessage(false, message);
        }
        this.setVisible(true);
        
        // TODO Figure out a better way to notify the user of a message.
        //if (!this.isActive()) {
        //    this.toFront();
        //}
    }
    
    public void pushTypingState(FriendData friend) {
        // TODO Add an option to open a window as they are typing?
        if (activeFriendChats.containsKey(friend)) {
            activeFriendChats.get(friend).notifyFriendTypingState();
        }
    }
    
    void setChatWindowTitle(boolean tabClosing) {
        int newTabIndex = chatTabPanel.getSelectedIndex();
        
        if (tabClosing) {
            logger.debug("New tab: {}, total tabs {}.", newTabIndex, chatTabPanel.getTabCount());
            
            newTabIndex = (newTabIndex == chatTabPanel.getTabCount()-1) ?
                    newTabIndex : newTabIndex + 1;
        }
        
        if (currentTabIndex != newTabIndex && newTabIndex != -1) {
            this.setTitle(chatTabPanel.getTitleAt(newTabIndex));
        }
        currentTabIndex = newTabIndex;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chatTabPanel = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(400, 300));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        chatTabPanel.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        chatTabPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                chatTabPanelMouseReleased(evt);
            }
        });
        chatTabPanel.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chatTabPanelStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chatTabPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chatTabPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chatTabPanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chatTabPanelMouseReleased
        // Remove the tab.
        // TODO Make it not close tab if mouse is not actually on the tab.
        Component tab = chatTabPanel.getSelectedComponent();
        
        if (tab != null && evt.getButton() == MouseEvent.BUTTON2) {
            chatTabPanel.remove(tab);
            ((SteamChatTab) tab).onRemoval();
            activeFriendChats.remove(((SteamChatTab) tab).friend);
            setChatWindowTitle(true);
        }
        
        if (chatTabPanel.getTabCount() == 0) {
            this.setVisible(false);
        }
    }//GEN-LAST:event_chatTabPanelMouseReleased

    private void chatTabPanelStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chatTabPanelStateChanged
        setChatWindowTitle(false);
    }//GEN-LAST:event_chatTabPanelStateChanged

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // If the window is closed, remove ALL the chats.
        activeFriendChats.clear();
        chatTabPanel.removeAll();
    }//GEN-LAST:event_formWindowClosed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane chatTabPanel;
    // End of variables declaration//GEN-END:variables
}