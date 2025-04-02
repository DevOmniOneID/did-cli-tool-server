package org.omnione.did.wallet.zkp.data.schema;

import java.util.HashSet;
import java.util.Set;

//TODO: 클래스 재고려 필요
public class NonCredentialSchema {
    private Set<String> nonCredSchema;

    public NonCredentialSchema() {
        nonCredSchema = new HashSet<String>();
    }

    public void addAttr(String attr) {
        nonCredSchema.add(attr);
    }

    public Set<String> getNonCredSchema() {
        return nonCredSchema;
    }
}
