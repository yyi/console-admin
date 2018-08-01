package com.founder.utils;

public class ConsoleUtils {
    static public boolean isWindows() {
        return getOsName().startsWith("Windows");
    }

    static private String getOsName() {
        return System.getProperty("os.name");
    }

}
