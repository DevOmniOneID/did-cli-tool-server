package org.omnione.did.wallet.zkp.data;

import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPrivateKey;
import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPublicKey;
import org.omnione.did.wallet.zkp.data.keypair.PublicKeyMetadata;

public class CredentialPrimaryKeyPair {

    private CredentialPrimaryPrivateKey privateKey;
    private CredentialPrimaryPublicKey publicKey;
    private PublicKeyMetadata publicKeyMetadata;

    public CredentialPrimaryKeyPair(CredentialPrimaryPublicKey publicKey, CredentialPrimaryPrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public CredentialPrimaryKeyPair(CredentialPrimaryPublicKey publicKey, CredentialPrimaryPrivateKey privateKey, PublicKeyMetadata publicKeyMetadata) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.publicKeyMetadata = publicKeyMetadata;
    }

    public CredentialPrimaryPublicKey getPublicKey() {
        return publicKey;
    }
    public CredentialPrimaryPrivateKey getPrivateKey() {
        return privateKey;
    }
    public PublicKeyMetadata getPublicKeyMetadata() {
        return publicKeyMetadata;
    }
}