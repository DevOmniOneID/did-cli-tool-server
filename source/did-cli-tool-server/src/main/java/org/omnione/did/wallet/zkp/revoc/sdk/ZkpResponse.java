package org.omnione.did.wallet.zkp.revoc.sdk;

import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;

/**
 * Copyright 2021 Raoncorp, Inc.
 * core_sdk
 * Class: ZkpResponse
 * Created by djpark on 2021/03/18.
 *
 * Description:
 */
public class ZkpResponse {

    private int errorCode;
    private String errorMessage;
    private boolean isSuccess;

    public ZkpResponse(){}

    public ZkpResponse(ZkpErrorCode errorCode, String errorMessage) {
        this.errorCode = errorCode.getCode();
        this.errorMessage = errorMessage;
        this.isSuccess = this.errorCode == 0 ? true: false;
    }

    /**
     * 성공 시 true, 실패 시 false 반환
     */
    public boolean isSuccess() {
        return isSuccess;
    }

    /**
     * 에러 코드 반환 (ErrorCode.java 참고)
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * 에러 메시지 반환 (ErrorCode.java 참고)
     */
    public String getErrorMessage() {
        return errorMessage;
    }


}
