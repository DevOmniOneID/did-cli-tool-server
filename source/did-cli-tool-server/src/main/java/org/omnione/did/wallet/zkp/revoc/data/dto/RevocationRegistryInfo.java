package org.omnione.did.wallet.zkp.revoc.data.dto;

import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.json.GsonWrapper;

import java.util.TreeSet;

public class RevocationRegistryInfo {

    // revocation reg id
    private String id;
    // 필요조건 currentId++ < max credential number
    @SerializedName("curr_id")
    private int currentId;
    // current id 값 저장 (발급시)
    @SerializedName("used_ids")
    private TreeSet<Integer> usedIds;

    public RevocationRegistryInfo() {}

    public RevocationRegistryInfo(String id, int currId, TreeSet<Integer>used_ids) {
        this.id = id;
        this.currentId = currId;
        this.usedIds = used_ids;
    }

    public TreeSet<Integer> getUsedIds() {
        return usedIds;
    }

    public void setUsedIds(TreeSet<Integer> usedIds) {
        this.usedIds = usedIds;
    }

    public int getCurrentId() {
        return currentId;
    }

    public void setCurrentId(int currentId) {
        this.currentId = currentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void fromJson(String val) {
        GsonWrapper gson = new GsonWrapper();
        RevocationRegistryInfo result = gson.fromJson(val, RevocationRegistryInfo.class);

        this.id = result.getId();
        this.currentId = result.getCurrentId();
        this.usedIds = result.getUsedIds();
    }
}
