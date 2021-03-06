package com.wallet.book.resource;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.wallet.book.core.Book;
import com.wallet.book.core.BookEntry;
import com.wallet.book.core.syncHelper;
import com.wallet.book.dao.BookConnector;
import com.wallet.book.dao.BookEntryConnector;
import com.wallet.book.dao.CategoryConnector;
import com.wallet.login.core.User;
import com.wallet.login.dao.UserDAOConnector;
import com.wallet.login.resource.SessionResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.wallet.book.core.Category;
import com.wallet.login.dao.SessionDAOConnector;
import com.wallet.utils.misc.ApiUtils;
import com.wallet.utils.misc.Dict;

@Path("/books")
public class BookEntryResource {
	private static final Logger logger_ = LoggerFactory.getLogger(BookEntryResource.class);
	private static final int bookEntrysEachLine = 4;

	private BookConnector bookDAOC = null;
	private BookEntryConnector bookEntryConnector = null;
	private CategoryConnector categoryDAOC = null;
	private UserDAOConnector userDAOC = null;

	public BookEntryResource() throws Exception {
		this.bookDAOC = BookConnector.instance();
		this.bookEntryConnector = BookEntryConnector.instance();
		this.categoryDAOC = CategoryConnector.instance();
		this.userDAOC = UserDAOConnector.instance();
	}

	@GET
	@Timed
	@Path("/allbookentry")
	@Produces(value = MediaType.APPLICATION_JSON)
	public List<BookEntry> getAll() throws Exception {
		return bookEntryConnector.getByUserID("admin");
	}

	public static final String PATH_BOOKS = "/books";

	/**
	 * @return bookEntryList view. This is the main view of path /books
	 */

	@GET
	@Timed
	@Path("/")
	@Produces(value = MediaType.TEXT_HTML)
	public Response bookEntryListView(@CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
		return doBookEntryListByMonthView(Calendar.getInstance().get(Calendar.YEAR),
				Calendar.getInstance().get(Calendar.MONTH) + 1, cookie);
	}

	public static final String PATH_BOOKS_BY_MONTH = "/books";

	@GET
	@Timed
	@Path("/{year}/{month}")
	@Produces(value = MediaType.TEXT_HTML)
	public Response bookEntryListByMonthView(@PathParam(Dict.YEAR) int year, @PathParam(Dict.MONTH) int month,
											 @CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
		return doBookEntryListByMonthView(year, month, cookie);
	}

	private Response doBookEntryListByMonthView(int year, int month, Cookie cookie) throws Exception {
		String user_id = ApiUtils.getUserIDFromCookie(cookie);
		if (SessionDAOConnector.instance().verifySessionCookie(cookie)== false || user_id == null) {
			return Response.seeOther(URI.create(SessionResource.PATH_RESTORE_SESSION)).build();
		}

		if (month < 1) {
			month = 12;
		} else if (month > 12) {
			month = 1;
		}

		User user = userDAOC.getByID(user_id);
		List<Book> bookList = bookDAOC.getByUserID(user_id);
		HashMap<String, Book> bookMap = new HashMap<>();
		List<BookEntry> bookEntryList = sortBookByEventTime(bookEntryConnector.getByUserIDAndMonth(user_id, year, month));
		List<Category> categoryList = categoryDAOC.getByUserID(user_id);
		HashMap<String, Category> categoryMap = new HashMap<>();

		for (Category category : categoryList) {
			categoryMap.put(category.getGroup_id(), category);
		}

		for (Book book : bookList) {
			bookMap.put(book.getGroup_id(), book);
		}

		return Response.ok().entity(views.bookEntryList.template(bookEntryList, bookMap, categoryList, categoryMap,
				bookEntrysEachLine, user, year, month)).build();
	}

