package org.omnione.did.wallet.zkp.data.proof;

import org.omnione.did.wallet.zkp.data.CredentialValue;
import org.omnione.did.wallet.zkp.data.Predicate;
import org.omnione.did.wallet.zkp.data.attribute.AttributeValue;

import org.omnione.did.wallet.zkp.enums.AttributeType;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;

import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;

public class CredentialValues {

    private Map<String, CredentialValue> attrValues;

    //TODO: TreeMap의 교체 가능 여부 검
    public CredentialValues() {
        this.attrValues = new TreeMap<String, CredentialValue>();
    }

    public Map<String, CredentialValue> getAttrValues() {
        return attrValues;
    }
    public void setAttrValues(Map<String, CredentialValue> attrValues) {
        this.attrValues = attrValues;
    }


    public void addHidden(String key, AttributeValue value) throws ZkpException {
        addHidden(key, value.getEncoded());
    }
    //TODO: String -> Bigint형변환의 위험성
    public void addHidden(String key, String decimalHexStrValue) throws ZkpException {
        addValue(AttributeType.Hidden, key, new BigInteger(decimalHexStrValue));
    }
    public void addHidden(String key, BigInteger value) throws ZkpException {
        addValue(AttributeType.Hidden, key, value);
    }

    public void addKnown(String key, AttributeValue value) throws ZkpException {
        addKnown(key, value.getEncoded());
    }
    //TODO: String -> Bigint형변환의 위험성
    public void addKnown(String key, String decimalHexStrValue) throws ZkpException {
        addValue(AttributeType.Known, key, new BigInteger(decimalHexStrValue));
    }
    public void addKnown(String key, BigInteger value) throws ZkpException {
        addValue(AttributeType.Known, key, value);
    }

    private void addValue(AttributeType type, String key, BigInteger value) throws ZkpException {
        if (attrValues.containsKey(key)) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_DUPLICATED, key);
        }
        CredentialValue credentialValue = new CredentialValue(type, value);
        attrValues.put(key, credentialValue);
    }

    //TODO: 테스트 메소드
    public CredentialValue get(String key) {
        return attrValues.get(key);
    }
    public CredentialValue get(Predicate predicate) {
        return attrValues.get(predicate.getAttrName());
    }
}
