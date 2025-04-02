package org.omnione.did.wallet.key;

import org.omnione.did.wallet.data.did.DIDs;
import org.omnione.did.wallet.data.iw.profile.Filter;
import org.omnione.did.wallet.data.iw.profile.VerifyInnerProfile;
import org.omnione.did.wallet.exception.IWException;

public interface BCFetchInterface {
	
	public enum VCStatus {
		VCStatusNone(-1),
		VCStatusIssued(0),
	    VCStatusPause(1),
		VCStatusNeedRenew(2),
	    VCStatusRevoke(9);
		
		private int value;
		
		VCStatus(int num) {
			this.value = num;
		}
		
		public static VCStatus fromValue(int value) {
			for (VCStatus type : values()) {
                  if (type.getValue() == value) {
                      return type;
                  }
            }
            return null;
        }
		  
		public int getValue() {
		    return value;
		}
		
	}
	
	public enum DIDStatus {
		DIDStatusNone(-1),
		DIDStatusIssued(0),
		DIDStatusRevoke(9);
		
		private int value;
		
		DIDStatus(int num) {
			this.value = num;
		}
		
		public static DIDStatus fromValue(int value) {
			for (DIDStatus type : values()) {
                  if (type.getValue() == value) {
                      return type;
                  }
            }
            return null;
        }
		
		public int getValue() {
		    return value;
		}
		
	}
	
	public interface DIDsCallBack{
		public void success(DIDs dids) throws IWException;
		public void failure(String errorMsg);
	}

	public interface FilterCallBack {
		public void success(Filter filter);
		public void failure(String errorMsg);
	}
	public interface VCStatusCallBack {
		public void success(VCStatus vcStatus);
		public void failure(String errorMsg);
	}
	public interface DIDRegistrationCheckCallBack {
		public void success(boolean bIsRegistered) throws IWException;
		public void failure(String errorMsg);
	}
	public interface DIDStatusCallBack {
		public void success(DIDStatus didStatus);
		public void failure(String errorMsg);
	}
	public interface ProfileCallBack {
		public void success(VerifyInnerProfile verifyInnerProfile);
		public void failure(String errMsg);
	}
	
	
}
