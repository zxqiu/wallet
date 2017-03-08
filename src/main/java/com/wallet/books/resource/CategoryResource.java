package com.wallet.books.resource;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Cookie;
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
import com.wallet.books.core.Category;
import com.wallet.books.dao.CategoryDAOConnector;
import com.wallet.login.dao.SessionDAOConnector;
import com.wallet.utils.misc.ApiUtils;
import com.wallet.utils.misc.NameDef;

@Path("/books")
public class CategoryResource {
	private static final Logger logger_ = LoggerFactory.getLogger(CategoryResource.class);
	
	private CategoryDAOConnector categoryDAOC = null;
	
	public CategoryResource() throws Exception {
		this.categoryDAOC = CategoryDAOConnector.instance();
	}
	
	/**
	 * Create a new category and insert to books
	 * @param user_id, name, picture_url
	 * @return
	 * @throws Exception 
	 */
	@POST
    @Timed
    @Path("/insertcategory")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Response newCategory(@Valid CategoryPostRequest request,
			@CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
		if (request == null || SessionDAOConnector.instance().verifySessionCookie(cookie)== false) {
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
		Category category = new Category(request.user_id, request.name, request.picture_id);
		logger_.info("Insert new category " + category.getName() + " for user " + request.user_id);
		try {
			categoryDAOC.insert(category);
		} catch (Exception e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert new category : " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}
		
		return Response.status(200).entity(ApiUtils.buildJSONResponse(true, ApiUtils.SUCCESS)).build();
	}
	
	public static class CategoryPostRequest {
		@JsonProperty(NameDef.USER_ID)
		String user_id;
		@JsonProperty(NameDef.NAME)
		String name;
		@JsonProperty(NameDef.PICTURE_ID)
		String picture_id;
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
	                .add(NameDef.USER_ID, user_id)
	                .add(NameDef.NAME, name)
	                .add(NameDef.PICTURE_URL, picture_id)
	                .toString();
		}
	}
	
	/**
	 * Get categories of a user
	 * @param user_id
	 * @return
	 * @throws Exception 
	 */
	@GET
    @Timed
    @Path("/getcategories")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Response getCategories(@QueryParam(NameDef.USER_ID) String user_id,
			@CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
		// 1. extract request
		// 2. verify and parse request
		// 3. verify parameters 
		if (user_id.length() == 0 || SessionDAOConnector.instance().verifySessionCookie(cookie)== false) {
			logger_.error("ERROR: invalid get books request for \'" + user_id + "\'");
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		// 4. transaction
		List<Category> categories = new ArrayList<Category>();
		try {
			categories = categoryDAOC.getByUserID(user_id);
		} catch (Exception e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert new books item for " + user_id + " : " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}
		
		// 5. generate response
		JSONArray jsonArray = new JSONArray();
		Gson gson = new Gson();
		for (Category category : categories) {
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
