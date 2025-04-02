/*
 * Copyright (c) 2017-2018 PLACTAL.
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.omnione.did.wallet.eoscommander.data.remote.model.chain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by swapnibble on 2017-09-11.
 */

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

public class TransactionTrace {

	@Expose
	private String id;
	
	@Expose
	private String block_num;
	
	@Expose
	private String dblock_time;

	@Expose
	private TransactionReceiptHeader receipt;

	@Expose
	private long elapsed;

	@Expose
	private long net_usage; // uint64_t

	@Expose
	private boolean scheduled = false;

	@Expose
	private List<ActionTrace> action_traces;

	@Expose
	private JsonElement failed_dtrx_trace;

	@Expose
	private JsonElement except;

	@Override
	public String toString() {
		if (receipt == null) {
			return "empty receipt";
		}

		String result = ": " + receipt.status;

		if (receipt.net_usage_words < 0) {
			result += "<unknown>";
		}
		else {
			result += (receipt.net_usage_words * 8);
		}
		result += " bytes ";

		if (receipt.cpu_usage_us < 0) {
			result += "<unknown>";
		}
		else {
			result += (receipt.net_usage_words * 8);
		}
		result += " us\n";

		return result;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBlock_num() {
		return block_num;
	}

	public void setBlock_num(String block_num) {
		this.block_num = block_num;
	}

	public String getDblock_time() {
		return dblock_time;
	}

	public void setDblock_time(String dblock_time) {
		this.dblock_time = dblock_time;
	}

	public TransactionReceiptHeader getReceipt() {
		return receipt;
	}

	public void setReceipt(TransactionReceiptHeader receipt) {
		this.receipt = receipt;
	}

	public long getElapsed() {
		return elapsed;
	}

	public void setElapsed(long elapsed) {
		this.elapsed = elapsed;
	}

	public long getNet_usage() {
		return net_usage;
	}

	public void setNet_usage(long net_usage) {
		this.net_usage = net_usage;
	}

	public boolean isScheduled() {
		return scheduled;
	}

	public void setScheduled(boolean scheduled) {
		this.scheduled = scheduled;
	}

	public List<ActionTrace> getAction_traces() {
		
		if(action_traces == null){
			return null;
		}
		
		List<ActionTrace> lAction_traces = new ArrayList<ActionTrace>();
		lAction_traces.addAll(action_traces);
		
		return lAction_traces;
	}

	public void setAction_traces(List<ActionTrace> action_traces) {
		
		if(action_traces != null){
			List<ActionTrace> new_action_traces = new ArrayList<ActionTrace>();
			new_action_traces.addAll(action_traces);
			this.action_traces = new_action_traces;
		}
		else{
			this.action_traces = null;
		}
		
				
	}

	public JsonElement getFailed_dtrx_trace() {
		return failed_dtrx_trace;
	}

	public void setFailed_dtrx_trace(JsonElement failed_dtrx_trace) {
		this.failed_dtrx_trace = failed_dtrx_trace;
	}

	public JsonElement getExcept() {
		return except;
	}

	public void setExcept(JsonElement except) {
		this.except = except;
	}
	
	

}
