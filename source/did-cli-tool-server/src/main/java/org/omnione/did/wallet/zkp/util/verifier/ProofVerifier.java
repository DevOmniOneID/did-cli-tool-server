package org.omnione.did.wallet.zkp.util.verifier;

import org.omnione.did.wallet.zkp.ZkpConstants;
import org.omnione.did.wallet.zkp.data.*;
import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPublicKey;
import org.omnione.did.wallet.zkp.data.proof.RequestedProof;
import org.omnione.did.wallet.zkp.data.proofrequest.AttributeInfo;
import org.omnione.did.wallet.zkp.data.proofrequest.PredicateInfo;
import org.omnione.did.wallet.zkp.data.schema.CredentialSchema;
import org.omnione.did.wallet.zkp.data.schema.NonCredentialSchema;
import org.omnione.did.wallet.zkp.data.subproof.PrimaryEqualProof;
import org.omnione.did.wallet.zkp.data.subproof.PrimaryPredicateInequalityProof;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.CredentialRevocationPublicKey;
import org.omnione.did.wallet.zkp.revoc.RevocationPublicKey;
import org.omnione.did.wallet.zkp.revoc.RevocationRegistry;
import org.omnione.did.wallet.zkp.revoc.RevocationRegistryDefinition;
import org.omnione.did.wallet.zkp.revoc.data.*;
import org.omnione.did.wallet.zkp.revoc.data.dto.Identifiers;
import org.omnione.did.wallet.zkp.revoc.data.dto.ProofVerifyParam;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpLogger;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpSetting;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.BIG;
import org.omnione.did.wallet.zkp.revoc.utils.acml.BN254.PAIR;
import org.omnione.did.wallet.zkp.util.BigIntegerUtil;
import org.omnione.did.wallet.zkp.util.bulider.ChallengeBuilder;
import org.omnione.did.wallet.zkp.util.gson.ZkpGsonWrapper;
import org.omnione.did.wallet.zkp.util.helper.ProofHelper;

import java.math.BigInteger;
import java.util.*;

public class ProofVerifier {

