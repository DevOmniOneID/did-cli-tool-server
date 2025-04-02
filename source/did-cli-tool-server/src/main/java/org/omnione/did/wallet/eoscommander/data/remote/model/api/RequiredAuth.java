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
package org.omnione.did.wallet.eoscommander.data.remote.model.api;

/**
 * Created by swapnibble on 2017-09-14.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RequiredAuth {

	@Expose
	private Integer threshold;

	@Expose
	private List<Key> keys = null;

	@Expose
	private List<Object> accounts = null;

	public Integer getThreshold() {
		return threshold;
	}

	public void setThreshold(Integer threshold) {
		this.threshold = threshold;
	}

	public List<Key> getKeys() {
		
		if(keys == null){
			return null;
		}
		
		List<Key> lKeys = new ArrayList<Key>();
		lKeys.addAll(keys);
		
		return lKeys;
	}

	public void setKeys(List<Key> keys) {
		
		if(keys != null){
			List<Key> new_keys = new ArrayList<Key>();
			new_keys.addAll(keys);
			this.keys = new_keys;
		}
		else{
			this.keys = null;
		}
		
				
		
	}

	public List<Object> getAccounts() {
		
		if(accounts == null){
			return null;
		}
		
		List<Object> lAccounts = new ArrayList<Object>();
		lAccounts.addAll(accounts);
		
		return lAccounts;
	}

	public void setAccounts(List<Object> accounts) {
		
		if(accounts != null){
			List<Object> new_accounts = new ArrayList<Object>();
			new_accounts.addAll(accounts);
			this.accounts = new_accounts;
		}
		else{
			this.accounts = null;
		}
		
		
	}

}