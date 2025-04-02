package org.omnione.did.wallet.zkp.util.verifier;

import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPublicKey;
import org.omnione.did.wallet.zkp.data.keypair.KeyCorrectnessProof;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpLogger;
import org.omnione.did.wallet.zkp.util.CommitmentHelper;
import org.omnione.did.wallet.zkp.util.bulider.ChallengeBuilder;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Vector;

public class KeyPairVerifier {

    public static boolean verify(CredentialPrimaryPublicKey publicKey, KeyCorrectnessProof keyProof) throws ZkpException {

        final BigInteger n = publicKey.getN();
        final BigInteger s = publicKey.getS();
        final BigInteger z = publicKey.getZ();
        final LinkedHashMap<String, BigInteger> r = publicKey.getR();

        final BigInteger proof_c = keyProof.getC();
        final BigInteger xz_cap = keyProof.getXzCap();
        final LinkedHashMap<String, BigInteger> xr_cap = keyProof.getXrCap();

        //z_cap 연산
        BigInteger z_cap = CommitmentHelper.commitment(z.modInverse(n), proof_c, s, xz_cap, n);

        //r_cap 연산
        Vector<BigInteger> r_cap = new Vector<BigInteger>();

        // TODO 개선필요
        /* c 버전 json parse시, attr 순서의 문제로 인한 수정
         * blockchain의 credential def를 받아올때 attr json string 과 의존
         * djpark0402 20210514
        */
        for (String attrName : publicKey.getR().keySet()) {
//        for (String attrName : xr_cap.keySet()) {
            BigInteger r_inverse = r.get(attrName).modInverse(n);
            BigInteger r_cap_value = CommitmentHelper.commitment(r_inverse, proof_c, s, xr_cap.get(attrName), n);
            r_cap.add(r_cap_value);
        }

        // TODO: challenge연산 실패시
        // exception throw를 할 것인가, log만 남길 것인가 판단 필요
        BigInteger c = new ChallengeBuilder()
                .add(z)
                .add(r)
                .add(z_cap)
                .add(r_cap)
                .buildWithHashing();

        if (c!= null && c.equals(proof_c)) {
            ZkpLogger.debug("KeyPairVerifier verify [c, proof_c] match\nc :"+c.toString()+"\nproof_c :"+proof_c.toString());
            return true;
        }
        ZkpLogger.info("KeyPairVerifier verify [c, proof_c] not match\nc :"+c.toString()+"\nproof_c :"+proof_c.toString());
//        throw new ZkpException(ErrorCode.OMNI_ERROR_ZKP_PROVER_CHECK_CREDENTIAL_KEY_CORRECTNESS_PROOF_FAIL);
        return false;
    }
}
