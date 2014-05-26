package com.nosoop.ministeam2.util;

import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class LocalizationResources {
    private static ResourceBundle resource = null;
    private static Logger logger = LoggerFactory.getLogger(
            LocalizationResources.class.getSimpleName());

    private LocalizationResources() {
    }

    public static String getString(String key) {
        if (resource == null) {
            resource = ResourceBundle.getBundle
                    ("com/nosoop/ministeam2/UIStrings");
        }
        
        if (resource.containsKey(key)) {
            logger.error("Could not find localization string for key {}.",
                    key);
        }
        
        return resource.getString(key);
    }
}