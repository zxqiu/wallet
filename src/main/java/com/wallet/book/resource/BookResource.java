package com.wallet.book.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.Gson;
import com.wallet.book.core.Book;
import com.wallet.book.core.BookEntry;
import com.wallet.book.dao.BookDAOConnector;
import com.wallet.book.dao.BookEntryDAOConnector;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Path("/books")
public class BookResource {
	private static final Logger logger_ = LoggerFactory.getLogger(BookResource.class);

	private BookDAOConnector bookDAOC = null;
	private BookEntryDAOConnector bookEntryDAOC = null;
	private SessionDAOConnector sessionDAOC = null;

	public BookResource() throws Exception {
		this.bookDAOC = BookDAOConnector.instance();
		this.bookEntryDAOC = BookEntryDAOConnector.instance();
		this.sessionDAOC = SessionDAOConnector.instance();
	}

	@GET
	@Timed
	@Path("/allbooks")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Book> getAll() throws Exception {
		return bookDAOC.getByUserID("admin");
	}

	public static final String PATH_BOOKS_LIST = "/books/booklist/";
	@GET
	@Timed
	@Path("/booklist")
	@Produces(MediaType.TEXT_HTML)
    public Response bookListView(@CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
		String user_id = ApiUtils.getUserIDFromCookie(cookie);
		if (user_id == null || sessionDAOC.verifySessionCookie(cookie)== false) {
			return Response.seeOther(URI.create(SessionResource.PATH_RESTORE_SESSION)).build();
		}

		return Response.ok().entity(views.bookList.template(bookDAOC.getByUserID(user_id))).build();
	}

