package com.nosoop.ministeam2;

import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class.  Launches one instance of the FrontendClient.
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
        /* Set the operating system look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.error("Error setting System L&F", ex);
        }
        //</editor-fold>

        try {
            int keyLength = Cipher.getMaxAllowedKeyLength("AES");
            
            if (keyLength == 128) {
                JOptionPane.showMessageDialog(null, "The JCE Unlimited Strength policy files are not installed.\nTerminating.");
                System.exit(1);
            }
        } catch (NoSuchAlgorithmException e) {
            return;
        }
        
        /* Create and display the form */
        new SteamClientMainForm();
    }
}
