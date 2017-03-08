package com.wallet.utils.misc;

public class XMLUtils {
	private static final String CDATA_BEGIN = "![CDATA[";
	private static final String CDATA_END = "]]";
	
	public XMLUtils() {
	}
	
	public static String addCDATATag(String in) {
		return CDATA_BEGIN + in + CDATA_END;
	}
}
