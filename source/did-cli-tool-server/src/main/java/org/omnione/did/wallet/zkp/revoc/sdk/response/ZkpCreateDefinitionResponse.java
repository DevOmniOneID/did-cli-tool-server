package org.omnione.did.wallet.zkp.revoc.sdk.response;

import org.omnione.did.wallet.zkp.data.CredentialDefinition;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.revoc.data.dto.CredentialDefinitionInfo;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpResponse;


/**
 * Copyright 2021 Raoncorp, Inc.
 * core_sdk
 * Class: ZkpCreateDefinitionResponse (Builder)
 * Created by djpark on 2021/03/18.
 *
 * Description:
 */
public class ZkpCreateDefinitionResponse extends ZkpResponse {

    private CredentialDefinitionInfo credentialDefinitionInfo;

    private ZkpCreateDefinitionResponse(Builder builder) {

        super(builder.errorCode, builder.errorMessage);
        this.credentialDefinitionInfo = builder.credentialDefinitionInfo;
    }

    public CredentialDefinitionInfo getCredentialDefinitionInfo() {
        return credentialDefinitionInfo;
    }

    public static class Builder {
        private ZkpErrorCode errorCode;
        private String errorMessage;
        private CredentialDefinition credentialDefinition;
        private CredentialDefinitionInfo credentialDefinitionInfo;

        public Builder() {

        }

        public Builder(ZkpErrorCode errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        /**
         * @param credentialDefinitionInfo credentialDefinitionInfo
         */
        public Builder setCredentialDefinitionInfo(CredentialDefinitionInfo credentialDefinitionInfo) {
            this.credentialDefinitionInfo = credentialDefinitionInfo;
            return this;
        }


        public ZkpCreateDefinitionResponse build() {
            return new ZkpCreateDefinitionResponse(this);
        }
    }
}
