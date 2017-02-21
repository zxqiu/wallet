package com.project.wallet.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Configuration {
	private static final Logger logger_ = LoggerFactory.getLogger(Configuration.class);
	
	public static final String PAYMENT_QR_CODE_PATH = "src/main/resources/assets/PaymentQRCodes";
	public static final String PRODUCT_QR_CODE_PATH = "src/main/resources/assets/ProductQRCodes";
	
	public static final String STORE_NAME = "store_name";
	private static String storeName = "";
	public static final String HOST_IPV4_ADDR = "host_ipv4_addr";
	private static String host_ipv4_addr = "";
	
	public static int init(String path) throws IOException {
		Path filePath = new File(path).toPath();	
		Charset charset = Charset.defaultCharset();        
		List<String> configList = Files.readAllLines(filePath, charset);
		Map<String, String> config = new HashMap<String, String>();
		
		for (String s : configList) {
			String src = s.replaceAll("[\\[\\](){}\\s+]", ""); // remove all brackets, braces, white space, non-visible characters
			String[] entry = src.split("=");
			
			if (entry.length != 2) {
				return -1;
			}
			
		    String name = entry[0].trim();
		    String value = entry[1].trim();
		    
		    config.put(name, value);
		    logger_.info(name + " : " + value);
		    if (name.equals(STORE_NAME)) {
		    	Configuration.setStoreName(value);
		    } else if (name.equals(HOST_IPV4_ADDR)) {
		    	Configuration.setHost_ipv4_addr(value);
		    }
		}
		
		if (config.containsKey(STORE_NAME) == false
				|| config.containsKey(HOST_IPV4_ADDR) == false) {
			logger_.info("Configuration init failed");
			return -1;
		}
		
		logger_.info("Configuration init success");
		return 0;
	}

	public static String getStoreName() {
		return storeName;
	}

	public static void setStoreName(String storeName) {
		Configuration.storeName = storeName;
	}
	
	public static String getHost_ipv4_addr() {
		return host_ipv4_addr;
	}

	public static void setHost_ipv4_addr(String host_ipv4_addr) {
		Configuration.host_ipv4_addr = host_ipv4_addr;
	}
	
	public static void main(String[] args) throws Exception {
		Configuration.init("store_motone.conf");
		logger_.info(Configuration.storeName);
	}
}
