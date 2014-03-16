package com.live.poonso.poopydebug;

/**
 * Some kind of output thingy.
 *
 * TODO Replace with a proper logging library.
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class DebugPrint {

    static boolean debug = false;

    public static void printf(String string, Object... objects) {
        if (!debug) {
            return;
        }
        System.out.printf(string, objects);
    }

    public static void println(String string) {
        if (!debug) {
            return;
        }
        System.out.println(string);
    }

    public static void print(String string) {
        if (!debug) {
            return;
        }
        System.out.print(string);
    }

    public static void setDebug(boolean isDebug) {
        debug = isDebug;
    }
}
