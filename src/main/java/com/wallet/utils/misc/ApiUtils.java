package com.wallet.utils.misc;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wallet.utils.data.mysql.MySqlConnector;

import javax.ws.rs.core.Cookie;

public class ApiUtils {
	private static final Logger logger_ = LoggerFactory.getLogger(ApiUtils.class);
	
	public static final String QUERY_PARAM  = "queryParam";
	
	public static final String SESSION_COOKIE_ERROR = "Session Cookie Invalid";
	public static final String QUERY_ARG_ERROR = "Query Argument Invalid";
	public static final String JSON_PARSE_ERROR = "JSON Parse error";
	public static final String INTERNAL_ERROR = "Server internal error";
	public static final String TIME_OUT_ERROR = "Timeout when get data from server";
	
	public static final String PRODUCT_NOT_AVAILABLE = "Product not available or amount not enough";
	
	public static final String STATUS = "status";
	public static final String SUCCESS = "success";
	public static final String FAIL = "fail";
	
	public static String buildJSONResponse(Boolean status, String msg) {
		JSONObject jsonObject = new JSONObject();
		
		try {
			jsonObject.append("success", status.toString());
			jsonObject.append("message", msg);
		} catch (JSONException e) {
			e.printStackTrace();
			logger_.error("Error : build JSON response failed : " + e.getMessage());
			return STATUS.toString();
		}
		
		return jsonObject.toString();
	}
	
	public static boolean verifyAndGetParameters(Map<String, Object> paramMap, JSONObject request) {
		for (String key : paramMap.keySet()) {
			if (!request.has(key)) {
				logger_.warn("Warning : request verify failed : verifying \'" + key + "\'");
				logger_.warn("Warning : request verify failed : " + request);
				return false;
			}
			try {
				paramMap.put(key, request.get(key));
			} catch (JSONException e) {
				e.printStackTrace();
				logger_.error("Error : get param from request failed : " + e.getMessage());
				logger_.error("Error : get param from request failed : getting \'" + key + "\'");
				logger_.error("Error : get param from request failed : " + request.toString());
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean keyValueExists(String key, Object value, String table) throws SQLException {
		Map<String, Object> keys = new HashMap<String, Object>();
		keys.put(key, value);
		
		List<Map<String, Object>> ret = MySqlConnector.instance().selectFromTable(keys, table);
		
		return !ret.isEmpty();
	}

	public static String getUserIDFromCookie(Cookie cookie) {
		if (cookie == null) {
			return null;
		}

		String param[] = cookie.getValue().split(":");
		if (param.length < 2 || param[0].length() == 0 || param[1].length() == 0) {
		    return null;
		}

		return param[0];
	}

	public static String getSessionKeyFromCookie(Cookie cookie) {
		if (cookie == null) {
			return null;
		}

		String param[] = cookie.getValue().split(":");
		if (param.length < 2 || param[0].length() == 0 || param[1].length() == 0) {
			return null;
		}

		return param[1];
	}
}
