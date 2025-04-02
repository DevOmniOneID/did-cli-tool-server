package org.omnione.did.wallet.zkp.util.generator;

import org.omnione.did.wallet.zkp.data.CredentialPrimaryKeyPair;
import org.omnione.did.wallet.zkp.data.credential.CredentialSignature;
import org.omnione.did.wallet.zkp.data.credential.SignatureCorrectnessProof;
import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPrivateKey;
import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPublicKey;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.util.BigIntegerUtil;
import org.omnione.did.wallet.zkp.util.bulider.ChallengeBuilder;

import java.math.BigInteger;

public class CredentialSignatureProofGenerator {

    //Wrapper method
    public static SignatureCorrectnessProof generateSignatureCorrectnessProof(CredentialSignature credSign, CredentialPrimaryKeyPair credentialKeyPair, BigInteger nonce) throws ZkpException {
        return generateSignatureCorrectnessProof(credSign, credentialKeyPair.getPublicKey(), credentialKeyPair.getPrivateKey(), nonce);
    }

    public static SignatureCorrectnessProof generateSignatureCorrectnessProof(CredentialSignature credSign,
                                                                              CredentialPrimaryPublicKey publicKey,
                                                                              CredentialPrimaryPrivateKey privateKey,
                                                                              BigInteger nonce) throws ZkpException {
        BigIntegerUtil generator = new BigIntegerUtil();

        final BigInteger a = credSign.getPrimaryCredential().getA();
        final BigInteger e = credSign.getPrimaryCredential().getE();
        final BigInteger q = credSign.getPrimaryCredential().getQ();

        final BigInteger n = publicKey.getN();
        final BigInteger pq = privateKey.getP().multiply(privateKey.getQ());

        BigInteger r = generator.createRandom(pq);
        BigInteger a_tidle = q.modPow(r, n);

        //add 인자들의 순서는 고정되어야 한다
        BigInteger c_prime = new ChallengeBuilder()
                .add(q)
                .add(a)
                .add(a_tidle)
                .add(nonce)
                .buildWithHashing();

        BigInteger se = r.subtract(c_prime.multiply(e.modInverse(pq).mod(pq))).mod(pq);

        return new SignatureCorrectnessProof(c_prime, se);
    }
}
