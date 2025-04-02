package org.omnione.did.wallet.zkp.data.keypair;

import com.google.gson.annotations.JsonAdapter;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerMapSerializer;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerSerializer;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

public class CredentialPrimaryPublicKey {

    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger n;

    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger z;

    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger s;

    @JsonAdapter(BigIntegerMapSerializer.class)
    private LinkedHashMap<String, BigInteger> r;

    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger rctxt;

    public CredentialPrimaryPublicKey() {}

    //TODO: Map의 구현체 타입을 강제하는 것 고려 (TreeMap)
    public CredentialPrimaryPublicKey(BigInteger n, BigInteger s, BigInteger z, LinkedHashMap<String, BigInteger> r) {
        this.n = n;
        this.s = s;
        this.z = z;
        this.r = r;
    }
    //TODO: Map의 구현체 타입을 강제하는 것 고려 (TreeMap)
    public CredentialPrimaryPublicKey(BigInteger n, BigInteger s, BigInteger z, LinkedHashMap<String, BigInteger> r, BigInteger rctxt) {
        this.n = n;
        this.s = s;
        this.z = z;
        this.r = r;
        this.rctxt = rctxt;
    }

    public BigInteger getN() {
        return n;
    }
    public void setN(BigInteger n) {
        this.n = n;
    }

    public BigInteger getZ() {
        return z;
    }
    public void setZ(BigInteger z) {
        this.z = z;
    }

    public BigInteger getS() {
        return s;
    }
    public void setS(BigInteger s) {
        this.s = s;
    }

    public LinkedHashMap<String, BigInteger> getR() {
        return r;
    }
    public void setR(LinkedHashMap<String, BigInteger> r) {
        this.r = r;
    }

    public BigInteger getRctxt() {
        return rctxt;
    }

    public void setRctxt(BigInteger rctxt) {
        this.rctxt = rctxt;
    }
}