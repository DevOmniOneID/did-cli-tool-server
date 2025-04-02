package org.omnione.did.wallet.zkp.data;

import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.enums.PredicateType;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;

import java.math.BigInteger;

public class Predicate {
//    @SerializedName("p_type")
    private PredicateType pType;

//    @SerializedName("p_value")
    private int pValue;

//    @SerializedName("attr_name")
    private String attrName;

    public Predicate() {}
    public Predicate(String attrName, PredicateType pType, int value) {
        this.pType = pType;
        this.attrName = attrName;
        this.pValue = value;
    }

    public PredicateType getPType() {
        return pType;
    }
    public void setPType(PredicateType pType) {
        this.pType = pType;
    }

    public int getPValue() {
        return pValue;
    }
    public void setPValue(int pValue) {
        this.pValue = pValue;
    }

    public String getAttrName() {
        return attrName;
    }
    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    //TODO: delta 0처리 재고려
    public int getDelta(int attr_value) {
        int delta = 0;

        switch(pType) {
            case GE:
                delta = attr_value - pValue;
                break;
            case GT:
                delta = attr_value - pValue - 1;
                break;
            case LE:
                delta = pValue - attr_value;
                break;
            case LT:
                delta = pValue - attr_value - 1;
                break;
        }
        return delta;
    }
    //Wrapper Method
    public int getDelta(BigInteger value) throws ZkpException {

        if (value == null) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "delta is null ");
        }
        return getDelta(value.intValue());
    }
    //Wrapper Method
    public int getDelta(CredentialValue credValue) throws ZkpException {
        if (credValue == null) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "delta is null ");
        }
        return getDelta(credValue.getValue().intValue());
    }

    public BigInteger getDeltaPrime() {
        int invalue = pValue;
        switch(pType) {
            case GT:
                invalue++;
                break;
            case LT:
                invalue--;
                break;
        }
        return new BigInteger(Integer.toString(invalue), 10);
    }

    public boolean isLess() {
        if (pType == PredicateType.LE || pType == PredicateType.LT) {
            return true;
        }
        return false;
    }
}
