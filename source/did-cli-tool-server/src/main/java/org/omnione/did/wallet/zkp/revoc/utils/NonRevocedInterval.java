package org.omnione.did.wallet.zkp.revoc.utils;

import com.google.gson.annotations.JsonAdapter;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerSerializer;

import java.math.BigInteger;

public class NonRevocedInterval {

    private String from;

    private String to;

    public NonRevocedInterval(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }
}

