package org.omnione.did.wallet.zkp.revoc;

import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.json.GsonWrapper;
import org.omnione.did.wallet.zkp.revoc.enums.RegistryType;
import org.omnione.did.wallet.zkp.revoc.utils.RevocationRegistryDefinitionValue;

public class RevocationRegistryDefinition {

    private String id;
    private RegistryType revocDefType;
    private String tag;
    @SerializedName("credDefId")
    private String credentialDefinitionId;
    private RevocationRegistryDefinitionValue value;

    public RevocationRegistryDefinition() {}

    public RevocationRegistryDefinition(String revocationRegistryId,
                                          RegistryType clAccum,
                                          String tag,
                                          String credentialDefinitionId,
                                          RevocationRegistryDefinitionValue revocationRegistryDefinitionValue) {
        this.id = revocationRegistryId;
        this.revocDefType = clAccum;
        this.tag = tag;
        this.credentialDefinitionId = credentialDefinitionId;
        this.value = revocationRegistryDefinitionValue;
    }

    public String getRevocationRegistryId() {
        return id;
    }

    public void setRevocationRegistryId(String revocationRegistryId) {
        this.id = revocationRegistryId;
    }

    public RegistryType getRevocDefType() {
        return revocDefType;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCredentialDefinitionId() {
        return credentialDefinitionId;
    }

    public void setCredentialDefinitionId(String credentialDefinitionId) {
        this.credentialDefinitionId = credentialDefinitionId;
    }

    public RevocationRegistryDefinitionValue getValue() {
        return value;
    }

    public void setValue(RevocationRegistryDefinitionValue value) {
        this.value = value;
    }

    public String toJson() {
        GsonWrapper gson = new GsonWrapper();
        return gson.toJson(this);
    }

}
