package org.omnione.did.wallet.zkp.revoc.data.dto;

import com.google.gson.annotations.SerializedName;

public class AttrSubReferent {

    private String raw;
//    @SerializedName("cred_id")
    @SerializedName("credId")
    private String credentialId;

//    @SerializedName("cred_def_id")
    @SerializedName("credDefId")
    private String credentialDefId;

    public AttrSubReferent(String raw, String credentialId, String credentialDefId) {
        this.raw = raw;
        this.credentialId = credentialId;
        this.credentialDefId = credentialDefId;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public String getCredentialDefId() {
        return credentialDefId;
    }

    public void setCredentialDefId(String credentialDefId) {
        this.credentialDefId = credentialDefId;
    }
}
