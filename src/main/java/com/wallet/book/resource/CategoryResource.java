package com.wallet.book.resource;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.wallet.book.core.syncHelper;
import com.wallet.book.dao.BookConnector;
import com.wallet.book.dao.CategoryConnector;
import com.wallet.login.resource.SessionResource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.Gson;
import com.wallet.book.core.Category;
import com.wallet.login.dao.SessionDAOConnector;
import com.wallet.utils.misc.ApiUtils;
import com.wallet.utils.misc.Dict;

@Path("/books")
public class CategoryResource {
	private static final Logger logger_ = LoggerFactory.getLogger(CategoryResource.class);
	
	private CategoryConnector categoryDAOC = null;
	private BookConnector bookDAOC = null;
	private SessionDAOConnector sessionDAOC = null;
	
	public CategoryResource() throws Exception {
		this.categoryDAOC = CategoryConnector.instance();
		this.bookDAOC = BookConnector.instance();
		this.sessionDAOC = SessionDAOConnector.instance();
	}

	@GET
	@Timed
	@Path("/allcategories")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Category> getAll() throws SQLException {
		return categoryDAOC.getByUserID("admin");
	}


	@GET
	@Timed
	@Path("/categories")
	@Produces(MediaType.TEXT_HTML)
    public Response categoryListView(@CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
		String user_id = ApiUtils.getUserIDFromCookie(cookie);
		if (user_id == null || sessionDAOC.verifySessionCookie(cookie)== false) {
			return Response.seeOther(URI.create(SessionResource.PATH_RESTORE_SESSION)).build();
		}

		return Response.ok()
				.entity(views.categoryList.template(categoryDAOC.getByUserID(user_id)
													, bookDAOC.getByUserID(user_id)
													, user_id
				)).build();
	}

	/**
	 * Create a new category and insert to book
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
			@FormParam(Dict.BOOK_GROUP_ID) String book_group_id,
			@FormParam(Dict.NAME) String name,
			@FormParam(Dict.PICTURE_ID) String picture_id,
			@CookieParam("walletSessionCookie") Cookie cookie) throws Exception {

		String user_id = ApiUtils.getUserIDFromCookie(cookie);
		if (user_id == null || sessionDAOC.verifySessionCookie(cookie)== false) {
			return Response.seeOther(URI.create(SessionResource.PATH_RESTORE_SESSION)).build();
		}
		
		// 1. extract request
		// 2. verify and parse request
		// 3. verify parameters 
		if (name.length() == 0 || picture_id.length() == 0) {
			return Response.serverError().build();
		}
		
		// 4. transaction
		Category category = new Category(user_id, book_group_id, name, picture_id);
		try {
		    if (id != null && id.length() > 1) {
				logger_.info("Update category " + category.getName() + " for user " + user_id);
		    	categoryDAOC.updateByID(category);
				syncHelper.syncCategory(category, syncHelper.SYNC_ACTION.UPDATE);
			} else {
				logger_.info("Insert category " + category.getName() + " for user " + user_id);
				//categoryDAOC.insert(category);
				syncHelper.syncCategory(category, syncHelper.SYNC_ACTION.ADD);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert or update category : " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}

		return Response.seeOther(URI.create(BookEntryResource.PATH_BOOKS)).build();
	}

	@POST
	@Timed
	@Path("/insertcategorylist")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    public Response insertCategoryList(
    		String request
			, @CookieParam("walletSessionCookie") Cookie cookie
	) throws Exception {
		String user_id = ApiUtils.getUserIDFromCookie(cookie);
		if (user_id == null || sessionDAOC.verifySessionCookie(cookie) == false) {
			Response.seeOther(URI.create(SessionResource.PATH_RESTORE_SESSION)).build();
		}

		JSONArray categories = new JSONArray(request);
		for (int i = 0; i < categories.length(); i++) {
			JSONObject jsonObject = categories.getJSONObject(i);

			if (!jsonObject.has(Dict.ID)
					|| !jsonObject.has(Dict.BOOK_GROUP_ID)
                    || !jsonObject.has(Dict.ACTION)
					|| !jsonObject.has(Dict.NAME)
					|| !jsonObject.has(Dict.PICTURE_ID)) {
			    continue;
			}

			if (jsonObject.getString(Dict.NAME).length() == 0) {
				continue;
			}

			String id = jsonObject.getString(Dict.ID);
			Category categoryNew = new Category(user_id
					, jsonObject.getString(Dict.BOOK_GROUP_ID)
					, jsonObject.getString(Dict.NAME)
					, jsonObject.getString(Dict.PICTURE_ID)
			);
			Category category = null;
			List<Category> categoryList = null;
			if (id.length() > 1) {
				categoryList = categoryDAOC.getByID(id);
				if (!categoryList.isEmpty()) {
					category = categoryList.get(categoryList.size() - 1);
				}
			}

			if (jsonObject.getString(Dict.ACTION).equals(Dict.EDIT)) {
				if (category == null) {
					logger_.info("Insert new category : " + jsonObject.toString());
					//categoryDAOC.insert(categoryNew);
					syncHelper.syncCategory(categoryNew, syncHelper.SYNC_ACTION.ADD);
				} else {
				    category.update(categoryNew);
					logger_.info("Updating category : " + jsonObject.toString());
					categoryDAOC.updateByID(category);
					syncHelper.syncCategory(category, syncHelper.SYNC_ACTION.UPDATE);
				}
			} else if (jsonObject.getString(Dict.ACTION).equals(Dict.DELETE)) {
				logger_.info("Delete category : " + jsonObject.toString());
				categoryDAOC.deleteByID(id);
				syncHelper.syncCategory(category, syncHelper.SYNC_ACTION.DELETE);
			}
		}

		return Response.status(200).entity(ApiUtils.buildJSONResponse(true, BookEntryResource.PATH_BOOKS)).build();
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
		if (user_id.length() == 0 || sessionDAOC.verifySessionCookie(cookie)== false) {
			logger_.error("ERROR: invalid get book request for \'" + user_id + "\'");
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		// 4. transaction
		List<Category> categories = new ArrayList<Category>();
		try {
			categories = categoryDAOC.getByUserID(user_id);
		} catch (Exception e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert new book item for " + user_id + " : " + e.getMessage());
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
