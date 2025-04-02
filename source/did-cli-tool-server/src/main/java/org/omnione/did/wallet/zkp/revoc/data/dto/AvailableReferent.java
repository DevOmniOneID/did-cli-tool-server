package org.omnione.did.wallet.zkp.revoc.data.dto;

import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.util.json.GsonWrapper;
import org.omnione.did.wallet.zkp.data.Credential;
import org.omnione.did.wallet.zkp.data.attribute.AttributeValue;
import org.omnione.did.wallet.zkp.data.proofrequest.AttributeInfo;
import org.omnione.did.wallet.zkp.data.proofrequest.PredicateInfo;
import org.omnione.did.wallet.zkp.enums.ZkpErrorCode;
import org.omnione.did.wallet.zkp.enums.PredicateType;
import org.omnione.did.wallet.zkp.exceptions.ZkpException;
import org.omnione.did.wallet.zkp.revoc.sdk.ZkpLogger;
import org.omnione.did.wallet.zkp.util.gson.ZkpGsonWrapper;

import java.util.*;

public class AvailableReferent {
//    @SerializedName("self_attr_referent")
    private Map<String, AttrReferent> selfAttrReferent;

//    @SerializedName("attr_referent")
    private Map<String, AttrReferent> attrReferent;

//    @SerializedName("predicates_referent")
    private Map<String, PredicateReferent> predicateReferent;

    public AvailableReferent() {}

    public AvailableReferent(AvailableReferent.Builder builder) {
        this.attrReferent = builder.attrReferent;
        this.predicateReferent = builder.predicateReferent;
        this.selfAttrReferent = builder.selfAttrReferent;
    }

    public Map<String, AttrReferent> getAttrReferent() {
        return attrReferent;
    }

    public Map<String, AttrReferent> getSelfAttrReferent() {
        return selfAttrReferent;
    }

    public void addAttrReferent(String key, AttrReferent value) {
        this.attrReferent.put(key, value);
    }

    public void addPredicates(String key, PredicateReferent value) {
        this.predicateReferent.put(key, value);
    }

    public Map<String, PredicateReferent> getPredicateReferent() {
        return predicateReferent;
    }



    public static class Builder {
        private Map<String, AttrReferent> selfAttrReferent = new HashMap<String, AttrReferent>();
        private Map<String, AttrReferent> attrReferent = new HashMap<String, AttrReferent>();
        private Map<String, PredicateReferent> predicateReferent = new HashMap<String, PredicateReferent>();

        public Builder() {}

        public Builder setSelfAttrReferent(Map<String, AttrReferent> selfAttrReferent) {
            this.selfAttrReferent = selfAttrReferent;
            return this;
        }
        public Builder setAttrReferent(Map<String, AttrReferent> attrReferent) {
            this.attrReferent = attrReferent;
            return this;
        }

        public Builder setPredicateReferent(Map<String, PredicateReferent> predicateReferent) {
            this.predicateReferent = predicateReferent;
            return this;
        }

        public AvailableReferent build() {
            return new AvailableReferent(this);
        }
    }

