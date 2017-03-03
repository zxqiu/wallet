package books.service;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import books.service.data.BooksInfo;
import books.service.data.BooksTable;
import books.service.data.CategoryInfo;
import books.service.data.CategoryTable;
import utils.ApiUtils;
import utils.NameDef;
import utils.TimeUtils;

@Path("/books")
public class BooksService {
	private static final Logger logger_ = LoggerFactory.getLogger(BooksService.class);
	
	
	/**
	 * Create a new item and insert to books
	 * @param user_id, event_date, category, amount, note, picture_url
	 * @return
	 */
	@POST
    @Timed
    @Path("/newitem")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Response newItem(@Valid booksPostJsonObj request) {
		if (request == null) {
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
			boolean exist = false;
			List<CategoryInfo> categoryList = CategoryTable.instance().getAllCategoriesForUser(request.user_id);
			for (CategoryInfo cat : categoryList) {
				if (cat.getName().equals(request.category)) {
					exist = true;
				}
			}
			
			if (!exist) {
				CategoryTable.instance().insertNewCategories(new CategoryInfo(request.user_id, request.category, ""));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}
		
		// 4.2 insert books
		// 4.2.1 update if id is received and exists. Otherwise insert new.
		boolean isItemExists = false;
		if (request.id.length() != 0) {
			
			try {
				List<BooksInfo> booksList = BooksTable.instance().getAllBooksForUser(request.user_id);
				for (BooksInfo item : booksList) {
					if (item.getId().equals(request.id)) {
						isItemExists = true;
						break;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
			}
		}
		
		if (!isItemExists) {
			request.id = request.user_id + currentTimeMS;
		}
		
		// 4.2.2 insert 
		BooksInfo books = new BooksInfo(request.id, currentTimeMS, request.user_id, request.category, request.event_date, request.amount, request.note, request.picture_url);
		try {
			if (isItemExists) {
				logger_.info("Update books item : " + books.toMap().toString());
				BooksTable.instance().updateBooksItem(books);
			} else {
				logger_.info("Insert new books item : " + books.toMap().toString());
				BooksTable.instance().insertNewBooksItem(books);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert new books : " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}
		
		return Response.status(200).entity(ApiUtils.buildJSONResponse(true, ApiUtils.SUCCESS)).build();
	}
	
	public static class booksPostJsonObj {
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
		@JsonProperty(NameDef.PICTURE_URL)
		String picture_url;
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
	                .add(NameDef.ID, id)
	                .add(NameDef.USER_ID, user_id)
	                .add(NameDef.EVENT_DATE, event_date)
	                .add(NameDef.CATEGORY, category)
	                .add(NameDef.AMOUNT, amount)
	                .add(NameDef.NOTE, note)
	                .add(NameDef.PICTURE_URL, picture_url)
	                .toString();
		}
	}
	
	/**
	 * Delete books item
	 * @param user_id
	 * @return
	 */
	@GET
    @Timed
    @Path("/deleteitem")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Response deleteItem(@QueryParam(NameDef.ID) String id) {
		// 1. extract request
		// 2. verify and parse request
		// 3. verify parameters 
		if (id == null || id.length() == 0) {
			logger_.error("ERROR: invalid remove books item request for \'" + id + "\'");
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		// 4. transaction
		try {
			BooksTable.instance().deleteBooksItem(id);;
		} catch (SQLException e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert new books item \'" + id + "\' : " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}
		
		// 5. generate response
		logger_.info("Books item \'" + id + "\' removed");
		return Response.status(200).entity(ApiUtils.buildJSONResponse(true, ApiUtils.SUCCESS)).build();
	}
	
	/**
	 * Get books of a user
	 * @param user_id
	 * @return
	 */
	@GET
    @Timed
    @Path("/getbooks")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Response getAllBooks(@QueryParam(NameDef.USER_ID) String user_id) {
		// 1. extract request
		// 2. verify and parse request
		// 3. verify parameters 
		if (user_id == null || user_id.length() == 0) {
			logger_.error("ERROR: invalid get books request for \'" + user_id + "\'");
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		// 4. transaction
		List<BooksInfo> books = new ArrayList<BooksInfo>();
		try {
			books = BooksTable.instance().getAllBooksForUser(user_id);
			books = sortBooksByTime(books);
		} catch (SQLException e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert new books item for " + user_id + " : " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}
		
		// 5. generate response
		JSONArray jsonArray = new JSONArray();
		Gson gson = new Gson();
		for (BooksInfo book : books) {
			try {
				jsonArray.put(new JSONObject(gson.toJson(book)));
			} catch (JSONException e) {
				e.printStackTrace();
				logger_.error("Error : failed to build response JSON array for " + user_id + " : " + e.getMessage());
				return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
			}
		}
		
		logger_.info("Response user " + user_id + "'s books: " + jsonArray.toString());
		return Response.status(200).entity(jsonArray.toString()).build();
	}
	
	private static Comparator<BooksInfo> booksTimeComparator = new Comparator<BooksInfo>() {
		public int compare(BooksInfo a, BooksInfo b) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
			try {
				if (sdf.parse(a.getEvent_date()).before(sdf.parse(b.getEvent_date()))) {
					return -1;
				} else {
					return 1;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return 0;
			}
		}
	};
	
	private List<BooksInfo> sortBooksByTime(List<BooksInfo> list) {
		Collections.sort(list, booksTimeComparator);
		
		return list;
	}
}