package com.wallet.book.resource;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.wallet.book.core.Book;
import com.wallet.book.core.BookEntry;
import com.wallet.book.core.syncHelper;
import com.wallet.book.dao.BookDAOConnector;
import com.wallet.login.core.User;
import com.wallet.login.dao.UserDAOConnector;
import com.wallet.login.resource.SessionResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.wallet.book.core.Category;
import com.wallet.book.dao.BookEntryDAOConnector;
import com.wallet.book.dao.CategoryDAOConnector;
import com.wallet.login.dao.SessionDAOConnector;
import com.wallet.utils.misc.ApiUtils;
import com.wallet.utils.misc.Dict;

@Path("/books")
public class BookEntryResource {
	private static final Logger logger_ = LoggerFactory.getLogger(BookEntryResource.class);
	private static final int bookEntrysEachLine = 4;

	private BookDAOConnector bookDAOC = null;
	private BookEntryDAOConnector bookEntryDAOC = null;
	private CategoryDAOConnector categoryDAOC = null;
	private UserDAOConnector userDAOC = null;

	public BookEntryResource() throws Exception {
	    this.bookDAOC = BookDAOConnector.instance();
		this.bookEntryDAOC = BookEntryDAOConnector.instance();
		this.categoryDAOC = CategoryDAOConnector.instance();
		this.userDAOC = UserDAOConnector.instance();
	}

	@GET
	@Timed
	@Path("/allbookentry")
	@Produces(value = MediaType.APPLICATION_JSON)
	public List<BookEntry> getAll() throws Exception {
		return bookEntryDAOC.getByUserID("admin");
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
		String user_id = ApiUtils.getUserIDFromCookie(cookie);
		if (SessionDAOConnector.instance().verifySessionCookie(cookie)== false || user_id == null) {
			return Response.seeOther(URI.create(SessionResource.PATH_RESTORE_SESSION)).build();
		}

		User user = userDAOC.getByID(user_id);
		List<Book> bookList = bookDAOC.getByUserID(user_id);
		HashMap<String, Book> bookMap = new HashMap<>();
		List<BookEntry> bookEntryList = sortBookByEventTime(bookEntryDAOC.getByUserID(user_id));
		List<Category> categoryList = categoryDAOC.getByUserID(user_id);
		HashMap<String, String> colorMap = new HashMap<>();

		for (Category category : categoryList) {
			colorMap.put(category.getName(), category.getPicture_id());
		}

		for (Book book : bookList) {
			bookMap.put(book.getGroup_id(), book);
		}

		return Response.ok().entity(views.bookEntryList.template(bookEntryList, bookMap, categoryList, colorMap, bookEntrysEachLine, user)).build();
	}

	public static final String PATH_INSERT_ENTRY_VIEW = "/books/entry";
	/**
	 * @return bookEntry view. This is for showing and edit entry details.
	 */
	@GET
	@Timed
	@Path("/entry/{id}")
	@Produces(value = MediaType.TEXT_HTML)
	public Response bookEntryView(@PathParam(Dict.ID) String id,
								   @CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
	    String user_id = ApiUtils.getUserIDFromCookie(cookie);
		if (SessionDAOConnector.instance().verifySessionCookie(cookie)== false || user_id == null) {
			return Response.seeOther(URI.create(SessionResource.PATH_RESTORE_SESSION)).build();
		}

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		String date = sdf.format(new Date());
		BookEntry bookEntry = null;
		if (!id.equals("0")) {
			List<BookEntry> bookEntryList = bookEntryDAOC.getByID(id);
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
	 * @param id, event_date, amount, category, note, photo, cookie
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
								@FormParam(Dict.CATEGORY) String category,
								@FormParam(Dict.NOTE) String note,
								@FormParam(Dict.PHOTO) String photo,
								@CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
		String user_id = ApiUtils.getUserIDFromCookie(cookie);
		if (SessionDAOConnector.instance().verifySessionCookie(cookie)== false || user_id == null) {
			return Response.seeOther(URI.create(SessionResource.PATH_RESTORE_SESSION)).build();
		}

		logger_.info("insertentry request : id:" + id + ", book_id:" + book_id + ", event_date:" + event_date
				+ ", amount_double:" + amount_double + ", category:" + category + ", note:" + note + ", photo:" + photo + ".");

		long amount = (long)(amount_double * 100);
		Book book = null;

		// 1. extract request
		// 2. verify and parse request
		// 3. verify parameters 
		if (category.length() == 0 || event_date.length() == 0 || book_id.length() == 0) {
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

		try {
			if (categoryDAOC.getByID(user_id + category).isEmpty()) {
				Category tmp = new Category(user_id, book.getGroup_id(), category, "#FFFFFF");
				categoryDAOC.insert(tmp);
				syncHelper.syncCategory(tmp, syncHelper.SYNC_Action.ADD);
			}
		} catch (Exception e1) {
			logger_.error("Error failed to get category or insert new category when insert book entry : " + category);
			e1.printStackTrace();
			return Response.serverError().build();
		}

		// 4.2 insert book
		// 4.2.1 update if id is received and exists. Otherwise insert new.
		BookEntry bookEntry = null;
		if (id.length() != 0) {
			try {
				List<BookEntry> list = bookEntryDAOC.getByID(id);
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
		try {
			if (bookEntry != null) {
				logger_.info("Update book item : " + bookEntry.getId());
				if (bookEntry.getBook_group_id().equals(book.getGroup_id())) {
					bookEntry.update(book.getGroup_id(), category, sdf.parse(event_date), amount, note, photo);
					bookEntryDAOC.update(bookEntry);
					// Update if book id is not changed.
					syncHelper.syncBookEntry(bookEntry, syncHelper.SYNC_Action.UPDATE);
				} else {
					// Delete if book id is changed.
                    syncHelper.syncBookEntry(bookEntry, syncHelper.SYNC_Action.DELETE);
					bookEntry.update(book.getGroup_id(), category, sdf.parse(event_date), amount, note, photo);
					bookEntryDAOC.insert(bookEntry);
				}
			} else {
				bookEntry = new BookEntry(user_id, user_id, book.getGroup_id(), category
						, sdf.parse(event_date), amount, note, photo);
				logger_.info("Insert new book item : " + bookEntry.getId());
				//bookEntryDAOC.insert(bookEntry); // remove this to avoid duplication
				syncHelper.syncBookEntry(bookEntry, syncHelper.SYNC_Action.ADD);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger_.error("Error : failed to insert or update book entry : " + e.getMessage());
			return Response.serverError().build();
		}

		return Response.seeOther(URI.create(PATH_BOOKS)).build();
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
		    List<BookEntry> bookEntryList = bookEntryDAOC.getByID(id);
		    if (!bookEntryList.isEmpty()) {
		    	BookEntry bookEntry = bookEntryList.get(bookEntryList.size() - 1);
				logger_.info("Delete book entry : " + bookEntry.getId());
				bookEntryDAOC.deleteByID(id);
				syncHelper.syncBookEntry(bookEntry, syncHelper.SYNC_Action.DELETE);
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
	
	private static Comparator<BookEntry> bookEntryTimeComparator = new Comparator<BookEntry>() {
		public int compare(BookEntry a, BookEntry b) {
			if (a.getEvent_date().before(b.getEvent_date())) {
				return -1;
			} else {
				return 1;
			}
		}
	};
	
	private List<BookEntry> sortBookByEventTime(List<BookEntry> list) {
		if (list == null) {
			return null;
		}

		Collections.sort(list, bookEntryTimeComparator);
		
		return list;
	}
}
