package com.nosoop.ministeam2.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Obtains the build properties from a file,
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class BuildProperties {
    private static final Properties properties;

    /**
     * Use a static initializer to read from file.
     */
    static {
        InputStream inputStream = BuildProperties.class.getResourceAsStream("/build.properties");
        properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read properties file", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Hide default constructor.
     */
    private BuildProperties() {
    }

    /**
     * Gets the build version.
     *
     * @return A {@code String} with the build version.
     */
    public static String getBuildVersion() {
        return properties.getProperty("buildversion");
    }

    /**
     * Gets the build time.
     *
     * @return A (@code String) with the build time.
     */
    public static String getBuildTime() {
        return properties.getProperty("buildtime");
    }
}
