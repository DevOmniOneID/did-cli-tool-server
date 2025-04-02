package org.omnione.did.wallet.zkp.revoc.data.dto;

import org.omnione.did.wallet.zkp.data.CredentialDefinition;
import org.omnione.did.wallet.zkp.data.schema.CredentialSchema;
import org.omnione.did.wallet.zkp.revoc.utils.RevocationState;

public class ProofParam {

    private ReferentInfo referentInfo;
    private CredentialSchema schema;
    private CredentialDefinition credDef;
    private RevocationState revState;

    public ProofParam(Builder builder) {
        referentInfo = builder.referentInfo;
        schema = builder.schema;
        credDef = builder.credDef;
        revState = builder.revState;
    }

    public ReferentInfo getReferentInfo() {
        return referentInfo;
    }

    public CredentialSchema getSchema() {
        return schema;
    }

    public CredentialDefinition getCredDef() {
        return credDef;
    }

    public RevocationState getRevState() {
        return revState;
    }

    public static class Builder {

        private ReferentInfo referentInfo;
        private CredentialSchema schema;
        private CredentialDefinition credDef;
        private RevocationState revState;

        public Builder() {

        }

        public Builder setReferentInfo(ReferentInfo referentInfo) {
            this.referentInfo = referentInfo;
            return this;
        }

        public Builder setSchema(CredentialSchema schema) {
            this.schema = schema;
            return this;
        }

        public Builder setCredDef(CredentialDefinition credDef) {
            this.credDef = credDef;
            return this;
        }

        public Builder setRevState(RevocationState revState) {
            this.revState = revState;
            return this;
        }

        public ProofParam build() {
            return new ProofParam(this);
        }
    }
}