	/**
	 * Create a new book and insert to book
	 * @param name, picture_url
	 * @return
	 * @throws Exception 
	 */
	@POST
    @Timed
    @Path("/insertbook")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response insertBook(
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
		Book book = new Book(user_id, user_id, name, new Date(), picture_id, "");
		try {
		    if (id != null && id.length() > 1) {
				logger_.info("Update book " + book.getName() + " for user " + user_id);
				logger_.info("Update book " + book.getName() + " for user " + user_id);
		    	bookDAOC.update(book);
			} else {
				logger_.info("Insert book " + book.getName() + " for user " + user_id);
				bookDAOC.insert(book);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert or update book : " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}

		return Response.seeOther(URI.create(BookEntryResource.PATH_BOOKS)).build();
	}

	@POST
	@Timed
	@Path("/insertbooklist")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    public Response insertBookList(
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

			Book book = new Book(user_id
					, user_id
					, jsonObject.getString(Dict.NAME)
					, new Date()
					, jsonObject.getString(Dict.PICTURE_ID)
					, ""
			);

			String id = jsonObject.getString(Dict.ID);
			if (jsonObject.getString(Dict.ACTION).equals(Dict.EDIT)) {
				if (id.length() > 1) {
					logger_.info("Updating book : " + jsonObject.toString());
					bookDAOC.update(book);
				} else {
					logger_.info("Insert new book : " + jsonObject.toString());
					bookDAOC.insert(book);
				}
			} else if (jsonObject.getString(Dict.ACTION).equals(Dict.DELETE)) {
				logger_.info("Delete book : " + jsonObject.toString());
				bookDAOC.deleteByID(id);
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
    @Path("/getbook")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Response getBook(@QueryParam(Dict.USER_ID) String user_id,
			@CookieParam("walletSessionCookie") Cookie cookie
	) throws Exception {
		// 1. extract request
		// 2. verify and parse request
		// 3. verify parameters 
		if (user_id.length() == 0 || sessionDAOC.verifySessionCookie(cookie)== false) {
			logger_.error("ERROR: invalid get book request for \'" + user_id + "\'");
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		// 4. transaction
		List<Book> categories;
		try {
			categories = bookDAOC.getByUserID(user_id);
		} catch (Exception e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert new book for " + user_id + " : " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}
		
		// 5. generate response
		JSONArray jsonArray = new JSONArray();
		Gson gson = new Gson();
		for (Book book : categories) {
			try {
				jsonArray.put(new JSONObject(gson.toJson(book)));
			} catch (JSONException e) {
				e.printStackTrace();
				logger_.error("Error : failed to build book response JSON array : " + e.getMessage());
				return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
			}
		}
		
		logger_.info("Response user " + user_id + "'s book : " + jsonArray.toString());
		return Response.status(200).entity(jsonArray.toString()).build();
	}

	public static final String PATH_RECEIVE_BOOKS = "/books/receivebook/";
	@GET
	@Timed
	@Path("/receivebook/{" + Dict.USER_ID + "}/{" + Dict.BOOK_ID + "}")
	@Produces(MediaType.TEXT_HTML)
	public Response receiveBook(
			@PathParam(Dict.USER_ID) String user_id
			, @PathParam(Dict.BOOK_ID) String book_id
			, @CookieParam("walletSessionCookie") Cookie cookie
	) throws Exception {
		String request_user = ApiUtils.getUserIDFromCookie(cookie);

		if (request_user == null || request_user.length() < 1 || !sessionDAOC.verifySessionCookie(cookie)) {
			return Response.ok().entity(views.login.template(PATH_RECEIVE_BOOKS + user_id + "/" + book_id)).build();
		}

		if (user_id.length() < 1 || book_id.length() < 1) {
			return Response
					.serverError()
					.entity(ApiUtils
							.buildJSONResponse(false, "wrong user id or book id"))
					.build();
		}

		List<Book> bookList = bookDAOC.getByID(book_id);
		Book book;
		if (bookList.isEmpty()) {
			return Response
					.serverError()
					.entity(ApiUtils
							.buildJSONResponse(false, "book not found"))
					.build();
		}

		book = bookList.get(bookList.size() - 1);
		if (!book.getUser_id().equals(user_id)) {
			return Response
					.serverError()
					.entity(ApiUtils
							.buildJSONResponse(false, "user not found"))
					.build();
		}

		book.setUser_id(request_user);
		book.updateBookID();
		book.appendUser(request_user);
		try {
			bookDAOC.insert(book);
		} catch (Exception e) {
			return Response
					.serverError()
					.entity(ApiUtils
							.buildJSONResponse(false, "failed to insert new book " + book.getId()))
					.build();
		}

		if (syncBookUserList(book.getName(), book.getCreate_user_id(), book.getUserList()).isEmpty()) {
			return Response
					.serverError()
					.entity(ApiUtils
							.buildJSONResponse(false, "failed to append user " + user_id))
					.build();
		}

		syncBookEntries(user_id, book.getId(), request_user);

		return Response.seeOther(URI.create(PATH_BOOKS_LIST)).build();
	}

	public List<Book> syncBookUserList(String book_name, String create_user_id, JSONArray user_list) throws Exception {
		List<Book> bookList = bookDAOC.getByNameAndCreateUserID(book_name, create_user_id);

		for (Book book : bookList) {
			book.updateUserList(user_list);
			bookDAOC.update(book);
		}

		return bookList;
	}

	public List<BookEntry> syncBookEntries(String user_id, String book_id, String target_user_id) throws Exception {
		List<BookEntry> entryList = bookEntryDAOC.getByUserIDAndBookID(user_id, book_id);
		List<BookEntry> existingEntryList = bookEntryDAOC.getByUserIDAndBookID(target_user_id, book_id);
		HashMap<String, BookEntry> fingerPrintMap = new HashMap<>();

		for (BookEntry entry : existingEntryList) {
			fingerPrintMap.put(entry.getFingerPrint(), entry);
		}

		for (BookEntry entry : entryList) {
			String finger_print = entry.getFingerPrint();
			if (fingerPrintMap.containsKey(finger_print)) {
			    BookEntry tmp = fingerPrintMap.get(finger_print);
			    tmp.update(tmp.getUser_id(), entry.getBook_id(), entry.getCategory(), entry.getEvent_date(), entry.getAmount()
						, entry.getNote(), entry.getPhoto());
			    bookEntryDAOC.update(tmp);
			} else {
				entry.setUser_id(target_user_id);
				entry.updateID();
				bookEntryDAOC.insert(entry);
			}
		}

		return entryList;
	}
}
