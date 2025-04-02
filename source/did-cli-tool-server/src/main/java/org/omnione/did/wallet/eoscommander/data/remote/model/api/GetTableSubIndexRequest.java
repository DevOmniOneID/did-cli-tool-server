package org.omnione.did.wallet.eoscommander.data.remote.model.api;

import com.google.gson.annotations.Expose;

public class GetTableSubIndexRequest extends GetTableRequest {

	@Expose
	private int index_position;

	@Expose
	private String key_type;

	public GetTableSubIndexRequest(String scope, String code, String table,
			String tableKey, String lowerBound, String upperBound, int limit) {
		super(scope, code, table, tableKey, lowerBound, upperBound, limit);
	}

	public int getIndex_position() {
		return index_position;
	}

	public void setIndex_position(int index_position) {
		this.index_position = index_position;
	}

	public String getKey_type() {
		return key_type;
	}

	public void setKey_type(String key_type) {
		this.key_type = key_type;
	}

}
