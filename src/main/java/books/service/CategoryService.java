package books.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
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
	public Response newCategory(@Valid CategoryPostJsonObj request) {
		if (request == null) {
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		// 1. extract request
		// 2. verify and parse request
		// 3. verify parameters 
		if (request.user_id.length() == 0 || request.name.length() == 0) {
			logger_.error("ERROR: invalid new category request: " + request.toString());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		// 4. transaction
		CategoryInfo category = new CategoryInfo(request.user_id, request.name, request.picture_url);
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
	
	public static class CategoryPostJsonObj {
		@JsonProperty(NameDef.USER_ID)
		String user_id;
		@JsonProperty(NameDef.NAME)
		String name;
		@JsonProperty(NameDef.PICTURE_URL)
		String picture_url;
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
	                .add(NameDef.USER_ID, user_id)
	                .add(NameDef.NAME, name)
	                .add(NameDef.PICTURE_URL, picture_url)
	                .toString();
		}
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
	public Response getAllBooks(@QueryParam(NameDef.USER_ID) String user_id) {
		// 1. extract request
		// 2. verify and parse request
		// 3. verify parameters 
		if (user_id.length() == 0) {
			logger_.error("ERROR: invalid get books request for \'" + user_id + "\'");
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		// 4. transaction
		List<CategoryInfo> categories = new ArrayList<CategoryInfo>();
		try {
			categories = CategoryTable.instance().getAllCategoriesForUser(user_id);
		} catch (SQLException e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert new books item for " + user_id + " : " + e.getMessage());
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
