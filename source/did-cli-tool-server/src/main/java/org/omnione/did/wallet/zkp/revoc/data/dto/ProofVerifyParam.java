package org.omnione.did.wallet.zkp.revoc.data.dto;

import org.omnione.did.wallet.zkp.data.CredentialDefinition;
import org.omnione.did.wallet.zkp.data.schema.CredentialSchema;
import org.omnione.did.wallet.zkp.revoc.RevocationRegistryDefinition;

public class ProofVerifyParam {

    private CredentialSchema schema;
    private CredentialDefinition credentialDefinition;
    private RevocationRegistryDefinition revocationRegistryDefinition;
    private RevocationRegistryParam revocationRegistry;

    public ProofVerifyParam(Builder builder) {
        schema = builder.schema;
        credentialDefinition = builder.credentialDefinition;
        revocationRegistryDefinition = builder.revocationRegistryDefinition;
        revocationRegistry = builder.revocationRegistry;
    }

    public CredentialSchema getSchema() {
        return schema;
    }

    public CredentialDefinition getCredentialDefinition() {
        return credentialDefinition;
    }

    public RevocationRegistryDefinition getRevocationRegistryDefinition() {
        return revocationRegistryDefinition;
    }

    public RevocationRegistryParam getRevocationRegistry() {
        return revocationRegistry;
    }

    public static class Builder {

        private CredentialSchema schema;
        private CredentialDefinition credentialDefinition;
        private RevocationRegistryDefinition revocationRegistryDefinition;
        private RevocationRegistryParam revocationRegistry;

        public Builder setSchema(CredentialSchema schema) {
            this.schema = schema;
            return this;
        }

        public Builder setCredentialDefinition(CredentialDefinition credentialDefinition) {
            this.credentialDefinition = credentialDefinition;
            return this;
        }

        public Builder setRevocationRegistryDefinition(RevocationRegistryDefinition revocationRegistryDefinition) {
            this.revocationRegistryDefinition = revocationRegistryDefinition;
            return this;
        }

        public Builder setRevocationRegistry(RevocationRegistryParam revocationRegistry) {
            this.revocationRegistry = revocationRegistry;
            return this;
        }

        public ProofVerifyParam build() {
            return new ProofVerifyParam(this);
        }
    }
}
