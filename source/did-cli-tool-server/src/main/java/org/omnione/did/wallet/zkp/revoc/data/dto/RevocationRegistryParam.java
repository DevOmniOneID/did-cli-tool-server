package org.omnione.did.wallet.zkp.revoc.data.dto;

import org.omnione.did.wallet.zkp.revoc.RevocationRegistry;
import org.omnione.did.wallet.zkp.revoc.data.PointG2;

public class RevocationRegistryParam extends RevocationRegistry {

    private String revRegId;

    public RevocationRegistryParam(String revRegId, PointG2 accum) {
        super(accum);
        this.revRegId = revRegId;
    }

    public String getRevRegId() {
        return revRegId;
    }

    public void setRevRegId(String revRegId) {
        this.revRegId = revRegId;
    }
}
