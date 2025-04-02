package org.omnione.did.wallet.data.did;

import org.omnione.did.wallet.data.IWObject;
import org.omnione.did.wallet.util.json.GsonWrapper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Payload extends IWObject {
  
    
    @SerializedName("jobTitle")
	@Expose
	private String jobTitle;
	
	@SerializedName("target")
	@Expose
    private String target;

    //shchoi: 추후 list 로 변경 고려필요
    @SerializedName("contents")
	@Expose
    private String contents;

    public String getJobTitle() {
		return this.jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

    public String getTarget() {
		return this.target;
	}
	public void setTarget(String target) {
		this.target = target;
	}

    public String getContents() {
		return this.contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}

    @Override
    public void fromJson(String val) {
        GsonWrapper gson = new GsonWrapper();
		Payload obj = gson.fromJson(val, Payload.class);
		jobTitle = obj.getJobTitle();
        target = obj.getTarget();
        contents = obj.getContents();        
    }

}