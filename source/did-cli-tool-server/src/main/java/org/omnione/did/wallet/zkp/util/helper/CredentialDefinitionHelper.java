package org.omnione.did.wallet.zkp.util.helper;

import org.omnione.did.wallet.zkp.data.CredentialDefinition;
import org.omnione.did.wallet.zkp.data.keypair.CredentialPrimaryPublicKey;
import org.omnione.did.wallet.zkp.enums.CredentialType;
import org.omnione.did.wallet.zkp.revoc.CredentialRevocationPublicKey;

public class CredentialDefinitionHelper {
    public static CredentialDefinition generateCredentialDefinition(String did, String schemaId, String ver, String tag,
                                                                    CredentialPrimaryPublicKey publicKey,
                                                                    CredentialRevocationPublicKey revocPublicKey
    ) {

        CredentialDefinition credDef = new CredentialDefinition();
        credDef.setSchemaId(schemaId);
        credDef.setId(ZkpIdHelper.generateCredentialDefinitionId(did, schemaId, tag));

        //TODO: 현재 CL 만 지원
        credDef.setType(CredentialType.CL);
        credDef.setVer(ver);
        credDef.setPrimaryKey(publicKey);
        credDef.setRevocationKey(revocPublicKey);
        credDef.setTag(tag);

        return credDef;
    }
}
