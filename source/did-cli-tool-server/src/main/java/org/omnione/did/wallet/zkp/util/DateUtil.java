package org.omnione.did.wallet.zkp.util;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpLogger;

import java.sql.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static long getTimestamp() {

        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String currentDateStr = format.format(date);
        long timestemp = Timestamp.valueOf(currentDateStr).getTime();
        return timestemp;
//        return String.valueOf(timestemp);
    }

    public static String timestampToDateFormat(long timestemp) {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String currentDateStr = format.format(new Date(timestemp));
        return currentDateStr;
    }

    public static void main(String[] args) throws InterruptedException {

        ZkpLogger.debug(DateUtil.timestampToDateFormat(DateUtil.getTimestamp()));
    }
}
