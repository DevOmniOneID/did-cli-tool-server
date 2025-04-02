package org.omnione.did.wallet.zkp.util.generator;

import org.omnione.did.wallet.zkp.data.CredentialPrimaryKeyPair;
import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPrivateKey;
import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPublicKey;
import org.omnione.did.wallet.zkp.data.keypair.KeyCorrectnessProof;
import org.omnione.did.wallet.zkp.data.keypair.PublicKeyMetadata;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.util.BigIntegerUtil;
import org.omnione.did.wallet.zkp.util.bulider.ChallengeBuilder;

import java.math.BigInteger;
import java.util.LinkedHashMap;

public class KeyCorrectnessProofGenerator {

    //Wrapper Method
    public static KeyCorrectnessProof generateKeyProof(CredentialPrimaryKeyPair keyPair) throws ZkpException {
        return generateKeyProof(keyPair.getPublicKey(), keyPair.getPrivateKey(), keyPair.getPublicKeyMetadata());
    }

    public static KeyCorrectnessProof generateKeyProof(CredentialPrimaryPublicKey publicKey, CredentialPrimaryPrivateKey privateKey, PublicKeyMetadata publicKeyMetadata) throws ZkpException {

        try {
            BigIntegerUtil generator = new BigIntegerUtil();

            final BigInteger n = publicKey.getN();
            final BigInteger s = publicKey.getS();

            final BigInteger p = privateKey.getP();
            final BigInteger q = privateKey.getQ();

            final BigInteger xz = publicKeyMetadata.getXz();
            final BigInteger xz_tilde = generator.generateX(p, q);
            final BigInteger z_tilde = s.modPow(xz_tilde, n);

            LinkedHashMap<String, BigInteger> xr = publicKeyMetadata.getXr();

            LinkedHashMap<String, BigInteger> xr_tilde = new LinkedHashMap<String, BigInteger>();
            LinkedHashMap<String, BigInteger> r_tilde = new LinkedHashMap<String, BigInteger>();
            // TODO 개선필요
            /* c 버전 json parsing시, attr 순서의 문제로 인한 수정
             * blockchain의 credential def를 받아올때 attr json string과 의존
             * djpark0402 20210514
             */
            for (String attrName : publicKey.getR().keySet()) {
//        for (String attrName : xr.keySet()) {
                BigInteger xr_tilde_value = generator.generateX(p, q);
                xr_tilde.put(attrName, xr_tilde_value);
                r_tilde.put(attrName, s.modPow(xr_tilde_value, n));
            }

            //TODO: add 되는 항목들의 순서 고정
            BigInteger c = new ChallengeBuilder()
                    .add(publicKey.getZ())
                    .add(publicKey.getR())
                    .add(z_tilde)
                    .add(r_tilde)
                    .buildWithHashing();

            BigInteger xz_cap = c.multiply(xz).add(xz_tilde);

            LinkedHashMap<String, BigInteger> xr_cap = new LinkedHashMap<String, BigInteger>();
            // TODO 개선필요
            /* c 버전 json parsing시, attr 순서의 문제로 인한 수정
             * blockchain의 credential def를 받아올때 attr json string과 의존
             * djpark0402 20210514
             */
            for (String attrName : publicKey.getR().keySet()) {
//            for (String attrName : xr.keySet()) {
                BigInteger xr_value = xr.get(attrName);
                BigInteger xr_tilde_value = xr_tilde.get(attrName);

                xr_cap.put(attrName, c.multiply(xr_value).add(xr_tilde_value));
            }
            return new KeyCorrectnessProof(c, xz_cap, xr_cap);

        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_ISSUER_GENERATE_CRED_KEY_CORRECTNESS_PROOF_FAIL, "generate key correctness proof");
        }
    }
}
