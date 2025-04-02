package org.omnione.did.wallet.zkp.data;

import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.json.GsonWrapper;
import org.omnione.did.wallet.zkp.data.proof.RequestedProof;
import org.omnione.did.wallet.zkp.data.proofrequest.AttributeInfo;
import org.omnione.did.wallet.zkp.data.proofrequest.PredicateInfo;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.utils.NonRevocedInterval;
import org.omnione.did.wallet.zkp.util.gson.BigIntegerSerializer;
import org.omnione.did.wallet.zkp.util.gson.ZkpGsonWrapper;

import java.math.BigInteger;
import java.util.Map;

public class ProofRequest {

    private String name;
    private String version;

    @JsonAdapter(BigIntegerSerializer.class)
    private BigInteger nonce;

//    @SerializedName("requested_attributes")
    private Map<String, AttributeInfo> requestedAttributes;

//    @SerializedName("requested_predicates")
    private Map<String, PredicateInfo> requestedPredicates;

    private NonRevocedInterval nonRevoked;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public BigInteger getNonce() {
        return nonce;
    }
    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public Map<String, AttributeInfo> getRequestedAttributes() {
        return requestedAttributes;
    }
    public void setRequestedAttributes(Map<String, AttributeInfo> requestedAttributes) {
        this.requestedAttributes = requestedAttributes;
    }

    public Map<String, PredicateInfo> getRequestedPredicates() {
        return requestedPredicates;
    }
    public void setRequestedPredicates(Map<String, PredicateInfo> requestedPredicates) {
        this.requestedPredicates = requestedPredicates;
    }

    public NonRevocedInterval getNonRevoked() {
        return nonRevoked;
    }
    public void setNonRevoked(NonRevocedInterval nonRevoked) {
        this.nonRevoked = nonRevoked;
    }

    //TODO: Hyperledger Indy에 데이터 타입에 맞추느라 생긴 메소드 (재고려 필요)
    public SubProofRequest getSubProofRequest(int index, RequestedProof requestedProof) throws ZkpException {



        try {
            Map<String, Map<String, String>> revealedAttrMap = requestedProof.getRevealedAttrs();

            Map<String, Map<String, String>> predicatedAttrMap = requestedProof.getPredicates();

            SubProofRequest subProofRequest = new SubProofRequest();

            for (String revealedAttrKey : revealedAttrMap.keySet()) {
                Map<String, String> revealedAttrValue = revealedAttrMap.get(revealedAttrKey);

                for (String revealedAttrSubKey : revealedAttrValue.keySet()) {

//                    if (revealedAttrSubKey.equals("sub_proof_index")) {
                    if (revealedAttrSubKey.equals("subProofIndex")) {
                        String subProofIndex = revealedAttrValue.get(revealedAttrSubKey);

                        if (index == Integer.parseInt(subProofIndex)) {

                            //TODO: 가독성 개선 필요
                            for (Map.Entry<String, AttributeInfo> entry : this.getRequestedAttributes().entrySet()) {

                                if (revealedAttrKey.equals(entry.getKey())) {

                                    subProofRequest.addRevealedAttribute(entry.getValue().getName());
//                                    System.out.println("subProofRequest.addRevealedAttribute: " + ZkpGsonWrapper.getGsonPrettyPrinting().toJson(subProofRequest.getRevealedAttrs()));
                                }
                            }
                        }
                    }
                }
            }

            for (String predicateAttrKey : predicatedAttrMap.keySet()) {
                Map<String, String> predicateAttrValue = predicatedAttrMap.get(predicateAttrKey);

                for (String predicateAttrSubKey : predicateAttrValue.keySet()) {

//                    if (predicateAttrSubKey.equals("sub_proof_index")) {
                    if (predicateAttrSubKey.equals("subProofIndex")) {
                        String subProofIndex = predicateAttrValue.get(predicateAttrSubKey);

                        if (index == Integer.parseInt(subProofIndex)) {

                            for (Map.Entry<String, PredicateInfo> entry : this.getRequestedPredicates().entrySet()) {
                                if (predicateAttrKey.equals(entry.getKey())) {
                                    subProofRequest.addPredicate(entry.getValue().getName(), entry.getValue().getPType(), entry.getValue().getPValue());
//                                    System.out.println("subProofRequest.addPredicate: " + ZkpGsonWrapper.getGsonPrettyPrinting().toJson(subProofRequest.getPredicates()));
                                }
                            }

                        }
                    }
                }
            }

            //TODO: 가독성 개선 필요
//            for (Map.Entry<String, AttributeInfo> entry : this.getRequestedAttributes().entrySet()) {
//
//                if (revealedAttrKey.equals(entry.getKey())) {
//                    subProofRequest.addRevealedAttribute(entry.getValue().getName());
//                }
//            }

            //TODO: 가독성 개선 필요
//            for (Map.Entry<String, PredicateInfo> entry : this.getRequestedPredicates().entrySet()) {
//                if (revealedAttrKey.equals(entry.getKey())) {
//                subProofRequest.addPredicate(entry.getValue().getName(), entry.getValue().getPType(), entry.getValue().getPValue());
//            }
//        }

            return subProofRequest;
        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_UNKNOWN, e.getMessage());
        }
    }

    public String toJson() {
        GsonWrapper gson = new GsonWrapper();
        return gson.toJson(this);
    }
}
