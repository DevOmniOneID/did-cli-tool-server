package org.omnione.did.wallet.zkp.revoc.sdk.response;

import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpResponse;

public class ZkpCreateMasterSecretResponse extends ZkpResponse {
    private String masterSecretId;

    private ZkpCreateMasterSecretResponse(Builder builder) {
        super(builder.errorCode, builder.errorMessage);
        this.masterSecretId = builder.masterSecretId;
    }

    public String getMasterSecreteId() {
        return masterSecretId;
    }

    public static class Builder {
        private ZkpErrorCode errorCode;
        private String errorMessage;
        private String masterSecretId;

        public Builder() {

        }

        public Builder(ZkpErrorCode errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }


        public Builder setMasterSecretId(String masterSecretId) {
            this.masterSecretId = masterSecretId;
            return this;
        }

        public ZkpCreateMasterSecretResponse build() {
            return new ZkpCreateMasterSecretResponse(this);
        }
    }
}

