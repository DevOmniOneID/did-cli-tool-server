package org.omnione.did.wallet.zkp.data.credentialdefinition;

import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPublicKey;
import org.omnione.did.wallet.zkp.revoc.CredentialRevocationPublicKey;

public class CredentialDefinitionValue {

    private CredentialPrimaryPublicKey primary;
    private CredentialRevocationPublicKey revocation;

    public CredentialPrimaryPublicKey getPrimary() {
        return primary;
    }
    public void setPrimary(CredentialPrimaryPublicKey primary) {
        this.primary = primary;
    }

    public CredentialRevocationPublicKey getRevocation() {
        return revocation;
    }
    public void setRevocation(CredentialRevocationPublicKey revocation) {
        this.revocation = revocation;
    }
}
