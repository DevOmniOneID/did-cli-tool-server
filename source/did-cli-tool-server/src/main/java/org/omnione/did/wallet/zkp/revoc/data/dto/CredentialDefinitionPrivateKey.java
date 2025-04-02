package org.omnione.did.wallet.zkp.revoc.data.dto;

import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPrivateKey;
import org.omnione.did.wallet.zkp.revoc.CredentialRevocationPrivateKey;

public class CredentialDefinitionPrivateKey {

    private CredentialPrimaryPrivateKey primary;
    private CredentialRevocationPrivateKey revocation;

    public CredentialDefinitionPrivateKey(CredentialPrimaryPrivateKey primary, CredentialRevocationPrivateKey revocation) {
        this.primary = primary;
        this.revocation = revocation;
    }

    public CredentialPrimaryPrivateKey getPrimary() {
        return primary;
    }

    public void setPrimary(CredentialPrimaryPrivateKey primary) {
        this.primary = primary;
    }

    public CredentialRevocationPrivateKey getRevocation() {
        return revocation;
    }

    public void setRevocation(CredentialRevocationPrivateKey revocation) {
        this.revocation = revocation;
    }
}
