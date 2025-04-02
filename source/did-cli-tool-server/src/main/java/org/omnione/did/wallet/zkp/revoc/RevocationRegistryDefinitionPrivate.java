package org.omnione.did.wallet.zkp.revoc;

public class RevocationRegistryDefinitionPrivate {

    private RevocationPrivateKey value;

    public RevocationPrivateKey getValue() {
        return value;
    }

    public void setValue(RevocationPrivateKey value) {
        this.value = value;
    }
}
