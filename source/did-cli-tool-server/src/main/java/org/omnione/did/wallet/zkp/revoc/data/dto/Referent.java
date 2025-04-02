package org.omnione.did.wallet.zkp.revoc.data.dto;

import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.data.attribute.AttributeValue;
import org.omnione.did.wallet.zkp.revoc.data.dto.ReferentAttributeValue;

import java.util.LinkedHashMap;
import java.util.List;

public class Referent {
    @SerializedName("schema_id")
    private String schemaId;

    @SerializedName("cred_def_id")
    private String credDefId;

    @SerializedName("rev_reg_id")
    private String revRegDefId;

    @SerializedName("rev_id")
    private String revId;

    @SerializedName("attrs")
    private LinkedHashMap<String, ReferentAttributeValue> attributes;
    public Referent() {}


    public String getSchemaId() {
        return schemaId;
    }

    public String getCredDefId() {
        return credDefId;
    }

    public String getRevRegDefId() {
        return revRegDefId;
    }

    public String getRevId() {
        return revId;
    }

    public LinkedHashMap<String, ReferentAttributeValue> getAttributes() {
        return attributes;
    }


    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public void setCredDefId(String credDefId) {
        this.credDefId = credDefId;
    }

    public void setRevRegDefId(String revRegDefId) {
        this.revRegDefId = revRegDefId;
    }

    public void setRevId(String revId) {
        this.revId = revId;
    }

    public void setAttributes(LinkedHashMap<String, ReferentAttributeValue> attributes) {
        this.attributes = attributes;
    }

}
