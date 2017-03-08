package com.wallet.utils.data.gauva;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheLoader;

public class testloadable extends CacheLoader<String,String>
{
	private static final Logger logger_ = LoggerFactory.getLogger(testloadable.class);
	int i = 0;
	@Override
	public String load(String key) throws Exception {
		++i;
		logger_.warn(key + i);
		return key + i;
		
	}
	
}


