package com.wallet.book.dao;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.wallet.book.core.BookEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookEntryDAOConnector {
	private static Lock createLock_ = new ReentrantLock();
	private static final Logger logger_ = LoggerFactory.getLogger(BookEntryDAOConnector.class);
	
	private static BookEntryDAO bookEntryDAO = null;
	private static BookEntryDAOConnector instance_ = null;
	
	public static final String TABLE_NAME = "book_table";

	public static BookEntryDAOConnector instance() throws Exception {
		if ( instance_ == null )
		{
			BookEntryDAOConnector.createLock_.lock();
			try
			{
				
				instance_ = new BookEntryDAOConnector();
			}
			finally
			{
				BookEntryDAOConnector.createLock_.unlock();
			}
		}
		
		return instance_;
	}
	
	private BookEntryDAOConnector() throws Exception {
		if (bookEntryDAO == null) {
			throw new Exception("BookEntryDAOConnector not initialized");
		}
		this.createTable();
	}
	
	public static void init(BookEntryDAO bookEntryDAO) {
		BookEntryDAOConnector.bookEntryDAO = bookEntryDAO;
	}
	
	private void createTable() {
		bookEntryDAO.createTable();
	}
	
	public void deleteTable() throws Exception {
		bookEntryDAO.dropTable();;
		instance_ = null;
	}

	public List<BookEntry> getByID(String id) throws Exception {
		return bookEntryDAO.findByID(id);
	}

	public List<BookEntry> getByBookID(String book_id) throws Exception {
		return bookEntryDAO.findByBookID(book_id);
	}

	public List<BookEntry> getByUserID(String user_id) throws Exception {
		return bookEntryDAO.findByUserID(user_id);
	}

	public List<BookEntry> getByUserIDAndBookID(String user_id, String book_id) throws Exception {
		return bookEntryDAO.findByUserIDAndBookID(user_id, book_id);
	}

	public void insert(BookEntry bookEntry) throws Exception {
		try {
			bookEntryDAO.insert(bookEntry.getId(), bookEntry.getUser_id(), bookEntry.getCreate_user_id()
					, bookEntry.getBook_id(), bookEntry.getCategory(), bookEntry.getEvent_date()
					, bookEntry.getAmount(), bookEntry.getNote(), bookEntry.getPhoto(), bookEntry.getData()
					, bookEntry.getEdit_time(),bookEntry.getCreate_time());
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
	
	public void update(BookEntry bookEntry) throws Exception {
		bookEntryDAO.update(bookEntry.getId(), bookEntry.getUser_id(), bookEntry.getBook_id()
				, bookEntry.getCategory(), bookEntry.getEvent_date(), bookEntry.getAmount(), bookEntry.getNote()
				, bookEntry.getPhoto(), bookEntry.getData(), bookEntry.getEdit_time());
	}
	
	public void deleteByID(String id) throws Exception {
		bookEntryDAO.deleteByID(id);
	}
	
	public void deleteByUserID(String user_id) throws Exception {
		bookEntryDAO.deleteByUserID(user_id);;
	}
	
	public static void test() throws Exception {
		BookEntry bookEntry = new BookEntry("admin", "admin", "adminbook","asdfasf", new Date()
				, (long)10, "note", "photo");
		
		logger_.info("BookEntryDAOConnector test ...");
		
		logger_.info("1. insert");
		
		BookEntryDAOConnector.instance().insert(bookEntry);
		if (BookEntryDAOConnector.instance().getByID(bookEntry.getId()).isEmpty()) {
			logger_.error("Error BookEntryDAOConnector test failed");
			throw new Exception("BookEntryDAOConnector test failed");
		}
		
		logger_.info("2. update");
		
		bookEntry.setNote("nooooooote");
		BookEntryDAOConnector.instance().update(bookEntry);
		if (BookEntryDAOConnector.instance().getByID(bookEntry.getId()).isEmpty()) {
			logger_.error("Error BookEntryDAOConnector test failed");
			throw new Exception("BookEntryDAOConnector test failed");
		}
		logger_.info("updated note : " + BookEntryDAOConnector.instance().getByID(bookEntry.getId()).get(0).getNote());
		
		logger_.info("3. delete");
		
		BookEntryDAOConnector.instance().deleteByID(bookEntry.getId());
		if (!BookEntryDAOConnector.instance().getByID(bookEntry.getId()).isEmpty()) {
			logger_.error("Error BookEntryDAOConnector test failed");
			throw new Exception("BookEntryDAOConnector test failed");
		}
		
		logger_.info("BookEntryDAOConnector test passed");
	}
}
