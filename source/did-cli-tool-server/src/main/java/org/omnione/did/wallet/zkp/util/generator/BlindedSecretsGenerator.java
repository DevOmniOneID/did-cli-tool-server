package org.omnione.did.wallet.zkp.util.generator;

import org.omnione.did.wallet.zkp.ZkpConstants;
import org.omnione.did.wallet.zkp.data.MasterSecret;
import org.omnione.did.wallet.zkp.data.credentialrequest.BlindedCredentialSecrets;
import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPublicKey;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.CredentialRevocationPublicKey;
import org.omnione.did.wallet.zkp.revoc.RevocationBlindedCredetialSecretsFactors;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpSetting;
import org.omnione.did.wallet.zkp.util.BigIntegerUtil;
import org.omnione.did.wallet.zkp.util.CommitmentHelper;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BlindedSecretsGenerator {

//    public static BlindedCredentialSecrets generateBlindedSecrets(CredentialPrimaryPublicKey keyPair, CredentialRevocationPublicKey credentialRevocationPublicKey, String masterSecretId) {
//
//        //TODO: wallet을 masterSecretId로 조회하여 획득
//        BigInteger masterSecret = TestConstants.MASTER_SECRET;
//
//        return generateBlindedSecrets(keyPair, credentialRevocationPublicKey, masterSecret);
//    }

    // P1. generate U, vPrime, Ur and vrPrime with prover-generated values
    public static BlindedCredentialSecrets generateBlindedSecrets(CredentialPrimaryPublicKey credentialPublicKey,
                                                                  CredentialRevocationPublicKey credentialRevocationPublicKey,
                                                                  MasterSecret masterSecret) throws ZkpException {
        try {
            /**
             * 2.3 Pseudonym registration
             * For the master secret m1 and Issuer’s public key pkI Prover
             * 1. Generates random r < ρ and computes nym = gm1 hr (mod Γ). 2. Sends nym to the Issuer.
             * */
            BigIntegerUtil generator = new BigIntegerUtil();

            final BigInteger s = credentialPublicKey.getS();
            final BigInteger n = credentialPublicKey.getN();
            final Map<String, BigInteger> r = credentialPublicKey.getR();

            RevocationBlindedCredetialSecretsFactors revocBlindedCredetialSecretsFactors
                    = ZkpSetting.getInstance().isSupportedRevocation() ? new RevocationBlindedCredetialSecretsFactors(credentialRevocationPublicKey) : null;

            BigInteger v_prime = generator.createRandomBigInteger(ZkpConstants.LARGE_VPRIME);

            /**
                issuer의 공개키(pk)와 masterSecret(m1)을 이용하여 nym을 생성
                generates r < p, nym = g^m1 * h^r (mod T)
                u : nym
                u = r1^m1 * s^vPrime (mod n)
             */
            BigInteger u = CommitmentHelper.commitment(s, v_prime,r.get(ZkpConstants.MASTER_SECRET_KEY),masterSecret.getMasterSecret(),n);
            // s.modPow(v_prime, n).multiply(r.get(ZkpConstants.MASTER_SECRET_KEY).modPow(masterSecret.getMasterSecret(), n)).mod(n);


            List<String> hiddenAttrs = new LinkedList<String>();
            hiddenAttrs.add(ZkpConstants.MASTER_SECRET_KEY);

            // TODO: committedAttrs 고려 필요
            BlindedCredentialSecrets blindedCredSecret =
                    new BlindedCredentialSecrets(u,
                            ZkpSetting.getInstance().isSupportedRevocation() ? revocBlindedCredetialSecretsFactors.getVrPrime() : null,
                            ZkpSetting.getInstance().isSupportedRevocation() ? revocBlindedCredetialSecretsFactors.getUr() : null,
                            v_prime,
                            hiddenAttrs, null);

            return blindedCredSecret;
        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_NEW_BLINDED_PRIMARY_CRED_SECRETS_FACTORS_FAIL);
        }
    }
}
