package org.omnione.did.wallet.zkp.revoc.utils;

import org.omnione.did.wallet.zkp.revoc.RevocationPublicKey;

public class RevocationRegistryDefinitionValuePublicKeys {

    private RevocationPublicKey accumKey;

    public RevocationRegistryDefinitionValuePublicKeys(RevocationPublicKey revocPublicKey) {
        this.accumKey = revocPublicKey;
    }

    public RevocationPublicKey getAccumKey() {
        return accumKey;
    }
    public void setAccumKey(RevocationPublicKey accumKey) {
        this.accumKey = accumKey;
    }
}
