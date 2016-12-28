package com.flamingo.filterdemo.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.flamingo.filterdemo.core.IFilter;
import com.flamingo.filterdemo.core.MessageData;

/**
 * 基于正则表达式的过滤器
 * @author boyliang
 */
public final class RegexFilter implements IFilter{
	private Pattern mPattern;
	private int mOpcode;

	public RegexFilter(int opcode, String pattern) throws PatternSyntaxException {
		mPattern = Pattern.compile(pattern, 0);
		mOpcode = opcode;
	}
	

	@Override
	public int onFiltering(MessageData data) {
		String phone = data.getString(MessageData.KEY_DATA);
		Matcher m = mPattern.matcher(phone);
		
		if(m.find()){
			return mOpcode;
		}else{
			return IFilter.OP_SKIP;
		}
	}
}
