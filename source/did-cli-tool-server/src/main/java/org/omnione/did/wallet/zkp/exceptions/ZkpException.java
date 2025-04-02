package org.omnione.did.wallet.zkp.exceptions;

import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;

public class ZkpException extends Exception {

    private ZkpErrorCode errCode;

    public ZkpException(ZkpErrorCode errCode) {
        this.errCode = errCode;
    }

    public ZkpException(ZkpErrorCode errCode, String errMessage) {

        this.errCode = errCode;
        this.errCode.setMessage(errMessage);
    }

    public ZkpErrorCode getErrorCode() {
        return errCode;
    }

    public void setErrorCode(ZkpErrorCode errCode) {
        this.errCode = errCode;
    }

    public String getErrorMsg() {
        return errCode.getMessage();
    }
}
