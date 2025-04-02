package org.omnione.did.wallet.key;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.omnione.did.wallet.data.did.Proof;
import org.omnione.did.wallet.data.iw.Assertion;
import org.omnione.did.wallet.data.iw.Unprotected;
import org.omnione.did.wallet.data.iw.profile.Filter;
import org.omnione.did.wallet.data.iw.v2.CredentialSubject;
import org.omnione.did.wallet.data.iw.v2.VerifiableCredential;
import org.omnione.did.wallet.eoscommander.crypto.digest.Sha256;
import org.omnione.did.wallet.exception.IWErrorCode;
import org.omnione.did.wallet.exception.IWException;

public class PresentationBuilder {

	public enum PRESENTATION_TYPE {
		COMPLEX(0),
		COMPRESSED(1);
		
		public final int value;
		
		PRESENTATION_TYPE(int num) {
			this.value = num;
		}
		
		public int getValue() {
		    return value;
		}
	}
	
	private List<VerifiableCredential> credentials;
	private PRESENTATION_TYPE type;
	private String userDid;

	private List<String> addedCredentialTypes;

	public PresentationBuilder(PRESENTATION_TYPE type){
		this.type = type;
		this.credentials = new ArrayList<VerifiableCredential>();
		
		this.addedCredentialTypes = new ArrayList<String>();
	}
	
	public void addCredential(VerifiableCredential credential, List<Unprotected> privacies) throws IWException {
		
		if(this.credentials.size() == 1 && this.type == PRESENTATION_TYPE.COMPRESSED) {
			clearStoredData();
			
			throw new IWException(IWErrorCode.ERR_CODE_PRESENTATION_BUILDER_ADD_CREDENTIAL_FAILED);
		}
		
		verifyCredential(credential, privacies);
		
		if(isDuplicatedCredential(credential)) {
			clearStoredData();
			
			throw new IWException(IWErrorCode.ERR_CODE_PRESENTATION_BUILDER_DUPLICATED_CREDENTIAL);
		}
		
		
		switch (this.type) {
		case COMPLEX:
			devideCredential(credential, privacies);
			break;
		case COMPRESSED:
			intersectCredential(credential, privacies);
			break;

		default:
			break;
		}
		
	}
	
	public List<VerifiableCredential> complete(Filter filter) throws IWException{
		
		if(this.credentials == null || this.credentials.size() == 0) {
			clearStoredData();
			
			throw new IWException(IWErrorCode.ERR_CODE_PRESENTATION_BUILDER_CREDENTIAL_NOT_ADDED);
		}

		if(filter != null) {
			checkFilter(filter);
		}
		
		List<VerifiableCredential> credentials = new ArrayList<VerifiableCredential>();
		
		credentials.addAll(this.credentials);
		clearStoredData();
		
		return credentials;
	}
	
	private void checkFilter(Filter filter) throws IWException{
		
		List<String> requiredPrivacies = new ArrayList<String>();
		List<String> privacies = filter.getRequiredPrivacyList();
		if(privacies != null && privacies.size() > 0) {
			for(String privacy : privacies) {
				requiredPrivacies.add(privacy);
			}
		}
		
		List<String> allowIssuerList = filter.getAllowIssuerList();
		List<Assertion> assertionList = filter.getRequiredAssertionList();
		for(VerifiableCredential credential : this.credentials) {
	    	
    		if(allowIssuerList != null && allowIssuerList.size() > 0) {
    			
    			String issuerDID = credential.getIssuer().getId();
    			if(!allowIssuerList.contains(issuerDID)) {
    				clearStoredData();
    				
    				throw new IWException(IWErrorCode.ERR_CODE_PRESENTATION_BUILDER_NOT_ALLOWED_ISSUER);
    			}
    		}
    		
    		if(assertionList != null && assertionList.size() > 0) {
    			
    			String assertionCode = credential.getAssertion().getCode();
    			boolean isContained = false;
    			for(Assertion assertion : assertionList) {
    				isContained = (assertionCode.compareTo(assertion.getId()) == 0);
    				
    				if(isContained) {
    					break;
    				}
    			}
    			if(isContained == false) {
    				clearStoredData();
    				
    				throw new IWException(IWErrorCode.ERR_CODE_PRESENTATION_BUILDER_NOT_ALLOWED_CREDENTIAL_TYPE);
    			}
    		}
    		
    		for(Unprotected privacy : credential.getCredentialSubject().getPrivacyList()) {
    			String type = privacy.getType();
    			if(requiredPrivacies.contains(type)) {
    				requiredPrivacies.remove(type);
    			}
    		}
		}
		
		if(requiredPrivacies.size() > 0) {
			clearStoredData();
			
			throw new IWException(IWErrorCode.ERR_CODE_PRESENTATION_BUILDER_INSUFFICIENT_PRIVACY);
		}
		
	}
	
