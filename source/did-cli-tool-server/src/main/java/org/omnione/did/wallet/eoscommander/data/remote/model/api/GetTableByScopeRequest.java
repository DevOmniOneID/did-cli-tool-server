package org.omnione.did.wallet.eoscommander.data.remote.model.api;

import com.google.gson.annotations.Expose;

public class GetTableByScopeRequest {

	@Expose
	protected String code;

	@Expose
	protected String table;

	protected String lower_bound;

	protected String upper_bound;

	protected Integer limit;

	protected Boolean reverse;

	public GetTableByScopeRequest(String code, String table, String lower_bound, String upper_bound, Integer limit,
			Boolean reverse) {
		super();
		this.code = code;
		this.table = table;
		this.lower_bound = lower_bound;
		this.upper_bound = upper_bound;
		this.limit = limit;
		this.reverse = reverse;
	}

}