    public static NonRevocProofTauList verifyNonRevocationProof(CredentialRevocationPublicKey rPubKey,
                                                                RevocationRegistry revReg,
                                                                RevocationPublicKey revKeyPublic,
                                                                BigInteger cHash,
                                                                NonRevocProof proof) throws ZkpException {

        try {
            BIG ch_num_z = GroupOrderElement.from_bytes(BigIntegerUtil.asUnsignedByteArray(cHash));

            NonRevocProofTauList tHatExpectedValues = createTauListExpectedValues(rPubKey,
                    revReg,
                    revKeyPublic,
                    proof.getCList());

            NonRevocProofTauList tHatCalcValues = ProofHelper.createTauListValues(rPubKey,
                    revReg,
                    proof.getXList(),
                    proof.getCList());
// 128
            PointG1 t1 = new PointG1();
            t1.getPoint().copy(tHatExpectedValues.getT1().getPoint());
            t1.setPoint(PAIR.G1mul(t1.getPoint(), ch_num_z));
            t1.getPoint().add(tHatCalcValues.getT1().getPoint());

//        AMCLUtils.toHashString(t1, "t1");
// 128
            PointG1 t2 = new PointG1();
            t2.getPoint().copy(tHatExpectedValues.getT2().getPoint());
            t2.setPoint(PAIR.G1mul(t2.getPoint(), ch_num_z));
            t2.getPoint().add(tHatCalcValues.getT2().getPoint());

//        AMCLUtils.toHashString(t2, "t2");
// 512
            Pair t3 = new Pair();
            t3.getPair().copy(tHatExpectedValues.getT3().getPair());
            t3.setPair(PAIR.GTpow(t3.getPair(), ch_num_z));
            t3.getPair().mul(tHatCalcValues.getT3().getPair());
            t3.getPair().reduce();

//        AMCLUtils.toHashString(t3.getPair(), "t3");

// 512
            Pair t4 = new Pair();
            t4.getPair().copy(tHatExpectedValues.getT4().getPair());
            t4.setPair(PAIR.GTpow(t4.getPair(), ch_num_z));
            t4.getPair().mul(tHatCalcValues.getT4().getPair());
            t4.getPair().reduce();

//        AMCLUtils.toHashString(t4.getPair(), "t4");
// t5 128
            PointG1 t5 = new PointG1();
            t5.getPoint().copy(tHatExpectedValues.getT5().getPoint());
            t5.setPoint(PAIR.G1mul(t5.getPoint(), ch_num_z));
            t5.getPoint().add(tHatCalcValues.getT5().getPoint());

//        AMCLUtils.toHashString(t5, "t5");
// t6 128
            PointG1 t6 = new PointG1();
            t6.getPoint().copy(tHatExpectedValues.getT6().getPoint());
            t6.setPoint(PAIR.G1mul(t6.getPoint(), ch_num_z));
            t6.getPoint().add(tHatCalcValues.getT6().getPoint());

//        AMCLUtils.toHashString(t6, "t6");
// t7
            Pair t7 = new Pair();
            t7.getPair().copy(tHatExpectedValues.getT7().getPair());
            t7.setPair(PAIR.GTpow(t7.getPair(), ch_num_z));
            t7.getPair().mul(tHatCalcValues.getT7().getPair());
            t7.getPair().reduce();
//        AMCLUtils.toHashString(t7.getPair(), "t7");

// t8
            Pair t8 = new Pair();
            t8.getPair().copy(tHatExpectedValues.getT8().getPair());
            t8.setPair(PAIR.GTpow(t8.getPair(), ch_num_z));
            t8.getPair().mul(tHatCalcValues.getT8().getPair());
            t8.getPair().reduce();
//        AMCLUtils.toHashString(t8.getPair(), "t8");
            return new NonRevocProofTauList(t1, t2, t3, t4, t5, t6, t7, t8);

        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_VERIFIER_VERIFY_NON_REVOCATION_PROOF_FAIL);
        }
    }

    public static NonRevocProofTauList createTauListExpectedValues(CredentialRevocationPublicKey rPubKey,
                                                                   RevocationRegistry revReg,
                                                                   RevocationPublicKey revocPubKey,
                                                                   NonRevocProofCList proofC) {
        PointG1 t1 = new PointG1();
        t1.getPoint().copy(proofC.getE().getPoint());

        PointG1 t2 = new PointG1();
        t2.getPoint().inf();
//        t2.getP();

        // t3
        PointG1 h0 = new PointG1();
        h0.getPoint().copy(rPubKey.getH0().getPoint());
        h0.getPoint().add(proofC.getG().getPoint());
        Pair t3_pair = new Pair(proofC.getA(), rPubKey.getY());
        t3_pair.getPair().conj();
        Pair t3 = new Pair(h0, rPubKey.getHCap());
        t3.getPair().mul(t3_pair.getPair());
        t3.getPair().reduce();

        // t4
        Pair t4_pair = new Pair(rPubKey.getG(), proofC.getW());
        t4_pair.getPair().mul(revocPubKey.getZ().getPair());
        t4_pair.getPair().reduce();
        t4_pair.getPair().conj();
        Pair t4 = new Pair(proofC.getG(), revReg.getAccum());
        t4.getPair().mul(t4_pair.getPair());
        t4.getPair().reduce();

        // t5
        PointG1 t5 = new PointG1();
        t5.getPoint().copy(proofC.getD().getPoint());

        // t6
        PointG1 t6 = new PointG1();
        t6.getPoint().inf();
//        t6.getP();

        // t7
        PointG1 pk = new PointG1();
        pk.getPoint().copy(rPubKey.getPk().getPoint());
        pk.getPoint().add(proofC.getG().getPoint());
        Pair t7_pair = new Pair(rPubKey.getG(), rPubKey.getGDash());
        t7_pair.getPair().conj();
        Pair t7 = new Pair(pk, proofC.getS());
        t7.getPair().mul(t7_pair.getPair());
        t7.getPair().reduce();

        // t8
        Pair t8_pair = new Pair(rPubKey.getG(), proofC.getU());
        t8_pair.getPair().conj();
        Pair t8 = new Pair(proofC.getG(), rPubKey.getU());
        t8.getPair().mul(t8_pair.getPair());
        t8.getPair().reduce();

        return new NonRevocProofTauList(t1, t2, t3, t4, t5, t6, t7, t8);
    }



    private static Vector<BigInteger> _verify_ne_predicate(CredentialPrimaryPublicKey p_pub_key,
                                                           PrimaryPredicateInequalityProof proof, BigInteger c_hash) throws ZkpException {

        try {
            Vector<BigInteger> tau_list = BigIntegerUtil.calc_tne(p_pub_key, proof.getU(), proof.getR(), proof.getMj(),
                    proof.getAlpha(), proof.getT(), proof.getPredicate().isLess());

            for (int i = 0; i < ZkpConstants.ITERATION; i++) {
                BigInteger cur_t = proof.getT().get(Integer.toString(i));
                if (cur_t == null)
                    throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "Value by key [" + i + "] not found in proof.t ");

                BigInteger tau = cur_t.modPow(c_hash, p_pub_key.getN()).modInverse(p_pub_key.getN())
                        .multiply(tau_list.get(i)).mod(p_pub_key.getN());
                tau_list.set(i, tau);
            }

            BigInteger delta = proof.getT().get(ZkpConstants.DELTA);
            if (delta == null)
                throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "Value by key DELTA not found in proof.t ");

            BigInteger delta_prime = (proof.getPredicate().isLess() == true) ? delta.modInverse(p_pub_key.getN()) : delta;

            BigInteger tau = p_pub_key.getZ().modPow(proof.getPredicate().getDeltaPrime(), p_pub_key.getN())
                    .multiply(delta_prime).modPow(c_hash, p_pub_key.getN()).modInverse(p_pub_key.getN())
                    .multiply(tau_list.get(ZkpConstants.ITERATION)).mod(p_pub_key.getN());

            tau_list.set(ZkpConstants.ITERATION, tau);

            tau = delta.modPow(c_hash, p_pub_key.getN()).modInverse(p_pub_key.getN())
                    .multiply(tau_list.get(ZkpConstants.ITERATION + 1)).mod(p_pub_key.getN());

            tau_list.set(ZkpConstants.ITERATION + 1, tau);

            return tau_list;
        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_VERIFIER_VERIFY_PRIMARY_NE_PROOF_FAIL);
        }
    }

    private static Vector<BigInteger> _verify_equality(CredentialPrimaryPublicKey p_pub_key,
                                                       PrimaryEqualProof proof,
                                                       BigInteger c_hash,
                                                       CredentialSchema cred_schema,
                                                       NonCredentialSchema non_cred_schema,
                                                       SubProofRequest sub_proof_request) throws ZkpException {

        try {
            HashSet<String> unrevealed_attrs = new HashSet<String>();

            // for 연산식 문제로 set 합집합, 차집합 연산 처리
            Set<String> nonCredSchemaList = non_cred_schema.getNonCredSchema();
            List<String> credSchemaList = cred_schema.getAttrNames();

            unrevealed_attrs.addAll(nonCredSchemaList);
            unrevealed_attrs.addAll(credSchemaList);

            unrevealed_attrs.removeAll(sub_proof_request.getRevealedAttrs());

            BigInteger t1 = BigIntegerUtil.calc_teq(p_pub_key, proof.getaPrime(), proof.getE(), proof.getV(), proof.getM(), proof.getM2(), unrevealed_attrs);

            BigInteger rar = proof.getaPrime().modPow(ZkpConstants.LARGE_E_START_VALUE, p_pub_key.getN());

            for (String attr : proof.getRevealedAttrs().keySet()) {

//                System.out.println("확인 "+ proof.getRevealedAttrs().get(attr));
                BigInteger encoded_value = proof.getRevealedAttrs().get(attr);
                BigInteger cur_r = p_pub_key.getR().get(attr);

                if (cur_r == null)
                    throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "Value by key '{}' not found in pk.r ");

                rar = rar.multiply(cur_r.modPow(encoded_value, p_pub_key.getN())).mod(p_pub_key.getN());
            }

            // z_n 상에서 계산 해야됨.
            BigInteger t2 = rar.modInverse(p_pub_key.getN()).multiply(p_pub_key.getZ()).modPow(c_hash, p_pub_key.getN()).modInverse(p_pub_key.getN());
            Vector<BigInteger> t_list = new Vector<BigInteger>();
            t_list.add(t1.multiply(t2).mod(p_pub_key.getN()));
            return t_list;

        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_VERIFIER_VERIFY_PRIMARY_EQ_PROOF_FAIL);
        }
    }

    private static void checkAddSubProofRequestParamsConsistency(RequestedProof requestedProof,
                                                                           ProofRequest proofRequest,
                                                                           SubProofRequest sub_proof_request,
                                                                           CredentialSchema cred_schema) throws ZkpException {
        List<String> attrSet = cred_schema.getAttrNames();

        if (attrSet.containsAll(sub_proof_request.getRevealedAttrs()) == false)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_VERIFIER_NOT_SATISFIED_ATTRIBUTE_IN_SUB_PROOF, "Credential doesn't contain requested attribute");

        System.out.println("sub_proof_request: "+sub_proof_request);
        for (Predicate entry : sub_proof_request.getPredicates()) {
            if (attrSet.contains(entry.getAttrName()) == false)
                throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_VERIFIER_NOT_SATISFIED_ATTRIBUTE_IN_SUB_PROOF, "Credential doesn't contain attribute requested in predicate");
        }

        Set<String> proofRequestKeySet = new HashSet<String>();
        Map<String, AttributeInfo> requestedAttributes = proofRequest.getRequestedAttributes();
        Map<String, PredicateInfo> predicateAttributes = proofRequest.getRequestedPredicates();
        for (String key: requestedAttributes.keySet()) {
            if (requestedAttributes.get(key).getRestrictions().size() > 0) {
                proofRequestKeySet.add(key);
            }
        }

        for (String key: predicateAttributes.keySet()) {
            if (predicateAttributes.get(key).getRestrictions().size() > 0) {
                proofRequestKeySet.add(key);
            }
        }

        ZkpLogger.debug("proofRequest keyList: "+ZkpGsonWrapper.getGsonPrettyPrinting().toJson(proofRequestKeySet));

        Set<String> reqProofKeySet = new HashSet<String>();

        Map<String, Map<String, String>> revealedAttrs = requestedProof.getRevealedAttrs();
        for (String revealedKey : revealedAttrs.keySet()) {
            ZkpLogger.debug("revealedKey: "+revealedKey);
            reqProofKeySet.add(revealedKey);
        }

        Map<String, Map<String, String>> unrevealedAttrs = requestedProof.getUnrevealedAttrs();
        for (String unrevealedKey : unrevealedAttrs.keySet()) {
            ZkpLogger.debug("unrevealedKey: "+unrevealedKey);
            reqProofKeySet.add(unrevealedKey);
        }

        Map<String, Map<String, String>> predicateAttrs = requestedProof.getPredicates();
        for (String predicateKey : predicateAttrs.keySet()) {
            ZkpLogger.debug("predicateKey: "+predicateKey);
            reqProofKeySet.add(predicateKey);
        }
        ZkpLogger.debug("requestedProof KeyList: "+ZkpGsonWrapper.getGsonPrettyPrinting().toJson(reqProofKeySet));
        /**
         * 사용자가 선택한 데이터(requested_proof in proof)가 proofRequest 의 조건에 만족하는지 체크
         **/

