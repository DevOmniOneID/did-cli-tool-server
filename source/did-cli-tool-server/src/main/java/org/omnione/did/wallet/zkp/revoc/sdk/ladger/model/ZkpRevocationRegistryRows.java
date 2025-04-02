package org.omnione.did.wallet.zkp.revoc.sdk.ladger.model;

import java.io.Serializable;

public class ZkpRevocationRegistryRows extends ZkpTableRows implements Serializable {

    private String revocentry_id;
    private String revocentry_id_hash;
    private String revocentry_desc;
    private String revocentry_value;


    public String getRevocentry_id() {
        return revocentry_id;
    }

    public void setRevocentry_id(String revocentry_id) {
        this.revocentry_id = revocentry_id;
    }

    public String getRevocentry_id_hash() {
        return revocentry_id_hash;
    }

    public void setRevocentry_id_hash(String revocentry_id_hash) {
        this.revocentry_id_hash = revocentry_id_hash;
    }

    public String getRevocentry_desc() {
        return revocentry_desc;
    }

    public void setRevocentry_desc(String revocentry_desc) {
        this.revocentry_desc = revocentry_desc;
    }

    public String getRevocentry_value() {
        return revocentry_value;
    }

    public void setRevocentry_value(String revocentry_value) {
        this.revocentry_value = revocentry_value;
    }
}
