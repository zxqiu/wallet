package com.wallet.books.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.Gson;
import com.wallet.books.core.Books;
import com.wallet.books.dao.BooksDAOConnector;
import com.wallet.login.dao.SessionDAOConnector;
import com.wallet.login.resource.SessionResource;
import com.wallet.utils.misc.ApiUtils;
import com.wallet.utils.misc.Dict;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Date;
import java.util.List;

@Path("/books")
public class BooksResource {
	private static final Logger logger_ = LoggerFactory.getLogger(BooksResource.class);

	private BooksDAOConnector booksDAOC = null;
	private SessionDAOConnector sessionDAOC = null;

	public BooksResource() throws Exception {
		this.booksDAOC = BooksDAOConnector.instance();
		this.sessionDAOC = SessionDAOConnector.instance();
	}

	@GET
	@Timed
	@Path("/allbooks")
	@Produces(value = MediaType.APPLICATION_JSON)
	public List<Books> getAll() throws Exception {
		return booksDAOC.getByUserID("admin");
	}

	@GET
	@Timed
	@Path("/bookslist")
	@Produces(MediaType.TEXT_HTML)
    public Response booksListView(@CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
		String user_id = ApiUtils.getUserIDFromCookie(cookie);
		if (user_id == null || sessionDAOC.verifySessionCookie(cookie)== false) {
			return Response.seeOther(URI.create(SessionResource.PATH_RESTORE_SESSION)).build();
		}

		return Response.ok().entity(views.booksList.template(booksDAOC.getByUserID(user_id))).build();
	}

	/**
	 * Create a new books and insert to books
	 * @param name, picture_url
	 * @return
	 * @throws Exception 
	 */
	@POST
    @Timed
    @Path("/insertbooks")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response insertBooks(
			@FormParam(Dict.ID) String id,
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
		Books books = new Books(user_id, user_id, name, new Date(), picture_id, "");
		try {
		    if (id != null && id.length() > 1) {
				logger_.info("Update books " + books.getName() + " for user " + user_id);
		    	booksDAOC.update(books);
			} else {
				logger_.info("Insert books " + books.getName() + " for user " + user_id);
				booksDAOC.insert(books);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert or update books : " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}

		return Response.seeOther(URI.create(BooksEntryResource.PATH_BOOKS)).build();
	}

	@POST
	@Timed
	@Path("/insertbookslist")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    public Response insertBooksList(
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
                    || !jsonObject.has(Dict.ACTION)
					|| !jsonObject.has(Dict.NAME)
					|| !jsonObject.has(Dict.PICTURE_ID)) {
			    continue;
			}

			if (jsonObject.getString(Dict.NAME).length() == 0) {
				continue;
			}

			Books books = new Books(user_id
					, user_id
					, jsonObject.getString(Dict.NAME)
					, new Date()
					, jsonObject.getString(Dict.PICTURE_ID)
					, ""
			);

			String id = jsonObject.getString(Dict.ID);
			if (jsonObject.getString(Dict.ACTION).equals(Dict.EDIT)) {
				if (id.length() > 1) {
					logger_.info("Updating books : " + jsonObject.toString());
					booksDAOC.update(books);
				} else {
					logger_.info("Insert new books : " + jsonObject.toString());
					booksDAOC.insert(books);
				}
			} else if (jsonObject.getString(Dict.ACTION).equals(Dict.DELETE)) {
				logger_.info("Delete books : " + jsonObject.toString());
				booksDAOC.deleteByID(id);
			}
		}

		return Response.status(200).entity(ApiUtils.buildJSONResponse(true, BooksEntryResource.PATH_BOOKS)).build();
	}

	/**
	 * Get categories of a user
	 * @param user_id
	 * @return
	 * @throws Exception 
	 */
	@GET
    @Timed
    @Path("/getbooks")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Response getCategories(@QueryParam(Dict.USER_ID) String user_id,
			@CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
		// 1. extract request
		// 2. verify and parse request
		// 3. verify parameters 
		if (user_id.length() == 0 || sessionDAOC.verifySessionCookie(cookie)== false) {
			logger_.error("ERROR: invalid get books request for \'" + user_id + "\'");
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		// 4. transaction
		List<Books> categories;
		try {
			categories = booksDAOC.getByUserID(user_id);
		} catch (Exception e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert new books for " + user_id + " : " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}
		
		// 5. generate response
		JSONArray jsonArray = new JSONArray();
		Gson gson = new Gson();
		for (Books books : categories) {
			try {
				jsonArray.put(new JSONObject(gson.toJson(books)));
			} catch (JSONException e) {
				e.printStackTrace();
				logger_.error("Error : failed to build books response JSON array : " + e.getMessage());
				return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
			}
		}
		
		logger_.info("Response user " + user_id + "'s books : " + jsonArray.toString());
		return Response.status(200).entity(jsonArray.toString()).build();
	}
}
