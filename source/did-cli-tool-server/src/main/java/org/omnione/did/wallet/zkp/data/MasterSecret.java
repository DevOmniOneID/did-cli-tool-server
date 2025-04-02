package org.omnione.did.wallet.zkp.data;

import org.omnione.did.wallet.util.json.GsonWrapper;

import java.math.BigInteger;
import java.util.UUID;

public class MasterSecret {

    private String masterSecretId;

//    @JsonIgnore
    private BigInteger masterSecret;

    public MasterSecret() {
        this.masterSecretId = UUID.randomUUID().toString();
    }

    public MasterSecret(String masterSecretId, BigInteger masterSecret) {
        this.masterSecretId = masterSecretId;
        this.masterSecret = masterSecret;
    }

    public String getMasterSecretId() {
        return masterSecretId;
    }
    public void setMasterSecretId(String masterSecretId) {
        this.masterSecretId = masterSecretId;
    }

    public BigInteger getMasterSecret() {
        return masterSecret;
    }
    public void setMasterSecret(BigInteger masterSecret) {
        this.masterSecret = masterSecret;
    }
    public String toJson() {
        return GsonWrapper.getGsonPrettyPrinting().toJson(this);
    }

    public void fromJson(String val) {

        GsonWrapper gson = new GsonWrapper();
        MasterSecret result = gson.fromJson(val, MasterSecret.class);
        this.masterSecretId = result.getMasterSecretId();
        this.masterSecret = result.getMasterSecret();
    }
}