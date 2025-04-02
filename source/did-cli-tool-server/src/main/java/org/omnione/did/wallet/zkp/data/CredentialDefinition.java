package org.omnione.did.wallet.zkp.data;

import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.json.GsonWrapper;
import org.omnione.did.wallet.zkp.data.credentialdefinition.CredentialDefinitionValue;
import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPublicKey;
import org.omnione.did.wallet.zkp.enums.CredentialType;
import org.omnione.did.wallet.zkp.revoc.CredentialRevocationPublicKey;

public class CredentialDefinition {

    private String id;

//    @SerializedName("schema_id")
    private String schemaId;

    private String ver;

    private CredentialType type;

    private CredentialDefinitionValue value = new CredentialDefinitionValue();

    private String tag;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getSchemaId() {
        return schemaId;
    }
    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public String getVer() {
        return ver;
    }
    public void setVer(String ver) {
        this.ver = ver;
    }

    public CredentialType getType() {
        return type;
    }
    public void setType(CredentialType type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }

    public CredentialDefinitionValue getValue() {
        return value;
    }
    public void setValue(CredentialDefinitionValue value) {
        this.value = value;
    }
    public void setValue(CredentialPrimaryPublicKey publicKey) {
        this.setPrimaryKey(publicKey);
    }

    // -- utils
    public void setPrimaryKey(CredentialPrimaryPublicKey publicKey) {
        this.value.setPrimary(publicKey);
    }
    public void setRevocationKey(CredentialRevocationPublicKey revocationKey) {
        this.value.setRevocation(revocationKey);
    }

    public String toJson() {
        GsonWrapper gson = new GsonWrapper();
        return gson.toJson(this);
    }
}
