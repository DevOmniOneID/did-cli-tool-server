package org.omnione.did.wallet.zkp.data.credential;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.GDPLogger;
import org.omnione.did.wallet.zkp.revoc.WitnessSignature;
import org.omnione.did.wallet.zkp.revoc.data.GroupOrderElement;
import org.omnione.did.wallet.zkp.revoc.data.PointG1;
import org.omnione.did.wallet.zkp.revoc.data.dto.ZkpSerializer;
import org.omnione.did.wallet.zkp.revoc.utils.AMCLUtils;

import java.math.BigInteger;

public class NonRevocationCredentialSignature {
    @JsonAdapter(ZkpSerializer.class)
    private PointG1 sigma;

    @JsonAdapter(ZkpSerializer.class)
    private GroupOrderElement c;

    @SerializedName("vr_prime_prime")
    @JsonAdapter(ZkpSerializer.class)
    private GroupOrderElement vrPrimePrime;

    @SerializedName("witness_signature")
    private WitnessSignature witnessSignature;

    @SerializedName("g_i")
    @JsonAdapter(ZkpSerializer.class)
    private PointG1 gI;

    @SerializedName("i")
    private Integer revIndex;

    @JsonAdapter(ZkpSerializer.class)
    private GroupOrderElement m2;

    public NonRevocationCredentialSignature(PointG1 sigma,
                                            GroupOrderElement c,
                                            GroupOrderElement vrPrimePrime,
                                            WitnessSignature witnessSignature,
                                            PointG1 g_i,
                                            Integer index,
                                            GroupOrderElement m2) {
//        System.out.println("\n====================================================================================================");
//        System.out.println("NonRevocationCredentialSignature build");
//        System.out.println("====================================================================================================");
//        AMCLUtils.toHashString(sigma, "sigma");
//        AMCLUtils.toHashString(c, "c");
//        AMCLUtils.toHashString(vrPrimePrime, "vr_prime_prime");
//        AMCLUtils.toHashString(g_i, "g_i");
//        AMCLUtils.toHashString(m2, "m2");

        this.sigma = sigma;
        this.c = c;
        this.vrPrimePrime = vrPrimePrime;
        this.witnessSignature = witnessSignature;
        this.gI = g_i;
        this.revIndex = index;
        this.m2 = m2;

    }

    public GroupOrderElement getC() {
        return c;
    }

    public void setC(GroupOrderElement c) {
        this.c = c;
    }



    public Integer getRevIndex() {
        return revIndex;
    }

    public GroupOrderElement getM2() {
        return m2;
    }

    public void setM2(GroupOrderElement m2) {
        this.m2 = m2;
    }

    public GroupOrderElement getVrPrimePrime() {
        return vrPrimePrime;
    }

    public void setVrPrimePrime(GroupOrderElement vrPrimePrime) {
        this.vrPrimePrime = vrPrimePrime;
    }

    public WitnessSignature getWitnessSignature() {
        return witnessSignature;
    }

    public void setWitnessSignature(WitnessSignature witnessSignature) {
        this.witnessSignature = witnessSignature;
    }

    public PointG1 getGI() {
        return gI;
    }

    public void setGI(PointG1 g_i) {
        this.gI = g_i;
    }

    public PointG1 getSigma() {
        return sigma;
    }

    public void setSigma(PointG1 sigma) {
        this.sigma = sigma;
    }
}
