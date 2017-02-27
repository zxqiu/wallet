package books.service;

import java.sql.SQLException;
import java.util.ArrayList;
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

import books.service.data.CategoryInfo;
import books.service.data.CategoryTable;
import utils.ApiUtils;
import utils.NameDef;

@Path("/books")
public class CategoryService {
	private static final Logger logger_ = LoggerFactory.getLogger(CategoryService.class);
	
	/**
	 * Create a new category and insert to books
	 * @param user_id, name, picture_url
	 * @return
	 */
	@POST
    @Timed
    @Path("/newcategory")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Response newCategory(@Valid String postString) {
		if (postString == null) {
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		JSONObject request;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String user_id = "";
		String name = "";
		String picture_url = "";
		
		// 1. extract request
		try {
			request = new JSONObject(postString);
		} catch (JSONException e) {
			logger_.error("ERROR: cannot parse new categury request: " + postString);
			logger_.error("ERROR: cannot parse new category request: " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		// 2. verify and get parameters
		paramMap.put(NameDef.USER_ID, null);
		paramMap.put(NameDef.NAME, null);
		paramMap.put(NameDef.PICTURE_URL, null);
		
		if (ApiUtils.verifyAndGetParameters(paramMap, request) == false) {
			logger_.error("ERROR: not enough parameters in new category request: " + postString);
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
			
		try {
			user_id = request.getString(NameDef.USER_ID);
			name = request.getString(NameDef.NAME);
			picture_url = request.getString(NameDef.PICTURE_URL);
		} catch (JSONException e) {
			logger_.error("ERROR: failed to get parameters from new category request: " + postString);
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		// 3. verify parameters 
		if (user_id.length() == 0 || name.length() == 0) {
			logger_.error("ERROR: invalid new category request: " + postString);
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		// 4. transaction
		CategoryInfo category = new CategoryInfo(user_id, name, picture_url);
		logger_.info("Insert new category : " + category.toMap().toString());
		try {
			CategoryTable.instance().insertNewCategories(category);
		} catch (SQLException e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert new category : " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}
		
		return Response.status(200).entity(ApiUtils.buildJSONResponse(true, ApiUtils.SUCCESS)).build();
	}
	
	/**
	 * Get categories of a user
	 * @param user_id
	 * @return
	 */
	@GET
    @Timed
    @Path("/getcategories")
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
				
		// 2. verify and get parameters
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
		List<CategoryInfo> categories = new ArrayList<CategoryInfo>();
		try {
			categories = CategoryTable.instance().getAllCategoriesForUser(user_id);
		} catch (SQLException e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert new books item : " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}
		
		// 5. generate response
		JSONArray jsonArray = new JSONArray();
		Gson gson = new Gson();
		for (CategoryInfo category : categories) {
			try {
				jsonArray.put(new JSONObject(gson.toJson(category)));
			} catch (JSONException e) {
				e.printStackTrace();
				logger_.error("Error : failed to build response JSON array : " + e.getMessage());
				return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
			}
		}
		
		logger_.info("Response user " + user_id + "'s categories: " + jsonArray.toString());
		return Response.status(200).entity(jsonArray.toString()).build();
	}
}