//        for (String ref : proofRequestKeySet) {
//            try {
//                if (!reqProofKeySet.contains(ref))
//                    throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_VERIFIER_NOT_SATISFIED_ATTRIBUTE_IN_SUB_PROOF, ref + " doesn't exist in requested proof");
//            } catch (Exception e) {
//                throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_VERIFIER_NOT_SATISFIED_ATTRIBUTE_IN_SUB_PROOF, ref + " doesn't exist in requested proof");
//            }
//        }
    }

    public static boolean verify(ProofRequest proofRequest, Proof proof, List<ProofVerifyParam> proofVerifyParams, BigInteger nonce) throws ZkpException {

        /** verify 검증 체크 리스트
         * 1. proof and request 의 attribute 비교
         * 2. revealed attributes values 체크
         * 3. restrictions attributes 체크
         * TODO 4. timestamps 체크 (proof 와 request)
         */
        ChallengeBuilder builder = new ChallengeBuilder();

        ZkpLogger.info("the number of certificates included is "+proof.getProofs().size());
        for (int i = 0 ; i < proof.getProofs().size(); i++) {

            ZkpLogger.info("#################### certificate "+i+" start");
            CredentialSchema schema = null;
            CredentialDefinition credentialDefinition = null;
            RevocationRegistryDefinition revocationRegistryDefinition = null;
            RevocationRegistry revocationRegistry = null;

            if (proof.getIdentifiers().size() == 0)
                throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "check verify [proof.getIdentifiers() is null]");

            Identifiers identifiers = proof.getIdentifiers().get(i);
            ZkpLogger.debug("identifiers: "+ZkpGsonWrapper.getGsonPrettyPrinting().toJson(identifiers));

            for (ProofVerifyParam proofVerifyParam : proofVerifyParams) {
                ZkpLogger.debug("proofVerifyParam: "+ZkpGsonWrapper.getGsonPrettyPrinting().toJson(proofVerifyParam));
                if (identifiers.getSchemaId().equals(proofVerifyParam.getSchema().getId()))
                    schema = proofVerifyParam.getSchema();
                if (identifiers.getCredDefId().equals(proofVerifyParam.getCredentialDefinition().getId()))
                    credentialDefinition = proofVerifyParam.getCredentialDefinition();
                if (ZkpSetting.getInstance().isSupportedRevocation() && identifiers.getRevRegId().equals(proofVerifyParam.getRevocationRegistryDefinition().getRevocationRegistryId()))
                    revocationRegistryDefinition = proofVerifyParam.getRevocationRegistryDefinition();
                if (ZkpSetting.getInstance().isSupportedRevocation() && identifiers.getRevRegId().equals(proofVerifyParam.getRevocationRegistryDefinition().getRevocationRegistryId()))
                    revocationRegistry = proofVerifyParam.getRevocationRegistry();
            }

            if (schema == null)
                throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "check verify [schema is null]");
            else if (credentialDefinition == null)
                throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "check verify [credentialDefinition is null]");
            else if (ZkpSetting.getInstance().isSupportedRevocation() && revocationRegistryDefinition == null)
                throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "check verify [revocationRegistryDefinition is null]");
            else if (ZkpSetting.getInstance().isSupportedRevocation() && revocationRegistry == null )
                throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "check verify [revocationRegistry is null]");

            Vector<byte[]> tauList = new Vector<byte[]>();
            Vector<BigInteger> tList = new Vector<BigInteger>();

            SubProof proofItem = proof.getProofs().get(i);

            if (ZkpSetting.getInstance().isSupportedRevocation()) {
                NonRevocProof nonRevocProof = proofItem.getNonRevocProof();
                NonRevocProofTauList nonRevocProofTauList = verifyNonRevocationProof(credentialDefinition.getValue().getRevocation(),
                        revocationRegistry,
                        revocationRegistryDefinition.getValue().getPublicKeys().getAccumKey(),
                        proof.getAggregatedProof().getcHash(),
                        nonRevocProof);
                tauList.addAll(nonRevocProofTauList.asSlice());
                builder.add(tauList);
            }

            PrimaryProof primaryProof = proofItem.getPrimaryProof();

            NonCredentialSchema nonCredSchema = new NonCredentialSchema();
            nonCredSchema.addAttr(ZkpConstants.MASTER_SECRET_KEY);

            //TODO: SCHEMA 조회
            SubProofRequest subProofRequest = proofRequest.getSubProofRequest(i, proof.getRequestedProof());
            ZkpLogger.debug("verify subProofRequest: "+ZkpGsonWrapper.getGsonPrettyPrinting().toJson(subProofRequest));
            //TODO: proof 체크
            checkAddSubProofRequestParamsConsistency(proof.getRequestedProof(), proofRequest, subProofRequest, schema);

            // _verify_primary_proof
            // 검증자는 모든 발급자 공개 키 pkI를 사용하여 자격 증명 생성 및 수신(c,e,v,{mi},A'). 또한 i ∈ Ar에 대해 공개된 mi를 사용합니다. T를 공집합으로 시작합니다.
            Vector<BigInteger> t_hat = _verify_equality(credentialDefinition.getValue().getPrimary(),
                    primaryProof.getEqProof(),
                    proof.getAggregatedProof().getcHash(),
                    schema,
                    nonCredSchema,
                    subProofRequest);

            for (PrimaryPredicateInequalityProof neProof : primaryProof.getNeProofs()) {
                t_hat.addAll(_verify_ne_predicate(credentialDefinition.getValue().getPrimary(), neProof, proof.getAggregatedProof().getcHash()));
            }
            // Add T_hat to T
            tList.addAll(t_hat);
            builder.add(tList);
            ZkpLogger.info("#################### certificate "+i+" end");
        }

//        ZkpLogger.debug("verify nonce: "+nonce.toString());

        builder.add(proof.getAggregatedProof().getcList());
        builder.add(nonce);

        BigInteger c_hver = builder.buildWithHashing();

        if (!c_hver.equals(proof.getAggregatedProof().getcHash())) {
            ZkpLogger.info("verifier proof verify [c_hver, proof.getAggregatedProof().getcHash()] not match " + "c_hver: "+c_hver + ", c_hash: "+proof.getAggregatedProof().getcHash());
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_BIG_NUMBER_COMPARE_FAIL, "verifier proof verify [c_hver, proof.getAggregatedProof().getcHash()] not match");
        }
        return true;
    }
}
