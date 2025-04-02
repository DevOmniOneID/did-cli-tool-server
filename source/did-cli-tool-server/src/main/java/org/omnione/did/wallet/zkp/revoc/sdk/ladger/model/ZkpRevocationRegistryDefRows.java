package org.omnione.did.wallet.zkp.revoc.sdk.ladger.model;

import java.io.Serializable;

public class ZkpRevocationRegistryDefRows extends ZkpTableRows implements Serializable {

    private String revocdef_id;
    private String revocdef_id_hash;
    private String revocdef_desc;
    private String revocdef_value;

    public String getRevocdef_id() {
        return revocdef_id;
    }

    public void setRevocdef_id(String revocdef_id) {
        this.revocdef_id = revocdef_id;
    }

    public String getRevocdef_id_hash() {
        return revocdef_id_hash;
    }

    public void setRevocdef_id_hash(String revocdef_id_hash) {
        this.revocdef_id_hash = revocdef_id_hash;
    }

    public String getRevocdef_desc() {
        return revocdef_desc;
    }

    public void setRevocdef_desc(String revocdef_desc) {
        this.revocdef_desc = revocdef_desc;
    }

    public String getRevocdef_value() {
        return revocdef_value;
    }

    public void setRevocdef_value(String revocdef_value) {
        this.revocdef_value = revocdef_value;
    }
}
