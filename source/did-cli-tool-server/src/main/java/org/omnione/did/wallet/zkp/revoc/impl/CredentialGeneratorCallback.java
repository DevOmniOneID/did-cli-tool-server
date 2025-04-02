package org.omnione.did.wallet.zkp.revoc.impl;

import org.omnione.did.wallet.zkp.data.Credential;
import org.omnione.did.wallet.zkp.data.credential.NonRevocationCredentialSignature;
import org.omnione.did.wallet.zkp.revoc.data.dto.RevocationRegistryDefinitionInfo;
import org.omnione.did.wallet.zkp.revoc.utils.RevocationRegistryDelta;
import org.omnione.did.wallet.zkp.revoc.utils.Witness;

public interface CredentialGeneratorCallback {
    void onComplete(Credential credential, RevocationRegistryDelta delta, RevocationRegistryDefinitionInfo revocationRegistryDefinitionInfo);
}
