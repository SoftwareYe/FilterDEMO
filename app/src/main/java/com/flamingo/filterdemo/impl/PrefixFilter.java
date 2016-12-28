package com.flamingo.filterdemo.impl;

import com.flamingo.filterdemo.core.IFilter;
import com.flamingo.filterdemo.core.MessageData;

/**
 * 前缀匹配 过滤器
 * @author boyliang
 *
 */
public final class PrefixFilter implements IFilter {
	private String[] mPrefixs;
	private int mOpcode;

	public PrefixFilter(int opcode, String... prefixs) {
		mOpcode = opcode;
		mPrefixs = prefixs;
	}

	@Override
	public int onFiltering(MessageData data) {
		String phone = data.getString(MessageData.KEY_DATA);
		
		for(String prefix : mPrefixs){
			if(phone.startsWith(prefix)){
				return mOpcode;
			}
		}
		
		return IFilter.OP_SKIP;
	}

}