    public static Map<String, AttrReferent> addSelfAttrReferent(Map<String, AttributeInfo> attrInfoMap) throws ZkpException {

        try {
//        System.out.println("attrInfoMap: "+ ZkpGsonWrapper.getGsonPrettyPrinting().toJson(attrInfoMap));
            Map<String, AttrReferent> attrMap = new HashMap<String, AttrReferent>();

            // LOOP: attribute referent
            for (String attrReferentKey : attrInfoMap.keySet()) {
                AttributeInfo attrReferentValue = attrInfoMap.get(attrReferentKey);
//            System.out.println("attrReferentValue: "+ ZkpGsonWrapper.getGsonPrettyPrinting().toJson(attrReferentValue));

                if (attrReferentValue.getRestrictions().size() == 0) {
                    List<AttrSubReferent> subList = new LinkedList<AttrSubReferent>();
                    attrMap.put(attrReferentKey, new AttrReferent.Builder().setName(attrReferentValue.getName()).setCheckRevealed(true).setAttrSubReferent(subList).build());
                }
            }

            return attrMap;
        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_NOT_FOUND_AVAILABLE_REQUEST_ATTRIBUTE);
        }
    }

    public static Map<String, AttrReferent> addAttrReferent(Map<String, AttributeInfo> attrInfoMap, ArrayList<CredentialInfo> credentialInfoList) throws ZkpException {

        ZkpLogger.debug("attrInfoMap: "+ ZkpGsonWrapper.getGsonPrettyPrinting().toJson(attrInfoMap));
        Map<String, AttrReferent> attrMap = new HashMap<String, AttrReferent>();
        try {
            // LOOP: attribute referent
            for (String attrReferentKey : attrInfoMap.keySet()) {
                AttributeInfo attrReferentValue = attrInfoMap.get(attrReferentKey);
                ZkpLogger.debug("attrReferentValue: " + ZkpGsonWrapper.getGsonPrettyPrinting().toJson(attrReferentValue));
                List<AttrSubReferent> subList = new LinkedList<AttrSubReferent>();

                for (CredentialInfo credInfo : credentialInfoList) {
//                    ZkpLogger.debug("key: "+ ZkpGsonWrapper.getGsonPrettyPrinting().toJson(credInfo.getCredentialId()));
//                    ZkpLogger.debug("value: "+ ZkpGsonWrapper.getGsonPrettyPrinting().toJson(credInfo.getCredential()));

                    Credential credential = credInfo.getCredential();

                    if (attrReferentValue.getRestrictions().size() > 0) {
                        for (Map<String, String> restrictionMap : attrReferentValue.getRestrictions()) {
//                            if (restrictionMap.get("cred_def_id").equals(credential.getCredDefId())) {
                            if (restrictionMap.get("credDefId").equals(credential.getCredDefId())) {
                                LinkedHashMap<String, AttributeValue> values = credential.getValues();

                                for (String attrKey : values.keySet()) {
                                    AttributeValue attrValue = values.get(attrKey);
                                    ZkpLogger.debug("attrKey: " + ZkpGsonWrapper.getGsonPrettyPrinting().toJson(attrKey));
                                    ZkpLogger.debug("attrValue: " + ZkpGsonWrapper.getGsonPrettyPrinting().toJson(attrValue));
                                    if (attrKey.equals(attrReferentValue.getName())) {
                                        // attrReferent 생성
                                        subList.add(new AttrSubReferent(attrValue.getRaw(), credInfo.getCredentialId(), credInfo.getCredential().getCredDefId()));
                                        attrMap.put(attrReferentKey, new AttrReferent.Builder().setName(attrKey).setCheckRevealed(true).setAttrSubReferent(subList).build());
                                    }
                                }
                            } else {

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_NOT_FOUND_AVAILABLE_REQUEST_ATTRIBUTE);
        }

        return attrMap;
    }

    public static Map<String, PredicateReferent> addPredicateReferent(Map<String, PredicateInfo> predInfoMap, ArrayList<CredentialInfo> credentialInfoList) throws ZkpException {

        ZkpLogger.debug(predInfoMap+ ZkpGsonWrapper.getGsonPrettyPrinting().toJson(predInfoMap));
        Map<String, PredicateReferent> predicateMap = new HashMap<String, PredicateReferent>();
        try {
            for (String predicateReferentKey : predInfoMap.keySet()) {
                PredicateInfo predicateReferentValue = predInfoMap.get(predicateReferentKey);
                ZkpLogger.debug("predicateReferentValue: " + ZkpGsonWrapper.getGsonPrettyPrinting().toJson(predicateReferentValue));

                List<PredicateSubReferent> predicateSubList = new LinkedList<PredicateSubReferent>();

                for (CredentialInfo credInfo : credentialInfoList) {
                    Credential credential = credInfo.getCredential();
//                    ZkpLogger.debug("key: " + ZkpGsonWrapper.getGsonPrettyPrinting().toJson(credInfo.getCredentialId()));
//                    ZkpLogger.debug("value: " + ZkpGsonWrapper.getGsonPrettyPrinting().toJson(credInfo.getCredential()));

                    if (predicateReferentValue.getRestrictions().size() > 0) {

                        for (Map<String, String> restrictionMap : predicateReferentValue.getRestrictions()) {
//                            if (restrictionMap.get("cred_def_id").equals(credential.getCredDefId())) {
                            if (restrictionMap.get("credDefId").equals(credential.getCredDefId())) {
                                LinkedHashMap<String, AttributeValue> values = credential.getValues();

                                for (String predicateKey : values.keySet()) {
                                    AttributeValue predValue = values.get(predicateKey);
                                    ZkpLogger.debug("predicateKey: " + ZkpGsonWrapper.getGsonPrettyPrinting().toJson(predicateKey));
                                    ZkpLogger.debug("predValue: " + ZkpGsonWrapper.getGsonPrettyPrinting().toJson(predValue));

                                    if (predicateKey.equals(predicateReferentValue.getName())) {

                                        /**
                                         *     GE(" > = "),
                                         *     LE(" < = "),
                                         *     GT(" > "),
                                         *     LT(" < "),
                                         **/
                                        int pValue = predicateReferentValue.getPValue();
                                        try {
                                            ZkpLogger.debug("GET VALUE: " + predicateReferentValue.getPType().getValue());
                                            ZkpLogger.debug("pValue: " + pValue);
                                            ZkpLogger.debug("raw: " + Integer.parseInt(predValue.getRaw()));
                                        } catch (Exception e) {
                                            ZkpLogger.debug("OMNI_ERROR_ZKP_NOT_SUPPORTED_PREDICATE_TYPE, " + "number format exception for input string {" + predValue.getRaw() + "}");
                                            continue;
//                                            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_NOT_SUPPORTED_PREDICATE_TYPE, "number format exception for input string {" + predValue.getRaw() + "}");
                                        }

                                        if (predicateReferentValue.getPType() == PredicateType.GE && Integer.parseInt(predValue.getRaw()) >= pValue) {
                                            predicateSubList.add(new PredicateSubReferent(predValue.getRaw(), credInfo.getCredentialId(), credInfo.getCredential().getCredDefId()));
                                            predicateMap.put(predicateReferentKey, new PredicateReferent.Builder().setName(predicateKey).setCheckRevealed(false).setPredicateReferent(predicateSubList).build());

                                        } else if (predicateReferentValue.getPType() == PredicateType.LE && Integer.parseInt(predValue.getRaw()) <= pValue) {
                                            predicateSubList.add(new PredicateSubReferent(predValue.getRaw(), credInfo.getCredentialId(), credInfo.getCredential().getCredDefId()));
                                            predicateMap.put(predicateReferentKey, new PredicateReferent.Builder().setName(predicateKey).setCheckRevealed(false).setPredicateReferent(predicateSubList).build());

                                        } else if (predicateReferentValue.getPType() == PredicateType.GT && Integer.parseInt(predValue.getRaw()) > pValue) {
                                            predicateSubList.add(new PredicateSubReferent(predValue.getRaw(), credInfo.getCredentialId(), credInfo.getCredential().getCredDefId()));
                                            predicateMap.put(predicateReferentKey, new PredicateReferent.Builder().setName(predicateKey).setCheckRevealed(false).setPredicateReferent(predicateSubList).build());

                                        } else if (predicateReferentValue.getPType() == PredicateType.LT && Integer.parseInt(predValue.getRaw()) < pValue) {
                                            predicateSubList.add(new PredicateSubReferent(predValue.getRaw(), credInfo.getCredentialId(), credInfo.getCredential().getCredDefId()));
                                            predicateMap.put(predicateReferentKey, new PredicateReferent.Builder().setName(predicateKey).setCheckRevealed(false).setPredicateReferent(predicateSubList).build());
                                        }
                                    }
                                }
                            } else {

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new ZkpException(ZkpErrorCode.OMNI_ERROR_ZKP_PROVER_NOT_FOUND_AVAILABLE_PREDICATE_ATTRIBUTE);
        }

        return predicateMap;

    }


    public static void main(String args[]) {

// attrReferent 생성
        List<AttrSubReferent>sublist = new LinkedList<AttrSubReferent>();
        sublist.add(new AttrSubReferent("english_name", "11111111-1111-1111-1111-11111111", "defid:111-1111"));

        Map<String, AttrReferent>attrMap = new HashMap<String, AttrReferent>();
        AttrReferent attrReferent = new AttrReferent.Builder()
                .setName("name")
                .setCheckRevealed(true)
                .setAttrSubReferent(sublist)
                .build();
        attrMap.put("attr2_referent", attrReferent);

// predicate Referent 생성
        List<PredicateSubReferent>predicateSubList = new LinkedList<PredicateSubReferent>();
        predicateSubList.add(new PredicateSubReferent("34", "11111111-1111-1111-1111-11111111", "def-id: 13122"));

        Map<String, PredicateReferent>predicateMap = new HashMap<String, PredicateReferent>();
        PredicateReferent predicateReferent = new PredicateReferent.Builder()
                .setName("age")
                .setCheckRevealed(false)
                .setPredicateReferent(predicateSubList)
                .build();
        predicateMap.put("predicates1_referent", predicateReferent);

// 결과
        AvailableReferent availableReferent = new AvailableReferent.Builder()
                .setAttrReferent(attrMap)
                .setPredicateReferent(predicateMap)
                .build();
        ZkpLogger.setEnabled(true);
        ZkpLogger.debug(GsonWrapper.getGsonPrettyPrinting().toJson(availableReferent));
    }
}

