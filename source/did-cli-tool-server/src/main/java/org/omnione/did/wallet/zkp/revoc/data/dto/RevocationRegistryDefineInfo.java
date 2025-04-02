package org.omnione.did.wallet.zkp.revoc.data.dto;

import org.omnione.did.wallet.util.json.GsonWrapper;

public class RevocationRegistryDefineInfo {

    private RevocationRegistryDefine revocationRegistryDefinition;

    public RevocationRegistryDefineInfo() {
    }
    public RevocationRegistryDefine getRevocationRegistryDefinition() {
        return revocationRegistryDefinition;
    }

    public void setRevocationRegistryDefinition(RevocationRegistryDefine revocationRegistryDefinition) {
        this.revocationRegistryDefinition = revocationRegistryDefinition;
    }

    public void fromJson(String val) {

        GsonWrapper gson = new GsonWrapper();
        RevocationRegistryDefineInfo result = gson.fromJson(val, RevocationRegistryDefineInfo.class);
        this.revocationRegistryDefinition = result.getRevocationRegistryDefinition();
    }

    public String toJson() {
        GsonWrapper gson = new GsonWrapper();
        return gson.toJson(this);
    }
}
