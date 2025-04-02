package org.omnione.did.wallet.zkp.data.credentialrequest;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerSerializer;

import java.math.BigInteger;

public class CredentialRequestMeta {

//    @SerializedName("master_secret_blinding_data")
    private MasterSecretBlindingData masterSecretBlindingData;

    @SerializedName("nonce")
    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger nonce;

//    @SerializedName("master_secret_name")
    private String masterSecretName;

    public CredentialRequestMeta(MasterSecretBlindingData masterSecretBlindingData, BigInteger nonce, String masterSecretName) {
        this.masterSecretBlindingData = masterSecretBlindingData;
        this.nonce = nonce;
        this.masterSecretName = masterSecretName;
    }

    public MasterSecretBlindingData getMasterSecretBlindingData() {
        return masterSecretBlindingData;
    }

    public void setMasterSecretBlindingData(MasterSecretBlindingData masterSecretBlindingData) {
        this.masterSecretBlindingData = masterSecretBlindingData;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public String getMasterSecretName() {
        return masterSecretName;
    }
}
