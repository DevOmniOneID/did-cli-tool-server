package org.omnione.did.wallet.zkp.data.keypair;

import com.google.gson.annotations.JsonAdapter;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerMapSerializer;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerSerializer;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

public class PublicKeyMetadata {

    @JsonAdapter(BigIntegerSerializer.class)
    private final BigInteger xz;

    @JsonAdapter(BigIntegerMapSerializer.class)
    private final LinkedHashMap<String, BigInteger> xr;

    //TODO: Map의 구현체 타입을 강제하는 것 고려 (TreeMap)
    public PublicKeyMetadata(BigInteger xz, LinkedHashMap<String, BigInteger> xr) {
        this.xz = xz;
        this.xr = xr;
    }

    public BigInteger getXz() {
        return xz;
    }

    public LinkedHashMap<String, BigInteger> getXr() {
        return xr;
    }
}
