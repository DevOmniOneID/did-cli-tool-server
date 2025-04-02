
package org.omnione.did.wallet.data.did;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.eoscommander.util.StringUtils;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class DIDs extends IWObject {

    @SerializedName("@context")
    @Expose
    private String context;
    @SerializedName("authentication")
    @Expose
    private List<Authentication> authentication = null;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("proof")
    @Expose
    private Proof proof;
    @SerializedName("publicKey")
    @Expose
    private List<PublicKey> publicKey = null;
    @SerializedName("service")
    @Expose
    private List<Service> service = null;
    @SerializedName("updated")
    @Expose
    private String updated;
    

    public DIDs() {
    	
    }
    
    public DIDs(String didsJson) {
    	fromJson(didsJson);
    }
    
    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public List<Authentication> getAuthentication() {
    	
    	if(authentication == null){
    		return null;
    	}
    	
    	List<Authentication> lAuthentication = new ArrayList<Authentication>();
    	lAuthentication.addAll(authentication);
    	
        return lAuthentication;
    }

    public void setAuthentication(List<Authentication> authentication) {
    	
    	if(authentication != null){
    		List<Authentication> new_authentication =  new ArrayList<Authentication>();
    		new_authentication.addAll(authentication);
    		this.authentication = new_authentication;
    	}
    	else{
    		this.authentication = null;
    	}   
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProof(Proof proof) {
        this.proof = proof;
    }


    public Proof getProof() {
        return proof;
    }

    public List<PublicKey> getPublicKey() {
    	
    	if(publicKey == null){
    		return null;
    	}
    	
    	List<PublicKey> lPublicKey = new ArrayList<PublicKey>();
    	lPublicKey.addAll(publicKey);
    	
        return lPublicKey;
    }

    public void setPublicKey(List<PublicKey> publicKey) {
    	
    	if(publicKey != null){
    		List<PublicKey> new_publicKey = new ArrayList<PublicKey>();
	    	new_publicKey.addAll(publicKey);
    		this.publicKey = new_publicKey;
    	}
    	else{
	    	this.publicKey = null;
    	}
    	
    }

    public List<Service> getService() {
    	
    	if (service == null) {
    		return null;
    	}
    	
    	List<Service> lService = new ArrayList<Service>();
    	lService.addAll(service);
		
        return lService;
    }

    public void setService(List<Service> service) {
    	
    	if(service != null){
    		List<Service> new_service =  new ArrayList<Service>();
    		new_service.addAll(service);
	    	this.service = new_service;
    	}
    	else{
    		this.service = null;
    	}    	
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    @Override
    public void fromJson(String val) {
    	GsonWrapper gson = new GsonWrapper();
        DIDs data = gson.fromJson(val, DIDs.class);
        
        context = data.getContext();

        authentication = data.getAuthentication();
    	for (Authentication authObj : authentication) {
    		authObj.checkEmpty();
		}

        if(!StringUtils.isEmpty(data.getCreated())) {
    		created = data.getCreated();
    	}

    	id = data.getId();
        
        proof = data.getProof();

        publicKey = data.getPublicKey();
    	
    	service = data.getService();
    	if(service != null) {
    		if(service.isEmpty()) {
    			service = null;
    		}
    	}
        
        if(!StringUtils.isEmpty(data.getUpdated())) {
    		updated = data.getUpdated();
    	}
    	
    }
}
