package org.omnione.did.wallet.zkp.util.helper;

import org.omnione.did.wallet.zkp.enums.CredentialType;
import org.omnione.did.wallet.zkp.enums.Marker;
import org.omnione.did.wallet.zkp.revoc.enums.RegistryType;

public class ZkpIdHelper {

    private static String DELIMITER = ":";

    // 2oTF7LvWLUft1UC6qVkTCg:2:DriverLicense:1.0
    public static String generateSchemaId(String issuerDID, String schemaName, String schemaVersion) {
        return issuerDID + DELIMITER + Marker.CRED_SCHEMA.getMaker() + DELIMITER + schemaName + DELIMITER + schemaVersion;
    }
    // 2oTF7LvWLUft1UC6qVkTCg:3:CL:2oTF7LvWLUft1UC6qVkTCg:2:DriverLicense:1.0:TAG1
    public static String generateCredentialDefinitionId(String issuerDID, String schemaId, String tag) {
        return issuerDID + DELIMITER + Marker.CRED_DEF.getMaker() +DELIMITER + CredentialType.CL + DELIMITER + schemaId + DELIMITER + tag;
    }
    // 2oTF7LvWLUft1UC6qVkTCg:4:2oTF7LvWLUft1UC6qVkTCg:3:CL:2oTF7LvWLUft1UC6qVkTCg:2:DriverLicense:1.0:TAG1:CL_ACCUM:TAG2
    public static String generateRevocationRegistryDefinitionId(String issuerDID, String credentialDefId, String tag) {
        return issuerDID + DELIMITER + Marker.REV_REG_DEF.getMaker() + DELIMITER + credentialDefId + DELIMITER + RegistryType.CL_ACCUM + DELIMITER + tag;
    }

}
