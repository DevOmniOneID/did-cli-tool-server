package org.omnione.did.wallet.zkp.util.helper;

import org.omnione.did.wallet.zkp.ZkpConstants;
import org.omnione.did.wallet.zkp.data.MasterSecret;
import org.omnione.did.wallet.zkp.data.attribute.AttributeValue;
import org.omnione.did.wallet.zkp.data.proof.CredentialValues;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Set;

public class CredentialValueHelper {
    // Credential Value 생성
    public static CredentialValues generateCredentialValues(LinkedHashMap<String, AttributeValue> credValues, MasterSecret masterSecret) throws ZkpException {

        CredentialValues credentialValues = new CredentialValues();
        credentialValues.addHidden(ZkpConstants.MASTER_SECRET_KEY, masterSecret.getMasterSecret());
        Set<String> set = credValues.keySet();
        for(String key: set) {
            AttributeValue value = credValues.get(key);
            try {
                credentialValues.addKnown(key, new BigInteger(value.getRaw()));
            } catch (NumberFormatException numberFormatException) {
                MessageDigest md = null;
                try {
                    md = MessageDigest.getInstance(ZkpConstants.HASH_ALG);
                    credentialValues.addKnown(key, new BigInteger(1, md.digest(value.getRaw().getBytes())));
                } catch (NoSuchAlgorithmException e) {
                    throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NO_SUCH_ALG);
                }
            }
        }

        return credentialValues;

    }
}
