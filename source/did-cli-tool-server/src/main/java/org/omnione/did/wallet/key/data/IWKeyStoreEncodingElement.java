package org.omnione.did.wallet.key.data;

public class IWKeyStoreEncodingElement {

	public enum ENCODING_TYPE {
		ENCODING_HEXA_DECIMAL(0), ENCODING_BASE58(1), ENCODING_BASE64(2);
		
		private int value;
		  private ENCODING_TYPE(int value) {
		    this.value = value;
		  }
		  public int getValue() {
		    return value;
		  }

		  public static ENCODING_TYPE fromValue(int value) {
              for (ENCODING_TYPE type : values()) {
                  if (type.getValue() == value) {
                      return type;
                  }
              }
              return null;
          }
		  
		@Override
		public String toString() {
		  switch (this) {
		    case ENCODING_HEXA_DECIMAL: return "HexaDecimal";
		    case ENCODING_BASE58:   	return "Base58";
		    case ENCODING_BASE64:    	return "Base64";
		    default:      				return "Unknown";
		  }
		}
	}
	
    private int key;
    private int privacy;
    private String keyString;
    private String privacyString;
    
    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
        ENCODING_TYPE encoding = ENCODING_TYPE.fromValue(key);
        this.keyString = encoding.toString();
    }
    
    public String getKeyString() {
    	return this.keyString;
    }
    
    public int getPrivacy() {
        return privacy;
    }

    public void setPrivacy(int privacy) {
        this.privacy = privacy;
        ENCODING_TYPE encoding = ENCODING_TYPE.fromValue(privacy);
        this.privacyString = encoding.toString();
    }
    
    public String getPrivacyString() {
    	return this.privacyString;
    }
    
}

