package org.omnione.did.wallet.zkp.data.subproof.primaryproof;

import org.omnione.did.wallet.zkp.data.subproof.PrimaryEqualProof;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.util.BigIntegerUtil;

import java.math.BigInteger;
import java.util.Vector;

public class PrimaryInitProof {

    private PrimaryEqualInitProof eq_proof;
    private Vector<PrimaryPredicateInequalityInitProof> ne_proofs;

    public PrimaryInitProof(PrimaryEqualInitProof eq_proof,
                            Vector<PrimaryPredicateInequalityInitProof> ne_proofs) {

        this.eq_proof = eq_proof;
        this.ne_proofs = ne_proofs;
    }

    public PrimaryEqualInitProof getEqProof() {
        return eq_proof;
    }

    public Vector<PrimaryPredicateInequalityInitProof> getNeProofs() {
        return ne_proofs;
    }

    public Vector<byte[]> getCommonValue() throws ZkpException {

        Vector<byte[]> retvalue = new Vector<byte[]>();

        retvalue.add(eq_proof.getCommonValue());

        for (PrimaryPredicateInequalityInitProof entry : ne_proofs) {
            for (BigInteger entry1 : entry.getCommonValues()) {
                retvalue.add(BigIntegerUtil.asUnsignedByteArray(entry1));
            }
        }

        return retvalue;
    }

    public Vector<byte[]> getTValue() throws ZkpException {
        Vector<byte[]> retvalue = new Vector<byte[]>();

        retvalue.add(eq_proof.getTvalue());

        for (PrimaryPredicateInequalityInitProof entry : ne_proofs) {
            for (BigInteger entry1 : entry.getTValues()) {
                retvalue.add(BigIntegerUtil.asUnsignedByteArray(entry1));
            }
        }

        return retvalue;

    }
}
