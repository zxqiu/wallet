package com.wallet.books.resource;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.wallet.login.resource.SessionResource;
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
import com.wallet.utils.misc.Dict;

@Path("/books")
public class CategoryResource {
	private static final Logger logger_ = LoggerFactory.getLogger(CategoryResource.class);
	
	private CategoryDAOConnector categoryDAOC = null;
	
	public CategoryResource() throws Exception {
		this.categoryDAOC = CategoryDAOConnector.instance();
	}

	@GET
	@Timed
	@Path("/allcategories")
	@Produces(value = MediaType.APPLICATION_JSON)
	public List<Category> getAll() throws SQLException {
		return categoryDAOC.getByUserID("admin");
	}


	@GET
	@Timed
	@Path("/categories")
	@Produces(MediaType.TEXT_HTML)
    public Response categoryListView(@CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
		String user_id = ApiUtils.getUserIDFromCookie(cookie);
		if (user_id == null || SessionDAOConnector.instance().verifySessionCookie(cookie)== false) {
			return Response.seeOther(URI.create(SessionResource.PATH_LOGIN)).build();
		}

		return Response.ok().entity(views.categoryList.template(categoryDAOC.getByUserID(user_id))).build();
	}

	/**
	 * Create a new category and insert to books
	 * @param name, picture_url
	 * @return
	 * @throws Exception 
	 */
	@POST
    @Timed
    @Path("/insertcategory")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response insertCategory(
			@FormParam(Dict.ID) String id,
			@FormParam(Dict.NAME) String name,
			@FormParam(Dict.PICTURE_ID) String picture_id,
			@CookieParam("walletSessionCookie") Cookie cookie) throws Exception {

		String user_id = ApiUtils.getUserIDFromCookie(cookie);
		if (user_id == null || SessionDAOConnector.instance().verifySessionCookie(cookie)== false) {
			return Response.seeOther(URI.create(SessionResource.PATH_LOGIN)).build();
		}
		
		// 1. extract request
		// 2. verify and parse request
		// 3. verify parameters 
		if (name.length() == 0 || picture_id.length() == 0) {
			return Response.serverError().build();
		}
		
		// 4. transaction
		Category category = new Category(user_id, name, picture_id);
		try {
		    if (id != null && id.length() > 1) {
		    	category.setId(id);
				logger_.info("Update category " + category.getName() + " for user " + user_id);
		    	categoryDAOC.updatePictureID(category);
			} else {
				logger_.info("Insert category " + category.getName() + " for user " + user_id);
				categoryDAOC.insert(category);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert or update category : " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}

		return Response.seeOther(URI.create(BooksEntryResource.PATH_BOOKS)).build();
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
	public Response getCategories(@QueryParam(Dict.USER_ID) String user_id,
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
