package org.omnione.did.wallet.zkp.revoc.sdk;

import org.omnione.did.wallet.util.GDPLogger;

public class ZkpLogger {

    /**
     * sysout print
     * @param message message
     */
    public static void print(String message) {
        GDPLogger.print(message);
    }

    /**
     * debug 로그
     * @param message message
     */
    public static void debug(String message){
        GDPLogger.debug(message);
    }

    /**
     * info 로그
     * @param message message
     */
    public static void info(String message){
        GDPLogger.info(message);
    }

    /**
     * Logger 활성화 여부
     */
    public static boolean isEnabled() {
        return GDPLogger.FLAG;
    }

    /**
     * Logger 설정
     * @param enabled enabled
     */
    public static void setEnabled(boolean enabled) {
        GDPLogger.FLAG = enabled;
    }
}
