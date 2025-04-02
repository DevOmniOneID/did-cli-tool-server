package org.omnione.did.wallet.zkp.revoc.data.dto;

import org.omnione.did.wallet.util.json.GsonWrapper;

public class RevocationRegistryDefine {

    private String id;

    public RevocationRegistryDefine() {}

    public String getRevocationRegistryId() {
        return id;
    }

    public void setRevocationRegistryId(String revocationRegistryId) {
        this.id = revocationRegistryId;
    }

    public String toJson() {
        GsonWrapper gson = new GsonWrapper();
        return gson.toJson(this);
    }
}
