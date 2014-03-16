/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.ministeam;

import java.util.HashMap;
import java.util.Map;

/**
 * A 'global' class that stores all of the UI text.
 * 
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class FrontendLanguageOverride {
    
    enum LanguageKeyPair {
        TRADE_COMPLETE("#DMT_TRADE_COMPLETE");
        
        private String jsonKey;
        
        LanguageKeyPair(String jsonKey) {
            this.jsonKey = jsonKey;
        }
        
        public String getText() {
            return languageValues.containsKey(jsonKey) ? 
                    languageValues.get(jsonKey) : this.jsonKey;
        }
    }
    
    private static final Map<String,String> languageValues;
    
    static {
        languageValues = new HashMap<>();
        
        // Iterate through default or language override, add keys/values to map.
    }
    
}
