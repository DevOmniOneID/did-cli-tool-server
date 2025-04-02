package org.omnione.did.wallet.zkp.util.helper;

import org.omnione.did.wallet.zkp.data.CredentialOffer;
import org.omnione.did.wallet.zkp.data.keypair.KeyCorrectnessProof;

import java.math.BigInteger;

public class CredentialOfferHelper {

    public static CredentialOffer generateCredentialOffer(String schemaId,
                                                          String credDefId,
                                                          BigInteger nonce,
                                                          KeyCorrectnessProof keyCorrectnessProof) {

        CredentialOffer credOffer = new CredentialOffer();
        credOffer.setSchemaId(schemaId);
        credOffer.setCredDefId(credDefId);
        credOffer.setNonce(nonce);
        credOffer.setKeyCorrectnessProof(keyCorrectnessProof);
        //TODO: MethodName 미지원
        return credOffer;
    }
}
