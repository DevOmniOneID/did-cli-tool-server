
package org.omnione.did.wallet.data.did;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServiceEndpoint {

    // @SerializedName("data")
    // @Expose
    // private String data;
    @SerializedName("dataBase64")
    @Expose
    private String dataBase64;
    @SerializedName("dataHex")
    @Expose
    private String dataHex;
    @SerializedName("type")
    @Expose
    private String type;
    
    
    public String getDataBase64() {
        return dataBase64;
    }

    public void setDataBase64(String dataBase64) {
        this.dataBase64 = dataBase64;
    }

    public String getDataHex() {
        return dataHex;
    }

    public void setDataHex(String dataHex) {
        this.dataHex = dataHex;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
