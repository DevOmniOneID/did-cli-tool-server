package org.omnione.did.wallet.zkp.revoc.data.dto;

import com.google.gson.annotations.SerializedName;

public class UserReferent {

     @SerializedName("credId")
     private String credentialId;
     private String raw;
     private String referentKey;
     private String referentName;
     private boolean isRevealed;

     public UserReferent(Builder builder) {
          credentialId = builder.credentialId;
          raw = builder.raw;
          referentKey = builder.referentKey;
          referentName = builder.referentName;
          isRevealed = builder.isRevealed;

     }

     public String getCredentialId() {
          return credentialId;
     }

     public String getRaw() {
          return raw;
     }

     public String getReferentKey() {
          return referentKey;
     }

     public String getReferentName() {
          return referentName;
     }

     public boolean isRevealed() {
          return isRevealed;
     }


     public static class Builder {
          private String credentialId;
          private String raw;
          private String referentKey;
          private String referentName;
          private boolean isRevealed;

          public Builder setCredentialId(String credentialId) {
               this.credentialId = credentialId;
               return this;
          }

          public Builder setRaw(String raw) {
               this.raw = raw;
               return this;
          }

          public Builder setReferentKey(String referentKey) {
               this.referentKey = referentKey;
               return this;
          }

          public Builder setReferentName(String referentName) {
               this.referentName = referentName;
               return this;
          }

          public Builder setRevealed(boolean revealed) {
               isRevealed = revealed;
               return this;
          }

          public UserReferent build() {
               return new UserReferent(this);
          }
     }
}
