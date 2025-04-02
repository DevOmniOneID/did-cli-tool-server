package org.omnione.did.wallet.zkp.revoc.utils.blob.tail.dto;


import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TailFileDto {

    private String rr_id;
    private byte[] tail_hash;

    public static String getNowDateStr() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = Date.format(cal.getTime());
        return dateStr;
    }

    public TailFileDto(String id, byte[] hash) {

        this.rr_id = id;
        this.tail_hash = hash;
    }

    public String getId() {

        return rr_id;
    }

    public void setId(String id) {

        this.rr_id = id;
    }


    public byte[] getTailHash() {

        return tail_hash;
    }

    public void setTailHash(byte[] hash) {

        this.tail_hash = hash;
    }

    public String toString() {

        return this.rr_id+" "+this.tail_hash;
    }
}
