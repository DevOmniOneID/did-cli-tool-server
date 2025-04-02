package org.omnione.did.wallet.zkp.revoc.sdk;

import org.omnione.did.wallet.zkp.data.Proof;
import org.omnione.did.wallet.zkp.data.ProofRequest;
import org.omnione.did.wallet.zkp.data.proofrequest.AttributeInfo;
import org.omnione.did.wallet.zkp.data.proofrequest.PredicateInfo;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.data.dto.ProofVerifyParam;
import org.omnione.did.wallet.zkp.revoc.utils.NonRevocedInterval;
import org.omnione.did.wallet.zkp.util.DateUtil;
import org.omnione.did.wallet.zkp.util.verifier.ProofVerifier;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
/**
 * Copyright 2021 Raoncorp, Inc.
 * core_sdk
 * Class: ZkpVerifierClient
 * Created by djpark on 2021/03/18.
 *
 * Description:
 */
public class ZkpVerifierClient {

    public ZkpVerifierClient() {
        ZkpSetting.getInstance();
    }

    /**
     * Request Proof 반환
     * 변경가능한 형태로 서비스에 맞게 변경하여 사용 권장
     */
    public static ProofRequest requestProofReq(String name,
                                               Map<String, AttributeInfo> proofRequestAttribute,
                                               Map<String, PredicateInfo> proofRequestPredicate,
                                               BigInteger verifierNonce) {
        ProofRequest proofRequest = new ProofRequest();
        proofRequest.setName(name);
        proofRequest.setVersion("1.0");
        proofRequest.setNonce(verifierNonce);
        proofRequest.setNonRevoked(new NonRevocedInterval("", String.valueOf(DateUtil.getTimestamp())));
        ZkpLogger.debug("requestProofReq verifier nonce: "+proofRequest.getNonce());
        proofRequest.setRequestedAttributes(proofRequestAttribute);
        proofRequest.setRequestedPredicates(proofRequestPredicate);
        return proofRequest;
    }

    /**
     * Proof 검증
     */
    public ZkpResponse verifyProof(Proof proof,
                                   BigInteger nonce,
                                   ProofRequest proofRequest,
                                   List<ProofVerifyParam> proofVerifyParams) throws ZkpException {

        ProofVerifier.verify(proofRequest, proof, proofVerifyParams, nonce);
        return new ZkpResponse(ZkpErrorCode.OMNI_ERROR_ZKP_SUCCESS, "success [verifyProof]");
    }
}
