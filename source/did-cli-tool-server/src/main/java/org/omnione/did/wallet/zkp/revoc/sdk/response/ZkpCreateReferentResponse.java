package org.omnione.did.wallet.zkp.revoc.sdk.response;

import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.revoc.data.dto.ReferentInfo;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpResponse;

public class ZkpCreateReferentResponse extends ZkpResponse {
    private ReferentInfo referentInfo;

    private ZkpCreateReferentResponse(Builder builder) {
        super(builder.errorCode, builder.errorMessage);
        this.referentInfo = builder.referentInfo;
    }

    public ReferentInfo getReferentInfo() {
        return referentInfo;
    }

    public static class Builder {
        private ZkpErrorCode errorCode;
        private String errorMessage;
        private ReferentInfo referentInfo;

        public Builder() {

        }

        public Builder(ZkpErrorCode errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        /**
         * @param referentInfo referentInfo
         */
        public Builder setReferentInfo(ReferentInfo referentInfo) {
            this.referentInfo = referentInfo;
            return this;
        }

        public ZkpCreateReferentResponse build() {
            return new ZkpCreateReferentResponse(this);
        }
    }
}