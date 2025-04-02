package org.omnione.did.wallet.zkp.data.subproof;

import org.omnione.did.wallet.zkp.data.SubProofRequest;
import org.omnione.did.wallet.zkp.data.proof.CredentialValues;
import org.omnione.did.wallet.zkp.data.schema.CredentialSchema;
import org.omnione.did.wallet.zkp.data.schema.NonCredentialSchema;
import org.omnione.did.wallet.zkp.data.subproof.primaryproof.PrimaryInitProof;
import org.omnione.did.wallet.zkp.revoc.NonRevocInitProof;

public class InitProof {

    private PrimaryInitProof primaryInitProof;
    private NonRevocInitProof nonRevocInitProof;
    private CredentialValues credentialValues;
    private SubProofRequest subProofRequest;
    private CredentialSchema credentialSchema;
    private NonCredentialSchema nonCredentialSchema;

    public InitProof(PrimaryInitProof primaryInitProof,
                     NonRevocInitProof nonRevocInitProof,
                     CredentialValues credentialValues,
                     SubProofRequest subProofRequest,
                     CredentialSchema credentialSchema,
                     NonCredentialSchema nonCredentialSchema) {

        this.primaryInitProof = primaryInitProof;
        this.nonRevocInitProof = nonRevocInitProof;
        this.credentialValues = credentialValues;
        this.subProofRequest = subProofRequest;
        this.credentialSchema = credentialSchema;
        this.nonCredentialSchema = nonCredentialSchema;
    }

    public InitProof(PrimaryInitProof primaryInitProof,
                     CredentialValues credentialValues,
                     SubProofRequest subProofRequest,
                     CredentialSchema credentialSchema,
                     NonCredentialSchema nonCredentialSchema) {

        this.primaryInitProof = primaryInitProof;
        this.credentialValues = credentialValues;
        this.subProofRequest = subProofRequest;
        this.credentialSchema = credentialSchema;
        this.nonCredentialSchema = nonCredentialSchema;
    }

    public PrimaryInitProof getPrimaryInitProof() {
        return primaryInitProof;
    }

    public CredentialValues getCredentialValues() {
        return credentialValues;
    }

    public SubProofRequest getSubProofRequest() {
        return subProofRequest;
    }

    public CredentialSchema getCredentialSchema() {
        return credentialSchema;
    }

    public NonCredentialSchema getNonCredentialSchema() {
        return nonCredentialSchema;
    }

    public NonRevocInitProof getNonRevocInitProof() {
        return nonRevocInitProof;
    }

    public void setNonRevocInitProof(NonRevocInitProof nonRevocInitProof) {
        this.nonRevocInitProof = nonRevocInitProof;
    }
}
