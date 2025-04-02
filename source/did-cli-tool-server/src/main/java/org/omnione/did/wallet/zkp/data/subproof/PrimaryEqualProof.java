package org.omnione.did.wallet.zkp.data.subproof;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerMapSerializer;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerSerializer;

import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;

public class PrimaryEqualProof {

//    @SerializedName("revealed_attr")
    @SerializedName("revealedAttrs")
    @JsonAdapter(BigIntegerMapSerializer.class)
    private TreeMap<String, BigInteger> revealedAttrs;

//    @SerializedName("a_prime")
    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger aPrime;

    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger e;

    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger v;

    @JsonAdapter(BigIntegerMapSerializer.class)
    private Map<String, BigInteger> m;

    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger m2;

    public PrimaryEqualProof(Map<String, BigInteger> revealedAttrs, BigInteger aPrime, BigInteger e,
                             BigInteger v, Map<String, BigInteger> m, BigInteger m2) {
        //TODO: TreeMap 제거 가능?
        this.revealedAttrs = new TreeMap<String, BigInteger>(revealedAttrs);
        this.aPrime = aPrime;
        this.e = e;
        this.v = v;
        this.m = m;
        this.m2 = m2;
    }

    public TreeMap<String, BigInteger> getRevealedAttrs() {
        return revealedAttrs;
    }

    public BigInteger getaPrime() {
        return aPrime;
    }
    public void setaPrime(BigInteger aPrime) {
        this.aPrime = aPrime;
    }

    public BigInteger getE() {
        return e;
    }
    public void setE(BigInteger e) {
        this.e = e;
    }

    public BigInteger getV() {
        return v;
    }
    public void setV(BigInteger v) {
        this.v = v;
    }

    public Map<String, BigInteger> getM() {
        return m;
    }
    public void setM(Map<String, BigInteger> m) {
        this.m = m;
    }

    public BigInteger getM2() {
        return m2;
    }

    public void setM2(BigInteger m2) {
        this.m2 = m2;
    }
}
