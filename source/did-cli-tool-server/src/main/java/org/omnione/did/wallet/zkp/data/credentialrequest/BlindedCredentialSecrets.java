package org.omnione.did.wallet.zkp.data.credentialrequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.json.GsonWrapper;
import org.omnione.did.wallet.zkp.revoc.data.GroupOrderElement;
import org.omnione.did.wallet.zkp.revoc.data.PointG1;
import org.omnione.did.wallet.zkp.revoc.data.dto.ZkpSerializer;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerMapSerializer;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerSerializer;

import java.math.BigInteger;
import java.util.*;

public class BlindedCredentialSecrets {

    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger u;

    @JsonAdapter(ZkpSerializer.class)
    private PointG1 ur;

//    @SerializedName("v_prime")
    @Expose(serialize = false, deserialize = false)
    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger vPrime;

//    @SerializedName("vr_prime")
    @JsonAdapter(ZkpSerializer.class)
    @Expose(serialize = false, deserialize = false)
    private GroupOrderElement vrPrime;

    //    @SerializedName("hiddenAttrs")
    @SerializedName("hiddenAttributes")
    private List<String> hiddenAttrs;

//    @SerializedName("committed_attrs")
    @JsonAdapter(BigIntegerMapSerializer.class)
    private LinkedHashMap<String, BigInteger> committedAttrs;

    public BlindedCredentialSecrets() {}

    public BlindedCredentialSecrets(BigInteger u, GroupOrderElement vr_prime, PointG1 ur, BigInteger v_prime,
                                    List<String> hiddenAttrs, LinkedHashMap<String, BigInteger> committedAttrs) {
        this.u = u;
        this.ur = ur;
        this.vrPrime = vr_prime;
        this.hiddenAttrs = (hiddenAttrs == null) ? new LinkedList<String>() : hiddenAttrs;
        this.committedAttrs = (committedAttrs == null) ? new LinkedHashMap<String, BigInteger>() : committedAttrs;

        this.vPrime = v_prime;
    }

    public BigInteger getU() {
        return u;
    }

    public PointG1 getUr() {
        return ur;
    }

    public void setUr(PointG1 ur) {
        this.ur = ur;
    }

    public BigInteger getVPrime() {
        return vPrime;
    }

    public void setVPrime(BigInteger v_prime) {
        this.vPrime = v_prime;
    }

    public List<String> getHiddenAttrs() {
        return hiddenAttrs;
    }

    public LinkedHashMap<String, BigInteger> getCommittedAttrs() {
        return committedAttrs;
    }

    public GroupOrderElement getVrPrime() {
        return vrPrime;
    }

    public void setVrPrime(GroupOrderElement vr_prime) {
        this.vrPrime = vr_prime;
    }

    public String toJson() {
        GsonWrapper gson = new GsonWrapper();
        return gson.toJson(this);
    }
}
