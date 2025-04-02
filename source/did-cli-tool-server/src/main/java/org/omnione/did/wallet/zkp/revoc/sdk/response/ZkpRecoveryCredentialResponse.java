package org.omnione.did.wallet.zkp.revoc.sdk.response;

import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpResponse;
import org.omnione.did.wallet.zkp.revoc.utils.RevocationRegistryDelta;


/**
 * Copyright 2021 Raoncorp, Inc.
 * core_sdk
 * Class: ZkpRecoveryCredentialResponse (Buildlr)
 * Created by djpark on 2021/03/18.
 *
 * Description:
 */
public class ZkpRecoveryCredentialResponse extends ZkpResponse {

    private RevocationRegistryDelta revocationRegistryDelta;

    private ZkpRecoveryCredentialResponse(Builder builder) {

        super(builder.errorCode, builder.errorMessage);
        this.revocationRegistryDelta = builder.revocationRegistryDelta;
    }

    public RevocationRegistryDelta getRevocationRegistryDelta() {
        return revocationRegistryDelta;
    }

    public static class Builder {
        private ZkpErrorCode errorCode;
        private String errorMessage;
        private RevocationRegistryDelta revocationRegistryDelta;

        public Builder() {

        }
        public Builder(ZkpErrorCode errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        /**
         * @param revocationRegistryDelta revocationRegistryDelta
         */
        public Builder setRevocationRegistryDelta(RevocationRegistryDelta revocationRegistryDelta) {
            this.revocationRegistryDelta = revocationRegistryDelta;
            return this;
        }

        public ZkpRecoveryCredentialResponse build() {
            return new ZkpRecoveryCredentialResponse(this);
        }
    }
}
