package org.omnione.did.wallet.zkp.revoc.sdk.response;

import org.omnione.did.wallet.zkp.data.schema.CredentialSchema;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpResponse;


/**
 * Copyright 2021 Raoncorp, Inc.
 * core_sdk
 * Class: ZkpCreateSchemaResponse (Builder)
 * Created by djpark on 2021/03/18.
 *
 * Description:
 */
public class ZkpCreateSchemaResponse extends ZkpResponse {

    private CredentialSchema credentialSchema;

    private ZkpCreateSchemaResponse(ZkpCreateSchemaResponse.Builder builder) {

        super(builder.errorCode, builder.errorMessage);
        credentialSchema = builder.CredentialSchema;
    }

    public CredentialSchema getCredentialSchema() {
        return credentialSchema;
    }

    public static class Builder {
        private ZkpErrorCode errorCode;
        private String errorMessage;
        private CredentialSchema CredentialSchema;

        public Builder() {

        }

        public Builder(ZkpErrorCode errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        /**
         * @param credentialSchema credentialSchema
         */
        public Builder setCredentialSchema(CredentialSchema credentialSchema) {
            CredentialSchema = credentialSchema;
            return this;
        }

        public ZkpCreateSchemaResponse build() {
            return new ZkpCreateSchemaResponse(this);
        }
    }
}
