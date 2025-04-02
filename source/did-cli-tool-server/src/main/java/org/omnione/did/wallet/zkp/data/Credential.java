package org.omnione.did.wallet.zkp.data;

import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.json.GsonWrapper;
import org.omnione.did.wallet.zkp.data.attribute.AttributeValue;
import org.omnione.did.wallet.zkp.data.credential.CredentialSignature;
import org.omnione.did.wallet.zkp.data.credential.SignatureCorrectnessProof;
import org.omnione.did.wallet.zkp.revoc.RevocationRegistry;
import org.omnione.did.wallet.zkp.revoc.utils.Witness;

import java.util.LinkedHashMap;
import java.util.Map;

public class Credential {

//    @SerializedName("schema_id")
    private String schemaId;

//    @SerializedName("cred_def_id")
    private String credDefId;

//    @SerializedName("rev_reg_id")
    private String revRegDefId;

    private LinkedHashMap<String, AttributeValue> values;

//    @SerializedName("rev_reg")
    private RevocationRegistry revReg;

    private Witness witness;

    @SerializedName("signature")
    private CredentialSignature credentialSignature;

//    @SerializedName("signature_correctness_proof")
    private SignatureCorrectnessProof signatureCorrectnessProof;

    public Credential() {
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

    public LinkedHashMap<String, AttributeValue> getValues() {
        return values;
    }
    public void setValues(LinkedHashMap<String, AttributeValue> values) {
        this.values = values;
    }

    public CredentialSignature getCredentialSignature() {
        return credentialSignature;
    }
    public void setCredentialSignature(CredentialSignature credentialSignature) {
        this.credentialSignature = credentialSignature;
    }

    public SignatureCorrectnessProof getSignatureCorrectnessProof() {
        return signatureCorrectnessProof;
    }
    public void setSignatureCorrectnessProof(SignatureCorrectnessProof signatureCorrectnessProof) {
        this.signatureCorrectnessProof = signatureCorrectnessProof;
    }

    public String getRevRegDefId() {
        return revRegDefId;
    }

    public void setRevRegDefId(String revRegDefId) {
        this.revRegDefId = revRegDefId;
    }

    public RevocationRegistry getRevReg() {
        return revReg;
    }

    public void setRevReg(RevocationRegistry revReg) {
        this.revReg = revReg;
    }

    public Witness getWitness() {
        return witness;
    }

    public void setWitness(Witness witness) {
        this.witness = witness;
    }

    public void fromJson(String val) {
        GsonWrapper gson = new GsonWrapper();
        Credential result = gson.fromJson(val, Credential.class);

        this.schemaId = result.getSchemaId();
        this.credDefId = result.getCredDefId();
        this.revRegDefId = result.getRevRegDefId();
        this.values = result.getValues();
        this.revReg = result.getRevReg();
        this.witness = result.getWitness();
        this.credentialSignature = result.getCredentialSignature();
        this.signatureCorrectnessProof = result.getSignatureCorrectnessProof();
    }

    public String toJson() {
        GsonWrapper gson = new GsonWrapper();
        return gson.toJson(this);
    }
}
