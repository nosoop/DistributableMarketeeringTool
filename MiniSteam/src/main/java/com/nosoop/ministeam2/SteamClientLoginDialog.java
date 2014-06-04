package com.nosoop.ministeam2;

import com.nosoop.ministeam2.util.LocalizationResources;
import com.nosoop.ministeam2.util.BuildProperties;
import bundled.steamtrade.org.json.*;
import com.nosoop.inputdialog.CallbackInputFrame;
import com.nosoop.ministeam2.SteamClientMainForm.SteamClientInfo;
import com.nosoop.ministeam2.util.XorStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.DefaultComboBoxModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A dialog window, prompting the user to sign in.
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class SteamClientLoginDialog extends CallbackInputFrame<SteamClientInfo> {
    // The projects that power the application.
    static final String[] POWERED_BY = {"SteamKit-Java", "SteamTrade-Java"};
    // Filename format for saved SSFN data.
    static final String SENTRY_FILENAME_FMT = "sentry_%s.bin";
    /**
     * Stores users as loaded from a file.
     */
    AccountStorage accounts;
    /**
     * Current login token.
     */
    String machineAuthCookie;
    String loginToken;

    /**
     * Package-private enum to communicate signing-in status.
     */
    enum ClientConnectivityState {
        CONNECTING(DialogActivityMode.LOGIN_BUTTON_BLOCK),
        SIGN_IN_WAITING(DialogActivityMode.ALL_FIELDS_ACTIVE),
        DISCONNECTED(DialogActivityMode.LOGIN_BUTTON_BLOCK),
        SIGNING_IN(DialogActivityMode.ALL_FIELDS_DISABLED),
        INCORRECT_LOGIN(DialogActivityMode.ALL_FIELDS_ACTIVE),
        SIGNED_IN(DialogActivityMode.CLOSED);
        private final DialogActivityMode DIALOG_MODE;

        private ClientConnectivityState(DialogActivityMode mode) {
            DIALOG_MODE = mode;
        }
    }

    /**
     * Private enum to determine which components should be enabled and
     * disabled.
     */
    private enum DialogActivityMode {
        ALL_FIELDS_ACTIVE, ALL_FIELDS_DISABLED, LOGIN_BUTTON_BLOCK, CLOSED;
    }

    /**
     * Creates new form SteamClientLoginDialog.
     */
    public SteamClientLoginDialog(DialogCallback<SteamClientInfo> callback) {
        super(callback);
        initComponents();

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                // TODO Handle disposal properly when not needed.
                System.exit(0);
            }
        });

        //accounts = new AccountStorage(new File("users.json"));
        accounts = new AccountStorage();

        final DefaultComboBoxModel model = (DefaultComboBoxModel) accountUserField.getModel();
        for (String user : accounts.userStore.keySet()) {
            model.addElement(user);
        }

        machineAuthCookie = null;
        loginToken = null;

        setSteamConnectionState(
                SteamClientLoginDialog.ClientConnectivityState.CONNECTING);
    }

    /**
     * Sets the appropriate connectivity state.
     *
     * @param state
     */
    final void setSteamConnectionState(ClientConnectivityState state) {
        setLoginStatusLabel(LocalizationResources.getString(
                "LoginDialog.ClientConnectivityState." + state.name()));
        setLoginDialogVisibilityState(state.DIALOG_MODE);
    }

    /**
     * Sets component enabled states.
     *
     * @param mode
     */
    private void setLoginDialogVisibilityState(DialogActivityMode mode) {
        switch (mode) {
            case ALL_FIELDS_ACTIVE:
                accountUserField.setEnabled(true);
                accountPasswordField.setEnabled(true);
                loginButton.setEnabled(true);
                quitButton.setEnabled(true);
                break;
            case LOGIN_BUTTON_BLOCK:
                accountUserField.setEnabled(true);
                accountPasswordField.setEnabled(true);
                loginButton.setEnabled(false);
                quitButton.setEnabled(true);
                break;
            case ALL_FIELDS_DISABLED:
                accountUserField.setEnabled(false);
                accountPasswordField.setEnabled(false);
                loginButton.setEnabled(false);
                quitButton.setEnabled(true);
                break;
            case CLOSED:
                this.setVisible(false);
                break;
            default:
                throw new Error("Unhandled dialog visibility state enum");
        }
    }

    /**
     * Sets the status label.
     *
     * @param text
     */
    private void setLoginStatusLabel(String text) {
        loginStatusLabel.setText("Status: " + text);
    }

    private void writeClientInfoToFile(SteamClientInfo info) {
        String userPath = "." + File.separator + "users" + File.separator
                + info.username;
        File userFile = new File(userPath);

        userFile.getParentFile().mkdirs();

        Map<String, String> keyValues = new HashMap<>();
        keyValues.put("username", info.username);
        keyValues.put("password", info.password);

        if (info.machineauthcookie != null) {
            keyValues.put("authcookie", info.machineauthcookie);
        }

        if (info.token != null) {
            keyValues.put("logintoken", info.token);
        }

        JSONObject data = new JSONObject(keyValues);

        try (FileOutputStream fos = new FileOutputStream(userFile);
                XorStream.XorOutputStream xos =
                new XorStream.XorOutputStream(fos,
                info.username.getBytes("UTF-8"))) {
            xos.write(data.toString().getBytes("UTF-8"));
        } catch (IOException e) {
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

        projectNameLabel = new javax.swing.JLabel();
        versionNumberLabel = new javax.swing.JLabel();
        accountUserLabel = new javax.swing.JLabel();
        accountPasswordLabel = new javax.swing.JLabel();
        accountPasswordField = new javax.swing.JPasswordField();
        loginButton = new javax.swing.JButton();
        quitButton = new javax.swing.JButton();
        rememberLoginCheckbox = new javax.swing.JCheckBox();
        loginStatusLabel = new javax.swing.JLabel();
        poweredByLabel = new javax.swing.JLabel();
        accountUserField = new javax.swing.JComboBox();
        advancedSignInLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("DMT - Sign In");
        setMinimumSize(new java.awt.Dimension(382, 255));
        setResizable(false);

        projectNameLabel.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        projectNameLabel.setText("Distributable Marketeering Tool");

        versionNumberLabel.setText("Version " + BuildProperties.getBuildVersion() + ", build " + BuildProperties.getBuildTime());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/nosoop/ministeam2/UIStrings"); // NOI18N
        accountUserLabel.setText(bundle.getString("LoginDialog.Username")); // NOI18N

        accountPasswordLabel.setText(bundle.getString("LoginDialog.Labels.Password")); // NOI18N

        loginButton.setText(bundle.getString("LoginDialog.Labels.LoginButton")); // NOI18N
        this.getRootPane().setDefaultButton(loginButton);
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        quitButton.setText(bundle.getString("LoginDialog.Labels.CancelButton")); // NOI18N
        quitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitButtonActionPerformed(evt);
            }
        });

        rememberLoginCheckbox.setText(bundle.getString("LoginDialog.Labels.RememberLogin")); // NOI18N

        loginStatusLabel.setText(bundle.getString("LoginDialog.ClientConnectivityState.CONNECTING")); // NOI18N

        poweredByLabel.setText(String.format(LocalizationResources.getString("LoginDialog.FmtLabels.PoweredBy"), POWERED_BY));

        accountUserField.setEditable(true);
        accountUserField.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                accountUserFieldItemStateChanged(evt);
            }
        });

        advancedSignInLabel.setText("<html><u>Advanced sign-in...</u></html>");
        advancedSignInLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        advancedSignInLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                advancedSignInLabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(poweredByLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(loginStatusLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(projectNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                    .addComponent(versionNumberLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                            .addComponent(rememberLoginCheckbox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(advancedSignInLabel))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(projectNameLabel)
                .addGap(8, 8, 8)
                .addComponent(poweredByLabel)
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
                .addComponent(advancedSignInLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(rememberLoginCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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

        if (clientInfo.username.length() == 0
                || clientInfo.password.length() == 0) {
            // TODO Not silently fail if we can't sign in with the givens.
            return;
        }

        if (accounts.userStore.containsKey(clientInfo.username)
                && accounts.userStore.get(clientInfo.username).password.equals(clientInfo.password)) {
            // Assuming we're using stored data.
            clientInfo = accounts.userStore.get(clientInfo.username);
        }

        if (machineAuthCookie != null) {
            clientInfo.machineauthcookie = machineAuthCookie;
        }

        if (loginToken != null) {
            clientInfo.token = loginToken;
        }

        clientInfo.sentryFile = new File(String.format(SENTRY_FILENAME_FMT,
                clientInfo.username));

        if (rememberLoginCheckbox.isSelected()) {
            writeClientInfoToFile(clientInfo);
        }

        setSteamConnectionState(ClientConnectivityState.SIGNING_IN);

        callback.run(clientInfo);
    }//GEN-LAST:event_loginButtonActionPerformed

    private void accountUserFieldItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_accountUserFieldItemStateChanged
        String user = accountUserField.getEditor().getItem().toString();

        // Load stored user / password if possible.
        if (accounts.userStore.containsKey(user)) {
            String password;
            if ((password = accounts.userStore.get(user).password) != null) {
                accountPasswordField.setText(password);
            }
        }
    }//GEN-LAST:event_accountUserFieldItemStateChanged

    private void quitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitButtonActionPerformed
        // TODO Handle disposal properly when undeeded.
        System.exit(0);
    }//GEN-LAST:event_quitButtonActionPerformed

    private void advancedSignInLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_advancedSignInLabelMouseClicked
        SteamClientLoginAdvanced dialog = new SteamClientLoginAdvanced(this,
                true);

        String user = accountUserField.getEditor().getItem().toString();
        if (accounts.userStore.containsKey(user)) {
            String authCookie;
            if ((authCookie = accounts.userStore.get(user).machineauthcookie)
                    != null) {
                dialog.setAuthCookieField(authCookie);
            }
            
            String token;
            if ((token = accounts.userStore.get(user).token) != null) {
                dialog.setLoginTokenField(token);
            }
        }

        dialog.setVisible(true);
        SteamClientLoginAdvanced.Response response = dialog.getResponse();
        machineAuthCookie = response.authCookie;
        loginToken = response.loginToken;
    }//GEN-LAST:event_advancedSignInLabelMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPasswordField accountPasswordField;
    private javax.swing.JLabel accountPasswordLabel;
    private javax.swing.JComboBox accountUserField;
    private javax.swing.JLabel accountUserLabel;
    private javax.swing.JLabel advancedSignInLabel;
    private javax.swing.JButton loginButton;
    private javax.swing.JLabel loginStatusLabel;
    private javax.swing.JLabel poweredByLabel;
    private javax.swing.JLabel projectNameLabel;
    private javax.swing.JButton quitButton;
    private javax.swing.JCheckBox rememberLoginCheckbox;
    private javax.swing.JLabel versionNumberLabel;
    // End of variables declaration//GEN-END:variables

    // TODO Use a better storage format.
    public static class AccountStorage {
        final static File USERDATA_FILE = new File("users.json");
        private Map<String, SteamClientInfo> userStore;
        Logger logger = LoggerFactory.getLogger(
                AccountStorage.class.getSimpleName());

        AccountStorage() {
            userStore = new TreeMap<>();
            SteamClientInfo baseInfo = new SteamClientInfo();
            baseInfo.username = "";
            baseInfo.password = "";
            baseInfo.machineauthcookie = "";
            userStore.put("", baseInfo);

            String userPath = "." + File.separator + "users" + File.separator;
            File userDirectory = new File(userPath);
            userDirectory.mkdirs();

            File[] userFiles = userDirectory.listFiles();

            for (File user : userFiles) {
                try (FileInputStream fis = new FileInputStream(user);
                        XorStream.XorInputStream xis =
                        new XorStream.XorInputStream(fis,
                        user.getName().getBytes("UTF-8"))) {
                    byte[] bytes = new byte[(int) user.length()];
                    xis.read(bytes);
                    
                    JSONObject userJSON = new JSONObject(
                            new String(bytes, "UTF-8"));
                    
                    SteamClientInfo userInfo = new SteamClientInfo();
                    userInfo.username = userJSON.getString("username");
                    userInfo.password = userJSON.getString("password");
                    userInfo.token = userJSON.optString("logintoken");
                    userInfo.machineauthcookie = userJSON.optString("authcookie");
                    
                    userStore.put(userInfo.username, userInfo);
                } catch (IOException | JSONException e) {
                }
            }
        }

        AccountStorage(File file) {
            userStore = new TreeMap<>();
            SteamClientInfo baseInfo = new SteamClientInfo();
            baseInfo.username = "";
            baseInfo.password = "";
            baseInfo.machineauthcookie = "";
            userStore.put("", baseInfo);

            try {
                if (file.exists()) {
                    JSONObject data = new JSONObject(
                            new JSONTokener(new FileInputStream(file)));

                    JSONArray clients = data.getJSONArray("clients");

                    for (int i = 0; i < clients.length(); i++) {
                        JSONObject client = clients.getJSONObject(i);

                        SteamClientInfo userInfo = new SteamClientInfo();
                        userInfo.username = client.getString("username");
                        userInfo.password = client.optString("password", "");
                        userInfo.machineauthcookie =
                                client.optString("machineauth", "");

                        userStore.put(client.getString("username"), userInfo);
                    }
                } else {
                    logger.info("User credential storage file does not exist.");
                }
            } catch (JSONException ex) {
                logger.error("Error loading user storage.", ex);
            } catch (FileNotFoundException ex) {
                logger.error("Unable to find file after checking for its "
                        + "existence.", ex);
            }
        }

        Map<String, SteamClientInfo> getUserList() {
            return userStore;
        }
    }

}
