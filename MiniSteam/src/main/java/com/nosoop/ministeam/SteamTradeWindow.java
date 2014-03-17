/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.ministeam;

import com.nosoop.ministeam.trade.TradeDisplayItem;
import com.nosoop.ministeam.trade.TradeOurDisplayItem;
import com.nosoop.steamtrade.inventory.AppContextPair;
import java.awt.EventQueue;
import java.util.Collection;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import bundled.steamtrade.org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A window that allows the user to interact in a Steam trade.
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class SteamTradeWindow extends javax.swing.JFrame {

    FrontendTrade tradeListener;
    Logger logger;

    /**
     * Creates new form SteamTradeWindow
     */
    SteamTradeWindow(FrontendTrade trade, String otherPlayerName) {
        initComponents();
        this.logger = LoggerFactory.getLogger(SteamTradeWindow.class.getSimpleName());

        this.setLocationRelativeTo(null);

        this.tradeListener = trade;

        this.setTitle(String.format("Trading with %s", otherPlayerName));
        otherOfferPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(String.format("%s's Offer", otherPlayerName)));

        //this.yourInventoryTable.getRowSorter().toggleSortOrder(0);

        //this.yourOfferTable.getRowSorter().toggleSortOrder(0);
        //this.otherOfferTable.getRowSorter().toggleSortOrder(0);

        this.setVisible(true);
    }

    public void addMessage(String name, String text) {
        tradeChatArea.setText(String.format("%s%s: %s\n",
                tradeChatArea.getText(), name, text));

        tradeScroller.scrollRectToVisible(
                new java.awt.Rectangle(0, tradeChatArea.getBounds(null).height, 1, 1));
    }

    /**
     * Clears the current inventory table and replaces it with another
     * collection of TradeOurDisplayItem instances.
     *
     * @param items
     */
    public void setOwnInventoryTable(Collection<TradeOurDisplayItem> items) {
        javax.swing.JTable modifiedTable = yourInventoryTable;

        DefaultTableModel table = (DefaultTableModel) modifiedTable.getModel();

        for (int i = table.getRowCount() - 1; i >= 0; i--) {
            table.removeRow(i);
        }

        for (TradeDisplayItem item : items) {
            table.addRow(new Object[]{item, item.getCount()});
        }

        modifiedTable.setModel(table);
    }

    /**
     * Updates the client's or the other trader's item display.
     *
     * @param me Boolean value to determine if our offer is being updated.
     * @param items Collection of TradeDisplayItem instances to update the
     * table.
     */
    public void updateTradeCount(boolean me, Collection<TradeDisplayItem> items) {
        javax.swing.JTable modifiedTable = me ? yourOfferTable : otherOfferTable;

        DefaultTableModel table = (DefaultTableModel) modifiedTable.getModel();

        for (int i = table.getRowCount() - 1; i >= 0; i--) {
            table.removeRow(i);
        }

        for (TradeDisplayItem item : items) {
            if (item.getCount() > 0) {
                table.addRow(new Object[]{item, item.getCount()});
            }
        }

        modifiedTable.setModel(table);
    }

    /**
     * Loads a list of named AppContextPair objects into the inventory selection
     * dropdown. The dropdown is used to determine which inventory to load.
     *
     * @param appContexts A list of named AppContextPairs to be added to the
     * dropdown.
     */
    public void loadInventorySet(List<AppContextPair> appContexts) {
        final javax.swing.DefaultComboBoxModel model =
                (javax.swing.DefaultComboBoxModel) yourInventoriesComboBox.getModel();

        model.removeAllElements();

        for (AppContextPair inventory : appContexts) {
            model.addElement(inventory);
        }

        yourInventoriesComboBox.setEnabled(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        yourOfferPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        yourOfferTable = new javax.swing.JTable();
        yourOfferReadyCheckbox = new javax.swing.JCheckBox();
        otherOfferPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        otherOfferTable = new javax.swing.JTable();
        otherOfferReadyCheckbox = new javax.swing.JCheckBox();
        yourInventoryPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        yourInventoryTable = new javax.swing.JTable();
        yourInventoriesComboBox = new javax.swing.JComboBox();
        completeTradeButton = new java.awt.Button();
        cancelTradeButton = new java.awt.Button();
        tradeChatPanel = new javax.swing.JPanel();
        tradeChatInput = new javax.swing.JTextField();
        tradeScroller = new javax.swing.JScrollPane();
        tradeChatArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        yourOfferPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Your Offer"));

        yourOfferTable.setAutoCreateRowSorter(true);
        yourOfferTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item", "Quantity"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Integer.class
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
        yourOfferTable.setFillsViewportHeight(true);
        yourOfferTable.setShowVerticalLines(false);
        yourOfferTable.getTableHeader().setReorderingAllowed(false);
        yourOfferTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                yourOfferTableMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                yourOfferTableMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(yourOfferTable);
        yourOfferTable.getColumnModel().getColumn(1).setMaxWidth(64);

        yourOfferReadyCheckbox.setText("Ready to trade.");
        yourOfferReadyCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yourOfferReadyCheckboxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout yourOfferPanelLayout = new javax.swing.GroupLayout(yourOfferPanel);
        yourOfferPanel.setLayout(yourOfferPanelLayout);
        yourOfferPanelLayout.setHorizontalGroup(
            yourOfferPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(yourOfferPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(yourOfferPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(yourOfferReadyCheckbox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        yourOfferPanelLayout.setVerticalGroup(
            yourOfferPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(yourOfferPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(yourOfferReadyCheckbox)
                .addContainerGap())
        );

        otherOfferPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Other Person's Offer"));

        otherOfferTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item", "Quantity"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Integer.class
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
        otherOfferTable.setEnabled(false);
        otherOfferTable.setFillsViewportHeight(true);
        otherOfferTable.setShowVerticalLines(false);
        otherOfferTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(otherOfferTable);
        otherOfferTable.getColumnModel().getColumn(1).setMaxWidth(64);

        otherOfferReadyCheckbox.setText("Ready to trade");
        otherOfferReadyCheckbox.setEnabled(false);

        javax.swing.GroupLayout otherOfferPanelLayout = new javax.swing.GroupLayout(otherOfferPanel);
        otherOfferPanel.setLayout(otherOfferPanelLayout);
        otherOfferPanelLayout.setHorizontalGroup(
            otherOfferPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(otherOfferPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(otherOfferPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(otherOfferReadyCheckbox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        otherOfferPanelLayout.setVerticalGroup(
            otherOfferPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(otherOfferPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(otherOfferReadyCheckbox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        yourInventoryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Your Inventory"));

        yourInventoryTable.setAutoCreateRowSorter(true);
        yourInventoryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item", "Quantity"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Integer.class
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
        yourInventoryTable.setFillsViewportHeight(true);
        yourInventoryTable.setShowVerticalLines(false);
        yourInventoryTable.getTableHeader().setReorderingAllowed(false);
        yourInventoryTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                yourInventoryTableMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                yourInventoryTableMouseReleased(evt);
            }
        });
        jScrollPane3.setViewportView(yourInventoryTable);
        yourInventoryTable.getColumnModel().getColumn(1).setMaxWidth(64);

        yourInventoriesComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "[Scraping trade page for your inventories...]" }));
        yourInventoriesComboBox.setEnabled(false);
        yourInventoriesComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                yourInventoriesComboBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout yourInventoryPanelLayout = new javax.swing.GroupLayout(yourInventoryPanel);
        yourInventoryPanel.setLayout(yourInventoryPanelLayout);
        yourInventoryPanelLayout.setHorizontalGroup(
            yourInventoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(yourInventoryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(yourInventoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                    .addComponent(yourInventoriesComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        yourInventoryPanelLayout.setVerticalGroup(
            yourInventoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(yourInventoryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(yourInventoriesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        completeTradeButton.setEnabled(false);
        completeTradeButton.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        completeTradeButton.setLabel("Trade!");
        completeTradeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                completeTradeButtonActionPerformed(evt);
            }
        });

        cancelTradeButton.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        cancelTradeButton.setLabel("Cancel");
        cancelTradeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelTradeButtonActionPerformed(evt);
            }
        });

        tradeChatPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Trade Chat"));

        tradeChatInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tradeChatInputKeyPressed(evt);
            }
        });

        tradeChatArea.setEditable(false);
        tradeChatArea.setColumns(20);
        tradeChatArea.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        tradeChatArea.setLineWrap(true);
        tradeChatArea.setRows(5);
        tradeChatArea.setWrapStyleWord(true);
        tradeScroller.setViewportView(tradeChatArea);

        javax.swing.GroupLayout tradeChatPanelLayout = new javax.swing.GroupLayout(tradeChatPanel);
        tradeChatPanel.setLayout(tradeChatPanelLayout);
        tradeChatPanelLayout.setHorizontalGroup(
            tradeChatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tradeChatPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tradeChatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tradeChatInput, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tradeScroller, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE))
                .addContainerGap())
        );
        tradeChatPanelLayout.setVerticalGroup(
            tradeChatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tradeChatPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tradeScroller, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tradeChatInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(yourInventoryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tradeChatPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(completeTradeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cancelTradeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(otherOfferPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(yourOfferPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(yourInventoryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(yourOfferPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(otherOfferPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(completeTradeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cancelTradeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(tradeChatPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void completeTradeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_completeTradeButtonActionPerformed
        if (tradeListener.trade.getSelf().isReady()
                && tradeListener.trade.getPartner().isReady()) {
            completeTradeButton.setEnabled(false);
            completeTradeButton.setLabel("Processing...");

            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        tradeListener.trade.getCmds().acceptTrade();
                    } catch (JSONException ex) {
                        logger.error("Error on complete trade button", ex);
                    }
                }
            });
        }
    }//GEN-LAST:event_completeTradeButtonActionPerformed

    private void cancelTradeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelTradeButtonActionPerformed
        try {
            tradeListener.trade.getCmds().cancelTrade();
        } catch (JSONException ex) {
            logger.error("Error on pressing cancel trade button", ex);
        }
    }//GEN-LAST:event_cancelTradeButtonActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        try {
            tradeListener.trade.getCmds().cancelTrade();
        } catch (JSONException ex) {
            logger.error("Error on closing trade window", ex);
        }
    }//GEN-LAST:event_formWindowClosed

    private void yourOfferReadyCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yourOfferReadyCheckboxActionPerformed
        // Runnable that readies up.
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                tradeListener.trade.getCmds().setReady(yourOfferReadyCheckbox.isSelected());

                if (tradeListener.trade.getSelf().isReady()
                        && tradeListener.trade.getPartner().isReady()) {
                    completeTradeButton.setEnabled(true);
                }
            }
        });
    }//GEN-LAST:event_yourOfferReadyCheckboxActionPerformed

    private void yourInventoryTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_yourInventoryTableMouseClicked
        int targetRow = yourInventoryTable.getSelectedRow();

        /**
         * Left-click on your inventory item twice to add it.
         */
        boolean activated = (evt.getClickCount() == 2
                && evt.getButton() == java.awt.event.MouseEvent.BUTTON1);

        if (activated) {
            if (targetRow >= 0) {
                targetRow = yourInventoryTable.convertRowIndexToModel(targetRow);

                TradeOurDisplayItem item = (TradeOurDisplayItem) yourInventoryTable.getModel().getValueAt(targetRow, 0);
                tradeListener.tradePutFirstValidItem(item);
            }
        }
    }//GEN-LAST:event_yourInventoryTableMouseClicked

    private void yourOfferTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_yourOfferTableMouseClicked
        int targetRow = yourOfferTable.getSelectedRow();

        /**
         * Similarly, left-click on your offered item twice or right-click once
         * to remove it.
         */
        boolean activated = (evt.getClickCount() == 2
                && evt.getButton() == java.awt.event.MouseEvent.BUTTON1);

        if (activated) {
            if (targetRow >= 0) {
                targetRow = yourOfferTable.convertRowIndexToModel(targetRow);

                TradeDisplayItem item = (TradeDisplayItem) yourOfferTable.getModel().getValueAt(targetRow, 0);
                tradeListener.tradeRemoveFirstValidItem(item);
            }
        }
    }//GEN-LAST:event_yourOfferTableMouseClicked

    private void tradeChatInputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tradeChatInputKeyPressed
        int key = evt.getKeyCode();

        String inputText = tradeChatInput.getText();

        if (key == java.awt.event.KeyEvent.VK_ENTER && inputText.trim().length() > 0) {
            tradeListener.trade.getCmds().sendMessage(inputText);

            // TODO Add trade chat actions.
            addMessage("You", inputText);
            tradeChatInput.setText("");
        }
    }//GEN-LAST:event_tradeChatInputKeyPressed

    private void yourInventoryTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_yourInventoryTableMouseReleased
        int targetRow;
        targetRow = yourInventoryTable.rowAtPoint(evt.getPoint());

        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
            if (targetRow >= 0 && targetRow < yourInventoryTable.getRowCount()) {
                Object item;

                targetRow = yourInventoryTable.convertRowIndexToModel(targetRow);
                item = yourInventoryTable.getModel().getValueAt(targetRow, 0);

                if (item instanceof TradeOurDisplayItem) {
                    tradeListener.tradePutFirstValidItem(
                            (TradeOurDisplayItem) item);
                }
            }
        }
    }//GEN-LAST:event_yourInventoryTableMouseReleased

    private void yourOfferTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_yourOfferTableMouseReleased
        int targetRow;
        targetRow = yourInventoryTable.rowAtPoint(evt.getPoint());

        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
            if (targetRow >= 0 && targetRow < yourOfferTable.getRowCount()) {
                Object item;

                targetRow = yourOfferTable.convertRowIndexToModel(targetRow);
                item = yourOfferTable.getModel().getValueAt(targetRow, 0);

                if (item instanceof TradeDisplayItem) {
                    tradeListener.tradeRemoveFirstValidItem(
                            (TradeDisplayItem) item);
                }
            }
        }
    }//GEN-LAST:event_yourOfferTableMouseReleased

    private void yourInventoriesComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_yourInventoriesComboBoxItemStateChanged
        final Object item = yourInventoriesComboBox.getSelectedItem();

        if (item instanceof AppContextPair) {
            // Run inventory loading on the event queue to not stall the UI.
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    tradeListener.loadInventory((AppContextPair) item);
                }
            });
        }
    }//GEN-LAST:event_yourInventoriesComboBoxItemStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button cancelTradeButton;
    java.awt.Button completeTradeButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel otherOfferPanel;
    javax.swing.JCheckBox otherOfferReadyCheckbox;
    private javax.swing.JTable otherOfferTable;
    javax.swing.JTextArea tradeChatArea;
    javax.swing.JTextField tradeChatInput;
    private javax.swing.JPanel tradeChatPanel;
    private javax.swing.JScrollPane tradeScroller;
    private javax.swing.JComboBox yourInventoriesComboBox;
    private javax.swing.JPanel yourInventoryPanel;
    private javax.swing.JTable yourInventoryTable;
    private javax.swing.JPanel yourOfferPanel;
    private javax.swing.JCheckBox yourOfferReadyCheckbox;
    private javax.swing.JTable yourOfferTable;
    // End of variables declaration//GEN-END:variables
}
