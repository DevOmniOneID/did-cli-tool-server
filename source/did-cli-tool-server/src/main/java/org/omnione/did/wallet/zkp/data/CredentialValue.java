package org.omnione.did.wallet.zkp.data;


import com.google.gson.annotations.JsonAdapter;
import org.omnione.did.wallet.zkp.enums.AttributeType;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerSerializer;

import java.math.BigInteger;

public class CredentialValue {
    private final AttributeType type;

    @JsonAdapter(BigIntegerSerializer.class)
    private final BigInteger value;

    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger blindingFactor;

    public CredentialValue(AttributeType type, BigInteger value) {
        this.type = type;
        this.value = value;
    }

    public boolean isKnown() {
        return type.equals(AttributeType.Known);
    }

    public boolean isHidden() {
        return type.equals(AttributeType.Hidden);
    }

    public boolean isCommitment() {
        return type.equals(AttributeType.Commitment);
    }

    public BigInteger getValue() {
        return value;
    }

    public AttributeType getType() {
        return type;
    }

    public BigInteger getBlindingFactor() {
        return blindingFactor;
    }
}