	private boolean isDuplicatedCredential(VerifiableCredential credential) {
		
		if(this.credentials.size() == 0) {
			return false;
		}
		
		String credentialId = credential.getId();
		
		for(VerifiableCredential each : this.credentials) {
			if(credentialId.compareTo(each.getId()) == 0) {
				return true;
			}
			
		}
		return false;
	}
	
	private void verifyCredential(VerifiableCredential credential, List<Unprotected> privacies) throws IWException {
		
		if(credential == null) {
			clearStoredData();
			
			throw new IWException(IWErrorCode.ERR_CODE_PRESENTATION_BUILDER_INVALID_CREDENTIAL);
		}
		
		String credentialString = credential.toJson();
		if(credentialString == null || credentialString.length() == 0) {
			clearStoredData();
			
			throw new IWException(IWErrorCode.ERR_CODE_PRESENTATION_BUILDER_INVALID_CREDENTIAL);
		}
		
		if(privacies == null || privacies.size() == 0) {
			clearStoredData();
			
			throw new IWException(IWErrorCode.ERR_CODE_PRESENTATION_BUILDER_EMPTY_PRIVACY);
		}
		
		
		
		List<Unprotected> totalPrivacy = credential.getCredentialSubject().getPrivacyList();
		if(totalPrivacy == null || totalPrivacy.size() == 0) {
			clearStoredData();
			
			throw new IWException(IWErrorCode.ERR_CODE_PRESENTATION_BUILDER_NOT_INCLUDED_PRIVACY);
		}
		
		
		
		HashSet<Unprotected> totalPrivacySet = new HashSet<Unprotected>(totalPrivacy);
		
		HashSet<Unprotected> requiredPrivacySet = new HashSet<Unprotected>(privacies);
		
		if(!totalPrivacySet.containsAll(requiredPrivacySet)) {
			clearStoredData();
			
			throw new IWException(IWErrorCode.ERR_CODE_PRESENTATION_BUILDER_INVALID_PRIVACY);
		}
		
		if(requiredPrivacySet.size() != privacies.size()) {
			clearStoredData();
			
			throw new IWException(IWErrorCode.ERR_CODE_PRESENTATION_BUILDER_SELECTED_PRIVACY_DUPLICATED);
		}
		
		Proof proof = credential.getProof();

		if(proof != null) {
			List<String> signatureList = proof.getSignatureValueList();
			if(signatureList != null && signatureList.size() != 0) {
//				clearStoredData();
//				throw new IWException(IWErrorCode.ERR_CODE_PRESENTATION_BUILDER_NOT_INCLUDED_SIGNATURE);
				
				if(totalPrivacy.size() + 1 != signatureList.size()) {
					clearStoredData();
					
					throw new IWException(IWErrorCode.ERR_CODE_PRESENTATION_BUILDER_FALSIFIED_CREDENTIAL);
				}
				
			}
		}
		
		
		
		
		String did = credential.getCredentialSubject().getId();
		if(did == null || did.length() == 0) {
			clearStoredData();
			
			throw new IWException(IWErrorCode.ERR_CODE_PRESENTATION_BUILDER_NOT_INCLUDED_DID);
		}
		
		String credentialId = credential.getId();
		if(credentialId == null || credentialId.length() == 0) {
			clearStoredData();
			
			throw new IWException(IWErrorCode.ERR_CODE_PRESENTATION_BUILDER_NOT_INCLUDED_CREDENTIAL_ID);
		}
		
		if(this.userDid != null) {
			if(this.userDid.compareTo(did) != 0) {
				clearStoredData();
				
				throw new IWException(IWErrorCode.ERR_CODE_PRESENTATION_BUILDER_DID_NOT_MATCHED);
			}
		}
		else {
			this.userDid = did;
		}
		
		String issuerDID = credential.getIssuer().getId();
		if(issuerDID == null || issuerDID.length() == 0) {
			clearStoredData();
			
			throw new IWException(IWErrorCode.ERR_CODE_PRESENTATION_BUILDER_NOT_ALLOWED_ISSUER);
		}
		
		String assertionCode = credential.getAssertion().getCode();
		if(assertionCode == null || assertionCode.length() == 0) {
			clearStoredData();
			
			throw new IWException(IWErrorCode.ERR_CODE_PRESENTATION_BUILDER_NOT_ALLOWED_CREDENTIAL_TYPE);
		}
		
		String hashSource = issuerDID + assertionCode;
		Sha256 hashed = Sha256.from(hashSource.getBytes());
		
    	hashed.toString();
    	
    	if(this.addedCredentialTypes.contains(hashed.toString())) {
    		clearStoredData();
    		
    		throw new IWException(IWErrorCode.ERR_CODE_PRESENTATION_BUILDER_DUPLICATED_CREDENTIAL_TYPE);
    	}
    	else {
    		this.addedCredentialTypes.add(hashed.toString());
    	}
	}
	
