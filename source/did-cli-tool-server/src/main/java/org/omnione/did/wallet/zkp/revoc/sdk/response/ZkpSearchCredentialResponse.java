package org.omnione.did.wallet.zkp.revoc.sdk.response;

import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.revoc.data.dto.AvailableReferent;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpResponse;

/**
 * Copyright 2021 Raoncorp, Inc.
 * core_sdk
 * Class: ZkpSearchCredentialResponse (Builder)
 * Created by djpark on 2021/03/18.
 *
 * Description:
 */
public class ZkpSearchCredentialResponse extends ZkpResponse {

    private AvailableReferent availableReferent;

    private ZkpSearchCredentialResponse(Builder builder) {

        super(builder.errorCode, builder.errorMessage);
        availableReferent = builder.availableReferent;
    }

    public AvailableReferent getAvailableReferent() {
        return availableReferent;
    }

    public static class Builder {
        private ZkpErrorCode errorCode;
        private String errorMessage;
        private AvailableReferent availableReferent;

        public Builder() {

        }

        public Builder(ZkpErrorCode errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        public Builder setAvailableReferent(AvailableReferent availableReferent) {
            this.availableReferent = availableReferent;
            return this;
        }

        public ZkpSearchCredentialResponse build() {
            return new ZkpSearchCredentialResponse(this);
        }
    }
}
