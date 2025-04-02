package org.omnione.did.wallet.zkp.data;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.json.GsonWrapper;
import org.omnione.did.wallet.zkp.data.credentialrequest.BlindedCredentialSecrets;
import org.omnione.did.wallet.zkp.data.credentialrequest.BlindedCredentialSecretsCorrectnessProof;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerSerializer;

import java.math.BigInteger;

public class CredentialRequest {

    @SerializedName("proverDID")
    private String proverDid;

//    @SerializedName("cred_def_id")
    private String credDefId;

    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger nonce;

//    @SerializedName("blinded_ms")
    private BlindedCredentialSecrets blindedMs;

//    @SerializedName("blinded_ms_correctness_proof")
    @SerializedName("blindedMsCorrectnessProof")
    private BlindedCredentialSecretsCorrectnessProof blindedMsProof;


    public String getProverDid() {
        return proverDid;
    }
    public void setProverDid(String proverDid) {
        this.proverDid = proverDid;
    }

    public String getCredDefId() {
        return credDefId;
    }
    public void setCredDefId(String credDefId) {
        this.credDefId = credDefId;
    }

    public BigInteger getNonce() {
        return nonce;
    }
    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public BlindedCredentialSecrets getBlindedMs() {
        return blindedMs;
    }
    public void setBlindedMs(BlindedCredentialSecrets blindedMs) {
        this.blindedMs = blindedMs;
    }

    public BlindedCredentialSecretsCorrectnessProof getBlindedMsProof() {
        return blindedMsProof;
    }
    public void setBlindedMsProof(BlindedCredentialSecretsCorrectnessProof blindedMsProof) {
        this.blindedMsProof = blindedMsProof;
    }

    public String toJson() {
        return GsonWrapper.getGsonPrettyPrinting().toJson(this);
    }

}
