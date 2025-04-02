package org.omnione.did.wallet.zkp.revoc;

import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPublicKey;

public class CredentialPublicKey {

    private CredentialPrimaryPublicKey publicKey;
    private CredentialRevocationPublicKey revocationKey;

    public CredentialPublicKey(CredentialPrimaryPublicKey pPublicKey, CredentialRevocationPublicKey rPublicKey) {
        this.publicKey = pPublicKey;
        this.revocationKey = rPublicKey;
    }

    public CredentialPrimaryPublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(CredentialPrimaryPublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public CredentialRevocationPublicKey getRevocationKey() {
        return revocationKey;
    }

    public void setRevocationKey(CredentialRevocationPublicKey revocationKey) {
        this.revocationKey = revocationKey;
    }
}
