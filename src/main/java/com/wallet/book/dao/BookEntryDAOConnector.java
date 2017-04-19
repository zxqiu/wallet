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

	public List<BookEntry> getByBookGroupID(String book_group_id) throws Exception {
		return bookEntryDAO.findByBookGroupID(book_group_id);
	}

	public List<BookEntry> getByUserID(String user_id) throws Exception {
		return bookEntryDAO.findByUserID(user_id);
	}

	public List<BookEntry> getByUserIDAndBookGroupID(String user_id, String book_id) throws Exception {
		return bookEntryDAO.findByUserIDAndBookGroupID(user_id, book_id);
	}

	public List<BookEntry> getByUserIDAndGroupID(String user_id, String group_id) throws Exception {
		return bookEntryDAO.findByUserIDAndGroupID(user_id, group_id);
	}

	public void insert(BookEntry bookEntry) throws Exception {
		try {
			bookEntryDAO.insert(bookEntry.getId(), bookEntry.getUser_id(), bookEntry.getCreate_user_id()
					, bookEntry.getBook_group_id(), bookEntry.getGroup_id(), bookEntry.getCategory_group_id()
					, bookEntry.getEvent_date(), bookEntry.getAmount(), bookEntry.getData().toByteArray()
					, bookEntry.getEdit_time());
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
	
	public void updateByID(BookEntry bookEntry) throws Exception {
		bookEntryDAO.update(bookEntry.getId(), bookEntry.getBook_group_id(), bookEntry.getCategory_group_id()
				, bookEntry.getEvent_date(), bookEntry.getAmount(), bookEntry.getData().toByteArray()
				, bookEntry.getEdit_time());
	}

	public void updateByGroupID(BookEntry bookEntry) throws Exception {
		bookEntryDAO.updateByGroupID(bookEntry.getGroup_id(), bookEntry.getCategory_group_id()
				, bookEntry.getEvent_date(), bookEntry.getAmount(), bookEntry.getData().toByteArray()
				, bookEntry.getEdit_time());
	}

	public void deleteByID(String id) throws Exception {
		bookEntryDAO.deleteByID(id);
	}

	public void deleteByGroupID(String group_id) throws Exception {
		bookEntryDAO.deleteByGroupID(group_id);
	}

	public void deleteByUserID(String user_id) throws Exception {
		bookEntryDAO.deleteByUserID(user_id);;
	}

	public void deleteByUserIDAndBookGroupID(String user_id, String book_group_id) throws Exception {
		bookEntryDAO.deleteByBookGroupIDAndUserID(book_group_id, user_id);;
	}

	public static void test() throws Exception {
		BookEntry bookEntry = new BookEntry("admin", "admin", "adminbook", "admincategory", new Date()
				, (long)10, "note", "pictureTimeStamp", "photo");
		
		logger_.info("BookEntryDAOConnector test ...");
		
		logger_.info("1. insert");
		
		BookEntryDAOConnector.instance().insert(bookEntry);
		if (BookEntryDAOConnector.instance().getByID(bookEntry.getId()).isEmpty()) {
			logger_.error("Error BookEntryDAOConnector test failed");
			throw new Exception("BookEntryDAOConnector test failed");
		}
		
		logger_.info("2. update");
		
		bookEntry.setNote("nooooooote");
		BookEntryDAOConnector.instance().updateByID(bookEntry);
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
