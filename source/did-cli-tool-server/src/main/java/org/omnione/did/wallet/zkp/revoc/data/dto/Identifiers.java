package org.omnione.did.wallet.zkp.revoc.data.dto;

import com.google.gson.annotations.SerializedName;

public class Identifiers {
//    @SerializedName("cred_def_id")
    private String credDefId;

//    @SerializedName("schema_id")
    private String schemaId;

//    @SerializedName("rev_reg_id")
    private String revRegId;

    @SerializedName("timestamp")
    private String timestemp;

    public Identifiers(String schemaId, String credDefId, String revRegId, String timestemp) {

        this.schemaId = schemaId;
        this.credDefId = credDefId;
        this.revRegId = revRegId;
        this.timestemp = timestemp;
    }

    public String getCredDefId() {
        return credDefId;
    }

    public void setCredDefId(String credDefId) {
        this.credDefId = credDefId;
    }

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public String getRevRegId() {
        return revRegId;
    }

    public void setRevRegId(String revRegId) {
        this.revRegId = revRegId;
    }

    public String getTimestemp() {
        return timestemp;
    }

    public void setTimestemp(String timestemp) {
        this.timestemp = timestemp;
    }
}
