package org.omnione.did.wallet.zkp.data.credential;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerSerializer;

import java.math.BigInteger;

public class SignatureCorrectnessProof {

    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger se;

    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger c;

    public SignatureCorrectnessProof() {}

    public SignatureCorrectnessProof(BigInteger c, BigInteger se) {
        this.c = c;
        this.se = se;
    }

    public BigInteger getSe() {
        return se;
    }
    public void setSe(BigInteger se) {
        this.se = se;
    }

    public BigInteger getC() {
        return c;
    }
    public void setC(BigInteger c) {
        this.c = c;
    }
}
