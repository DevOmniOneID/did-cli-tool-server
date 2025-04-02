package org.omnione.did.wallet.zkp.revoc.sdk.response;

import org.omnione.did.wallet.zkp.data.Credential;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpResponse;
import org.omnione.did.wallet.zkp.revoc.utils.Tail;


/**
 * Copyright 2021 Raoncorp, Inc.
 * core_sdk
 * Class: ZkpCreateCredentialResponse (Builder)
 * Created by djpark on 2021/03/18.
 *
 * Description:
 */
public class ZkpCreateCredentialResponse extends ZkpResponse {

    private Credential credential;
    private String credentialId;

    private ZkpCreateCredentialResponse(Builder builder) {

        super(builder.errorCode, builder.errorMessage);
        this.credential = builder.credential;
        this.credentialId = builder.credentialId;
    }

    public Credential getCredential() {
        return this.credential;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public static class Builder {
        private ZkpErrorCode errorCode;
        private String errorMessage;
        private Credential credential;
        private String credentialId;

        public Builder() {

        }

        public Builder(ZkpErrorCode errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
        /**
         * @param credential credential
         */
        public Builder setCredential(Credential credential) {
            this.credential = credential;
            return this;
        }

        public Builder setCredentialId(String credentialId) {
            this.credentialId = credentialId;
            return this;
        }

        public ZkpCreateCredentialResponse build() {
            return new ZkpCreateCredentialResponse(this);
        }


    }

}
