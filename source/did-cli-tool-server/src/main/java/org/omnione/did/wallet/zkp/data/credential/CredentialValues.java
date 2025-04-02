package org.omnione.did.wallet.zkp.data.credential;

import org.omnione.did.wallet.zkp.data.attribute.AttributeValue;

import java.util.Map;

public class CredentialValues {


    private Map<String, AttributeValue> values;

    public Map<String, AttributeValue> getValues() {
        return values;
    }
    public void setValues(Map<String, AttributeValue> values) {
        this.values = values;
    }
}
