package books.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.Gson;

import books.service.data.BooksInfo;
import books.service.data.BooksTable;
import books.service.data.CategoryInfo;
import books.service.data.CategoryTable;
import utils.ApiUtils;
import utils.NameDef;
import utils.TimeUtils;

@Path("/books")
public class BooksService {
	private static final Logger logger_ = LoggerFactory.getLogger(BooksService.class);
	
	
	/**
	 * Create a new item and insert to books
	 * @param user_id, event_date, category, amount, note, picture_url
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
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String user_id = "";
		String event_date = "";
		String category = "";
		long amount = -1;
		String amountStr = "";
		String note = "";
		String picture_url = "";
		
		// 1. extract request
		try {
			request = new JSONObject(postString);
		} catch (JSONException e) {
			logger_.error("ERROR: cannot parse new item request: " + postString);
			logger_.error("ERROR: cannot parse new item request: " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		// 2. verify and parse request
		paramMap.put(NameDef.USER_ID, null);
		paramMap.put(NameDef.AMOUNT, null);
		paramMap.put(NameDef.NOTE, null);
		paramMap.put(NameDef.PICTURE_URL, null);
		paramMap.put(NameDef.CATEGORY, null);
		paramMap.put(NameDef.EVENT_DATE, null);
		
		if (ApiUtils.verifyAndGetParameters(paramMap, request) == false) {
			logger_.error("ERROR: not enough parameters in new item request: " + postString);
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
			
		try {
			user_id = request.getString(NameDef.USER_ID).trim();
			amountStr = request.getString(NameDef.AMOUNT).trim();
			note = request.getString(NameDef.NOTE).trim();
			picture_url = request.getString(NameDef.PICTURE_URL).trim();
			category = request.getString(NameDef.CATEGORY).trim();
			event_date = request.getString(NameDef.EVENT_DATE).trim();
		} catch (JSONException e) {
			logger_.error("ERROR: failed to get parameters from new item request: " + postString);
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		// 3. verify parameters 
		if (user_id.length() == 0 || amountStr.length() == 0 || category.length() == 0 || event_date.length() == 0) {
			logger_.error("ERROR: invalid new item request: " + postString);
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		amount = Long.valueOf(amountStr);
		
		// 4. transaction
		// 4.1 insert new category
		try {
			boolean exist = false;
			List<CategoryInfo> categoryList = CategoryTable.instance().getAllCategoriesForUser(user_id);
			for (CategoryInfo cat : categoryList) {
				if (cat.getName().equals(category)) {
					exist = true;
				}
			}
			
			if (!exist) {
				CategoryTable.instance().insertNewCategories(new CategoryInfo(user_id, category, ""));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}
		
		// 4.2 insert books
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
	 * Get books of a user
	 * @param user_id
	 * @return
	 */
	@GET
    @Timed
    @Path("/getbooks")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Response getAllBooks(@Context UriInfo ui) {
		JSONObject request = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		String user_id = "";
		
		// 1. extract request
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
				
		// 2. verify and parse request
		paramMap.put(NameDef.USER_ID, null);
		
		if (ApiUtils.verifyAndGetParameters(paramMap, request) == false) {
			logger_.error("ERROR: not enough parameters in get books request: " + ui.getRequestUri());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
			
		try {
			user_id = request.getString(utils.NameDef.USER_ID);
		} catch (JSONException e) {
			logger_.error("ERROR: failed to get parameters from get books request: " + ui.getRequestUri());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		// 3. verify parameters 
		if (user_id.length() == 0) {
			logger_.error("ERROR: invalid get books request: " + ui.getRequestUri());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		// 4. transaction
		List<BooksInfo> books = new ArrayList<BooksInfo>();
		try {
			books = BooksTable.instance().getAllBooksForUser(user_id);
			sortBooksByTime(books);
		} catch (SQLException e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert new books item : " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}
		
		// 5. generate response
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
		
		logger_.info("Response user " + user_id + "'s books: " + jsonArray.toString());
		return Response.status(200).entity(jsonArray.toString()).build();
	}
	
	private static Comparator<BooksInfo> booksTimeComparator = new Comparator<BooksInfo>() {
		public int compare(BooksInfo a, BooksInfo b) {
			return a.getEvent_date().compareTo(b.getEvent_date());
		}
	};
	
	private List<BooksInfo> sortBooksByTime(List<BooksInfo> list) {
		Collections.sort(list, booksTimeComparator);
		
		return list;
	}
}