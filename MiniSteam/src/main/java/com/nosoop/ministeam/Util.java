/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.ministeam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class Util {
    public static String readFile(File file) {
        StringBuilder buff = new StringBuilder();
        try {
            Scanner sc = new Scanner(new FileReader(file));
            while (sc.hasNext()) {
                buff.append(sc.nextLine()).append('\n');
            }
            return buff.toString();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return buff.toString();
    }
}
