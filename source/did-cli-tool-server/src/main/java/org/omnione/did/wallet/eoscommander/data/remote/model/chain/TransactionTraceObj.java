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

/**
 * Created by swapnibble on 2017-09-11.
 */

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

public class TransactionTraceObj {

	@Expose
	private String id;
	
	@Expose
	private String block_num;
	
	@Expose
	private String block_time;

	@Expose
	private TransactionReceiptHeader receipt;

	@Expose
	private long elapsed;

	@Expose
	private long net_usage; // uint64_t

	@Expose
	private boolean scheduled = false;

	@Expose
	private JsonElement failed_dtrx_trace;

	@Expose
	private JsonElement except;


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

	public String getBlock_time() {
		return block_time;
	}

	public void setBlock_time(String block_time) {
		this.block_time = block_time;
	}

	@Override
	public String toString() {
		return "TransactionTraceObj [id=" + id + ", block_num=" + block_num + ", block_time=" + block_time
				+ ", receipt=" + receipt + ", elapsed=" + elapsed + ", net_usage=" + net_usage + ", scheduled="
				+ scheduled + ", failed_dtrx_trace=" + failed_dtrx_trace + ", except=" + except + "]";
	}

	
	

}
