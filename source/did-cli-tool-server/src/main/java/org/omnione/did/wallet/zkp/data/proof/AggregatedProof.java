package org.omnione.did.wallet.zkp.data.proof;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerSerializer;

import java.math.BigInteger;
import java.util.Vector;

public class AggregatedProof {

//    @SerializedName("c_hash")
    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger cHash;

//    @SerializedName("c_list")
    private Vector<byte[]> cList;

    public AggregatedProof() {
    }

    public AggregatedProof(BigInteger cHash, Vector<byte[]> cList) {
        this.cHash = cHash;
        this.cList = cList;
    }

    public BigInteger getcHash() {
        return cHash;
    }

    public void setcHash(BigInteger cHash) {
        this.cHash = cHash;
    }

    public Vector<byte[]> getcList() {
        return cList;
    }

    public void setcList(Vector<byte[]> cList) {
        this.cList = cList;
    }
}