	private void devideCredential(VerifiableCredential credential, List<Unprotected> privacies) {
	
		List<Unprotected> innerPrivacy = credential.getCredentialSubject().getPrivacyList();
		
		
		for(int index = 0; index < innerPrivacy.size(); index++) {
			Unprotected privacy = innerPrivacy.get(index);
			
			if(!privacies.contains(privacy)) {
				continue;
			}
			
			String credentialString = credential.toJson();
			VerifiableCredential tempCredential = new VerifiableCredential();
			tempCredential.fromJson(credentialString);
			
			CredentialSubject credentialSubject = tempCredential.getCredentialSubject();
			
			
			
			List<Unprotected> tempPrivacy = new ArrayList<Unprotected>();
			tempPrivacy.add(privacy);
			
			credentialSubject.setPrivacyList(tempPrivacy);
			tempCredential.setCredentialSubject(credentialSubject);
			
			Proof proof = tempCredential.getProof();
			if(proof != null) {
				List<String> signatureList = proof.getSignatureValueList();
				
				if(signatureList != null && signatureList.size() > 0) {
					
					String signatureValue = signatureList.get(index + 1); 
					proof.setSignatureValue(signatureValue);
					proof.setSignatureValueList(null);
					tempCredential.setProof(proof);
				}
				else {
					tempCredential.setProof(null);
				}
				
			}
			
			
			this.credentials.add(tempCredential);
				
		}
	}
	
	private void intersectCredential(VerifiableCredential credential, List<Unprotected> privacies) {
		
		String credentialString = credential.toJson();
		VerifiableCredential tempCredential = new VerifiableCredential();
		tempCredential.fromJson(credentialString);
		
		List<Unprotected> innerPrivacy = tempCredential.getCredentialSubject().getPrivacyList();
		List<String> signatureList = new ArrayList<String>();
		
		Proof proof = tempCredential.getProof();
		
		if(proof != null) {
			List<String> tempSignatureList = proof.getSignatureValueList();
			
			if(tempSignatureList != null && tempSignatureList.size() > 0) {
				
				for(int index = 0; index < innerPrivacy.size(); index++) {
					Unprotected privacy = innerPrivacy.get(index);
					
					if(!privacies.contains(privacy)) {
						continue;
					}

					
					String signatureValue = tempSignatureList.get(index + 1); 
					signatureList.add(signatureValue);
						
				}
			}
		}
		
		
		
		
		CredentialSubject credentialSubject = tempCredential.getCredentialSubject();
		credentialSubject.setPrivacyList(privacies);
		tempCredential.setCredentialSubject(credentialSubject);
		
		if(signatureList.size() > 0) {
			proof.setSignatureValueList(signatureList);
			tempCredential.setProof(proof);
		}
		else{
			tempCredential.setProof(null);
		}
		
	
		this.credentials.add(tempCredential);
	}
	
	private void clearStoredData() {
		this.credentials.clear();
		this.userDid = null;
		
		this.addedCredentialTypes.clear();
		
		
	}
}
