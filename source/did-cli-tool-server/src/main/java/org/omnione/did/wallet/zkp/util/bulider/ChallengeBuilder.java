package org.omnione.did.wallet.zkp.util.bulider;

import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.util.BigIntegerUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

/**
 * for Fiat-Shamir challenge
 */
public class ChallengeBuilder {
    private final ByteArrayOutputStream values;

    public ChallengeBuilder() {
        this.values = new ByteArrayOutputStream();
    }

    public ChallengeBuilder add(BigInteger value) throws ZkpException {
        return add(BigIntegerUtil.asUnsignedByteArray(value));
    }

    //TODO: byte[]가 unsignedByte 인지 확인 필요
    public ChallengeBuilder add(byte[] value) throws ZkpException {
        try {
            this.values.write(value);
            return this;
        } catch(IOException e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_IO, "[Challenge Building]");
        }
    }

    public ChallengeBuilder add(LinkedHashMap<String, BigInteger> map) throws ZkpException {

        if (map == null || map.size() == 0) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "[Challenge Building] map");
        }

//        Map<String, BigInteger> treeMap;
//        if (map instanceof HashMap) {
//            ZkpLogger.debug("HashMap >>");
//            treeMap = new TreeMap<String, BigInteger>(map);
//        } else if (map instanceof TreeMap) {
//            ZkpLogger.debug("TreeMap >>");
//            treeMap = map;
//        } else if (map instanceof LinkedTreeMap) {
//            ZkpLogger.debug("LinkedTreeMap >>");
//            treeMap = map;
//        } else {
//            throw new ZkpException(ErrorCode.NOT_SUPPORTED_TYPE, "[Challenge Building] not supported Map Type ");
//        }

//        Map<String, BigInteger> result = MapSortUtil.sortMapByKey(map);
//        for (Map.Entry<String, BigInteger> entry : result.entrySet()) {
//            ZkpLogger.debug("Key >>> " + entry.getKey() + ", "+ "Value: " + entry.getValue());
//            this.add(entry.getValue());
//        }

        for (String key : map.keySet()) {
            this.add(map.get(key));
        }

        return this;
    }

    public <T> ChallengeBuilder add(Vector<T> vector) throws ZkpException {

        if (vector == null || vector.size() == 0) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "[Challenge Building] vector ");
        }
        T element = vector.get(0);

        if (element instanceof BigInteger) {
            for (T value : vector) {
                this.add((BigInteger)value);
//                ZkpLogger.debug(str + " add >>>> t :" + value.toString());
            }
        } else if (element instanceof byte[]) {
            for (T value : vector) {
                this.add((byte[])value);
//                ZkpLogger.debug(str+" add >>>> t :"+ AMCLUtils.byteArrayToHex((byte[])value));
            }
        } else {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NOT_SUPPORTED_TYPE, "[Challenge Building] not supported Type " + element.getClass().getName());
        }
        return this;
    }

    public final byte[] build() throws ZkpException {
        if (values == null || values.size() == 0) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NULL, "ChallengeBuilder value is null");
        }
        return this.values.toByteArray();
    }

    public final BigInteger buildWithHashing() throws ZkpException {

//        System.out.println("build hash :"+AMCLUtils.byteArrayToInteger(this.build()));
        return BigIntegerUtil.getHash(this.build());
    }
}
