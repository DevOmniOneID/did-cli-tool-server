package org.omnione.did.wallet.zkp.util.verifier;

import org.omnione.did.wallet.zkp.data.credentialrequest.BlindedCredentialSecrets;
import org.omnione.did.wallet.zkp.data.credentialrequest.BlindedCredentialSecretsCorrectnessProof;
import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPublicKey;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpLogger;
import org.omnione.did.wallet.zkp.util.bulider.ChallengeBuilder;

import java.math.BigInteger;
import java.util.LinkedHashMap;

public class CredentialRequestVerifier {

    public static boolean verify(CredentialPrimaryPublicKey publicKey,
                                 BlindedCredentialSecrets secrets,
                                 BlindedCredentialSecretsCorrectnessProof proof,
                                 BigInteger nonce) throws ZkpException {

        final BigInteger s = publicKey.getS();
        final BigInteger n = publicKey.getN();
        final LinkedHashMap<String, BigInteger> r = publicKey.getR();

        final BigInteger u = secrets.getU();

        final BigInteger proof_c = proof.getC();
        final BigInteger v_dash_cap = proof.getVDashCap();
        final LinkedHashMap<String, BigInteger> m_caps = proof.getmCaps();

        BigInteger u_cap = u.modInverse(n).modPow(proof_c, n).multiply(s.modPow(v_dash_cap, n));

        for (String key : r.keySet()) {
            if (m_caps.containsKey(key)) {
                BigInteger m_cap = m_caps.get(key);
                u_cap = u_cap.multiply(r.get(key).modPow(m_cap, n));
            }
        }
        //TODO: 수식 확인 필요
        u_cap = u_cap.mod(n);

        //TODO: challenge연산 실패시
        // exception throw를 할 것인가, log만 남길 것인가 판단 필요
        BigInteger c_cap = new ChallengeBuilder()
                            .add(u)
                            .add(u_cap)
                            .add(nonce)
                            .buildWithHashing();


        if (c_cap != null && c_cap.equals(proof_c)) {
            ZkpLogger.debug("verifyCredentialRequest c_cap and proof_c match");
            return true;
        }
        // OMNI_ERROR_ZKP_ISSUER_CHECK_BLINDED_SECRETS_CORRECTNESS_PROOF_FAIL
        ZkpLogger.info("verify CredentialRequest c_cap and proof_c not match\n" + "c_cap :"+c_cap.toString()+"\n" + "proof_c :"+proof_c.toString());
//        throw new ZkpException(ErrorCode.OMNI_ERROR_ZKP_BIG_NUMBER_COMPARE_FAIL, "verify CredentialRequest c_cap and proof_c not match");
        return false;
    }
}
