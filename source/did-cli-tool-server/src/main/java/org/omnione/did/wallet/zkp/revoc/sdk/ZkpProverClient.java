package org.omnione.did.wallet.zkp.revoc.sdk;

import org.omnione.did.wallet.exception.IWCommonException;
import org.omnione.did.wallet.key.IWKeyManager;
import org.omnione.did.wallet.util.json.GsonWrapper;
import org.omnione.did.wallet.zkp.ZkpConstants;
import org.omnione.did.wallet.zkp.data.*;
import org.omnione.did.wallet.zkp.data.attribute.AttributeValue;
import org.omnione.did.wallet.zkp.data.credentialrequest.CredentialRequestMeta;
import org.omnione.did.wallet.zkp.data.credentialrequest.MasterSecretBlindingData;
import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPublicKey;
import org.omnione.did.wallet.zkp.data.proof.RequestedProof;
import org.omnione.did.wallet.zkp.data.proofrequest.AttributeInfo;
import org.omnione.did.wallet.zkp.data.proofrequest.PredicateInfo;
import org.omnione.did.wallet.zkp.data.schema.NonCredentialSchema;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.CredentialRevocationPublicKey;
import org.omnione.did.wallet.zkp.revoc.RevocationPublicKey;
import org.omnione.did.wallet.zkp.revoc.RevocationRegistry;
import org.omnione.did.wallet.zkp.revoc.data.dto.*;
import org.omnione.did.wallet.zkp.revoc.sdk.response.*;
import org.omnione.did.wallet.zkp.revoc.verifier.NonRevocationCredentialVerifier;
import org.omnione.did.wallet.zkp.util.BigIntegerUtil;
import org.omnione.did.wallet.zkp.util.DateUtil;
import org.omnione.did.wallet.zkp.util.bulider.ProofBuilder;
import org.omnione.did.wallet.zkp.util.bulider.SubProofRequestBuilder;
import org.omnione.did.wallet.zkp.util.gson.ZkpGsonWrapper;
import org.omnione.did.wallet.zkp.util.helper.CredentialRequestHelper;
import org.omnione.did.wallet.zkp.util.helper.CredentialValueHelper;
import org.omnione.did.wallet.zkp.util.verifier.CredentialVerifier;
import org.omnione.did.wallet.zkp.util.verifier.KeyPairVerifier;

import java.math.BigInteger;
import java.util.*;


/**
 * Copyright 2021 Raoncorp, Inc.
 * core_sdk
 * Class: ZkpProverClient
 * Created by djpark on 2021/03/18.
 *
 * Description:
 */
public class ZkpProverClient {

    public ZkpProverClient() {
        ZkpSetting.getInstance();
    }

