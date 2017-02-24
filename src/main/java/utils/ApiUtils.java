package utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiUtils {
	private static final Logger logger_ = LoggerFactory.getLogger(ApiUtils.class);
	
	public static final String QUERY_PARAM  = "queryParam";
	
	public static final String QUERY_ARG_ERROR = "Query Argument Invalid";
	public static final String JSON_PARSE_ERROR = "JSON Parse error";
	public static final String INTERNAL_ERROR = "Server internal error";
	public static final String TIME_OUT_ERROR = "Timeout when get data from server";
	
	public static final String PRODUCT_NOT_AVAILABLE = "Product not available or amount not enough";
	
	public static final String STATUS = "status";
	public static final String SUCCESS = "success";
	
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
}
