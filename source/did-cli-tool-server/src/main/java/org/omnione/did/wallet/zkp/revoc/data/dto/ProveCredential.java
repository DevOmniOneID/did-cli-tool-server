package org.omnione.did.wallet.zkp.revoc.data.dto;

import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.zkp.data.CredentialDefinition;
import org.omnione.did.wallet.zkp.data.schema.CredentialSchema;
import org.omnione.did.wallet.zkp.enums.PredicateType;
import org.omnione.did.wallet.zkp.revoc.RevocationRegistryDefinition;
import org.omnione.did.wallet.zkp.revoc.utils.RevocationState;
import org.omnione.did.wallet.zkp.util.gson.ZkpGsonWrapper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ProveCredential {

    @SerializedName("cred_id")
    private String credentialId;

    private String timestemp;

    @SerializedName("revealed_attrs")
    private List<ProveRevealedAttribute> revealedAttrs;
    private List<ProveUnrevealedAttribute> unrevealedAttrs;
    private List<ProvePredicate> predicates;

    private CredentialSchema schema;
    private CredentialDefinition credentialDefinition;
    private RevocationState revocationState;

    public ProveCredential() {

    }

    public ProveCredential(Builder builder) {
        this.credentialId = builder.credentialId;
        this.timestemp = builder.timestemp;
        this.revealedAttrs = builder.revealedAttrs;
        this.unrevealedAttrs = builder.unrevealedAttrs;
        this.predicates = builder.predicates;
        this.schema = builder.schema;
        this.credentialDefinition = builder.credentialDefinition;
        this.revocationState = builder.revocationState;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public String getTimestemp() {
        return timestemp;
    }

    public List<ProveRevealedAttribute> getRevealedAttrs() {
        return revealedAttrs;
    }
    public List<ProveUnrevealedAttribute> getUnrevealedAttrs() {
        return unrevealedAttrs;
    }

    public List<ProvePredicate> getPredicates() {
        return predicates;
    }

    public CredentialSchema getSchema() {
        return schema;
    }

    public CredentialDefinition getCredentialDefinition() {
        return credentialDefinition;
    }

    public RevocationState getRevocationState() {
        return revocationState;
    }

    public static class Builder {

        private String credentialId;
        private String timestemp;
        private List<ProveRevealedAttribute> revealedAttrs = new LinkedList<ProveRevealedAttribute>();
        private List<ProveUnrevealedAttribute> unrevealedAttrs = new LinkedList<ProveUnrevealedAttribute>();
        private List<ProvePredicate> predicates = new LinkedList<ProvePredicate>();
        private CredentialSchema schema;
        private CredentialDefinition credentialDefinition;
        private RevocationState revocationState;

        public Builder() {

        }

        public Builder setCredentialId(String credentialId) {
            this.credentialId = credentialId;
            return this;
        }

        public Builder setTimestemp(String timestemp) {
            this.timestemp = timestemp;
            return this;
        }

        public Builder setRevealedAttrs(List<ProveRevealedAttribute> revealedAttrs) {
            this.revealedAttrs = revealedAttrs;
            return this;
        }
        public Builder setUnrevealedAttrs(List<ProveUnrevealedAttribute> unrevealedAttrs) {
            this.unrevealedAttrs = unrevealedAttrs;
            return this;
        }

        public Builder setPredicates(List<ProvePredicate> predicates) {
            this.predicates = predicates;
            return this;
        }

        public Builder setSchema(CredentialSchema schema) {
            this.schema = schema;
            return this;
        }
        public Builder setCredentialDefinition(CredentialDefinition credentialDefinition) {
            this.credentialDefinition = credentialDefinition;
            return this;
        }

        public Builder setRevocationState(RevocationState revocationState) {
            this.revocationState = revocationState;
            return this;
        }


        public ProveCredential build() {
            return new ProveCredential(this);
        }


    }


    /**
     *
      [{
        "cred_id" : "11111111-1111-1111-11111111",
        "timestamp" : 123456789,

        "revealed_attrs" : [ {
          "attr_name" : "Name",
          "referent_key" : "attr2_referent"
        },
        {
          "attr_name" : "Name",
          "referent_key" : "attr2_referent"
        } ],

        "predicates" : [ {
          "attr_name" : "Age",
          "referent_key" : "predicates1_referent",
          "p_type" : ">=",
          "p_value" : "1"
        },
         {
         "attr_name" : "Age",
         "referent_key" : "predicates1_referent",
         "p_type" : ">=",
         "p_value" : "1"
         } ]
      }]
     *
     * */
    public static void main(String args[]) {

        List<ProveRevealedAttribute> revealedAttrs = new LinkedList<ProveRevealedAttribute>();
        revealedAttrs.add(new ProveRevealedAttribute.Builder().setAttributeName("test").setReferentKey("key").build());

        List<ProvePredicate> predicates = new LinkedList<ProvePredicate>();
        predicates.add(new ProvePredicate.Builder().setAttributeName("test").setReferentKey("key").setPType(PredicateType.GE).setPValue(1).build());

        ProveCredential proveCredential = new ProveCredential.Builder()
                .setCredentialId("cred_id")
                .setRevealedAttrs(revealedAttrs)
                .setPredicates(predicates)
                .setTimestemp("1231231231231")
                .build();
        System.out.println(ZkpGsonWrapper.getGsonPrettyPrinting().toJson(proveCredential));
    }
}
