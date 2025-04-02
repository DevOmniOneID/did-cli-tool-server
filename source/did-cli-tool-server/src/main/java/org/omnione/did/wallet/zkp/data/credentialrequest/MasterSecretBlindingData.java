package org.omnione.did.wallet.zkp.data.credentialrequest;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.revoc.data.GroupOrderElement;
import org.omnione.did.wallet.zkp.revoc.data.dto.ZkpSerializer;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerSerializer;

import java.math.BigInteger;
public class MasterSecretBlindingData {

//    @SerializedName("v_prime")
    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger vPrime;

//    @SerializedName("vr_prime")
    @JsonAdapter(ZkpSerializer.class)
    private GroupOrderElement vrPrime;

    public MasterSecretBlindingData(GroupOrderElement vr_prime, BigInteger v_prime) {

        this.vrPrime = vr_prime;
        this.vPrime = v_prime;
    }

    public BigInteger getVPrime() {
        return vPrime;
    }

    public void setVPrime(BigInteger v_prime) {
        this.vPrime = v_prime;
    }

    public GroupOrderElement getVrPrime() {
        return vrPrime;
    }

    public void setVrPrime(GroupOrderElement vr_prime) {
        this.vrPrime = vr_prime;
    }
}
