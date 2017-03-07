package com.wallet.books.resource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.wallet.books.core.BooksEntry;
import com.wallet.books.core.Category;
import com.wallet.books.dao.BooksEntryDAOConnector;
import com.wallet.books.dao.CategoryDAOConnector;
import com.wallet.login.dao.SessionDAOConnector;

import utils.ApiUtils;
import utils.NameDef;
import utils.TimeUtils;

@Path("/books")
public class BooksEntryResource {
	private static final Logger logger_ = LoggerFactory.getLogger(BooksEntryResource.class);
	
	private BooksEntryDAOConnector booksEntryDAOC = null;
	private CategoryDAOConnector categoryDAOC = null;
	
	public BooksEntryResource() throws Exception {
		this.booksEntryDAOC = BooksEntryDAOConnector.instance();
		this.categoryDAOC = CategoryDAOConnector.instance();
	}
	
	/**
	 * Create a new item and insert to books
	 * @param user_id, event_date, category, amount, note, picture_url
	 * @return
	 * @throws Exception 
	 */
	@POST
    @Timed
    @Path("/insertentry")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Response insertItem(@Valid entryPostRequest request,
			@CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
		if (request == null || SessionDAOConnector.instance().verifySessionCookie(cookie)== false) {
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		long currentTimeMS = TimeUtils.getUniqueTimeStampInMS();
		logger_.info(request.toString());
		
		// 1. extract request
		// 2. verify and parse request
		// 3. verify parameters 
		if (request.amount == null || request.category.length() == 0 || request.event_date.length() == 0) {
			logger_.error("ERROR: invalid new item request: " + request.toString());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		// 4. transaction
		// 4.1 insert new category
		try {
			if (categoryDAOC.getByID(request.user_id + request.category).isEmpty()) {
				categoryDAOC.insert(new Category(request.user_id, request.category, ""));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}
		
		// 4.2 insert books
		// 4.2.1 update if id is received and exists. Otherwise insert new.
		boolean exist = false;
		if (request.id.length() != 0) {
			try {
				exist = !booksEntryDAOC.getByID(request.id).isEmpty();
			} catch (Exception e) {
				e.printStackTrace();
				logger_.error("Error find out if item already exists : " + e.getMessage());
				return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
			}
		}
		
		if (!exist) {
			request.id = request.user_id + currentTimeMS;
		}
		
		// 4.2.2 insert 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		BooksEntry booksEntry = new BooksEntry(request.id, request.user_id, request.category, sdf.parse(request.event_date), request.amount, request.note, request.photo);
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
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}
		
		return Response.status(200).entity(ApiUtils.buildJSONResponse(true, ApiUtils.SUCCESS)).build();
	}
	
	public static class entryPostRequest {
		@JsonProperty(NameDef.ID)
		String id;
		@JsonProperty(NameDef.USER_ID)
		String user_id;
		@JsonProperty(NameDef.EVENT_DATE)
		String event_date;
		@JsonProperty(NameDef.CATEGORY)
		String category;
		@JsonProperty(NameDef.AMOUNT)
		Long amount;
		@JsonProperty(NameDef.NOTE)
		String note;
		@JsonProperty(NameDef.PHOTO)
		String photo;
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
	                .add(NameDef.ID, id)
	                .add(NameDef.USER_ID, user_id)
	                .add(NameDef.EVENT_DATE, event_date)
	                .add(NameDef.CATEGORY, category)
	                .add(NameDef.AMOUNT, amount)
	                .add(NameDef.NOTE, note)
	                .add(NameDef.PHOTO, photo)
	                .toString();
		}
	}
	
	/**
	 * Delete books item
	 * @param user_id
	 * @return
	 * @throws Exception 
	 */
	@GET
    @Timed
    @Path("/deleteentry")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Response deleteItem(@QueryParam(NameDef.ID) String id,
			@CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
		// 1. extract request
		// 2. verify and parse request
		// 3. verify parameters 
		if (id == null || id.length() == 0
				|| SessionDAOConnector.instance().verifySessionCookie(cookie)== false) {
			logger_.error("ERROR: invalid delete books entry request for \'" + id + "\'");
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		// 4. transaction
		try {
			booksEntryDAOC.deleteByID(id);
		} catch (Exception e) {
			e.printStackTrace();
			logger_.error("Error : failed to delete new books entry \'" + id + "\' : " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}
		
		// 5. generate response
		logger_.info("Books entry \'" + id + "\' removed");
		return Response.status(200).entity(ApiUtils.buildJSONResponse(true, ApiUtils.SUCCESS)).build();
	}
	
	/**
	 * Get books of a user
	 * @param user_id
	 * @return
	 * @throws Exception 
	 */
	@GET
    @Timed
    @Path("/getentries")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Response getAllBooks(@QueryParam(NameDef.USER_ID) String user_id,
			@CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
		// 1. extract request
		// 2. verify and parse request
		// 3. verify parameters 
		if (user_id == null || user_id.length() == 0
				|| SessionDAOConnector.instance().verifySessionCookie(cookie)== false) {
			logger_.error("ERROR: invalid get books request for \'" + user_id + "\'");
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		// 4. transaction
		List<BooksEntry> booksEntryList = new ArrayList<BooksEntry>();
		try {
			booksEntryList = booksEntryDAOC.getByUserID(user_id);
			booksEntryList = sortBooksByTime(booksEntryList);
		} catch (Exception e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert new books item for " + user_id + " : " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}
		
		// 5. generate response
		JSONArray jsonArray = new JSONArray();
		Gson gson = new Gson();
		for (BooksEntry entry : booksEntryList) {
			try {
				jsonArray.put(new JSONObject(gson.toJson(entry)));
			} catch (JSONException e) {
				e.printStackTrace();
				logger_.error("Error : failed to build response JSON array for " + user_id + " : " + e.getMessage());
				return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
			}
		}
		
		logger_.info("Response user " + user_id + "'s books: " + jsonArray.toString());
		return Response.status(200).entity(jsonArray.toString()).build();
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
		Collections.sort(list, booksEntryTimeComparator);
		
		return list;
	}
}
