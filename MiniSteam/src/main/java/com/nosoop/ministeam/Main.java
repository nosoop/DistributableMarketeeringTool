/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.ministeam;

/**
 * The main class.  Launches one instance of the FrontendClient.
 * 
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class Main {

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
            java.util.logging.Logger.getLogger(SteamMainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        new FrontendClient();
    }
}
