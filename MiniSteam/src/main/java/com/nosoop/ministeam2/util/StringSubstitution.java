package com.nosoop.ministeam2.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class whose only method replaces ${variable} -style tokens.
 * 
 * Source: http://stackoverflow.com/a/16083135
 * 
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class StringSubstitution {
    static final Pattern TOKEN_PATTERN = Pattern.compile("\\$\\{(.+?)\\}");
    
    public static String substituteVariables(String template, Map<String, String> variables) {
        Matcher matcher = TOKEN_PATTERN.matcher(template);
        // StringBuilder cannot be used here because Matcher expects StringBuffer
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            if (variables.containsKey(matcher.group(1))) {
                String replacement = variables.get(matcher.group(1));
                // quote to work properly with $ and {,} signs
                matcher.appendReplacement(buffer, replacement != null ?
                        Matcher.quoteReplacement(replacement) : "null");
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
