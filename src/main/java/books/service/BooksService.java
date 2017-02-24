package books.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import utils.ApiUtils;
import utils.TimeUtils;

@Path("/books")
public class BooksService {
	private static final Logger logger_ = LoggerFactory.getLogger(BooksService.class);
	
	
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
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		JSONObject request;
		String user_id = "";
		String event_date = "";
		String category = "";
		long amount = -1;
		String note = "";
		String picture_url = "";
		
		logger_.error("insert: " + postString);
		/* Prepare  */
		try {
			request = new JSONObject(postString);
		} catch (JSONException e) {
			logger_.error("ERROR: cannot parse new item request: " + postString);
			logger_.error("ERROR: cannot parse new item request: " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		if (request.has(utils.NameDef.USER_ID) == false
				|| request.has(utils.NameDef.AMOUNT) == false
				|| request.has(utils.NameDef.NOTE) == false
				|| request.has(utils.NameDef.PICTURE_URL) == false
				|| request.has(utils.NameDef.CATEGORY) == false
				|| request.has(utils.NameDef.EVENT_DATE) == false) {
			logger_.error("ERROR: not enough parameters in new item request: " + postString);
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
			
		try {
			user_id = request.getString(utils.NameDef.USER_ID);
			amount = request.getLong(utils.NameDef.AMOUNT);
			note = request.getString(utils.NameDef.NOTE);
			picture_url = request.getString(utils.NameDef.PICTURE_URL);
			category = request.getString(utils.NameDef.CATEGORY);
			event_date = request.getString(utils.NameDef.EVENT_DATE);
		} catch (JSONException e) {
			logger_.error("ERROR: failed to get parameters from new item request: " + postString);
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		
		/* Process  */
		// verify request
		if (user_id.length() == 0 || category.length() == 0 || event_date.length() == 0) {
			logger_.error("ERROR: invalid new item request: " + postString);
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		BooksInfo books = new BooksInfo(TimeUtils.getUniqueTimeStampInMS(), user_id, category, event_date, amount, note, picture_url);
		logger_.info("Insert new books : " + books.toMap().toString());
		try {
			BooksTable.instance().insertNewBooks(books);
		} catch (SQLException e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert new books : " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}
		
		return Response.status(200).entity(ApiUtils.buildJSONResponse(true, ApiUtils.SUCCESS)).build();
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
		
		String user_id = "";
		
		
		/* Prepare  */
		MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
		
		if (queryParams == null || queryParams.isEmpty())
		{
			logger_.error("ERROR: cannot parse get books request to MultivaluedMap: " + ui.getRequestUri());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
			
		if (queryParams.containsKey(ApiUtils.QUERY_PARAM)) {
			try {
				request = new JSONObject(queryParams.getFirst(ApiUtils.QUERY_PARAM));
			} catch (JSONException e) {
				logger_.error("ERROR: cannot parse get books request to JSON: " + ui.getRequestUri());
				return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.JSON_PARSE_ERROR)).build();
			}
		} else {
			logger_.error("ERROR: no query parameter in get books request: " + ui.getRequestUri());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
				
		if (request.has(utils.NameDef.USER_ID) == false) {
			logger_.error("ERROR: not enough parameters in get books request: " + ui.getRequestUri());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
			
		try {
			user_id = request.getString(utils.NameDef.USER_ID);
		} catch (JSONException e) {
			logger_.error("ERROR: failed to get parameters from get books request: " + ui.getRequestUri());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		
		/* Process  */
		// verify request
		if (user_id.length() == 0) {
			logger_.error("ERROR: invalid get books request: " + ui.getRequestUri());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		List<BooksInfo> books = new ArrayList<BooksInfo>();
		try {
			books = BooksTable.instance().getAllBooksForUser(user_id);
		} catch (SQLException e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert new books item : " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}
		
		
		JSONArray jsonArray = new JSONArray();
		Gson gson = new Gson();
		for (BooksInfo book : books) {
			try {
				jsonArray.put(new JSONObject(gson.toJson(book)));
			} catch (JSONException e) {
				e.printStackTrace();
				logger_.error("Error : failed to build response JSON array : " + e.getMessage());
				return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
			}
		}
		
		return Response.status(200).entity(jsonArray.toString()).build();
	}
}