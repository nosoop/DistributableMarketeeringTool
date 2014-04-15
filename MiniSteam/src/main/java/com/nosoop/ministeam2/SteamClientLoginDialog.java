/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.ministeam2;

import com.nosoop.inputdialog.CallbackInputFrame;
import com.nosoop.ministeam.BuildProperties;
import com.nosoop.ministeam2.SteamClientMainForm.SteamClientInfo;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class SteamClientLoginDialog extends CallbackInputFrame<SteamClientInfo> {
    
    /**
     * Creates new form SteamClientLoginDialog.
     */
    public SteamClientLoginDialog(Callback<SteamClientInfo> callback) {
        super(callback);
        initComponents();

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                // Close.
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        projectNameLabel = new javax.swing.JLabel();
        versionNumberLabel = new javax.swing.JLabel();
        accountUserLabel = new javax.swing.JLabel();
        accountPasswordLabel = new javax.swing.JLabel();
        accountPasswordField = new javax.swing.JPasswordField();
        loginButton = new javax.swing.JButton();
        quitButton = new javax.swing.JButton();
        rememberLoginCheckbox = new javax.swing.JCheckBox();
        loginStatusLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        accountUserField = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("DMT");
        setMinimumSize(new java.awt.Dimension(382, 255));
        setResizable(false);

        projectNameLabel.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        projectNameLabel.setText("Distributable Marketeering Tool");

        versionNumberLabel.setText("Version " + BuildProperties.getBuildVersion() + ", build " + BuildProperties.getBuildTime());

        accountUserLabel.setText("Steam Username:");

        accountPasswordLabel.setText("Steam Password:");

        loginButton.setText("Login");
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        quitButton.setText("Cancel");
        quitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitButtonActionPerformed(evt);
            }
        });

        rememberLoginCheckbox.setText("Remember login details");

        loginStatusLabel.setText("Status: Waiting to sign in...");

        jLabel2.setText("(Powered by SteamKit-Java and SteamTrade-Java code)");

        accountUserField.setEditable(true);
        accountUserField.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                accountUserFieldItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(loginStatusLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(accountUserLabel)
                            .addComponent(accountPasswordLabel))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(accountPasswordField)
                            .addComponent(accountUserField, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(loginButton, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(quitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(rememberLoginCheckbox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(projectNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                    .addComponent(versionNumberLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(projectNameLabel)
                .addGap(8, 8, 8)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(versionNumberLabel)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(accountUserLabel)
                            .addComponent(accountUserField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(25, 25, 25))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(accountPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(accountPasswordLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addComponent(rememberLoginCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loginButton)
                    .addComponent(quitButton))
                .addGap(18, 18, 18)
                .addComponent(loginStatusLabel)
                .addGap(6, 6, 6))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        SteamClientInfo clientInfo;
        
        clientInfo = new SteamClientInfo();
        clientInfo.username = accountUserField.getEditor().getItem().toString();
        clientInfo.password = String.valueOf(accountPasswordField.getPassword());
        
        callback.run(clientInfo);
    }//GEN-LAST:event_loginButtonActionPerformed

    private void accountUserFieldItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_accountUserFieldItemStateChanged
    }//GEN-LAST:event_accountUserFieldItemStateChanged

    private void quitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitButtonActionPerformed
    }//GEN-LAST:event_quitButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPasswordField accountPasswordField;
    private javax.swing.JLabel accountPasswordLabel;
    private javax.swing.JComboBox accountUserField;
    private javax.swing.JLabel accountUserLabel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton loginButton;
    private javax.swing.JLabel loginStatusLabel;
    private javax.swing.JLabel projectNameLabel;
    private javax.swing.JButton quitButton;
    private javax.swing.JCheckBox rememberLoginCheckbox;
    private javax.swing.JLabel versionNumberLabel;
    // End of variables declaration//GEN-END:variables
}