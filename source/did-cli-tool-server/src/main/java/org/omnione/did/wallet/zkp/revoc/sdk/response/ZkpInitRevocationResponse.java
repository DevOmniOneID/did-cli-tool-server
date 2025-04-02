package org.omnione.did.wallet.zkp.revoc.sdk.response;

import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.revoc.RevocationRegistry;
import org.omnione.did.wallet.zkp.revoc.RevocationRegistryDefinition;
import org.omnione.did.wallet.zkp.revoc.data.dto.RevocationRegistryDefinitionInfo;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpResponse;


/**
 * Copyright 2021 Raoncorp, Inc.
 * core_sdk
 * Class: ZkpInitRevocationResponse (Builder)
 * Created by djpark on 2021/03/18.
 *
 * Description:
 */
public class ZkpInitRevocationResponse extends ZkpResponse {

    private RevocationRegistryDefinitionInfo revocationRegistryDefinitionInfo;

    private ZkpInitRevocationResponse(Builder builder) {

        super(builder.errorCode, builder.errorMessage);
        this.revocationRegistryDefinitionInfo = builder.revocationRegistryDefinitionInfo;
    }

    public RevocationRegistryDefinitionInfo getRevocationRegistryDefinitionInfo() {
        return revocationRegistryDefinitionInfo;
    }

    public static class Builder {

        private ZkpErrorCode errorCode;
        private String errorMessage;
        private RevocationRegistryDefinitionInfo revocationRegistryDefinitionInfo;

        public Builder() {

        }
        public Builder(ZkpErrorCode errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        /**
         * @param revocationRegistryDefinitionInfo revocationRegistryDefinitionInfo
         */
        public Builder setRevocationRegistryDefinitionInfo(RevocationRegistryDefinitionInfo revocationRegistryDefinitionInfo) {
            this.revocationRegistryDefinitionInfo = revocationRegistryDefinitionInfo;
            return this;
        }

        public ZkpInitRevocationResponse build() {
            return new ZkpInitRevocationResponse(this);
        }
    }
}
