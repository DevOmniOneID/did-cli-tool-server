package org.omnione.did.wallet.zkp.util.helper;

import org.omnione.did.wallet.exception.IWErrorCode;
import org.omnione.did.wallet.exception.IWException;
import org.omnione.did.wallet.key.IWKeyManager;
import org.omnione.did.wallet.zkp.data.*;
import org.omnione.did.wallet.zkp.data.attribute.AttributeValue;

import org.omnione.did.wallet.zkp.data.credential.*;
import org.omnione.did.wallet.zkp.data.credentialrequest.BlindedCredentialSecrets;

import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.*;
import org.omnione.did.wallet.zkp.revoc.data.PointG2;
import org.omnione.did.wallet.zkp.revoc.data.dto.RevocationRegistryDefinitionInfo;
import org.omnione.did.wallet.zkp.revoc.data.dto.RevocationRegistryInfo;
import org.omnione.did.wallet.zkp.revoc.enums.IssuanceType;
import org.omnione.did.wallet.zkp.revoc.impl.CredentialGeneratorCallback;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpSetting;
import org.omnione.did.wallet.zkp.revoc.utils.*;
import org.omnione.did.wallet.zkp.util.generator.CredentialSignatureGenerator;
import org.omnione.did.wallet.zkp.util.generator.CredentialSignatureProofGenerator;
import org.omnione.did.wallet.zkp.util.gson.ZkpGsonWrapper;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.TreeSet;


public class CredentialHelper {

    public static Credential genCredential(int revocationIndex, RevocationRegistryDefinitionInfo revocationRegistryDefinitionInfo,
                                                    CredentialDefinition credentialDefinition,
                                                    CredentialRequest credentialRequest,
                                                    LinkedHashMap<String, AttributeValue> values,
                                                    CredentialPrimaryKeyPair credentialKeyPair,
                                                    CredentialRevocationKeyPair credentialRevocationKeyPair) throws ZkpException {

        CredentialValues credentialValues = new CredentialValues();
        credentialValues.setValues(values);

        // CL
        // Create Primary Claim Credential c1 = c1(...,(m2, m3,...m(n))
        PrimaryCredentialSignature pCredSignature
                = CredentialSignatureGenerator.generateCredential(credentialRequest.getProverDid(), revocationIndex, credentialKeyPair, credentialValues, credentialRequest.getBlindedMs());

        CredentialSignature credSignature = new CredentialSignature();
        credSignature.setPrimaryCredential(pCredSignature);

        // Create Non-Revocation Credentials c2
        if (ZkpSetting.getInstance().isSupportedRevocation()) {
            NonRevocationCredentialSignature nonRevocationCredentialSignature = CredentialSignatureGenerator.generateRevocationCredentialSignature(
                    credentialRequest.getProverDid(),
                    revocationIndex,
                    credentialRevocationKeyPair,
                    revocationRegistryDefinitionInfo.getRevocationPrivateKey(),
                    credentialRequest.getBlindedMs());
            credSignature.setNonRevocationCredential(nonRevocationCredentialSignature);
        }

        SignatureCorrectnessProof proof
                = CredentialSignatureProofGenerator.generateSignatureCorrectnessProof(
                credSignature,
                credentialKeyPair,
                credentialRequest.getNonce());

        Credential credential = new Credential();
        credential.setCredDefId(credentialDefinition.getId());
        credential.setSchemaId(credentialDefinition.getSchemaId());
        credential.setRevRegDefId(ZkpSetting.getInstance().isSupportedRevocation() ? revocationRegistryDefinitionInfo.getRevocationRegistryDefinition().getRevocationRegistryId(): null);
        credential.setValues(values);
        credential.setCredentialSignature(credSignature);
        credential.setSignatureCorrectnessProof(proof);

        return credential;
    }

