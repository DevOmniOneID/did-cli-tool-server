package org.omnione.did.wallet.zkp.revoc.utils;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.json.GsonWrapper;
import org.omnione.did.wallet.zkp.revoc.RevocationRegistry;
import org.omnione.did.wallet.zkp.revoc.data.PointG2;
import org.omnione.did.wallet.zkp.revoc.data.dto.ZkpSerializer;

import java.util.TreeSet;

public class RevocationRegistryDelta {

    @JsonAdapter(ZkpSerializer.class)
    private PointG2 prevAccum;

    @JsonAdapter(ZkpSerializer.class)
    private PointG2 accum;

    private TreeSet<Integer> issued;
    private TreeSet<Integer> revoked;

    public RevocationRegistryDelta() {

        prevAccum = new PointG2();
        accum = new PointG2();
        issued = new TreeSet<Integer>();
        revoked = new TreeSet<Integer>();
    }

    public RevocationRegistryDelta(PointG2 prev_accum,
                                   PointG2 accum,
                                   TreeSet<Integer> issued,
                                   TreeSet<Integer> revoked) {

        this.prevAccum = prev_accum;
        this.accum = accum;
        this.issued = issued;
        this.revoked = revoked;

//        AMCLUtils.toHashString(prev_accum, "prev_accum");
//        AMCLUtils.toHashString(accum, "accum");
//        System.out.println("issued: "+issued);
//        System.out.println("revoked: "+revoked);
    }

    public static RevocationRegistryDelta fromParts(RevocationRegistry revRegFrom,
                                             RevocationRegistry revRegTo,
                                             TreeSet<Integer> issued,
                                             TreeSet<Integer> revoked) {

        return new RevocationRegistryDelta(revRegFrom == null ? null: revRegFrom.getAccum(),
                revRegTo.getAccum(),
                issued,
                revoked
                );
    }

    public void merge(RevocationRegistryDelta other_delta) {
        this.accum = other_delta.accum;
/**
 *         self.issued
 *             .extend(other_delta.issued.difference(&self.revoked));
 *
 *         self.revoked
 *             .extend(other_delta.revoked.difference(&self.issued));
 *
 *         for index in other_delta.revoked.iter() {
 *             self.issued.remove(index);
 *         }
 *
 *         for index in other_delta.issued.iter() {
 *             self.revoked.remove(index);
 *         }
 * */
    }

    public PointG2 getPrevAccum() {
        return prevAccum;
    }

    public void setPrevAccum(PointG2 prevAccum) {
        this.prevAccum = prevAccum;
    }

    public PointG2 getAccum() {
        return accum;
    }

    public void setAccum(PointG2 accum) {
        this.accum = accum;
    }

    public TreeSet<Integer> getIssued() {
        return issued;
    }

    public void setIssued(TreeSet<Integer> issued) {
        this.issued = issued;
    }

    public TreeSet<Integer> getRevoked() {
        return revoked;
    }

    public void setRevoked(TreeSet<Integer> revoked) {
        this.revoked = revoked;
    }

    public String toJson() {
        GsonWrapper gson = new GsonWrapper();
        return gson.toJson(this);
    }
}