    /**
     * Credential Request 반환
     */
    public ZkpRequestCredentialResponse createCredentialRequest(IWKeyManager keyManager,
                                                                String masterSecretId,
                                                                String proverDid,
                                                                CredentialPrimaryPublicKey credentialPublicKey,
                                                                CredentialRevocationPublicKey revocPubKey,
                                                                CredentialOffer credOffer,
                                                                LinkedHashMap<String, String> credentialValueMap,
                                                                BigInteger proverNonce) throws IWCommonException, ZkpException {

        if (keyManager == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [keyManager has null parameter]");
        if (masterSecretId == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [masterSecretId has null parameter]");
        if (proverDid == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [proverDid has null parameter]");
        if (credentialPublicKey == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [credentialPublicKey has null parameter]");
        if (ZkpSetting.getInstance().isSupportedRevocation() && revocPubKey == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [revocPubKey has null parameter]");
        if (credOffer == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [credOffer has null parameter]");
        //cred value 를 server가 생성하기로 했다면 cred value가 null인것을 정상으로 판단한다.
        if (credentialValueMap == null && ZkpSetting.getInstance().isEnabledUserInfoToServer() == false)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [credentialValueMap has null parameter]");
        if (proverNonce == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [proverNonce has null parameter]");

        if (!KeyPairVerifier.verify(credentialPublicKey, credOffer.getKeyCorrectnessProof())) {
            ZkpLogger.info("check credential key correctness proof fail");
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_BIG_NUMBER_COMPARE_FAIL, "check credential key correctness proof fail");
        }

        if (!keyManager.isExistMasterSecret(masterSecretId)) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_SELECT_MASTER_SECRET_FROM_WALLET_FAIL, "select master secret from wallet fail");
        }

        MasterSecret masterSecret = keyManager.getMasterSecret(masterSecretId);
        ZkpLogger.debug("createCredentialRequest prover nonce: "+proverNonce);

        CredentialRequest credentialRequest = CredentialRequestHelper.generateCredentialRequest(credentialPublicKey, revocPubKey, proverDid, masterSecret, credOffer, proverNonce);

        LinkedHashMap credentialValue = new LinkedHashMap<String, AttributeValue>();
        if (ZkpSetting.getInstance().isEnabledUserInfoToServer() == false) {
            Iterator iterator = credentialValueMap.entrySet().iterator();
            while(iterator.hasNext()) {
                Map.Entry mutableEntry = (Map.Entry)iterator.next();
                AttributeValue attributeValue = new AttributeValue();
                attributeValue.setRaw((String)mutableEntry.getValue());
                credentialValue.put(mutableEntry.getKey(), attributeValue);
            }
        }

        CredentialRequestMeta credentialRequestMeta = new CredentialRequestMeta(
                new MasterSecretBlindingData(credentialRequest.getBlindedMs().getVrPrime(),
                    credentialRequest.getBlindedMs().getVPrime()),
                    proverNonce,
                    masterSecretId);

        return new ZkpRequestCredentialResponse.Builder(
                ZkpErrorCode.OMNI_ERROR_ZKP_SUCCESS, "success [createCredentialRequest]").
                setCredentialRequest(credentialRequest).
                setCredentialValue(credentialValue).
                setCredentialRequestMeta(credentialRequestMeta).
                build();
    }

    /**
     * Master secret 반환
     */
    public ZkpCreateMasterSecretResponse generateMasterSecret(IWKeyManager keyManager) throws IWCommonException, ZkpException {

        if (keyManager == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [keyManager has null parameter]");

        MasterSecret masterSecret = new MasterSecret();
        masterSecret.setMasterSecret(new BigIntegerUtil().generateMasterSecret());

        System.out.println("masterSecret: "+masterSecret.getMasterSecret().toString());

        // Store m1
        keyManager.addMasterSecrets(ZkpGsonWrapper.getGson().toJson(masterSecret));

        return new ZkpCreateMasterSecretResponse.Builder(
                ZkpErrorCode.OMNI_ERROR_ZKP_SUCCESS, "success [generateMasterSecret]")
                .setMasterSecretId(masterSecret.getMasterSecretId())
                .build();
    }

    /**
     * 발급 받은 Credential 유효성 체크 및 월렛에 저장
     */
    public ZkpResponse verifyAndStoreCredential(IWKeyManager keyManager,
                                                CredentialRequestMeta credentialRequestMeta,
                                                CredentialRevocationPublicKey credentialRevocationPublicKey,
                                                CredentialPrimaryPublicKey credentialPrimaryPublicKey,
                                                RevocationPublicKey revocationPublicKey,
                                                RevocationRegistry revocationRegistry,
                                                String credentialId,
                                                Credential credential) throws IWCommonException, ZkpException {

        if (keyManager == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [keyManager has null parameter]");
//        if (masterSecretId == null)
//            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [masterSecretId has null parameter]");
        if (credentialRequestMeta == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [credentialRequestMeta has null parameter]");
        if (ZkpSetting.getInstance().isSupportedRevocation() && credentialRevocationPublicKey == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [credentialRevocationPublicKey has null parameter]");
        if (credentialPrimaryPublicKey == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [credentialPrimaryPublicKey has null parameter]");
        if (ZkpSetting.getInstance().isSupportedRevocation() && revocationPublicKey == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [revocationPublicKey has null parameter]");
        if (ZkpSetting.getInstance().isSupportedRevocation() && revocationRegistry == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [revocationRegistry has null parameter]");
        if (credentialId == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [credentialId has null parameter]");
        if (credential == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [credential has null parameter]");
//        if (proverNonce == null)
//            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [proverNonce has null parameter]");

        if (!keyManager.isExistMasterSecret(credentialRequestMeta.getMasterSecretName()))
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_SELECT_MASTER_SECRET_FROM_WALLET_FAIL, "select master secret from wallet fail");

        MasterSecret masterSecret = keyManager.getMasterSecret(credentialRequestMeta.getMasterSecretName());

        if (!CredentialVerifier.verify(credential.getCredentialSignature(),
                credential.getSignatureCorrectnessProof(),
                CredentialValueHelper.generateCredentialValues(credential.getValues(), masterSecret),
                credentialPrimaryPublicKey, credentialRequestMeta.getMasterSecretBlindingData().getVPrime(),
                credentialRequestMeta.getNonce())) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_BIG_NUMBER_COMPARE_FAIL, "verify primary credential fail in verify and store credential");
        }

        if (ZkpSetting.getInstance().isSupportedRevocation() && !NonRevocationCredentialVerifier.verify(credential.getCredentialSignature().getNonRevocationCredential(), credentialRequestMeta.getMasterSecretBlindingData().getVrPrime(),
                                                     credentialRevocationPublicKey, revocationPublicKey, revocationRegistry, credential.getWitness())) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_BIG_NUMBER_COMPARE_FAIL, "verify non revocation credential fail in verify and store credential");
        }

        // Store c1, c2, m2
        keyManager.addZkpCredential(credentialId, credential);

        return new ZkpResponse(ZkpErrorCode.OMNI_ERROR_ZKP_SUCCESS, "success [verifyAndStoreCredential]");
    }

    /**
     * 조건에 맞는 Credential을 조회하여 사용가능한 Referent 반환
     */
    public ZkpSearchCredentialResponse searchCredentials(IWKeyManager keyManager, ProofRequest proofRequest) throws IWCommonException, ZkpException {

        if (keyManager == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [keyManager has null parameter]");
        if (proofRequest == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [proofRequest has null parameter]");

        ArrayList<CredentialInfo> credentialInfoList = keyManager.getZkpCredentials();

        // User 에게 반환할 데이터 생성
        Map<String, AttrReferent> selfAttrMap = new HashMap<String, AttrReferent>();
        Map<String, AttrReferent> attrMap = new HashMap<String, AttrReferent>();
        Map<String, PredicateReferent> predicateMap = new HashMap<String, PredicateReferent>();

        Map<String, AttributeInfo> attrInfoMap = proofRequest.getRequestedAttributes();
        Map<String, PredicateInfo> predInfoMap = proofRequest.getRequestedPredicates();

        ZkpLogger.debug("attrInfo: "+ ZkpGsonWrapper.getGsonPrettyPrinting().toJson(attrInfoMap));
        ZkpLogger.debug("predicateInfo: "+ ZkpGsonWrapper.getGsonPrettyPrinting().toJson(predInfoMap));

        selfAttrMap = AvailableReferent.addSelfAttrReferent(attrInfoMap);
        // VC안에 속성을 못찾은 경우
        attrMap = AvailableReferent.addAttrReferent(attrInfoMap, credentialInfoList);
        // attrMap -> 0개
        predicateMap = AvailableReferent.addPredicateReferent(predInfoMap, credentialInfoList);
        // predicateMap -> 0개
        /*
        * 1) "restrictions":"cred_def_id" 로 발급받은 VC를 못 찾은 경우 에러 코드 추가
        * 2) VC 안에 속성을 못 찾은 경우 에러코드 추가 - 항목을 못 찾는 경우와 조건에 안 맞는 경우 분리
        * */

        // proofRequest 조건에 부합하지 않으면 실패 리턴 djpark0402 2021.12.16
        if (attrInfoMap.size() != attrMap.size()) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_NOT_FOUND_AVAILABLE_REQUEST_ATTRIBUTE);
        }
        if (predInfoMap.size() != predicateMap.size()) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_NOT_FOUND_AVAILABLE_PREDICATE_ATTRIBUTE);
        }

        AvailableReferent availableReferent = new AvailableReferent.Builder()
                .setSelfAttrReferent(selfAttrMap)
                .setAttrReferent(attrMap)
                .setPredicateReferent(predicateMap)
                .build();

        ZkpLogger.debug("availableReferent: "+ GsonWrapper.getGsonPrettyPrinting().toJson(availableReferent));

        return new ZkpSearchCredentialResponse.Builder(
                ZkpErrorCode.OMNI_ERROR_ZKP_SUCCESS, "success [searchCredentials]")
                .setAvailableReferent(availableReferent).build();
    }

    /**
     * 사용가능한 Referent 목록에서 사용자가 선택한 정보로 최종 Referent 반환
     */
    public ZkpCreateReferentResponse createReferent(IWKeyManager keyManager, List<UserReferent> customReferents) throws IWCommonException, ZkpException {

        if (keyManager == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [keyManager has null parameter]");

        if (customReferents == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [customReferents has null parameter]");

        ReferentInfo referentInfo = new ReferentInfo();
        ArrayList<CredentialInfo> credentialInfoList = keyManager.getZkpCredentials();

        // LOOP: Credential Key/Value
        for (CredentialInfo credentialInfo: credentialInfoList) {
            Referent ref = new Referent();
            LinkedHashMap<String, ReferentAttributeValue> attr = new LinkedHashMap<String, ReferentAttributeValue>();

            for (UserReferent inputReferent : customReferents) {

                if (credentialInfo.getCredentialId().equals(inputReferent.getCredentialId())) {
                    if (inputReferent.getReferentKey() == null)
                        throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "referent key is null in createReferent");
                    else if (inputReferent.getRaw() == null)
                        throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "referent raw is null in createReferent");
                    else if (inputReferent.getReferentName() == null)
                        throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "referent name is null in createReferent");

                    Credential credential = credentialInfo.getCredential();
                    ZkpLogger.debug("credentialKey: "+credentialInfo.getCredentialId()+", inputReferent.getReferentName(): "+ZkpGsonWrapper.getGsonPrettyPrinting().toJson(inputReferent.getReferentName()));
                    attr.put(inputReferent.getReferentName(), new ReferentAttributeValue(inputReferent.getReferentKey(), inputReferent.isRevealed()));
                    ref.setSchemaId(credential.getSchemaId());
                    ref.setCredDefId(credential.getCredDefId());
                    ref.setRevRegDefId(ZkpSetting.getInstance().isSupportedRevocation() ? credential.getRevRegDefId() : null);
                    ref.setRevId(ZkpSetting.getInstance().isSupportedRevocation() ?credential.getCredentialSignature().getNonRevocationCredential().getRevIndex() + "" : null);
                }
            }

            if (attr.size() > 0) {
                ref.setAttributes(attr);
                referentInfo.addReferent(credentialInfo.getCredentialId(), ref);
            }
        }

        return new ZkpCreateReferentResponse
                .Builder(ZkpErrorCode.OMNI_ERROR_ZKP_SUCCESS, "success [createReferent]")
                .setReferentInfo(referentInfo)
                .build();
    }

    private List<ProveCredential> getProvingCredential(ProofRequest proofRequest, List<ProofParam> proofParams) throws ZkpException {

        ZkpLogger.debug("proving start ===============================================================proofParams.size: "+proofParams.size());
        List<ProveCredential> proveCredentialList = new LinkedList<ProveCredential>();
        // TODO 리펙토링 필요 (Map형태의 key값을 빼내기 위한 for문이 너무 많이 사용된다.)
        for (ProofParam proofParam : proofParams) {
            HashMap<String, Referent> referents = proofParam.getReferentInfo().getReferents();

            List<ProveRevealedAttribute> revealedAttrs = new LinkedList<ProveRevealedAttribute>();
            List<ProveUnrevealedAttribute> unrevealedAttrs = new LinkedList<ProveUnrevealedAttribute>();
            List<ProvePredicate> predicates = new LinkedList<ProvePredicate>();

            // LOOP: referents
            for (String referentKey : referents.keySet()) {
                Referent referent = referents.get(referentKey);

                // LOOP: attr
                for (String attrKey : referent.getAttributes().keySet()) {
                    ReferentAttributeValue referentAttributeValue = referent.getAttributes().get(attrKey);

                    // revealed Attrs 생성
                    for (String proofRequestAttrKey : proofRequest.getRequestedAttributes().keySet()) {
                        AttributeInfo requestAttrInfo = proofRequest.getRequestedAttributes().get(proofRequestAttrKey);

                        if (requestAttrInfo.getName().equals(attrKey) && referentAttributeValue.getRevealed()) {
                            revealedAttrs.add(new ProveRevealedAttribute.Builder()
                                    .setAttributeName(attrKey)
                                    .setReferentKey(referentAttributeValue.getReferentKey())
                                    .build());
                        } else if (requestAttrInfo.getName().equals(attrKey) && !referentAttributeValue.getRevealed()) {
                            unrevealedAttrs.add(new ProveUnrevealedAttribute.Builder()
                                    .setReferentKey(referentAttributeValue.getReferentKey())
                                    .build());
                        }
                    }

                    // predicate Attrs 생성
                    for (String proofRequestPredicateKey : proofRequest.getRequestedPredicates().keySet()) {
                        PredicateInfo requestPredicateInfo = proofRequest.getRequestedPredicates().get(proofRequestPredicateKey);

                        if (requestPredicateInfo.getName().equals(attrKey) && !referentAttributeValue.getRevealed()) {
                            predicates.add(new ProvePredicate.Builder()
                                    .setAttributeName(attrKey)
                                    .setReferentKey(referentAttributeValue.getReferentKey())
                                    .setPType(requestPredicateInfo.getPType())
                                    .setPValue(requestPredicateInfo.getPValue())
                                    .build());
                        }
                    }
                }

                proveCredentialList.add(new ProveCredential.Builder()
                        .setCredentialId(referentKey)
                        .setRevealedAttrs(revealedAttrs)
                        .setUnrevealedAttrs(unrevealedAttrs)
                        .setPredicates(predicates)
                        .setTimestemp(String.valueOf(DateUtil.getTimestamp()))
                        .setSchema(proofParam.getSchema())
                        .setCredentialDefinition(proofParam.getCredDef())
                        .setRevocationState(proofParam.getRevState())
                        .build());
            }
        }

        if (proveCredentialList.size() == 0)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_BUILD_CREDENTIAL_FOR_PROVING_FAIL);

        ZkpLogger.debug("credential for proving: " + ZkpGsonWrapper.getGsonPrettyPrinting().toJson(proveCredentialList));
        ZkpLogger.debug("=============================================================== proving end");
        return proveCredentialList;

    }

    /**
     * Proof 생성
     */
    public ZkpCreateProofResponse createProof(IWKeyManager keyManager, String masterSecretId, ProofRequest proofRequest,
                                              List<ProofParam> proofParams, Map<String, String> selfAttributes) throws IWCommonException, ZkpException {
        if (keyManager == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [keyManager has null parameter]");
        if (masterSecretId == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [masterSecretId has null parameter]");
        if (proofParams == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [proofParams has null parameter]");
        if (proofRequest == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [proofRequest has null parameter]");
        if (selfAttributes == null)
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL, "validRequest fail [selfAttributes has null parameter]");

        ZkpLogger.debug("proofParams: " + ZkpGsonWrapper.getGsonPrettyPrinting().toJson(proofParams));
        ZkpLogger.debug("selfAttributes: " + ZkpGsonWrapper.getGsonPrettyPrinting().toJson(selfAttributes));

        if (!keyManager.isExistMasterSecret(masterSecretId))
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_SELECT_MASTER_SECRET_FROM_WALLET_FAIL);

        MasterSecret masterSecret = keyManager.getMasterSecret(masterSecretId);
        List<ProveCredential> proveCredentialList = this.getProvingCredential(proofRequest, proofParams);

        // proof 생성
        ProofBuilder builder = new ProofBuilder(ZkpConstants.MASTER_SECRET_KEY);

        List<Identifiers> identifierList = new LinkedList<Identifiers>();
        RequestedProof requestedProof = new RequestedProof();

        ArrayList<CredentialInfo> credentialInfoList = keyManager.getZkpCredentials();

        //LOOP proving, credential,schema, def
        for (int i = 0 ; i < proveCredentialList.size() ; i ++) {

            ProveCredential proveCredential = proveCredentialList.get(i);
            // LOOP: Credential Key/Value
            for (CredentialInfo credentialInfo : credentialInfoList) {

                if (credentialInfo.getCredentialId().equals(proveCredential.getCredentialId())) {
                    Credential credential = credentialInfo.getCredential();
                    System.out.println("credential:! "+ZkpGsonWrapper.getGsonPrettyPrinting().toJson(credential));
                    // Referent : credential 1개가 subProof
                    Map<String, Map<String, String>> revealedAttrs = new LinkedHashMap<String, Map<String, String>>();

                    for (ProveRevealedAttribute revealedAttribute: proveCredential.getRevealedAttrs()) {
                        Map<String, String> subRevealedAttrs = new LinkedHashMap<String, String>();
//                        subRevealedAttrs.put("sub_proof_index", String.valueOf(i));
                        subRevealedAttrs.put("subProofIndex", String.valueOf(i));
                        subRevealedAttrs.put("row", credential.getValues().get(revealedAttribute.getAttributeName()).getRaw());
                        subRevealedAttrs.put("encoded", credential.getValues().get(revealedAttribute.getAttributeName()).getEncode().toString());
                        revealedAttrs.put(revealedAttribute.getReferentKey(), subRevealedAttrs);
                    }

                    System.out.println("revealedAttrs: "+ZkpGsonWrapper.getGsonPrettyPrinting().toJson(revealedAttrs));
                    requestedProof.addRevealedAttrs(revealedAttrs);

                    Map<String, Map<String, String>> unrevealedAttrs = new LinkedHashMap<String, Map<String, String>>();
                    Map<String, String> subUnrevealedAttrs = new LinkedHashMap<String, String>();

                    for (ProveUnrevealedAttribute unrevealedAttribute: proveCredential.getUnrevealedAttrs()) {
//                        subUnrevealedAttrs.put("sub_proof_index", String.valueOf(i));
                        subUnrevealedAttrs.put("subProofIndex", String.valueOf(i));
                        unrevealedAttrs.put(unrevealedAttribute.getReferentKey(), subUnrevealedAttrs);
                    }
                    requestedProof.addUnrevealedAttrs(unrevealedAttrs);

                    Map<String, Map<String, String>>predicates = new LinkedHashMap<String, Map<String, String>>();
                    Map<String, String>subPredicates = new HashMap<String, String>();

                    for (ProvePredicate provePredicate: proveCredential.getPredicates()) {
//                        subPredicates.put("sub_proof_index", String.valueOf(i));
                        subPredicates.put("subProofIndex", String.valueOf(i));
                        predicates.put(provePredicate.getReferentKey(), subPredicates);
                    }
                    requestedProof.addPredicates(predicates);

                    Map<String, String> attestedAttr = new LinkedHashMap<String, String>();

                    for (String key : selfAttributes.keySet()) {
                        attestedAttr.put(key, selfAttributes.get(key));
                    }
                    requestedProof.addSelfAttestedAttrs(attestedAttr);
                    ZkpLogger.debug("requestedProof: "+ZkpGsonWrapper.getGsonPrettyPrinting().toJson(requestedProof));

                    if (proveCredential.getSchema() == null)
                        throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_NOT_FOUND_SCHEMA_FROM_LIST, "not found schema from list");
                    else if (ZkpSetting.getInstance().isSupportedRevocation() && proveCredential.getCredentialDefinition() == null)
                        throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_NOT_FOUND_CRED_DEF_FROM_LIST, "not found credential definition from list");
                    else if (ZkpSetting.getInstance().isSupportedRevocation() && proveCredential.getRevocationState() == null)
                        throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_NOT_FOUND_REV_REG_STATE_FROM_LIST, "not found revocation registry state from list");

                    identifierList.add(new Identifiers(proveCredential.getSchema().getId(),
                            proveCredential.getCredentialDefinition().getId(),
                            ZkpSetting.getInstance().isSupportedRevocation() ? proveCredential.getRevocationState().getRevRegId() : null,
                            ZkpSetting.getInstance().isSupportedRevocation() ? proveCredential.getRevocationState().getTimestamp() : null));

                    SubProofRequestBuilder subProofRequestBuilder = new SubProofRequestBuilder();

                    HashSet<String> requestedAttributes = new LinkedHashSet<String>();
                    for (ProveRevealedAttribute proveRevealedAttribute : proveCredential.getRevealedAttrs()) {
                        requestedAttributes.add(proveRevealedAttribute.getAttributeName());
                    }
                    subProofRequestBuilder.addRevealedAttr(requestedAttributes);// referent 의 name address.. encoded value

                    HashSet<PredicateInfo> predicateAttributes = new LinkedHashSet<PredicateInfo>();

                    for (ProvePredicate provePredicate : proveCredential.getPredicates()) {
                        PredicateInfo predicateInfo = new PredicateInfo();
                        predicateInfo.setName(provePredicate.getAttrName());
                        predicateInfo.setPType(provePredicate.getPType());
                        predicateInfo.setPValue(provePredicate.getPValue());

                        predicateAttributes.add(predicateInfo);
                    }

                    subProofRequestBuilder.addPredicateAttr(predicateAttributes);
                    SubProofRequest subProofRequest = subProofRequestBuilder.build();

                    ZkpLogger.debug("subProofRequest: "+ZkpGsonWrapper.getGsonPrettyPrinting().toJson(subProofRequest));

                    NonCredentialSchema nonCredentialSchema = new NonCredentialSchema();
                    nonCredentialSchema.addAttr(ZkpConstants.MASTER_SECRET_KEY);

                    ZkpLogger.debug("credential=====================================================================");
                    ZkpLogger.debug(ZkpGsonWrapper.getGsonPrettyPrinting().toJson(credential));
                    ZkpLogger.debug("=====================================================================credential");

                    builder.addSubProofRequest(subProofRequest,
                            proveCredential.getSchema(),
                            nonCredentialSchema,
                            CredentialValueHelper.generateCredentialValues(credential.getValues(), masterSecret),
                            credential.getCredentialSignature(),
                            proveCredential.getCredentialDefinition().getValue().getPrimary(),
                            ZkpSetting.getInstance().isSupportedRevocation() ? proveCredential.getCredentialDefinition().getValue().getRevocation() : null,
                            ZkpSetting.getInstance().isSupportedRevocation() ? proveCredential.getRevocationState().getRevReg() : null,
                            ZkpSetting.getInstance().isSupportedRevocation() ? proveCredential.getRevocationState().getWitness() : null);
                }
            }
        }

        Proof proof = builder.build(proofRequest.getNonce(), requestedProof, identifierList);
        ZkpLogger.debug("proof: "+ZkpGsonWrapper.getGson().toJson(proof));

        return new ZkpCreateProofResponse
                .Builder(ZkpErrorCode.OMNI_ERROR_ZKP_SUCCESS, "success [createProof]")
                .setProof(proof)
                .build();
    }
}
