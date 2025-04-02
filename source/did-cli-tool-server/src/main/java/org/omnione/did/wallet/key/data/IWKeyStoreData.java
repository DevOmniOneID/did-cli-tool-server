package org.omnione.did.wallet.key.data;

import java.util.ArrayList;
import java.util.Collection;

import org.omnione.did.wallet.data.iw.VerifiableClaim;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class IWKeyStoreData {
    private IWHeadElement head;
    private ArrayList<IWKeyElement> keys;
    private ArrayList<VerifiableClaim> claims;
    private ArrayList<String> encClaims;
    private ArrayList<String> encCredentials;
    
	

	/////////////////////////////////////////////////////////////////////////////////////////
	// ZKP Extra Data
	// using issuer
	private ArrayList<String> credDefInfos;
	private ArrayList<String> revRegInfos;
	// using prover
	private ArrayList<String> encZkpCredentials;
	private ArrayList<String> masterSecrets;
	/////////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////////
	// Fabric Extra Data
	private String fabricUserPrivateKey;
	/////////////////////////////////////////////////////////////////////////////////////////

    public IWHeadElement getHead() {
        return head;
    }

    public void setHead(IWHeadElement head) {
        this.head = head;
    }

    public ArrayList<IWKeyElement> getKeys() {
    	
    	if(keys == null){
    		return null;
    	}
    	
    	ArrayList<IWKeyElement> lKeys = new ArrayList<IWKeyElement>();
    	lKeys.addAll(keys);
		
        return lKeys;
    }

    public void setKeys(ArrayList<IWKeyElement> keys) {
    	
    	if(keys != null){
    		ArrayList<IWKeyElement> new_keys = new ArrayList<IWKeyElement>();
    		new_keys.addAll(keys);
	    	this.keys = new_keys;
    	}
    	else{
	    	this.keys = null;
    	}
    	
    }

    public ArrayList<VerifiableClaim> getClaims() {
    	
    	if(claims == null){
    		return null;
    	}
    	
    	ArrayList<VerifiableClaim> lClaims = new ArrayList<VerifiableClaim>();
    	lClaims.addAll(claims);
    	
        return lClaims;
    }

    public void setClaims(ArrayList<VerifiableClaim> claims) {
    	
    	if(claims != null){
    		ArrayList<VerifiableClaim> new_claims = new ArrayList<VerifiableClaim>();
    		new_claims.addAll(claims);
    		this.claims = new_claims;
    	}
    	else{
	    	this.claims = null;
    	}
    }
    
    //dlchoi 
    public ArrayList<String> getEncClaims() {
    	if(encClaims == null){
    		return null;
    	}
    	
    	ArrayList<String> lClaims = new ArrayList<String>();
    	lClaims.addAll(encClaims);
    	
        return lClaims;
    }

    public void setEncClaims(ArrayList<String> claims) {
    	if(claims != null){
    		ArrayList<String> new_claims = new ArrayList<String>();
    		new_claims.addAll(claims);
    		this.encClaims = new_claims;
    	}
    	else{
	    	this.encClaims = null;
    	}
    }
    
    //dlchoi 
    public ArrayList<String> getEncCredentials() {
    	if(encCredentials == null){
    		return null;
    	}
    	
    	ArrayList<String> lClaims = new ArrayList<String>();
    	lClaims.addAll(encCredentials);
    	
        return encCredentials;
    }

    public void setEncCredential(ArrayList<String> credentials) {
    	if(credentials != null){
    		ArrayList<String> new_credentials = new ArrayList<String>();
    		new_credentials.addAll(credentials);
    		this.encCredentials = new_credentials;
    	}
    	else{
	    	this.encCredentials = null;
    	}
    }
    
    
    public String toJson() {
    	GsonWrapper gson = new GsonWrapper();
		return gson.toJson(this);
	}
    
    public void fromJson(String val) {
    	GsonWrapper gson = new GsonWrapper();
    	IWKeyStoreData data = gson.fromJson(val, IWKeyStoreData.class);
    	head = data.getHead();
    	keys = data.getKeys();
    	claims = data.getClaims();
    	encClaims = data.getEncClaims();
    	encCredentials = data.getEncCredentials();


    	/////////////////////////////////////////////////////////////////////////////////////////
		// ZKP Extra Data
		/////////////////////////////////////////////////////////////////////////////////////////
		revRegInfos = data.getRevRegInfos();
		credDefInfos = data.getCredDefInfos();
		masterSecrets = data.getMasterSecrets();
		encZkpCredentials = data.getEncZkpCredentials();


		/////////////////////////////////////////////////////////////////////////////////////////
		// Fabric Extra Data
		/////////////////////////////////////////////////////////////////////////////////////////
		fabricUserPrivateKey = data.getFabricUserPrivateKey();
    }



    /////////////////////////////////////////////////////////////////////////////////////////
    // ZKP Extra Data
	/////////////////////////////////////////////////////////////////////////////////////////

	public ArrayList<String> getEncZkpCredentials() {
		if(encZkpCredentials == null){
			return null;
		}

		ArrayList<String> lClaims = new ArrayList<String>();
		lClaims.addAll(encZkpCredentials);

		return encZkpCredentials;
	}

	public void setEncZkpCredential(ArrayList<String> credentials) {
		if(credentials != null){
			ArrayList<String> new_credentials = new ArrayList<String>();
			new_credentials.addAll(credentials);
			this.encZkpCredentials = new_credentials;
		}
		else{
			this.encZkpCredentials = null;
		}
	}

	public ArrayList<String> getCredDefInfos() {

		if(credDefInfos == null){
			return null;
		}

		ArrayList<String> lcredDefInfos = new ArrayList<String>();
		lcredDefInfos.addAll(credDefInfos);

		return lcredDefInfos;
	}

	public void setCredDefInfos(ArrayList<String> credDefInfos) {

		if(credDefInfos != null){
			ArrayList<String> new_credDefInfos = new ArrayList<String>();
			new_credDefInfos.addAll(credDefInfos);
			this.credDefInfos = new_credDefInfos;
		}
		else {
			this.credDefInfos = null;
		}
	}

	public ArrayList<String> getRevRegInfos() {

		if(revRegInfos== null){
			return null;
		}

		ArrayList<String> lrevRegInfos = new ArrayList<String>();
		lrevRegInfos.addAll(revRegInfos);

		return lrevRegInfos;
	}

	public void setRevRegInfos(ArrayList<String> revRegInfos) {

		if(revRegInfos != null){
			ArrayList<String> new_revRegInfos = new ArrayList<String>();
			new_revRegInfos.addAll(revRegInfos);
			this.revRegInfos = new_revRegInfos;
		}
		else {
			this.revRegInfos = null;
		}
	}

	public ArrayList<String> getMasterSecrets() {
		if(masterSecrets == null){
			return null;
		}

		ArrayList<String> lmasterSecrets = new ArrayList<String>();
		lmasterSecrets.addAll(masterSecrets);

		return lmasterSecrets;
	}

	public void setMasterSecrets(ArrayList<String> masterSecrets) {

		if(masterSecrets != null){
			ArrayList<String> new_masterSecrets = new ArrayList<String>();
			new_masterSecrets.addAll(masterSecrets);
			this.masterSecrets = new_masterSecrets;
		}
		else {
			this.masterSecrets = null;
		}
	}


	/////////////////////////////////////////////////////////////////////////////////////////
	// Fabric Extra Data
	/////////////////////////////////////////////////////////////////////////////////////////

	public String getFabricUserPrivateKey() {

		return this.fabricUserPrivateKey;
	}

	public void setFabricUserPrivateKey(String privateKey) {
		this.fabricUserPrivateKey = privateKey;
	}
}
