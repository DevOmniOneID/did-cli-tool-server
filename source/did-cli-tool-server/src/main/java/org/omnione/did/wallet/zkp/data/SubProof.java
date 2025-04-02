package org.omnione.did.wallet.zkp.data;

import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class SubProof {

//    @SerializedName("primary_proof")
    private PrimaryProof primaryProof;

//    @SerializedName("non_revoc_proof")
    private NonRevocProof nonRevocProof;

    public SubProof(PrimaryProof primaryProof, NonRevocProof nonRevocProof) {
        this.primaryProof = primaryProof;
        this.nonRevocProof = nonRevocProof;
    }

    public PrimaryProof getPrimaryProof() {
        return this.primaryProof;
    }

    public Map<String, String> getRevealedAttrs() {
        Map<String, String> ret = new HashMap<String, String>();
        for (Map.Entry<String, BigInteger> entry : primaryProof.getEqProof().getRevealedAttrs().entrySet()) {
            ret.put(entry.getKey(), entry.getValue().toString());
        }
        return ret;
    }

    public NonRevocProof getNonRevocProof() {
        return nonRevocProof;
    }
}
