package com.wallet.book.dao;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.wallet.book.core.BookEntry;
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

	public List<BookEntry> getByID(String id) throws Exception {
		return bookEntryDAO.findByID(id);
	}

	public List<BookEntry> getByBookGroupID(String book_group_id) throws Exception {
		return bookEntryDAO.findByBookGroupID(book_group_id);
	}

	public List<BookEntry> getByUserID(String user_id) throws Exception {
		//return bookEntryDAO.findByUserID(user_id);
		return bookEntryCache.get(user_id);
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

			/*
			// update cache
			List<BookEntry> bookEntryList = bookEntryCache.get(bookEntry.getUser_id());
			if (!bookEntryList.contains(bookEntry.getId())) {
				bookEntryList.add(bookEntry);
				bookEntryCache.put(bookEntry.getUser_id(), bookEntryList);
			}
			*/
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

		/*
		// update cache
		List<BookEntry> bookEntryList = bookEntryCache.get(bookEntry.getUser_id());
		for (BookEntry entry : bookEntryList) {
		    if (entry.getId().equals(bookEntry.getId())) {
		    	entry.update(bookEntry.getBook_group_id(), bookEntry.getCategory_group_id(), bookEntry.getEvent_date()
						, bookEntry.getAmount(), bookEntry.getNote(), bookEntry.getPicture_id());
				bookEntryCache.put(bookEntry.getUser_id(), bookEntryList);
				break;
			}
		}
		*/
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
				, (long)10, "note", "photo");
		
		logger_.info("BookEntryConnector test ...");
		
		logger_.info("1. insert");
		
		BookEntryConnector.instance().insert(bookEntry);
		if (BookEntryConnector.instance().getByID(bookEntry.getId()).isEmpty()) {
			logger_.error("Error BookEntryConnector test failed");
			throw new Exception("BookEntryConnector test failed");
		}
		
		logger_.info("2. update");
		
		bookEntry.setNote("nooooooote");
		BookEntryConnector.instance().updateByID(bookEntry);
		if (BookEntryConnector.instance().getByID(bookEntry.getId()).isEmpty()) {
			logger_.error("Error BookEntryConnector test failed");
			throw new Exception("BookEntryConnector test failed");
		}
		logger_.info("updated note : " + BookEntryConnector.instance().getByID(bookEntry.getId()).get(0).getNote());
		
		logger_.info("3. delete");
		
		BookEntryConnector.instance().deleteByID(bookEntry.getId());
		if (!BookEntryConnector.instance().getByID(bookEntry.getId()).isEmpty()) {
			logger_.error("Error BookEntryConnector test failed");
			throw new Exception("BookEntryConnector test failed");
		}
		
		logger_.info("BookEntryConnector test passed");
	}
}
