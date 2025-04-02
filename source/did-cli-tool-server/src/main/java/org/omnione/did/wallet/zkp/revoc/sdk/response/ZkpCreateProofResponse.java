package org.omnione.did.wallet.zkp.revoc.sdk.response;

import org.omnione.did.wallet.zkp.data.Proof;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpResponse;


/**
 * Copyright 2021 Raoncorp, Inc.
 * core_sdk
 * Class: ZkpCreateProofResponse (Builder)
 * Created by djpark on 2021/03/18.
 *
 * Description:
 */
public class ZkpCreateProofResponse extends ZkpResponse {
    private Proof proof;

    private ZkpCreateProofResponse(Builder builder) {
        super(builder.errorCode, builder.errorMessage);
        this.proof = builder.proof;
    }

    public Proof getProof() {
        return proof;
    }

    public static class Builder {
        private ZkpErrorCode errorCode;
        private String errorMessage;
        private Proof proof;

        public Builder() {

        }

        public Builder(ZkpErrorCode errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        /**
         * @param proof proof
         */
        public Builder setProof(Proof proof) {
            this.proof = proof;
            return this;
        }

        public ZkpCreateProofResponse build() {
            return new ZkpCreateProofResponse(this);
        }
    }
}
