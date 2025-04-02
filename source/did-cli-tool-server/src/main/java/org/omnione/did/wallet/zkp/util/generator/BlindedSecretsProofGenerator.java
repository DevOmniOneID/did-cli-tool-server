package org.omnione.did.wallet.zkp.util.generator;

import org.omnione.did.wallet.zkp.ZkpConstants;
import org.omnione.did.wallet.zkp.data.MasterSecret;
import org.omnione.did.wallet.zkp.data.credentialrequest.BlindedCredentialSecrets;
import org.omnione.did.wallet.zkp.data.credentialrequest.BlindedCredentialSecretsCorrectnessProof;
import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPublicKey;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.util.BigIntegerUtil;
import org.omnione.did.wallet.zkp.util.bulider.ChallengeBuilder;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class BlindedSecretsProofGenerator {

//    //Wrapper Method
//    public static BlindedCredentialSecretsCorrectnessProof generateBlindedSecretsProof(CredentialPrimaryPublicKey publicKey, BlindedCredentialSecrets blindedSecret,
//                                                                                       MasterSecret masterSecret, BigInteger nonce) throws ZkpException {
//        //TODO: wallet을 masterSecretId로 조회하여 획득
////        BigInteger masterSecret = TestConstants.MASTER_SECRET;
//        return generateBlindedSecretsProof(publicKey, blindedSecret, masterSecret, nonce);
//    }

    public static BlindedCredentialSecretsCorrectnessProof generateBlindedSecretsProof(CredentialPrimaryPublicKey publicKey,
                                                                                       BlindedCredentialSecrets blindedSecret,
                                                                                       MasterSecret masterSecret,
                                                                                       BigInteger nonce) throws ZkpException {
        try {
            BigIntegerUtil generator = new BigIntegerUtil();

            final BigInteger s = publicKey.getS();
            final BigInteger n = publicKey.getN();
            final Map<String, BigInteger> r = publicKey.getR();

            //TODO: 상수값 검증 필요
            BigInteger v_dash_tilde = generator.createRandomBigInteger(ZkpConstants.LARGE_VPRIME_TILDE);
            BigInteger m_tilde = generator.createRandomBigInteger(ZkpConstants.LARGE_MTILDE);
            //1 + Fiat-Shamir Heuristic + security parameter + attribute size

            BigInteger u_tilde = s.modPow(v_dash_tilde, n);
            u_tilde = u_tilde.multiply(r.get(ZkpConstants.MASTER_SECRET_KEY).modPow(m_tilde, n));

            u_tilde = u_tilde.mod(n);

            //add 항목들의 순서는 고정되어야 한다
            BigInteger c = new ChallengeBuilder()
                    .add(blindedSecret.getU())
                    .add(u_tilde)
                    .add(nonce)
                    .buildWithHashing();

            BigInteger v_dash_cap = c.multiply(blindedSecret.getVPrime()).add(v_dash_tilde);

            LinkedHashMap<String, BigInteger> mCaps = new LinkedHashMap<String, BigInteger>();
            //TODO: commitment 타입때 사용
            TreeMap<String, BigInteger> rCaps = new TreeMap<String, BigInteger>();


            //TODO: hidden Value들에 대한 재고려 필요 (현재 1개만 존재)
            BigInteger mCap = c.multiply(masterSecret.getMasterSecret()).add(m_tilde);

            mCaps.put(ZkpConstants.MASTER_SECRET_KEY, mCap);

            BlindedCredentialSecretsCorrectnessProof secretProof = new BlindedCredentialSecretsCorrectnessProof();
            secretProof.setC(c);
            secretProof.setVDashCap(v_dash_cap);
            secretProof.setMCaps(mCaps);
            secretProof.setRCaps(rCaps);

            return secretProof;
        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_NEW_BLINDED_CRED_SECRETS_CORRECTNESS_PROOF_FAIL);
        }
    }
}
