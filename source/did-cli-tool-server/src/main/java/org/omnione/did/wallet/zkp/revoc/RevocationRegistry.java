package org.omnione.did.wallet.zkp.revoc;


import com.google.gson.annotations.JsonAdapter;
import org.omnione.did.wallet.util.json.GsonWrapper;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.data.PointG2;
import org.omnione.did.wallet.zkp.revoc.data.dto.ZkpSerializer;
import org.omnione.did.wallet.zkp.revoc.generator.RevocationRegistryKeys;

import org.omnione.did.wallet.zkp.revoc.utils.Tail;


public class RevocationRegistry {
    @JsonAdapter(ZkpSerializer.class)
    private PointG2 accum;

    public RevocationRegistry(PointG2 accum) {
        this.accum = accum;
    }

    public RevocationRegistry(boolean issuance_by_default,
                              int maxCredNum,
                              CredentialRevocationPublicKey credentialRevocationPublicKey,
                              RevocationRegistryKeys revocationRegistryKeys) throws ZkpException {
        accum = new PointG2();
        accum.getPoint().inf();

        if (issuance_by_default) {

            for (int i = 1; i <= maxCredNum; i++) {
                int index = maxCredNum + 1 - i;
                Tail tail = new Tail();
                accum.getPoint().add(tail.new_tail(
                        index, credentialRevocationPublicKey.getGDash(),
                        revocationRegistryKeys.getRevocPrivateKey().getGamma()));
            }
        }
    }

    public PointG2 getAccum() {
        return accum;
    }

    public void setAccum(PointG2 accum) {
        this.accum = accum;
    }

    public String toJson() {
        GsonWrapper gson = new GsonWrapper();
        return gson.toJson(this);
    }
}
