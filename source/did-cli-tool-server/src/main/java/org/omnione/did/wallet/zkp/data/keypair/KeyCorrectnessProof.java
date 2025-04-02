package org.omnione.did.wallet.zkp.data.keypair;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerMapSerializer;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerSerializer;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class KeyCorrectnessProof {
    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger c;

//    @SerializedName("xz_cap")
    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger xzCap;

//    @SerializedName("xr_cap")
    @JsonAdapter(BigIntegerMapSerializer.class)
    private LinkedHashMap<String, BigInteger> xrCap;

    public KeyCorrectnessProof() {}
    public KeyCorrectnessProof(BigInteger c, BigInteger xzCap, LinkedHashMap<String, BigInteger> xrCap) {
        this.c = c;
        this.xzCap = xzCap;
        this.xrCap = xrCap;
    }

    public BigInteger getC() {
        return c;
    }public void setC(BigInteger c) {
        this.c = c;
    }
    public BigInteger getXzCap() {
        return xzCap;
    }

    public LinkedHashMap<String, BigInteger> getXrCap() {
        return new LinkedHashMap<String, BigInteger>(xrCap);
    }
}
