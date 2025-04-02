package org.omnione.did.wallet.eoscommander.data.remote.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.wallet.eoscommander.util.StringUtils;

/**
 * Created by swapnibble on 2017-09-15.
 */

public class GetTableRequest {

	protected static final int DEFAULT_FETCH_LIMIT = 20;

	@Expose
	protected boolean json = true;

	@Expose
	protected String code;

	@Expose
	protected String scope;

	@Expose
	protected String table;

	@Expose
	protected String table_key = "";

	@Expose
	protected String lower_bound = "";

	@Expose
	protected String upper_bound = "";

	@Expose
	protected int limit;

	public GetTableRequest(String scope, String code, String table, String tableKey,
			String lowerBound, String upperBound, int limit) {
		this.scope = scope;
		this.code = code;
		this.table = table;

		this.table_key = StringUtils.isEmpty(tableKey) ? "" : tableKey;
		this.lower_bound = StringUtils.isEmpty(lowerBound) ? "" : lowerBound;
		this.upper_bound = StringUtils.isEmpty(upperBound) ? "" : upperBound;
		this.limit = limit <= 0 ? DEFAULT_FETCH_LIMIT : limit;
	}

	public GetTableRequest(String scope, String code, String table, int limit) {
		this.scope = scope;
		this.code = code;
		this.table = table;
		this.limit = limit <= 0 ? DEFAULT_FETCH_LIMIT : limit;
	}

}
