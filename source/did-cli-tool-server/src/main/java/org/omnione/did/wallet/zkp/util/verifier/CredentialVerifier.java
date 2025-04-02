package org.omnione.did.wallet.zkp.util.verifier;

import org.omnione.did.wallet.zkp.data.credential.CredentialSignature;
import org.omnione.did.wallet.zkp.data.credential.PrimaryCredentialSignature;
import org.omnione.did.wallet.zkp.data.credential.SignatureCorrectnessProof;
import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPublicKey;
import org.omnione.did.wallet.zkp.data.proof.CredentialValues;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpLogger;
import org.omnione.did.wallet.zkp.util.CommitmentHelper;
import org.omnione.did.wallet.zkp.util.bulider.ChallengeBuilder;

import java.math.BigInteger;
import java.util.Map;

public class CredentialVerifier {

    public static boolean verify(CredentialSignature credSign,
                                 SignatureCorrectnessProof credSignProof,
                                 CredentialValues credValues,
                                 CredentialPrimaryPublicKey publicKey,
                                 BigInteger v_prime,
                                 BigInteger nonce) throws ZkpException {

        BigInteger s = publicKey.getS();
        BigInteger z = publicKey.getZ();
        BigInteger n = publicKey.getN();
        Map<String, BigInteger> r = publicKey.getR();
        PrimaryCredentialSignature pCredSign = credSign.getPrimaryCredential();

        // v = v' + v''
        BigInteger v = pCredSign.getV().add(v_prime);
        /** update C1 with vPrime
         *2021.05.04 djpark
         **/
        pCredSign.setV(v);

        BigInteger a = pCredSign.getA();
        BigInteger e = pCredSign.getE();

//        BigInteger sv = s.modPow(v, n);

        // djpark 2021.04.30 primary 미구현된 부분 추가
        BigInteger m2 = credSign.getPrimaryCredential().getM2();
        BigInteger rctxt = publicKey.getRctxt();

        BigInteger sv = CommitmentHelper.commitment(s, v, rctxt, m2, n); //        s.modPow(v,n).multiply(rctxt.modPow(m2, n)).mod(n);

        try {
            for (String key : credValues.getAttrValues().keySet()) {
                ZkpLogger.debug("key: " + key);
                sv = sv.multiply(r.get(key).modPow(credValues.get(key).getValue(), n)).mod(n);
            }
        } catch(Exception ex) {
            ZkpLogger.debug("exception: " + ex.toString());
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_CHECK_SIGNATURE_CORRECTNESS_PROOF_FAIL, "publicKey r is null");
        }

        //TODO: sv연산 마지막에 mod연산이 필요한지 검토 필요
        if (sv.bitLength() == 0) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_CHECK_SIGNATURE_CORRECTNESS_PROOF_FAIL, "publicKey not match ");
        }
        // e와 v가 주어진 범위내 있는지, e가 소수인지 확인
        // Ae ≡ Z Sv ⋅ ∏i R mi i (mod n)
        BigInteger q = z.multiply(sv.modInverse(n)).mod(n);
        BigInteger q_cap = a.modPow(e, n);

        // 서명 검증
        if (q.equals(q_cap)) {
            BigInteger exp = credSignProof.getC().add(credSignProof.getSe().multiply(e));
            BigInteger a_cap = a.modPow(exp, n);

            //TODO: challenge연산 실패시
            // exception throw를 할 것인가, log만 남길 것인가 판단 필요
            BigInteger c_cap = new ChallengeBuilder()
                    .add(q)
                    .add(a)
                    .add(a_cap)
                    .add(nonce)
                    .buildWithHashing();

            if (c_cap != null && c_cap.equals(credSignProof.getC())) {
                ZkpLogger.debug("Credential primary [c_cap, c] match");
                return true;
            } else {
                ZkpLogger.info("Credential primary [c_cap, c] not match");
//                throw new ZkpException(ErrorCode.OMNI_ERROR_ZKP_PROVER_CHECK_SIGNATURE_CORRECTNESS_PROOF_FAIL, "credential primary [c_cap, c] not match");
                return false;
            }
        } else {
            ZkpLogger.info("Credential primary [q, q_cap] not match (s, n, z, rctxt, vPrime)");
//            throw new ZkpException(ErrorCode.OMNI_ERROR_ZKP_PROVER_CHECK_SIGNATURE_CORRECTNESS_PROOF_FAIL, "credential primary [q, q_cap] not match");
            return false;
        }
    }
}
