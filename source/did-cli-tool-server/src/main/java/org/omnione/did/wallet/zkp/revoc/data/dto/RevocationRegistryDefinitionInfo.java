package org.omnione.did.wallet.zkp.revoc.data.dto;

import org.omnione.did.wallet.util.json.GsonWrapper;
import org.omnione.did.wallet.zkp.revoc.RevocationPrivateKey;
import org.omnione.did.wallet.zkp.revoc.RevocationRegistry;
import org.omnione.did.wallet.zkp.revoc.RevocationRegistryDefinition;

public class RevocationRegistryDefinitionInfo {

    private RevocationRegistryDefinition revocationRegistryDefinition;
    private RevocationPrivateKey revocationPrivateKey;
    private RevocationRegistryInfo revocationRegistryInfo;
    private RevocationRegistry revocationRegistry;

    public RevocationRegistryDefinitionInfo() {}

    public RevocationRegistryDefinitionInfo(RevocationRegistryDefinition revocationRegistryDefinition,
                                            RevocationPrivateKey revocationPrivateKey,
                                            RevocationRegistry revocationRegistry,
                                            RevocationRegistryInfo revocationRegistryInfo) {

        this.revocationRegistryDefinition = revocationRegistryDefinition;
        this.revocationPrivateKey = revocationPrivateKey;
        this.revocationRegistry = revocationRegistry;
        this.revocationRegistryInfo = revocationRegistryInfo;
    }


    public RevocationRegistryDefinition getRevocationRegistryDefinition() {
        return revocationRegistryDefinition;
    }

    public void setRevocationRegistryDefinition(RevocationRegistryDefinition revocationRegistryDefinition) {
        this.revocationRegistryDefinition = revocationRegistryDefinition;
    }

    public RevocationPrivateKey getRevocationPrivateKey() {
        return revocationPrivateKey;
    }

    public void setRevocationPrivateKey(RevocationPrivateKey revocationPrivateKey) {
        this.revocationPrivateKey = revocationPrivateKey;
    }

    public RevocationRegistryInfo getRevocationRegistryInfo() {
        return revocationRegistryInfo;
    }

    public void setRevocationRegistryInfo(RevocationRegistryInfo revocationRegistryInfo) {
        this.revocationRegistryInfo = revocationRegistryInfo;
    }

    public RevocationRegistry getRevocationRegistry() {
        return revocationRegistry;
    }

    public void setRevocationRegistry(RevocationRegistry revocationRegistry) {
        this.revocationRegistry = revocationRegistry;
    }

    public void fromJson(String val) {

        GsonWrapper gson = new GsonWrapper();
        RevocationRegistryDefinitionInfo result = gson.fromJson(val, RevocationRegistryDefinitionInfo.class);

        this.revocationRegistryDefinition = result.getRevocationRegistryDefinition();
        this.revocationPrivateKey = result.getRevocationPrivateKey();
        this.revocationRegistry = result.getRevocationRegistry();
        this.revocationRegistryInfo = result.getRevocationRegistryInfo();
    }

    public String toJson() {
        GsonWrapper gson = new GsonWrapper();
        return gson.toJson(this);
    }
}
