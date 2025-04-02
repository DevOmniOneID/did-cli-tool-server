package org.omnione.did.wallet.zkp.util.helper;

import org.omnione.did.wallet.zkp.data.CredentialOffer;
import org.omnione.did.wallet.zkp.data.CredentialPrimaryKeyPair;
import org.omnione.did.wallet.zkp.data.CredentialRequest;
import org.omnione.did.wallet.zkp.data.MasterSecret;
import org.omnione.did.wallet.zkp.data.credentialrequest.BlindedCredentialSecrets;
import org.omnione.did.wallet.zkp.data.credentialrequest.BlindedCredentialSecretsCorrectnessProof;
import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPublicKey;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.CredentialRevocationPublicKey;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpLogger;
import org.omnione.did.wallet.zkp.util.generator.BlindedSecretsGenerator;
import org.omnione.did.wallet.zkp.util.generator.BlindedSecretsProofGenerator;

import java.math.BigInteger;

public class CredentialRequestHelper {

    public static CredentialRequest generateCredentialRequest(CredentialPrimaryPublicKey credentialPublicKey,
                                                              CredentialRevocationPublicKey credentialRevocationPublicKey,
                                                              String did,
                                                              MasterSecret masterSecret,
                                                              CredentialOffer credOffer,
                                                              BigInteger nonce) throws ZkpException {

        final BlindedCredentialSecrets blindedCredSecret = generateCredentialSecret(credentialPublicKey, credentialRevocationPublicKey, masterSecret);
        ZkpLogger.debug("generateCredentialRequest prover nonce: "+nonce);
        final BlindedCredentialSecretsCorrectnessProof blindedCredSecretProof =
                generateCredentialSecretProof(credentialPublicKey, blindedCredSecret, credOffer, masterSecret);

        CredentialRequest credReq = new CredentialRequest();
        credReq.setBlindedMs(blindedCredSecret);
        credReq.setBlindedMsProof(blindedCredSecretProof);
        credReq.setCredDefId(credOffer.getCredDefId());
        credReq.setProverDid(did);
        credReq.setNonce(nonce);
        return credReq;
    }


    private static BlindedCredentialSecrets generateCredentialSecret(CredentialPrimaryPublicKey credentialPublicKey,
                                                                     CredentialRevocationPublicKey credentialRevocationPublicKey,
                                                                     MasterSecret masterSecret) throws ZkpException {
        return BlindedSecretsGenerator.generateBlindedSecrets(credentialPublicKey, credentialRevocationPublicKey, masterSecret);
    }

//    //TODO: 타입 이름과 메소드 이름이 너무 길다
//    //Wrapper Method
    private static BlindedCredentialSecretsCorrectnessProof generateCredentialSecretProof(CredentialPrimaryKeyPair credentialKeyPair,
                                                                                          BlindedCredentialSecrets credSecret,
                                                                                          CredentialOffer credOffer,
                                                                                          MasterSecret masterSecret) throws ZkpException {

        return generateCredentialSecretProof(credentialKeyPair.getPublicKey(), credSecret, credOffer.getNonce(), masterSecret);
    }
//    //Wrapper Method
    private static BlindedCredentialSecretsCorrectnessProof generateCredentialSecretProof(
            CredentialPrimaryPublicKey publicKey,
            BlindedCredentialSecrets credSecret,
            CredentialOffer credOffer,
            MasterSecret masterSecret) throws ZkpException {

        return generateCredentialSecretProof(publicKey, credSecret, credOffer.getNonce(), masterSecret);
    }

    private static BlindedCredentialSecretsCorrectnessProof generateCredentialSecretProof(
            CredentialPrimaryPublicKey publicKey, BlindedCredentialSecrets credSecret, BigInteger nonce, MasterSecret masterSecret) throws ZkpException {

        return BlindedSecretsProofGenerator.generateBlindedSecretsProof(publicKey, credSecret, masterSecret, nonce);
    }
}
