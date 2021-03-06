package com.nosoop.ministeam2.prefs;

import com.nosoop.ministeam2.SteamClientChatTab;
import java.awt.EventQueue;
import java.util.IllegalFormatException;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class SettingsDialog extends javax.swing.JDialog {
    /**
     * Creates new form SteamClientPreferences
     */
    public SettingsDialog(java.awt.Frame parent) {
        super(parent, false);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        activityTab = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        chatLogTab = new javax.swing.JPanel();
        messageFmtLabel = new javax.swing.JLabel();
        messageFmtField = new javax.swing.JTextField();
        dateFormatLabel = new javax.swing.JLabel();
        dateFormatField = new javax.swing.JTextField();
        previewLabel = new javax.swing.JLabel();
        previewField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        cancelButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/nosoop/ministeam2/UIStrings"); // NOI18N
        setTitle(bundle.getString("ClientMenu.Settings")); // NOI18N

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        jLabel1.setText("This space accidentally left blank.");

        javax.swing.GroupLayout activityTabLayout = new javax.swing.GroupLayout(activityTab);
        activityTab.setLayout(activityTabLayout);
        activityTabLayout.setHorizontalGroup(
            activityTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(activityTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(142, Short.MAX_VALUE))
        );
        activityTabLayout.setVerticalGroup(
            activityTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(activityTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(240, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("User Status", activityTab);

        messageFmtLabel.setLabelFor(messageFmtField);
        messageFmtLabel.setText("Message format:");

        messageFmtField.setText(com.nosoop.ministeam2.prefs.ClientSettings.getInstance().chatLogEntryFormat);
        messageFmtField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                messageFmtFieldUpdated(e);
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                messageFmtFieldUpdated(e);
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                messageFmtFieldUpdated(e);
            }
        });
        messageFmtField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                messageFmtFieldActionPerformed(evt);
            }
        });

        dateFormatLabel.setLabelFor(dateFormatField);
        dateFormatLabel.setText("Date format:");

        dateFormatField.setText(com.nosoop.ministeam2.prefs.ClientSettings.getInstance().dateTimeFormat);

        previewLabel.setText("Preview:");

        previewField.setEditable(false);
        previewField.setText(SteamClientChatTab.mockFormatEvent(messageFmtField.getText()));

        javax.swing.GroupLayout chatLogTabLayout = new javax.swing.GroupLayout(chatLogTab);
        chatLogTab.setLayout(chatLogTabLayout);
        chatLogTabLayout.setHorizontalGroup(
            chatLogTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chatLogTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(chatLogTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(messageFmtField)
                    .addComponent(dateFormatField, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dateFormatLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(messageFmtLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                    .addComponent(previewField)
                    .addComponent(previewLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1))
                .addContainerGap())
        );
        chatLogTabLayout.setVerticalGroup(
            chatLogTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chatLogTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(previewLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(previewField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateFormatLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateFormatField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(messageFmtLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(messageFmtField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(121, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Chat Logging", chatLogTab);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        saveButton.setText("OK");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(saveButton)
                .addGap(15, 15, 15)
                .addComponent(cancelButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void messageFmtFieldUpdated(javax.swing.event.DocumentEvent evt) {
        try {
            previewField.setText(SteamClientChatTab
                    .mockFormatEvent(messageFmtField.getText()));
        } catch (IllegalFormatException e) {
            // Drop.
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        // TODO Figure out how to validate without catching exceptions.
        try {
            String.format(messageFmtField.getText());
            ClientSettings.getInstance().chatLogEntryFormat =
                    messageFmtField.getText();
        } catch (IllegalFormatException e) {
            // Do not save.
        }

        try {
            String.format(dateFormatField.getText());
            ClientSettings.getInstance().dateTimeFormat =
                    dateFormatField.getText();
        } catch (IllegalFormatException e) {
            // Do not save.
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                dispose();
            }
        });
    }//GEN-LAST:event_saveButtonActionPerformed

    private void messageFmtFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_messageFmtFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_messageFmtFieldActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                dispose();
            }
        });
    }//GEN-LAST:event_cancelButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel activityTab;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel chatLogTab;
    private javax.swing.JTextField dateFormatField;
    private javax.swing.JLabel dateFormatLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField messageFmtField;
    private javax.swing.JLabel messageFmtLabel;
    private javax.swing.JTextField previewField;
    private javax.swing.JLabel previewLabel;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables
}
