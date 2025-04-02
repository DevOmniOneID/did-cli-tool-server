package org.omnione.did.wallet.zkp.data.credentialrequest;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerMapSerializer;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerSerializer;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

public class BlindedCredentialSecretsCorrectnessProof {

    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger c;

//    @SerializedName("v_dash_cap")
    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger vDashCap;

//    @SerializedName("m_caps")
    @JsonAdapter(BigIntegerMapSerializer.class)
    private LinkedHashMap<String, BigInteger> mCaps;

//    @SerializedName("r_caps")
    @JsonAdapter(BigIntegerMapSerializer.class)
    private Map<String, BigInteger> rCaps;

    public BigInteger getC() {
        return c;
    }
    public void setC(BigInteger c) {
        this.c = c;
    }

    public BigInteger getVDashCap() {
        return vDashCap;
    }
    public void setVDashCap(BigInteger vDashCap) {
        this.vDashCap = vDashCap;
    }

    public LinkedHashMap<String, BigInteger> getmCaps() {
        return mCaps;
    }
    public void setMCaps(LinkedHashMap<String, BigInteger> mCaps) {
        this.mCaps = mCaps;
    }

    public Map<String, BigInteger> getRCaps() {
        return rCaps;
    }
    public void setRCaps(Map<String, BigInteger> rCaps) {
        this.rCaps = rCaps;
    }
}
