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

	public static final String PATH_BOOKS_LIST = "/books/bookslist/";
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
	public Response getBooks(@QueryParam(Dict.USER_ID) String user_id,
			@CookieParam("walletSessionCookie") Cookie cookie
	) throws Exception {
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

	public static final String PATH_RECEIVE_BOOKS = "/books/receivebooks/";
	@GET
	@Timed
	@Path("/receivebooks/{" + Dict.USER_ID + "}/{" + Dict.BOOKS_ID + "}")
	@Produces(MediaType.TEXT_HTML)
	public Response receiveBooks(
			@PathParam(Dict.USER_ID) String user_id
			, @PathParam(Dict.BOOKS_ID) String books_id
			, @CookieParam("walletSessionCookie") Cookie cookie
	) throws Exception {
		String request_user = ApiUtils.getUserIDFromCookie(cookie);

		if (request_user == null || request_user.length() < 1 || !sessionDAOC.verifySessionCookie(cookie)) {
			return Response.ok().entity(views.login.template(PATH_RECEIVE_BOOKS + user_id + "/" + books_id)).build();
		}

		if (user_id.length() < 1 || books_id.length() < 1) {
			return Response
					.serverError()
					.entity(ApiUtils
							.buildJSONResponse(false, "wrong user id or books id"))
					.build();
		}

		List<Books> booksList = booksDAOC.getByID(books_id);
		Books book;
		if (booksList.isEmpty()) {
			return Response
					.serverError()
					.entity(ApiUtils
							.buildJSONResponse(false, "books not found"))
					.build();
		}

		book = booksList.get(booksList.size() - 1);
		if (!book.getUser_id().equals(user_id)) {
			return Response
					.serverError()
					.entity(ApiUtils
							.buildJSONResponse(false, "user not found"))
					.build();
		}

		book.setUser_id(request_user);
		book.updateBooksID();
		try {
			booksDAOC.insert(book);
		} catch (Exception e) {
			return Response
					.serverError()
					.entity(ApiUtils
							.buildJSONResponse(false, "failed to insert new book " + book.getId()))
					.build();
		}

		if (appendUserToAll(book.getName(), book.getCreate_user_id(), request_user).isEmpty()) {
			return Response
					.serverError()
					.entity(ApiUtils
							.buildJSONResponse(false, "failed to append user " + user_id))
					.build();
		}


		return Response.seeOther(URI.create(PATH_BOOKS_LIST)).build();
	}

	public List<Books> appendUserToAll(String book_name, String create_user_id, String append_user_id) throws Exception {
		List<Books> booksList = booksDAOC.getByNameAndCreateUserID(book_name, create_user_id);

		for (Books book : booksList) {
			book.appendUser(append_user_id);
			booksDAOC.update(book);
		}

		return booksList;
	}
}
