package com.wallet.book.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.wallet.book.core.BookEntry;
import com.wallet.book.core.BookLog;
import com.wallet.book.core.BookLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookEntryConnector {
	private static Lock createLock_ = new ReentrantLock();
	private static final Logger logger_ = LoggerFactory.getLogger(BookEntryConnector.class);
	
	private static BookEntryDAO bookEntryDAO = null;
	private static BookEntryCache bookEntryCache = null;
	private static BookEntryConnector instance_ = null;

	public static final String TABLE_NAME = "book_table";

	public static BookEntryConnector instance() throws Exception {
		if ( instance_ == null )
		{
			BookEntryConnector.createLock_.lock();
			try
			{
				instance_ = new BookEntryConnector();
			}
			finally
			{
				BookEntryConnector.createLock_.unlock();
			}
		}
		
		return instance_;
	}
	
	private BookEntryConnector() throws Exception {
		if (bookEntryDAO == null) {
			throw new Exception("BookEntryConnector not initialized");
		}
		this.createTable();
	}
	
	public static void init(BookEntryDAO bookEntryDAO) {
		BookEntryConnector.bookEntryDAO = bookEntryDAO;
		bookEntryCache = BookEntryCache.instance();
	}
	
	private void createTable() {
		bookEntryDAO.createTable();
	}
	
	public void deleteTable() throws Exception {
		bookEntryDAO.dropTable();
		instance_ = null;
	}

	public List<BookEntry> getByUserIDAndID(String user_id, String id) throws Exception {
		return bookEntryCache.getByUserIDAndID(user_id, id);
	}

	public List<BookEntry> getByUserID(String user_id) throws Exception {
		return bookEntryCache.getByUserID(user_id);
	}

	public List<BookEntry> getByUserIDAndMonth(String user_id, int year, int month) throws Exception {
		List<BookEntry> bookEntries = bookEntryCache.getByUserID(user_id);
		List<BookEntry> bookEntriesFiltered = new LinkedList<>();
		Calendar cal = Calendar.getInstance();

		for (BookEntry entry : bookEntries) {
		    cal.setTime(entry.getEvent_date());
			if (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == month - 1) {
				bookEntriesFiltered.add(entry);
			}
		}

		return bookEntriesFiltered;
	}

	public void insert(BookEntry bookEntry) throws Exception {
		try {
			bookEntryDAO.insert(bookEntry.getId(), bookEntry.getUser_id(), bookEntry.getCreate_user_id()
					, bookEntry.getBook_group_id(), bookEntry.getGroup_id(), bookEntry.getCategory_group_id()
					, bookEntry.getEvent_date(), bookEntry.getAmount(), bookEntry.getData().toByteArray()
					, bookEntry.getEdit_time(), bookEntry.getType().getVal(), bookEntry.getStart_date()
					, bookEntry.getEnd_date());

			// update cache
            bookEntryCache.insert(bookEntry);
			BookLogger.addBookEntry(bookEntry.getUser_id(), bookEntry.getBook_group_id(), bookEntry.getId()
					, bookEntry.getGroup_id(), bookEntry.getCategory_group_id(), BookLog.BOOK_LOG_NOTE.NONE);
		} catch (Exception e) {
			if (e.getMessage().contains("Duplicate entry")) {
				logger_.info("Book entry already exists : " + bookEntry.getId());
			} else {
				logger_.error("Error insert book entry " + bookEntry.getId() + " failed : " + e.getMessage());
				e.printStackTrace();
				throw new Exception(e);
			}
		}
	}
	
	public void updateByUserIDAndID(BookEntry bookEntry) throws Exception {
		bookEntryDAO.update(bookEntry.getId(), bookEntry.getBook_group_id(), bookEntry.getCategory_group_id()
				, bookEntry.getEvent_date(), bookEntry.getAmount(), bookEntry.getData().toByteArray()
				, bookEntry.getEdit_time());

		// update cache
        bookEntryCache.updateByUserAndID(bookEntry);
        BookLogger.updateBookEntry(bookEntry.getUser_id(), bookEntry.getBook_group_id(), bookEntry.getId()
				, bookEntry.getGroup_id(), bookEntry.getCategory_group_id(), BookLog.BOOK_LOG_NOTE.BY_USER_ID_AND_ID);
	}

	public void deleteByUserIDAndID(String user_id, String id) throws Exception {
		bookEntryDAO.deleteByID(id);

		// update cache
        bookEntryCache.deleteByUserIDAndID(user_id, id);
        BookLogger.deleteBookEntry(user_id, "", id, "", ""
				, BookLog.BOOK_LOG_NOTE.BY_USER_ID_AND_ID);
	}

	public void deleteByUserID(String user_id) throws Exception {
		bookEntryDAO.deleteByUserID(user_id);;

		// update cache
		bookEntryCache.deleteByUserID(user_id);
		BookLogger.deleteBookEntry(user_id, "", "", "", ""
				, BookLog.BOOK_LOG_NOTE.BY_USER_ID);
	}

	/********************* Group Operations ************************/
	public List<BookEntry> getByBookGroupID(String book_group_id) throws Exception {
		return bookEntryDAO.findByBookGroupID(book_group_id);
	}

	public List<BookEntry> getByUserIDAndBookGroupID(String user_id, String book_id) throws Exception {
		return bookEntryDAO.findByUserIDAndBookGroupID(user_id, book_id);
	}

	public List<BookEntry> getByUserIDAndGroupID(String user_id, String group_id) throws Exception {
		return bookEntryDAO.findByUserIDAndGroupID(user_id, group_id);
	}

	public void deleteByGroupID(String group_id) throws Exception {
		bookEntryDAO.deleteByGroupID(group_id);
		BookLogger.deleteBookEntry("", "", "", group_id, ""
				, BookLog.BOOK_LOG_NOTE.BY_BOOK_ENTRY_GROUP_ID);
	}

	public void deleteByUserIDAndBookGroupID(String user_id, String book_group_id) throws Exception {
		bookEntryDAO.deleteByBookGroupIDAndUserID(book_group_id, user_id);

		// updated cache
		bookEntryCache.deleteByUserIDAndBookGroupID(user_id, book_group_id);
		BookLogger.deleteBookEntry(user_id, book_group_id, "", "", ""
				, BookLog.BOOK_LOG_NOTE.BY_USER_ID_AND_BOOK_GROUP_ID);
	}

	public void updateByGroupID(BookEntry bookEntry) throws Exception {
		bookEntryDAO.updateByGroupID(bookEntry.getGroup_id(), bookEntry.getCategory_group_id()
				, bookEntry.getEvent_date(), bookEntry.getAmount(), bookEntry.getData().toByteArray()
				, bookEntry.getEdit_time());
		BookLogger.updateBookEntry(bookEntry.getUser_id(), bookEntry.getBook_group_id(), bookEntry.getId()
				, bookEntry.getGroup_id(), bookEntry.getCategory_group_id(), BookLog.BOOK_LOG_NOTE.BY_BOOK_ENTRY_GROUP_ID);
	}


	public static void test() throws Exception {
		Date now = new Date();
		BookEntry bookEntry = new BookEntry("admin", "admin", "adminbook", "admincategory", now
				, (long)10, "note", "photo", BookEntry.Type.NORMAL, now, now);
		
		logger_.info("BookEntryConnector test ...");
		
		logger_.info("1. insert");
		
		BookEntryConnector.instance().insert(bookEntry);
		if (BookEntryConnector.instance().getByUserIDAndID(bookEntry.getUser_id(), bookEntry.getId()).isEmpty()
				|| bookEntryDAO.findByID(bookEntry.getId()).isEmpty()) {
			logger_.error("Error BookEntryConnector test failed");
			throw new Exception("BookEntryConnector test failed");
		}
		
		logger_.info("2. update");
		
		bookEntry.setNote("nooooooote");
		BookEntryConnector.instance().updateByUserIDAndID(bookEntry);
		if (BookEntryConnector.instance().getByUserIDAndID(bookEntry.getUser_id(), bookEntry.getId()).isEmpty()
				|| bookEntryDAO.findByID(bookEntry.getId()).isEmpty()) {
			logger_.error("Error BookEntryConnector test failed");
			throw new Exception("BookEntryConnector test failed");
		}
		logger_.info("updated note : " + BookEntryConnector.instance()
				.getByUserIDAndID(bookEntry.getUser_id(), bookEntry.getId()).get(0).getNote());
		
		logger_.info("3. delete");
		
		BookEntryConnector.instance().deleteByUserIDAndID(bookEntry.getUser_id(), bookEntry.getId());
		if (!BookEntryConnector.instance().getByUserIDAndID(bookEntry.getUser_id(), bookEntry.getId()).isEmpty()
				|| !bookEntryDAO.findByID(bookEntry.getId()).isEmpty()) {
			logger_.error("Error BookEntryConnector test failed");
			throw new Exception("BookEntryConnector test failed");
		}
		
		logger_.info("BookEntryConnector test passed");
	}
}
