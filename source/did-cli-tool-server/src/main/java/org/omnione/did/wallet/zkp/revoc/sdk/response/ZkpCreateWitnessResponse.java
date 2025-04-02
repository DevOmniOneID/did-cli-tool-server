package org.omnione.did.wallet.zkp.revoc.sdk.response;

import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpResponse;
import org.omnione.did.wallet.zkp.revoc.utils.RevocationState;


/**
 * Copyright 2021 Raoncorp, Inc.
 * core_sdk
 * Class: ZkpCreateWitnessResponse (Builder)
 * Created by djpark on 2021/03/18.
 *
 * Description:
 */
public class ZkpCreateWitnessResponse extends ZkpResponse {
    private RevocationState revocationState;

    private ZkpCreateWitnessResponse(Builder builder) {
        super(builder.errorCode, builder.errorMessage);
        this.revocationState = builder.revocationState;
    }

    public RevocationState getRevocationState() {
        return revocationState;
    }

    public static class Builder {
        private ZkpErrorCode errorCode;
        private String errorMessage;
        private RevocationState revocationState;

        public Builder() {

        }

        public Builder(ZkpErrorCode errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        /**
         * @param revocationState revocationState
         */
        public Builder setRevocationState(RevocationState revocationState) {
            this.revocationState = revocationState;
            return this;
        }

        public ZkpCreateWitnessResponse build() {
            return new ZkpCreateWitnessResponse(this);
        }
    }
}

