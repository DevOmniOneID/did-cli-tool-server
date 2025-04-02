package org.omnione.did.wallet.zkp.data.keypair;

import com.google.gson.annotations.JsonAdapter;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerSerializer;

import java.math.BigInteger;

public class CredentialPrimaryPrivateKey {

    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger p;

    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger q;

    public CredentialPrimaryPrivateKey(BigInteger p, BigInteger q) {
        this.p = p;
        this.q = q;
    }

    public BigInteger getP() {
        return p;
    }
    public void setP(BigInteger p) {
        this.p = p;
    }

    public BigInteger getQ() {
        return q;
    }
    public void setQ(BigInteger q) {
        this.q = q;
    }
}
