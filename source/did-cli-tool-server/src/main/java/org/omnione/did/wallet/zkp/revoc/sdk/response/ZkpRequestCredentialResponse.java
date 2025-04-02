package org.omnione.did.wallet.zkp.revoc.sdk.response;

import org.omnione.did.wallet.zkp.data.CredentialRequest;
import org.omnione.did.wallet.zkp.data.attribute.AttributeValue;
import org.omnione.did.wallet.zkp.data.credentialrequest.CredentialRequestMeta;
import org.omnione.did.wallet.zkp.data.credentialrequest.MasterSecretBlindingData;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpResponse;

import java.util.LinkedHashMap;


/**
 * Copyright 2021 Raoncorp, Inc.
 * core_sdk
 * Class: ZkpRequestCredentialResponse (Builder)
 * Created by djpark on 2021/03/18.
 *
 * Description:
 */
public class ZkpRequestCredentialResponse extends ZkpResponse {

    private CredentialRequest credentialRequest;
    private CredentialRequestMeta credentialRequestMeta;
//    private MasterSecretBlindingData masterSecretBlindingData;
    private LinkedHashMap<String, AttributeValue> credentialValue;

    private ZkpRequestCredentialResponse(Builder builder) {

        super(builder.errorCode, builder.errorMessage);
        credentialRequest = builder.credentialRequest;
        credentialValue = builder.credentialValue;
        credentialRequestMeta = builder.credentialRequestMeta;
//        masterSecretBlindingData = builder.masterSecretBlindingData;
    }

    public CredentialRequest getCredentialRequest() {
        return credentialRequest;
    }

    public LinkedHashMap<String, AttributeValue> getCredentialValue() {
        return credentialValue;
    }

    public CredentialRequestMeta getCredentialRequestMeta() {
        return credentialRequestMeta;
    }
//    public MasterSecretBlindingData getMasterSecretBlindingData() {
//        return masterSecretBlindingData;
//    }

    public static class Builder {
        private ZkpErrorCode errorCode;
        private String errorMessage;
        private CredentialRequest credentialRequest;
        private LinkedHashMap<String, AttributeValue> credentialValue;
        private CredentialRequestMeta credentialRequestMeta;
//        private MasterSecretBlindingData masterSecretBlindingData;

        public Builder() {

        }

        public Builder(ZkpErrorCode errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        /**
         * @param credentialRequest credentialRequest
         */
        public Builder setCredentialRequest(CredentialRequest credentialRequest) {
            this.credentialRequest = credentialRequest;
            return this;
        }

        public Builder setCredentialValue(LinkedHashMap<String, AttributeValue> credentialValue) {
            this.credentialValue = credentialValue;
            return this;
        }
        public Builder setCredentialRequestMeta(CredentialRequestMeta credentialRequestMeta) {

            /** Gson 어노테이션 @Expose(serialize = false, deserialize = false)은 jackson 과 호환되지 않는 문제로 추가로 null 값 대입
             * vr_prime, v_prime 를 prover 가 보관하고 issuer 에게 공개되어서는 값이므로 초기화
             */
//            this.masterSecretBlindingData = masterSecretBlindingData;
            this.credentialRequestMeta = credentialRequestMeta;
            this.credentialRequest.getBlindedMs().setVrPrime(null);
            this.credentialRequest.getBlindedMs().setVPrime(null);
            return this;
        }

        public ZkpRequestCredentialResponse build() {
            return new ZkpRequestCredentialResponse(this);
        }
    }

}
