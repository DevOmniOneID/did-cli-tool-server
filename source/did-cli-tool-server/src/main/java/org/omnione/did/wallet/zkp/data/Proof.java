package org.omnione.did.wallet.zkp.data;

import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.json.GsonWrapper;
import org.omnione.did.wallet.zkp.data.proof.AggregatedProof;
import org.omnione.did.wallet.zkp.data.proof.RequestedProof;
import org.omnione.did.wallet.zkp.revoc.data.dto.Identifiers;


import java.util.List;
import java.util.Vector;

public class Proof {

//    @SerializedName("proofs")
    private Vector<SubProof> proofs;

//    @SerializedName("aggregated_proof")
    private AggregatedProof aggregatedProof;

//    @SerializedName("requested_proof")
    private RequestedProof requestedProof;

//    @SerializedName("identifiers")
    private List<Identifiers> identifiers;


    public Proof(Vector<SubProof> proofs, AggregatedProof aggregated_proof, RequestedProof requestedProof, List<Identifiers> identifiers) {
        this.proofs = proofs;
        this.aggregatedProof = aggregated_proof;
        this.requestedProof = requestedProof;
        this.identifiers = identifiers;
    }

    public Vector<SubProof> getProofs() {
        return proofs;
    }

    public AggregatedProof getAggregatedProof() {
        return aggregatedProof;
    }


    public RequestedProof getRequestedProof() {
        return requestedProof;
    }

    public void setRequestedProof(RequestedProof requestedProof) {
        this.requestedProof = requestedProof;
    }

    public List<Identifiers> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<Identifiers> identifiers) {
        this.identifiers = identifiers;
    }
    public String toJson() {
        return GsonWrapper.getGsonPrettyPrinting().toJson(this);
    }


    /**
     "requested_proof":{
        "self_attested_attrs":{
            "12345":"department"
         },

         "revealed_attrs":{
            "attribute_referent_1":{
                "sub_proof_index":0,
                "raw":"백승룡",
                "encoded":"41440224498274700026154110175728197752744900050379147319278593295723915494171"
            }
         },
         "unrevealed_attrs":{

         },
         "predicates":{
            "predicate_referent_2":{
                "sub_proof_index":0
            },
         "predicate_referent_1":{
            "sub_proof_index":0
         }
        }
    },

     "identifiers":[{
         "schema_id":"did:omn:NcYxiDXkpYi6ov5FcYDi1e:2:transcript:1.0:raon:2:License:1.0",
         "cred_def_id":"did:omn:NcYxiDXkpYi6ov5FcYDi1e:3:CL:did:omn:NcYxiDXkpYi6ov5FcYDi1e:2:transcript:1.0:raon:2:License:1.0:Tag1",
         "rev_reg_id":"did:omn:NcYxiDXkpYi6ov5FcYDi1e:4:did:omn:NcYxiDXkpYi6ov5FcYDi1e:3:CL:did:omn:NcYxiDXkpYi6ov5FcYDi1e:2:transcript:1.0:raon:2:License:1.0:Tag1:CL_ACCUM:Tag1",
         "timestamp":0
        }]

     * */
}
