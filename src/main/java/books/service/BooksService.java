package books.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.Gson;

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
	public Response newItem(@Valid String postString) {
		if (postString == null) {
			return Response.status(500).entity(QUERY_ARG_ERROR).build();
		}
		
		JSONObject request;
		String user_id = "";
		long event_time = -1;
		String category = "";
		long amount = -1;
		String note = "";
		String picture_url = "";
		
		logger_.error("insert: " + postString);
		/* Prepare  */
		try {
			request = new JSONObject(postString);
		} catch (JSONException e1) {
			logger_.error("ERROR: cannot parse new item request: " + postString);
			logger_.error("ERROR: cannot parse new item request: " + e1.getMessage());
			return Response.status(500).entity(QUERY_ARG_ERROR).build();
		}
		
		if (request.has(utils.NameDef.USER_ID) == false
				|| request.has(utils.NameDef.AMOUNT) == false
				|| request.has(utils.NameDef.NOTE) == false
				|| request.has(utils.NameDef.PICTURE_URL) == false
				|| request.has(utils.NameDef.CATEGORY) == false
				|| request.has(utils.NameDef.EVENT_TIME) == false) {
			logger_.error("ERROR: not enough parameters in new item request: " + postString);
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
			logger_.error("ERROR: failed to get parameters from new item request: " + postString);
			return Response.status(500).entity(QUERY_ARG_ERROR).build();
		}
		
		
		/* Process  */
		// verify request
		if (user_id.length() == 0 || category.length() == 0 || event_time < 0) {
			logger_.error("ERROR: invalid new item request: " + postString);
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
		
		return Response.status(200).entity(SUCCESS).build();
	}
	
		/**
	 * Create a new order
	 * @param PRODUCT_ID, AMOUNT, ADDRESS, DESCRIPTION
	 * @return
	 */
	@GET
    @Timed
    @Path("/getbooks")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Response getAllBooks(@Context UriInfo ui) {
		JSONObject request = null;
		JSONObject returnVal = new JSONObject();
		
		String user_id = "";
		
		
		/* Prepare  */
		MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
		
		if (queryParams == null || queryParams.isEmpty())
		{
			logger_.error("ERROR: cannot parse get books request to MultivaluedMap: " + ui.getRequestUri());
			return Response.status(500).entity(QUERY_ARG_ERROR).build();
		}
			
		if (queryParams.containsKey(QUERY_PARAM)) {
			try {
				request = new JSONObject(queryParams.getFirst(QUERY_PARAM));
			} catch (JSONException e) {
				logger_.error("ERROR: cannot parse get books request to JSON: " + ui.getRequestUri());
				return Response.status(500).entity(JSON_PARSE_ERROR).build();
			}
		} else {
			logger_.error("ERROR: no query parameter in get books request: " + ui.getRequestUri());
			return Response.status(500).entity(QUERY_ARG_ERROR).build();
		}
				
		if (request.has(utils.NameDef.USER_ID) == false) {
			logger_.error("ERROR: not enough parameters in get books request: " + ui.getRequestUri());
			return Response.status(500).entity(QUERY_ARG_ERROR).build();
		}
			
		try {
			user_id = request.getString(utils.NameDef.USER_ID);
		} catch (JSONException e) {
			logger_.error("ERROR: failed to get parameters from get books request: " + ui.getRequestUri());
			return Response.status(500).entity(QUERY_ARG_ERROR).build();
		}
		
		
		/* Process  */
		// verify request
		if (user_id.length() == 0) {
			logger_.error("ERROR: invalid get books request: " + ui.getRequestUri());
			return Response.status(500).entity(QUERY_ARG_ERROR).build();
		}
		
		List<BooksInfo> books = new ArrayList<BooksInfo>();
		try {
			books = BooksTable.instance().getAllBooksForUser(user_id);
		} catch (SQLException e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert new books item : " + e.getMessage());
			return Response.status(500).entity(INTERNAL_ERROR).build();
		}
		
		
		JSONArray jsonArray = new JSONArray();
		Gson gson = new Gson();
		for (BooksInfo book : books) {
			try {
				jsonArray.put(new JSONObject(gson.toJson(book)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return Response.status(200).entity(jsonArray.toString()).build();
	}
}