package org.omnione.did.wallet.zkp.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.json.GsonWrapper;
import org.omnione.did.wallet.zkp.data.keypair.KeyCorrectnessProof;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerSerializer;

import java.math.BigInteger;

public class CredentialOffer {

    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger nonce;

//    @SerializedName("schema_id")
    private String schemaId;

//    @SerializedName("cred_def_id")
    private String credDefId;

//    @SerializedName("key_correctness_proof")
    private KeyCorrectnessProof keyCorrectnessProof;

    //TODO: 필요성을 고려 해봐야 한다.
//    @SerializedName("method_name")
    @Expose
    private String methodName;

    public BigInteger getNonce() {
        return nonce;
    }
    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public String getSchemaId() {
        return schemaId;
    }
    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public String getCredDefId() {
        return credDefId;
    }
    public void setCredDefId(String credDefId) {
        this.credDefId = credDefId;
    }

    public KeyCorrectnessProof getKeyCorrectnessProof() {
        return keyCorrectnessProof;
    }
    public void setKeyCorrectnessProof(KeyCorrectnessProof keyCorrectnessProof) {
        this.keyCorrectnessProof = keyCorrectnessProof;
    }

    public String getMethodName() {
        return methodName;
    }
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String toJson() {
        GsonWrapper gson = new GsonWrapper();
        return gson.toJson(this);
    }
}
