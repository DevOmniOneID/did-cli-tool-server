package org.omnione.did.wallet.zkp.revoc.data.dto;

import org.omnione.did.wallet.util.json.GsonWrapper;
import org.omnione.did.wallet.zkp.data.CredentialDefinition;
import org.omnione.did.wallet.zkp.data.keypair.KeyCorrectnessProof;

public class CredentialDefinitionInfo {
    private CredentialDefinition credentialDefinition;
    private CredentialDefinitionPrivateKey credentialDefinitionPrivateKey;
    private KeyCorrectnessProof credentialDefinitionCorrectnessProof;

    public CredentialDefinitionInfo() {}

    public CredentialDefinitionInfo(CredentialDefinition credentialDefinition, CredentialDefinitionPrivateKey credentialDefinitionPrivateKey, KeyCorrectnessProof credentialDefinitionCorrectnessProof) {
        this.credentialDefinition = credentialDefinition;
        this.credentialDefinitionPrivateKey = credentialDefinitionPrivateKey;
        this.credentialDefinitionCorrectnessProof = credentialDefinitionCorrectnessProof;
    }

    public CredentialDefinition getCredentialDefinition() {
        return credentialDefinition;
    }

    public void setCredentialDefinition(CredentialDefinition credentialDefinition) {
        this.credentialDefinition = credentialDefinition;
    }

    public CredentialDefinitionPrivateKey getCredentialDefinitionPrivateKey() {
        return credentialDefinitionPrivateKey;
    }

    public void setCredentialDefinitionPrivateKey(CredentialDefinitionPrivateKey credentialDefinitionPrivateKey) {
        this.credentialDefinitionPrivateKey = credentialDefinitionPrivateKey;
    }

    public void fromJson(String val) {
        GsonWrapper gson = new GsonWrapper();
        CredentialDefinitionInfo result = gson.fromJson(val, CredentialDefinitionInfo.class);

        this.credentialDefinition = result.getCredentialDefinition();
        this.credentialDefinitionPrivateKey = result.getCredentialDefinitionPrivateKey();
        this.credentialDefinitionCorrectnessProof = result.getCredentialDefinitionCorrectnessProof();
    }

    public KeyCorrectnessProof getCredentialDefinitionCorrectnessProof() {
        return credentialDefinitionCorrectnessProof;
    }

    public void setCredentialDefinitionCorrectnessProof(KeyCorrectnessProof credentialDefinitionCorrectnessProof) {
        this.credentialDefinitionCorrectnessProof = credentialDefinitionCorrectnessProof;
    }
}
