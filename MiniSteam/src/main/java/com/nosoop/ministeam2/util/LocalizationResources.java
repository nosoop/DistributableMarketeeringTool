package com.nosoop.ministeam2.util;

import java.util.ResourceBundle;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class LocalizationResources {
    private static ResourceBundle resource = null;

    private LocalizationResources() {
    }

    public static String getString(String key) {
        if (resource == null) {
            resource = ResourceBundle.getBundle
                    ("com/nosoop/ministeam2/UIStrings");
        }
        
        return resource.getString(key);
    }
}