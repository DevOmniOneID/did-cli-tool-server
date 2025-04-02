package org.omnione.did.wallet.eoscommander.data.remote.model.api;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import org.omnione.did.wallet.data.iw.VerifiableClaim;

public class GetTableByScopeResponse {

	public class TableByScope {
		@Expose
		protected String code;
		@Expose
		protected String scope;
		@Expose
		protected String table;
		@Expose
		protected String payer;
		@Expose
		protected Integer count = 0;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getScope() {
			return scope;
		}

		public void setScope(String scope) {
			this.scope = scope;
		}

		public String getTable() {
			return table;
		}

		public void setTable(String table) {
			this.table = table;
		}

		public String getPayer() {
			return payer;
		}

		public void setPayer(String payer) {
			this.payer = payer;
		}

		public Integer getCount() {
			return count/2;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

	}

	@Expose
	protected List<TableByScope> rows;

	@Expose
	protected String more;

	public List<TableByScope> getRows() {
		
		if(rows == null){
			return null;
		}
		
		ArrayList<TableByScope> lRows = new ArrayList<TableByScope>();
		lRows.addAll(rows);
    	
		return lRows;
	}

	public void setRows(List<TableByScope> rows) {
		
		if(rows != null){
			List<TableByScope> new_rows = new ArrayList<TableByScope>();
			new_rows.addAll(rows);
			this.rows = new_rows;
		}
		else{
			this.rows = null;
		}
				
	}

	public String getMore() {
		return more;
	}

	public void setMore(String more) {
		this.more = more;
	}

}