	public static final String PATH_INSERT_ENTRY_VIEW = "/books/entry";
	/**
	 * @return bookEntry view. This is for showing and edit entry details.
	 */
	@GET
	@Timed
	@Path("/entry/{id}")
	@Produces(value = MediaType.TEXT_HTML)
	public Response bookEntryView(@PathParam(Dict.ID) String id, @QueryParam(Dict.YEAR) Integer year,
								  @QueryParam(Dict.MONTH) Integer month,
								  @CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
	    String user_id = ApiUtils.getUserIDFromCookie(cookie);
		if (SessionDAOConnector.instance().verifySessionCookie(cookie)== false || user_id == null) {
			return Response.seeOther(URI.create(SessionResource.PATH_RESTORE_SESSION)).build();
		}

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

		if (year != null) {
			cal.set(Calendar.YEAR, year);
		}

		if (month != null) {
			cal.set(Calendar.MONTH, month - 1);
		}

		String date = sdf.format(cal.getTime());
		BookEntry bookEntry = null;
		if (!id.equals("0")) {
			List<BookEntry> bookEntryList = bookEntryConnector.getByUserIDAndID(user_id, id);
			if (!bookEntryList.isEmpty()) {
				bookEntry = bookEntryList.get(bookEntryList.size() - 1);
				date = sdf.format(bookEntry.getEvent_date());
			}
		}

		return Response.ok()
				.entity(views.bookEntry.template(bookEntry
                        , bookDAOC.getByUserID(user_id)
						, categoryDAOC.getByUserID(user_id)
						, date
						, user_id
				))
				.build();
	}

