package org.omnione.did.wallet.zkp.data.attribute;

import com.google.gson.annotations.JsonAdapter;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerSerializer;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AttributeValue {

    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger encoded;
    private String raw;

    public String getRaw() {
        return raw;
    }
    public BigInteger getEncoded() {
        return encoded;
    }

    public void setRaw(String raw) throws ZkpException {
        this.raw = raw;
        this.encoded = genEncode(raw);
//        this.encoded = new BigInteger(raw);
    }
    public void setEncoded(BigInteger encoded) {
        this.encoded = encoded;
    }

    public BigInteger getEncode() {
        return this.encoded;
    }

    private BigInteger genEncode(String raw) throws ZkpException {

        try {
            return new BigInteger(raw);

        } catch (NumberFormatException ne) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                return new BigInteger(1, md.digest(raw.getBytes()));

            } catch (NoSuchAlgorithmException ex) {
                throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NO_SUCH_ALG);
            } catch (Exception e) {
                throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_RAW_TO_ENCODED_FAIL);
            }
        }
//        try {
//            MessageDigest md = MessageDigest.getInstance(ZkpConstants.HASH_ALG);
//            return new BigInteger(1, md.digest(raw.getBytes()));
//        } catch (NoSuchAlgorithmException e) {
//            throw new ZkpException(ErrorCode.NO_SUCH_ALG, ZkpConstants.HASH_ALG);
//        }
    }
}
