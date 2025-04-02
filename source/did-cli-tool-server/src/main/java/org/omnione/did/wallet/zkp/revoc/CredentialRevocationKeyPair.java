package org.omnione.did.wallet.zkp.revoc;

public class CredentialRevocationKeyPair {

    private CredentialRevocationPrivateKey privateKey;
    private CredentialRevocationPublicKey publicKey;

    public CredentialRevocationKeyPair(CredentialRevocationPrivateKey privateKey,
                                       CredentialRevocationPublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public CredentialRevocationPrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(CredentialRevocationPrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public CredentialRevocationPublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(CredentialRevocationPublicKey publicKey) {
        this.publicKey = publicKey;
    }
}