	/**
	 * Create a insert new or update existing book entry.
	 * @param id, event_date, amount, category, note, picture_id, cookie
	 * @return
	 * @throws Exception 
	 */
	@POST
    @Timed
    @Path("/insertentry")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response insertEntry(@FormParam(Dict.ID) String id,
								@FormParam(Dict.BOOK_ID) String book_id,
								@FormParam(Dict.EVENT_DATE) String event_date,
								@FormParam(Dict.AMOUNT) double amount_double,
								@FormParam(Dict.CATEGORY_NAME) String category_name,
								@FormParam(Dict.CATEGORY_ID) String category_id,
								@FormParam(Dict.NOTE) String note,
								@FormParam(Dict.PICTURE_ID) String picture_id,
								@CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
		String user_id = ApiUtils.getUserIDFromCookie(cookie);
		if (SessionDAOConnector.instance().verifySessionCookie(cookie)== false || user_id == null) {
			return Response.seeOther(URI.create(SessionResource.PATH_RESTORE_SESSION)).build();
		}

		logger_.info("insertentry request : id:" + id + ", book_id:" + book_id + ", event_date:" + event_date
				+ ", amount_double:" + amount_double + ", category_name:" + category_name + ", category_id:"
				+ category_id + ", note:" + note + ", picture_id:" + picture_id + ".");

		long amount = (long)(amount_double * 100);
		Book book = null;

		// 1. extract request
		// 2. verify and parse request
		// 3. verify parameters 
		if (category_name.length() == 0 || event_date.length() == 0 || book_id.length() == 0) {
			logger_.error("ERROR: invalid new item request: " + id);
			return Response.serverError().build();
		}
		
		// 4. transaction
		// 4.1 insert default book if not exists
		try {
			if (book_id != null) {
				List<Book> bookList = bookDAOC.getByID(book_id);
				if (!bookList.isEmpty()) {
					book = bookList.get(bookList.size() - 1);
				}
			}

			if (book_id == null || book == null) {
				book = new Book(user_id, user_id, "default", new Date(), "#FFFFFF");
				bookDAOC.insert(book);
			}
		} catch (Exception e) {
			logger_.error("Error failed to get book or insert new book when insert book entry : " + book_id);
			e.printStackTrace();
			return Response.serverError().build();
		}

		List<Category> categoryList = null;
		Category category = null;
		try {
			if (category_id != null && category_id.length() > 1) {
				categoryList = categoryDAOC.getByID(category_id);
				if (categoryList != null && !categoryList.isEmpty()) {
					category = categoryList.get(categoryList.size() - 1);
				}
			}
			if (category == null || !category.getName().equals(category_name)) {
			    // Prioritize category_name over category_id
				category = new Category(user_id, book.getGroup_id(), category_name, "#FFFFFF");
				//categoryDAOC.insert(category);
				syncHelper.syncCategory(category, syncHelper.SYNC_ACTION.ADD);
			}
		} catch (Exception e1) {
			logger_.error("Error failed to get category or insert new category when insert book entry : " + category_id);
			e1.printStackTrace();
			return Response.serverError().build();
		}

		// 4.2 insert book entry
		// 4.2.1 update if id is received and exists. Otherwise insert new.
		BookEntry bookEntry = null;
		if (id.length() != 0) {
			try {
				List<BookEntry> list = bookEntryConnector.getByUserIDAndID(user_id, id);
				if (!list.isEmpty()) {
					bookEntry = list.get(list.size() - 1);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger_.error("Error find out if item already exists : " + e.getMessage());
				return Response.serverError().build();
			}
		}
		
		// 4.2.2 insert
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Date date = null;

		try {
			date = sdf.parse(event_date);
		} catch (ParseException pe) {
			logger_.error("Error : failed to parse event date : " + pe.getMessage());
			pe.printStackTrace();
			return Response.serverError().build();
		}

		try {
			if (bookEntry != null) {
				logger_.info("Update book item : " + bookEntry.getId());
				if (bookEntry.getBook_group_id().equals(book.getGroup_id())) {
					bookEntry.update(book.getGroup_id(), category.getGroup_id(), date, amount, note
							, picture_id);
					bookEntryConnector.updateByUserIDAndID(bookEntry);
					// Update if book id is not changed.
					syncHelper.syncBookEntry(bookEntry, syncHelper.SYNC_ACTION.UPDATE);
				} else {
					// Re-insert if book id is changed.
                    syncHelper.syncBookEntry(bookEntry, syncHelper.SYNC_ACTION.DELETE);
					bookEntry.update(book.getGroup_id(), category.getGroup_id(), date, amount, note
							, picture_id);
					//bookEntryConnector.insert(bookEntry);
					syncHelper.syncBookEntry(bookEntry, syncHelper.SYNC_ACTION.ADD);
				}
			} else {
				Date eventDate = sdf.parse(event_date);
				bookEntry = new BookEntry(user_id, user_id, book.getGroup_id(), category.getGroup_id()
						, eventDate, amount, note, picture_id, BookEntry.Type.NORMAL, eventDate, eventDate);
				logger_.info("Insert new book item : " + bookEntry.getId());
				//bookEntryConnector.insert(bookEntry); // remove this to avoid duplication
				syncHelper.syncBookEntry(bookEntry, syncHelper.SYNC_ACTION.ADD);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert or update book entry : " + e.getMessage());
			return Response.serverError().build();
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return Response.seeOther(URI.create(PATH_BOOKS + "/" + cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1))).build();
	}
	
	/**
	 * Delete book entry
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	@POST
    @Timed
    @Path("/deleteentry")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response deleteEntry(@FormParam(Dict.ID) String id,
			@CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
		String user_id = ApiUtils.getUserIDFromCookie(cookie);
		if (SessionDAOConnector.instance().verifySessionCookie(cookie)== false || user_id == null) {
			return Response.seeOther(URI.create(SessionResource.PATH_RESTORE_SESSION)).build();
		}
		// 1. extract request
		// 2. verify and parse request
		// 3. verify parameters 
		if (id == null || id.length() == 0) {
			logger_.error("ERROR: invalid delete book entry request for \'" + id + "\'");
			return Response.serverError().build();
		}
		
		// 4. transaction
		try {
		    List<BookEntry> bookEntryList = bookEntryConnector.getByUserIDAndID(user_id, id);
		    if (!bookEntryList.isEmpty()) {
		    	BookEntry bookEntry = bookEntryList.get(bookEntryList.size() - 1);
				logger_.info("Delete book entry : " + bookEntry.getId());
				bookEntryConnector.deleteByUserIDAndID(user_id, id);
				syncHelper.syncBookEntry(bookEntry, syncHelper.SYNC_ACTION.DELETE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger_.error("Error : failed to delete new book entry \'" + id + "\' : " + e.getMessage());
			return Response.serverError().build();
		}
		
		// 5. generate response
		logger_.info("Book entry \'" + id + "\' removed");
		return Response.seeOther(URI.create(PATH_BOOKS)).build();
	}

	private List<BookEntry> sortBookByEventTime(List<BookEntry> list) {
		if (list == null) {
			return null;
		}

		Collections.sort(list, bookEntryTimeComparator);

		return list;
	}

    private static Comparator<BookEntry> bookEntryTimeComparator = new Comparator<BookEntry>() {
        public int compare(BookEntry a, BookEntry b) {
            return -1 * a.getEvent_date().compareTo(b.getEvent_date());
        }
    };
}
