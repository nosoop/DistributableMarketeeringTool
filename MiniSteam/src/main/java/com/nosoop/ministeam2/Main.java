package com.nosoop.ministeam2;

import bundled.steamtrade.org.json.JSONException;
import bundled.steamtrade.org.json.JSONObject;
import bundled.steamtrade.org.json.JSONTokener;
import com.nosoop.json.VDF;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import javax.crypto.Cipher;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class. Launches one instance of the FrontendClient.
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class Main {
    static Logger logger = LoggerFactory.getLogger(Main.class.getSimpleName());

    /**
     * @param args the command line arguments
     */
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void main(String args[]) {
        /*
         * Set the operating system look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.error("Error setting System L&F", ex);
        }
        //</editor-fold>

        verifyJCEUSPFInstall();

        // Create and display the form
        new SteamClientMainForm();
    }

    /**
     * Verifies that the JCE Unlimited Strength Policy files are installed by
     * checking the allowed key length for AES.
     */
    private static void verifyJCEUSPFInstall() {
        try {
            int keyLength = Cipher.getMaxAllowedKeyLength("AES");

            if (keyLength == 128) {
                JOptionPane.showMessageDialog(null,
                        String.format("The JCE Unlimited Strength policy files "
                        + "are not installed.\n"
                        + "Please install the mentioned files corresponding to "
                        + "the currently running Java runtime, version %s.",
                        System.getProperty("java.version")));
                System.exit(1);
            }
        } catch (NoSuchAlgorithmException e) {
            JOptionPane.showMessageDialog(null, "For some reason, this system "
                    + "refuses to acknowledge the existence of the AES "
                    + "algorithm.");
            System.exit(2);
        }
    }

    /**
     * Helper method that parses a given Steam config file, copying the stored
     * sentry file to multiple DMT users.
     */
    private static void copySentryFiles(File configFile) throws JSONException,
            FileNotFoundException, IOException {
        JSONObject cfg = VDF.toJSONObject(new JSONTokener(
                new FileInputStream(configFile)), false);

        JSONObject steam = cfg.getJSONObject("InstallConfigStore").
                getJSONObject("Software").getJSONObject("Valve").
                getJSONObject("Steam");

        // List of available accounts.
        Set<String> accounts = steam.getJSONObject("Accounts").keySet();

        // Grabs the path for the installed sentry file.
        File sentryFile = new File(steam.getString("SentryFile"));

        // Assumes that all the accounts in the config use the same sentry file.
        for (String s : accounts) {
            File fs = new File(".\\SentryFile_" + s);

            /**
             * We can't use Files.copy(...) because it copies the hidden
             * attribute of the sentry file.
             */
            if (!fs.exists()) {
                fs.createNewFile();
                try (FileChannel in = new FileInputStream(sentryFile).getChannel();
                        FileChannel out = new FileOutputStream(fs).getChannel()) {
                    long bytesCopied = out.transferFrom(in, 0, in.size());

                    assert (bytesCopied == in.size());
                }
            }
        }
    }
}
