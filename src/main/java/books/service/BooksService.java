package books.service;

import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.codahale.metrics.annotation.Timed;

import books.service.data.BooksInfo;
import books.service.data.BooksTable;
import utils.TimeUtils;

@Path("/books")
public class BooksService {
	private static final Logger logger_ = LoggerFactory.getLogger(BooksService.class);
	private static final String QUERY_PARAM  = "queryParam";
	
	private static final String QUERY_ARG_ERROR = "Query Argument Invalid";
	private static final String JSON_PARSE_ERROR = "JSON Parse error";
	private static final String INTERNAL_ERROR = "Server internal error";
	private static final String TIME_OUT_ERROR = "Timeout when get data from server";
	
	private static final String PRODUCT_NOT_AVAILABLE = "Product not available or amount not enough";
	
	private static final String STATUS = "status";
	private static final String SUCCESS = "success";
	
	
	/**
	 * Create a new order
	 * @param PRODUCT_ID, AMOUNT, ADDRESS, DESCRIPTION
	 * @return
	 */
	@POST
    @Timed
    @Path("/newitem")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Response newItem(@Context UriInfo ui) {
		JSONObject request = null;
		JSONObject returnVal = new JSONObject();
		
		String user_id = "";
		long event_time = -1;
		String category = "";
		long amount = -1;
		String note = "";
		String picture_url = "";
		
		
		/* Prepare  */
		MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
		
		if (queryParams == null || queryParams.isEmpty())
		{
			logger_.error("ERROR: cannot parse neworder request to MultivaluedMap: " + ui.getRequestUri());
			return Response.status(500).entity(QUERY_ARG_ERROR).build();
		}
			
		if (queryParams.containsKey(QUERY_PARAM)) {
			try {
				request = new JSONObject(queryParams.getFirst(QUERY_PARAM));
			} catch (JSONException e) {
				logger_.error("ERROR: cannot parse neworder request to JSON: " + ui.getRequestUri());
				return Response.status(500).entity(JSON_PARSE_ERROR).build();
			}
		} else {
			logger_.error("ERROR: no query parameter in neworder request: " + ui.getRequestUri());
			return Response.status(500).entity(QUERY_ARG_ERROR).build();
		}
				
		if (request.has(utils.NameDef.USER_ID) == false
				|| request.has(utils.NameDef.AMOUNT) == false
				|| request.has(utils.NameDef.NOTE) == false
				|| request.has(utils.NameDef.PICTURE_URL) == false
				|| request.has(utils.NameDef.CATEGORY) == false
				|| request.has(utils.NameDef.EVENT_TIME) == false) {
			logger_.error("ERROR: not enough parameters in neworder request: " + ui.getRequestUri());
			return Response.status(500).entity(QUERY_ARG_ERROR).build();
		}
			
		try {
			user_id = request.getString(utils.NameDef.USER_ID);
			amount = request.getLong(utils.NameDef.AMOUNT);
			note = request.getString(utils.NameDef.NOTE);
			picture_url = request.getString(utils.NameDef.PICTURE_URL);
			category = request.getString(utils.NameDef.CATEGORY);
			event_time = request.getLong(utils.NameDef.EVENT_TIME);
		} catch (JSONException e) {
			logger_.error("ERROR: failed to get parameters from neworder request: " + ui.getRequestUri());
			return Response.status(500).entity(QUERY_ARG_ERROR).build();
		}
		
		
		/* Process  */
		// verify request
		if (user_id.length() == 0 || category.length() == 0 || event_time < 0) {
			logger_.error("ERROR: invalid order request: " + ui.getRequestUri());
			return Response.status(500).entity(QUERY_ARG_ERROR).build();
		}
		
		BooksInfo books = new BooksInfo(TimeUtils.getUniqueTimeStampInMS(), user_id, category, event_time, amount, note, picture_url);
		logger_.info("Insert new books : " + books.toMap().toString());
		try {
			BooksTable.instance().insertNewBooks(books);
		} catch (SQLException e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert new books : " + e.getMessage());
			return Response.status(500).entity(INTERNAL_ERROR).build();
		}
		
		return Response.status(200).entity(returnVal.toString()).build();
	}
}