    public static Credential genTestCredential(RevocationRegistryDefinitionInfo revocationRegistryDefinitionInfo,
                                                         CredentialDefinition credentialDefinition,
                                                         CredentialRequest credentialRequest,
                                                         LinkedHashMap<String, AttributeValue> values,
                                                         int maxCredNum,
                                                         CredentialPrimaryKeyPair credentialKeyPair,
                                                         CredentialRevocationKeyPair credentialRevocationKeyPair) throws ZkpException {

        CredentialValues credentialValues = new CredentialValues();
        credentialValues.setValues(values);

        TreeSet<Integer> issued = new TreeSet<Integer>();
        TreeSet<Integer> revoked = new TreeSet<Integer>();

        if (ZkpSetting.getInstance().isSupportedRevocation()) {
            // get Revocation Registration info from wallet
            int currentIndex = revocationRegistryDefinitionInfo.getRevocationRegistryInfo().getCurrentId();
            revocationRegistryDefinitionInfo.getRevocationRegistryInfo().setCurrentId(++currentIndex);

            if (ZkpSetting.getInstance().getIssuanceType() == IssuanceType.ISSUANCE_BY_DEFAULT) {
                revoked = revocationRegistryDefinitionInfo.getRevocationRegistryInfo().getUsedIds();

            } else {
                issued = revocationRegistryDefinitionInfo.getRevocationRegistryInfo().getUsedIds();
                issued.add(revocationRegistryDefinitionInfo.getRevocationRegistryInfo().getCurrentId());
                revocationRegistryDefinitionInfo.getRevocationRegistryInfo().setUsedIds(issued);
            }
        }

        // Select accumulator with ID = iA (issue a new one if needed)
        int revocationIndex = ZkpSetting.getInstance().isSupportedRevocation() ? revocationRegistryDefinitionInfo.getRevocationRegistryInfo().getCurrentId() : 0;

        // wallet 저장 - credential 발급시 current_id가 max_cred_number 보다 크면 accum is full
        if ( ZkpSetting.getInstance().isSupportedRevocation() && revocationIndex > ZkpSetting.getInstance().getMaxCredNumber()) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_ISSUER_ISSUED_CRED_NUMBER_OVER);
        }

        // CL
        // Create Primary Claim Credential c1 = c1(...,(m2, m3,...m(n))
        PrimaryCredentialSignature pCredSignature = CredentialSignatureGenerator.generateCredential(credentialRequest.getProverDid(), revocationIndex,
                                                                                    credentialKeyPair, credentialValues, credentialRequest.getBlindedMs());

        CredentialSignature credSignature = new CredentialSignature();
        credSignature.setPrimaryCredential(pCredSignature);

        // Create Non-Revocation Credentials c2
        NonRevocationCredentialSignature nonRevocationCredentialSignature
                = ZkpSetting.getInstance().isSupportedRevocation() ? CredentialSignatureGenerator.generateRevocationCredentialSignature(credentialRequest.getProverDid(),
                                                                                                                                revocationIndex, credentialRevocationKeyPair,
                                                                                                                                revocationRegistryDefinitionInfo.getRevocationPrivateKey(),
                                                                                                                                credentialRequest.getBlindedMs()) : null;

        Credential credential = new Credential();

        if (ZkpSetting.getInstance().isSupportedRevocation()) {
            // index
            int index = maxCredNum + 1 - revocationIndex;

            if (ZkpSetting.getInstance().getIssuanceType() == IssuanceType.ISSUANCE_ON_DEMAND) {
                PointG2 prev_acc = new PointG2();
                prev_acc.getPoint().copy(revocationRegistryDefinitionInfo.getRevocationRegistry().getAccum().getPoint());
                AMCLUtils.toHashString(prev_acc, "prev_acc");
                // 누적
                PointG2 accumulator = new PointG2();
                accumulator.getPoint().copy(prev_acc.getPoint());

                // rev_tails_accessor.access_tail
                Tail tail = RevocationTailsAccessor.accessTail(revocationRegistryDefinitionInfo.getRevocationRegistryDefinition().getValue().getTailsHash(), index);

                accumulator.getPoint().add(tail.getTail().getPoint());
                revocationRegistryDefinitionInfo.getRevocationRegistry().getAccum().getPoint().copy(accumulator.getPoint());
            }

            revocationRegistryDefinitionInfo.getRevocationRegistry().setAccum(revocationRegistryDefinitionInfo.getRevocationRegistry().getAccum());

            RevocationRegistryDelta delta = RevocationRegistryDelta.fromParts(null, revocationRegistryDefinitionInfo.getRevocationRegistry(), issued, revoked);

            Witness witness = new Witness(revocationRegistryDefinitionInfo.getRevocationRegistryDefinition().getValue().getTailsHash(),
                    nonRevocationCredentialSignature.getRevIndex(),
                    maxCredNum,
                    ZkpSetting.getInstance().getIssuanceType() == IssuanceType.ISSUANCE_BY_DEFAULT,
                    delta);

            credSignature.setNonRevocationCredential(nonRevocationCredentialSignature);

            credential.setRevRegDefId(revocationRegistryDefinitionInfo.getRevocationRegistryDefinition().getRevocationRegistryId());
            credential.setWitness(witness);
            credential.setRevReg(revocationRegistryDefinitionInfo.getRevocationRegistry());
        }

        SignatureCorrectnessProof proof
                = CredentialSignatureProofGenerator.generateSignatureCorrectnessProof(
                credSignature,
                credentialKeyPair,
                credentialRequest.getNonce());


        credential.setCredDefId(credentialDefinition.getId());
        credential.setSchemaId(credentialDefinition.getSchemaId());
        credential.setValues(values);
        credential.setCredentialSignature(credSignature);
        credential.setSignatureCorrectnessProof(proof);


        return credential;
    }

    public static Credential generateCredential(RevocationRegistryDefinitionInfo revocationRegistryDefinitionInfo,
                                          CredentialDefinition credentialDefinition,
                                          CredentialRequest credentialRequest,
                                          LinkedHashMap<String, AttributeValue> values,
                                          int maxCredNum,
                                          CredentialPrimaryKeyPair credentialKeyPair,
                                          CredentialRevocationKeyPair credentialRevocationKeyPair) throws ZkpException {

        CredentialValues credentialValues = new CredentialValues();
        credentialValues.setValues(values);

        // get Revocation Registration info from wallet
        int currentIndex = revocationRegistryDefinitionInfo.getRevocationRegistryInfo().getCurrentId();
        revocationRegistryDefinitionInfo.getRevocationRegistryInfo().setCurrentId(++currentIndex);

        TreeSet<Integer> issued = new TreeSet<Integer>();
        TreeSet<Integer> revoked = new TreeSet<Integer>();

        if (ZkpSetting.getInstance().getIssuanceType() == IssuanceType.ISSUANCE_BY_DEFAULT) {
            revoked = revocationRegistryDefinitionInfo.getRevocationRegistryInfo().getUsedIds();

        } else {
            issued = revocationRegistryDefinitionInfo.getRevocationRegistryInfo().getUsedIds();
            issued.add(revocationRegistryDefinitionInfo.getRevocationRegistryInfo().getCurrentId());
            revocationRegistryDefinitionInfo.getRevocationRegistryInfo().setUsedIds(issued);
        }

        // Select accumulator with ID = iA (issue a new one if needed)
        int revocationIndex = revocationRegistryDefinitionInfo.getRevocationRegistryInfo().getCurrentId();

        // wallet 저장 - credential 발급시 current_id가 max_cred_number 보다 크면 accum is full
        if (revocationIndex > ZkpSetting.getInstance().getMaxCredNumber()) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_ISSUER_ISSUED_CRED_NUMBER_OVER);
        }

        // CL
        // Create Primary Claim Credential c1 = c1(...,(m2, m3,...m(n))
        PrimaryCredentialSignature pCredSignature
                = CredentialSignatureGenerator.generateCredential(credentialRequest.getProverDid(), revocationIndex, credentialKeyPair, credentialValues, credentialRequest.getBlindedMs());

        CredentialSignature credSignature = new CredentialSignature();
        credSignature.setPrimaryCredential(pCredSignature);

        // Create Non-Revocation Credentials c2
        NonRevocationCredentialSignature nonRevocationCredentialSignature = CredentialSignatureGenerator.generateRevocationCredentialSignature(
                credentialRequest.getProverDid(),
                revocationIndex,
                credentialRevocationKeyPair,
                revocationRegistryDefinitionInfo.getRevocationPrivateKey(),
                credentialRequest.getBlindedMs()
        );

        // index
        int index = maxCredNum + 1 - revocationIndex;

        Tail tail;
        if (ZkpSetting.getInstance().getIssuanceType() == IssuanceType.ISSUANCE_ON_DEMAND) {
            PointG2 prev_acc = new PointG2();
            prev_acc.getPoint().copy(revocationRegistryDefinitionInfo.getRevocationRegistry().getAccum().getPoint());
            // 누적
            PointG2 accumulator = new PointG2();
            accumulator.getPoint().copy(prev_acc.getPoint());

            // rev_tails_accessor.access_tail
            tail = RevocationTailsAccessor.accessTail(revocationRegistryDefinitionInfo.getRevocationRegistryDefinition().getValue().getTailsHash(), index);
            accumulator.getPoint().add(tail.getTail().getPoint());
            revocationRegistryDefinitionInfo.getRevocationRegistry().getAccum().getPoint().copy(accumulator.getPoint());
        }

        revocationRegistryDefinitionInfo.getRevocationRegistry().setAccum(revocationRegistryDefinitionInfo.getRevocationRegistry().getAccum());

        RevocationRegistryDelta delta = RevocationRegistryDelta.fromParts(null, revocationRegistryDefinitionInfo.getRevocationRegistry(), issued, revoked);

        Witness witness = new Witness(revocationRegistryDefinitionInfo.getRevocationRegistryDefinition().getValue().getTailsHash(),
                nonRevocationCredentialSignature.getRevIndex(),
                maxCredNum,
                ZkpSetting.getInstance().getIssuanceType() == IssuanceType.ISSUANCE_BY_DEFAULT,
                delta);

        credSignature.setNonRevocationCredential(nonRevocationCredentialSignature);

        SignatureCorrectnessProof proof
                = CredentialSignatureProofGenerator.generateSignatureCorrectnessProof(
                credSignature,
                credentialKeyPair,
                credentialRequest.getNonce());

        Credential credential = new Credential();
        credential.setCredDefId(credentialDefinition.getId());
        credential.setSchemaId(credentialDefinition.getSchemaId());
        credential.setRevRegDefId(revocationRegistryDefinitionInfo.getRevocationRegistryDefinition().getRevocationRegistryId());
        credential.setValues(values);
        credential.setCredentialSignature(credSignature);
        credential.setSignatureCorrectnessProof(proof);
        credential.setWitness(witness);
        credential.setRevReg(revocationRegistryDefinitionInfo.getRevocationRegistry());

        return credential;
    }

    public static void generateCredential(RevocationRegistryDefinitionInfo revocationRegistryDefinitionInfo,
                                          CredentialDefinition credentialDefinition,
                                          CredentialRequest credentialRequest,
                                          LinkedHashMap<String, AttributeValue> values,
                                          int maxCredNum,
                                          CredentialPrimaryKeyPair credentialKeyPair,
                                          CredentialRevocationKeyPair credentialRevocationKeyPair,
                                          CredentialGeneratorCallback callback) throws ZkpException, IWException {


        CredentialValues credentialValues = new CredentialValues();
        credentialValues.setValues(values);

        // get Revocation Registration info from wallet
        int prevCurrentId = revocationRegistryDefinitionInfo.getRevocationRegistryInfo().getCurrentId();
        revocationRegistryDefinitionInfo.getRevocationRegistryInfo().setCurrentId(++prevCurrentId);

        TreeSet<Integer> issued = new TreeSet<Integer>();
        TreeSet<Integer> revoked = new TreeSet<Integer>();

        if (ZkpSetting.getInstance().getIssuanceType() == IssuanceType.ISSUANCE_BY_DEFAULT) {
            revoked = revocationRegistryDefinitionInfo.getRevocationRegistryInfo().getUsedIds();

        } else {
            issued = revocationRegistryDefinitionInfo.getRevocationRegistryInfo().getUsedIds();
            issued.add(revocationRegistryDefinitionInfo.getRevocationRegistryInfo().getCurrentId());
            revocationRegistryDefinitionInfo.getRevocationRegistryInfo().setUsedIds(issued);
        }

        // Select accumulator with ID = iA (issue a new one if needed)
        int revocationIndex = revocationRegistryDefinitionInfo.getRevocationRegistryInfo().getCurrentId();

        // wallet 저장 - credential 발급시 current_id가 max_cred_number 보다 크면 accum is full
        if (revocationIndex > ZkpSetting.getInstance().getMaxCredNumber()) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_ISSUER_ISSUED_CRED_NUMBER_OVER);
        }

        // CL
        // Create Primary Claim Credential c1 = c1(...,(m2, m3,...m(n))
        PrimaryCredentialSignature pCredSignature
                = CredentialSignatureGenerator.generateCredential(credentialRequest.getProverDid(), revocationIndex, credentialKeyPair, credentialValues, credentialRequest.getBlindedMs());

        CredentialSignature credSignature = new CredentialSignature();
        credSignature.setPrimaryCredential(pCredSignature);

        // Create Non-Revocation Credentials c2
        NonRevocationCredentialSignature nonRevocationCredentialSignature = CredentialSignatureGenerator.generateRevocationCredentialSignature(
                credentialRequest.getProverDid(),
                maxCredNum,
                credentialRevocationKeyPair,
                revocationRegistryDefinitionInfo.getRevocationPrivateKey(),
                credentialRequest.getBlindedMs());

        // index
        int index = maxCredNum + 1 - revocationIndex;

        if (ZkpSetting.getInstance().getIssuanceType() == IssuanceType.ISSUANCE_ON_DEMAND) {
            PointG2 prev_acc = new PointG2();
            prev_acc.getPoint().copy(revocationRegistryDefinitionInfo.getRevocationRegistry().getAccum().getPoint());
            PointG2 accumulator = new PointG2();
            accumulator.getPoint().copy(prev_acc.getPoint());

            // rev_tails_accessor.access_tail
            Tail tail = RevocationTailsAccessor.accessTail(revocationRegistryDefinitionInfo.getRevocationRegistryDefinition().getValue().getTailsHash(), index);
            accumulator.getPoint().add(tail.getTail().getPoint());
            revocationRegistryDefinitionInfo.getRevocationRegistry().getAccum().getPoint().copy(accumulator.getPoint());
        }
        revocationRegistryDefinitionInfo.getRevocationRegistry().setAccum(revocationRegistryDefinitionInfo.getRevocationRegistry().getAccum());

        RevocationRegistryDelta delta = RevocationRegistryDelta.fromParts(null, revocationRegistryDefinitionInfo.getRevocationRegistry(), issued, revoked);

        // TODO 속도 개선 필요
        Witness witness = new Witness(revocationRegistryDefinitionInfo.getRevocationRegistryDefinition().getValue().getTailsHash(),
                nonRevocationCredentialSignature.getRevIndex(),
                maxCredNum,
                ZkpSetting.getInstance().getIssuanceType() == IssuanceType.ISSUANCE_BY_DEFAULT,
                delta);

        credSignature.setNonRevocationCredential(nonRevocationCredentialSignature);
        SignatureCorrectnessProof proof
                = CredentialSignatureProofGenerator.generateSignatureCorrectnessProof(
                credSignature,
                credentialKeyPair,
                credentialRequest.getNonce());

        Credential credential = new Credential();
        credential.setCredDefId(credentialDefinition.getId());
        credential.setSchemaId(credentialDefinition.getSchemaId());
        credential.setRevRegDefId(revocationRegistryDefinitionInfo.getRevocationRegistryDefinition().getRevocationRegistryId());
        credential.setValues(values);
        credential.setCredentialSignature(credSignature);
        credential.setSignatureCorrectnessProof(proof);
        credential.setWitness(witness);
        credential.setRevReg(revocationRegistryDefinitionInfo.getRevocationRegistry());
        callback.onComplete(credential, delta, revocationRegistryDefinitionInfo);

    }


    public static Credential generateCredential(String schemaId,
                                                String credDefId,
                                                String revRegId,
                                                BigInteger nonce,
                                                LinkedHashMap<String, AttributeValue> values,
                                                CredentialPrimaryKeyPair credentialKeyPair,
                                                CredentialRevocationKeyPair credentialRevocationKeyPair,
                                                BlindedCredentialSecrets blindedSecrets,
                                                RevocationPrivateKey revocationPrivateKey,
                                                RevocationRegistry revocationRegistry) throws ZkpException {

        //TODO: 테스트 코드
        CredentialValues credentialValues = new CredentialValues();
        credentialValues.setValues(values);

        // m2 연산 추가
        PrimaryCredentialSignature pCredSignature
                = CredentialSignatureGenerator.generateCredential(credentialKeyPair,
                credentialValues,
                blindedSecrets);
        CredentialSignature credSignature = new CredentialSignature();
        credSignature.setPrimaryCredential(pCredSignature);

        // r_Credential 생성
//        NonRevocationCredentialSignature rCredSignature
//                = CredentialSignatureGenerator.generateRevocationCredentialSignature(
//                credentialRevocationKeyPair,
//                revocationKeyPrivate,
//                blindedSecrets,
//                revocationRegistry);

        credSignature.setNonRevocationCredential(null);

        SignatureCorrectnessProof proof
                = CredentialSignatureProofGenerator.generateSignatureCorrectnessProof(
                        credSignature,
                        credentialKeyPair,
                        nonce);

        Credential credential = new Credential();

        credential.setCredDefId(schemaId);
        credential.setSchemaId(credDefId);
        credential.setRevRegDefId(revRegId);

        credential.setValues(values);
        credential.setCredentialSignature(credSignature);
        credential.setSignatureCorrectnessProof(proof);
        credential.setWitness(null);
        credential.setRevReg(revocationRegistry);

        return credential;
    }

    public static RevocationRegistryDelta revokeCredential(IWKeyManager keyManager,
                                                           int maxCredNum,
                                                           int revIdx,
                                                           RevocationRegistryDefinition revocationRegistryDefinition,
                                                           RevocationRegistry revocationRegistry) throws ZkpException, IWException {
        PointG2 prevAccum = new PointG2();
        prevAccum.getPoint().copy(revocationRegistry.getAccum().getPoint());
        int index = maxCredNum + 1 - revIdx;

        Tail tail = RevocationTailsAccessor.accessTail(revocationRegistryDefinition.getValue().getTailsHash(), index);
        PointG2 accum = new PointG2();
        accum.getPoint().copy(revocationRegistry.getAccum().getPoint());
        accum.getPoint().sub(tail.getTail().getPoint());

        TreeSet<Integer> issued = new TreeSet<Integer>();
        TreeSet<Integer> revoked = new TreeSet<Integer>();
        revoked.add(revIdx);

        try {
            RevocationRegistryDefinitionInfo revocationRegistryDefinitionInfo = keyManager.getRevRegInfo(revocationRegistryDefinition.getRevocationRegistryId());
            TreeSet<Integer> usedIds = revocationRegistryDefinitionInfo.getRevocationRegistryInfo().getUsedIds();

            if (!usedIds.contains(revIdx)) {
                throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_ISSUER_NOT_CONTAIN_REVOCATION_INDEX_IN_USED_ID, "revIdx: {"+revIdx +"} is not contains in usedIds: {"+usedIds+"}");
            }

            if (revocationRegistryDefinition.getValue().getIssuanceType() == IssuanceType.ISSUANCE_ON_DEMAND) { // 차집합
                revocationRegistryDefinitionInfo.getRevocationRegistryInfo().setUsedIds(usedIds);
                usedIds.removeAll(revoked);
            } else {    // 합집합
                usedIds.addAll(revoked);
                revocationRegistryDefinitionInfo.getRevocationRegistryInfo().setUsedIds(usedIds);
            }

            keyManager.removeRevRegInfos();
            keyManager.addRevRegInfos(ZkpGsonWrapper.getGson().toJson(revocationRegistryDefinitionInfo));
            RevocationRegistryDelta revocationRegistryDelta = new RevocationRegistryDelta(prevAccum, accum, issued, revoked);
            revocationRegistry.setAccum(revocationRegistryDelta.getAccum());
            return revocationRegistryDelta;

        } catch (IWException e) {
            throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_DUPLICATED_VC_ID);
        }
    }

    public static RevocationRegistryDelta recoveryCredential(IWKeyManager keyManager,
                                                             int maxCredNum,
                                                             int revIdx,
                                                             RevocationRegistryDefinition revocationRegistryDefinition,
                                                             RevocationRegistry revocationRegistry) throws ZkpException, IWException {

        PointG2 prevAccum = new PointG2();
        prevAccum.getPoint().copy(revocationRegistry.getAccum().getPoint());
        int index = maxCredNum + 1 - revIdx;

        Tail tail = RevocationTailsAccessor.accessTail(revocationRegistryDefinition.getValue().getTailsHash(), index);
        PointG2 accum = new PointG2();
        accum.getPoint().copy(revocationRegistry.getAccum().getPoint());
        accum.getPoint().add(tail.getTail().getPoint());

        TreeSet<Integer> issued = new TreeSet<Integer>();
        issued.add(revIdx);
        TreeSet<Integer> revoked = new TreeSet<Integer>();

        try {
            RevocationRegistryDefinitionInfo revocationRegistryDefinitionInfo = keyManager.getRevRegInfo(revocationRegistryDefinition.getRevocationRegistryId());
            TreeSet<Integer> usedIds = revocationRegistryDefinitionInfo.getRevocationRegistryInfo().getUsedIds();

            if (revocationRegistryDefinitionInfo.getRevocationRegistryInfo().getCurrentId() < revIdx || usedIds.contains(revIdx)) {
                throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_ISSUER_NOT_CONTAIN_REVOCATION_INDEX_IN_USED_ID, "revIdx: {"+revIdx +"} is not contains in usedIds: {"+usedIds+"}");
            }

            if (revocationRegistryDefinition.getValue().getIssuanceType() == IssuanceType.ISSUANCE_ON_DEMAND) { // 합집합
                revocationRegistryDefinitionInfo.getRevocationRegistryInfo().setUsedIds(usedIds);
                usedIds.addAll(issued);
            }
            else {    // 차집합
                usedIds.removeAll(issued);
                revocationRegistryDefinitionInfo.getRevocationRegistryInfo().setUsedIds(usedIds);
            }

            keyManager.removeRevRegInfos();
            keyManager.addRevRegInfos(ZkpGsonWrapper.getGson().toJson(revocationRegistryDefinitionInfo));
        } catch (IWException e) {
            throw new IWException(IWErrorCode.ERR_CODE_KEYMANAGER_DUPLICATED_VC_ID);
        }

        RevocationRegistryDelta revocationRegistryDelta = new RevocationRegistryDelta(prevAccum, accum, issued, revoked);
        revocationRegistry.setAccum(revocationRegistryDelta.getAccum());
        return revocationRegistryDelta;
    }

    public static void main(String args[]) {

        RevocationRegistryInfo regInfo = new RevocationRegistryInfo();
        regInfo.setCurrentId(1);
//        regInfo.setUsedIds(new TreeSet<>());

        TreeSet<Integer> useds = new TreeSet<Integer>();
        useds.add(1);
        useds.add(2);
        useds.add(3);
        useds.add(4);
        regInfo.setUsedIds(useds);

        TreeSet<Integer> issued = new TreeSet<Integer>();
        TreeSet<Integer> revoked = new TreeSet<Integer>();
        revoked.add(1);

        TreeSet<Integer> usedIds = regInfo.getUsedIds();
        System.out.println("usedIds: "+usedIds.toString());

        usedIds.removeAll(revoked);
        regInfo.setUsedIds(usedIds);
        System.out.println("usedIds: "+usedIds.toString());
    }
}
