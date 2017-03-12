package com.wallet.books.resource;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

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
import com.wallet.books.core.BooksEntry;
import com.wallet.books.core.Category;
import com.wallet.books.dao.BooksEntryDAOConnector;
import com.wallet.books.dao.CategoryDAOConnector;
import com.wallet.login.dao.SessionDAOConnector;
import com.wallet.utils.misc.ApiUtils;
import com.wallet.utils.misc.NameDef;
import com.wallet.utils.misc.TimeUtils;

@Path("/books")
public class BooksEntryResource {
	private static final Logger logger_ = LoggerFactory.getLogger(BooksEntryResource.class);
	private static final int booksEntrysEachLine = 6;
	
	private BooksEntryDAOConnector booksEntryDAOC = null;
	private CategoryDAOConnector categoryDAOC = null;
	
	public BooksEntryResource() throws Exception {
		this.booksEntryDAOC = BooksEntryDAOConnector.instance();
		this.categoryDAOC = CategoryDAOConnector.instance();
	}

	public static final String PATH_BOOKS = "/books";
	/**
	 * @return booksEntryList view. This is the main view of path /books
	 */
	@GET
	@Timed
	@Path("/")
	@Produces(value = MediaType.TEXT_HTML)
	public Response booksEntryListView(@CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
		String user_id = ApiUtils.getUserIDFromCookie(cookie);
		if (SessionDAOConnector.instance().verifySessionCookie(cookie)== false || user_id == null) {
			Response.seeOther(URI.create(SessionResource.PATH_LOGIN)).build();
		}

		return Response.ok().entity(views.booksEntryList.template(sortBooksByTime(booksEntryDAOC.getByUserID(user_id)), booksEntrysEachLine)).build();
	}

	public static final String PATH_INSERT_ENTRY_VIEW = "/books/entry";
	/**
	 * @return booksEntry view. This is for showing and edit entry details.
	 */
	@GET
	@Timed
	@Path("/entry/{id}")
	@Produces(value = MediaType.TEXT_HTML)
	public Response booksEntryView(@PathParam(NameDef.ID) String id,
								   @CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
	    String user_id = ApiUtils.getUserIDFromCookie(cookie);
		if (SessionDAOConnector.instance().verifySessionCookie(cookie)== false || user_id == null) {
			Response.seeOther(URI.create(SessionResource.PATH_LOGIN)).build();
		}

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		String date = sdf.format(new Date());
		BooksEntry booksEntry = null;
		if (!id.equals("0")) {
			List<BooksEntry> booksEntryList = booksEntryDAOC.getByID(id);
			if (!booksEntryList.isEmpty()) {
				booksEntry = booksEntryList.get(booksEntryList.size() - 1);
				date = sdf.format(booksEntry.getEvent_date());
			}
		}

		return Response.ok().entity(views.booksEntry.template(id, booksEntry, categoryDAOC.getByUserID(user_id), date)).build();
	}

	/**
	 * Create a insert new or update existing books entry.
	 * @param id, event_date, amount, category, note, photo, cookie
	 * @return
	 * @throws Exception 
	 */
	@POST
    @Timed
    @Path("/insertentry")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response insertItem(@FormParam(NameDef.ID) String id,
							   @FormParam(NameDef.EVENT_DATE) String event_date,
							   @FormParam(NameDef.AMOUNT) long amount,
							   @FormParam(NameDef.CATEGORY) String category,
							   @FormParam(NameDef.NOTE) String note,
							   @FormParam(NameDef.PHOTO) String photo,
							   @CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
		if (SessionDAOConnector.instance().verifySessionCookie(cookie)== false) {
			return Response.seeOther(URI.create(SessionResource.PATH_LOGIN)).build();
		}
		
		long currentTimeMS = TimeUtils.getUniqueTimeStampInMS();
		String user_id = ApiUtils.getUserIDFromCookie(cookie);

		// 1. extract request
		// 2. verify and parse request
		// 3. verify parameters 
		if (category.length() == 0 || event_date.length() == 0) {
			logger_.error("ERROR: invalid new item request: " + id);
			return Response.serverError().build();
		}
		
		// 4. transaction
		// 4.1 insert new category
		try {
			if (categoryDAOC.getByID(user_id + category).isEmpty()) {
				categoryDAOC.insert(new Category(user_id, category, ""));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			return Response.serverError().build();
		}
		
		// 4.2 insert books
		// 4.2.1 update if id is received and exists. Otherwise insert new.
		boolean exist = false;
		if (id.length() != 0) {
			try {
				exist = !booksEntryDAOC.getByID(id).isEmpty();
			} catch (Exception e) {
				e.printStackTrace();
				logger_.error("Error find out if item already exists : " + e.getMessage());
				return Response.serverError().build();
			}
		}
		
		if (!exist) {
			id = user_id + currentTimeMS;
		}
		
		// 4.2.2 insert 
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		BooksEntry booksEntry = new BooksEntry(id, user_id, category, sdf.parse(event_date), amount, note, photo);
		try {
			if (exist) {
				logger_.info("Update books item : " + booksEntry.getId());
				booksEntryDAOC.update(booksEntry);
			} else {
				logger_.info("Insert new books item : " + booksEntry.getId());
				booksEntryDAOC.insert(booksEntry);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert new books : " + e.getMessage());
			return Response.serverError().build();
		}

		return Response.seeOther(URI.create(PATH_BOOKS)).build();
	}
	
	/**
	 * Delete books item
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	@POST
    @Timed
    @Path("/deleteentry")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response deleteItem(@FormParam(NameDef.ID) String id,
			@CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
		// 1. extract request
		// 2. verify and parse request
		// 3. verify parameters 
		if (id == null || id.length() == 0
				|| SessionDAOConnector.instance().verifySessionCookie(cookie)== false) {
			logger_.error("ERROR: invalid delete books entry request for \'" + id + "\'");
			return Response.serverError().build();
		}
		
		// 4. transaction
		try {
			booksEntryDAOC.deleteByID(id);
		} catch (Exception e) {
			e.printStackTrace();
			logger_.error("Error : failed to delete new books entry \'" + id + "\' : " + e.getMessage());
			return Response.serverError().build();
		}
		
		// 5. generate response
		logger_.info("Books entry \'" + id + "\' removed");
		return Response
				.seeOther(URI.create(PATH_BOOKS))
				.build();
	}
	
	private static Comparator<BooksEntry> booksEntryTimeComparator = new Comparator<BooksEntry>() {
		public int compare(BooksEntry a, BooksEntry b) {
			if (a.getEvent_date().before(b.getEvent_date())) {
				return -1;
			} else {
				return 1;
			}
		}
	};
	
	private List<BooksEntry> sortBooksByTime(List<BooksEntry> list) {
		if (list == null) {
			return null;
		}

		Collections.sort(list, booksEntryTimeComparator);
		
		return list;
	}
}
