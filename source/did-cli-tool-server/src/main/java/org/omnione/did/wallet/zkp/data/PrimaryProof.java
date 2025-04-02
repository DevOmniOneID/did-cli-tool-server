package org.omnione.did.wallet.zkp.data;

import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.data.subproof.PrimaryEqualProof;
import org.omnione.did.wallet.zkp.data.subproof.PrimaryPredicateInequalityProof;

import java.util.Vector;

public class PrimaryProof {

//    @SerializedName("eq_proof")
    private PrimaryEqualProof eqProof;

//    @SerializedName("ne_proofs")
    private Vector<PrimaryPredicateInequalityProof> neProofs;

    public PrimaryProof() {}
    public PrimaryProof(PrimaryEqualProof eqProof, Vector<PrimaryPredicateInequalityProof> neProofs) {
        this.eqProof = eqProof;
        this.neProofs = neProofs;
    }

    public PrimaryEqualProof getEqProof() {
        return eqProof;
    }
    public void setEqProof(PrimaryEqualProof eqProof) {
        this.eqProof = eqProof;
    }

    public Vector<PrimaryPredicateInequalityProof> getNeProofs() {
        return neProofs;
    }
    public void setNeProofs(Vector<PrimaryPredicateInequalityProof> neProofs) {
        this.neProofs = neProofs;
    }
}
