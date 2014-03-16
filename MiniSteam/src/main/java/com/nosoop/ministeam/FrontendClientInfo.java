/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.ministeam;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class FrontendClientInfo {
    private String accountUsername;
    private String accountPassword;
    private String accountToken;
    
    public FrontendClientInfo(String user, String pass, String token) {
        accountUsername = user;
        accountPassword = pass;
        accountToken = token;
    }

    public String getAccountUsername() {
        return accountUsername;
    }

    public String getAccountPassword() {
        return accountPassword;
    }
    
    public String getAccountToken() {
        return accountToken;
    }
}
