package org.omnione.did.wallet.zkp.util.bulider;

import org.omnione.did.wallet.zkp.data.SubProofRequest;
import org.omnione.did.wallet.zkp.data.proofrequest.PredicateInfo;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import java.util.HashSet;

public class SubProofRequestBuilder {

    private SubProofRequest subProofRequest = new SubProofRequest();

    public SubProofRequestBuilder() {

    }

    public SubProofRequestBuilder addRevealedAttr(HashSet<String> requestedAttributes) throws ZkpException {

        for (String attr : requestedAttributes) {
            subProofRequest.addRevealedAttribute(attr);
        }
        return this;
    }

    public SubProofRequestBuilder addPredicateAttr(HashSet<PredicateInfo> requestedPredicates) throws ZkpException {

        for (PredicateInfo predicateInfo :requestedPredicates) {
            subProofRequest.addPredicate(predicateInfo.getName(), predicateInfo.getPType(), predicateInfo.getPValue());
        }
        return this;
    }


    public SubProofRequest build() {

        return subProofRequest;
    }
}
