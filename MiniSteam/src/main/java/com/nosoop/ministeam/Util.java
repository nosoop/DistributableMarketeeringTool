/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.ministeam;

import com.nosoop.steamtrade.assetbuilders.TF2AssetBuilder;
import com.nosoop.steamtrade.inventory.AssetBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class Util {
    static Logger logger = LoggerFactory.getLogger(Util.class.getSimpleName());
    
    public static String readFile(File file) {
        StringBuilder buff = new StringBuilder();
        try {
            Scanner sc = new Scanner(new FileReader(file));
            while (sc.hasNext()) {
                buff.append(sc.nextLine()).append('\n');
            }
            return buff.toString();
        } catch (FileNotFoundException ex) {
            logger.error("Error reading file", ex);
        }
        return buff.toString();
    }
    
    final static ArrayList<AssetBuilder> ASSET_BUILDERS;
    static {
        ASSET_BUILDERS = new ArrayList<>();
        ASSET_BUILDERS.add(new TF2AssetBuilder());
    }
}
