package com.wallet.book.dao;

import com.wallet.book.core.BookLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BookLogConnector {
	private static Lock createLock_ = new ReentrantLock();
	private static final Logger logger_ = LoggerFactory.getLogger(BookLogConnector.class);

	private static BookLogDAO bookLogDAO = null;
	private static BookLogConnector instance_ = null;

	public static final String TABLE_NAME = "book_table";

	public static BookLogConnector instance() throws Exception {
		if ( instance_ == null )
		{
			BookLogConnector.createLock_.lock();
			try
			{

				instance_ = new BookLogConnector();
			}
			finally
			{
				BookLogConnector.createLock_.unlock();
			}
		}

		return instance_;
	}

	private BookLogConnector() throws Exception {
		if (bookLogDAO == null) {
			throw new Exception("BookConnector not initialized");
		}
		this.createTable();
	}
	
	public static void init(BookLogDAO bookLogDAO) {
		BookLogConnector.bookLogDAO = bookLogDAO;
	}
	
	private void createTable() {
		bookLogDAO.createTable();
	}
	
	public void deleteTable() throws Exception {
		bookLogDAO.dropTable();;
		instance_ = null;
	}

	public List<BookLog> ID(String id) throws Exception {
		return bookLogDAO.findByID(id);
	}

	public List<BookLog> getByID(String id) throws Exception {
		return bookLogDAO.findByID(id);
	}

	public List<BookLog> getByUserID(String user_id) throws Exception {
		return bookLogDAO.findByUserID(user_id);
	}

	public List<BookLog> getByBookGroupID(String book_group_id) throws Exception {
		return bookLogDAO.findByBookGroupID(book_group_id);
	}

	public List<BookLog> getByBookEntryGroupID(String book_entry_group_id) throws Exception {
		return bookLogDAO.findByBookEntryGroupID(book_entry_group_id);
	}

	public List<BookLog> getByCategoryGroupID(String category_group_id) throws Exception {
		return bookLogDAO.findByCategoryGroupID(category_group_id);
	}

	public List<BookLog> getByBookGroupIDAndUserID(String book_group_id, String user_id) throws Exception {
		return bookLogDAO.findByBookGroupIDAndUserID(book_group_id, user_id);
	}

	public void insert(BookLog bookLog) throws Exception {
		try {
			bookLogDAO.insert(bookLog.getId(), bookLog.getCreate_time(), bookLog.getUser_id(), bookLog.getBook_id()
					, bookLog.getBook_group_id(), bookLog.getBook_entry_id(), bookLog.getBook_entry_group_id()
					, bookLog.getCategory_id(), bookLog.getCategory_group_id(), bookLog.getOperation().name()
					, bookLog.getType().name(), bookLog.getData().toByteArray());
		} catch (Exception e) {
			if (e.getMessage().contains("Duplicate entry")) {
				logger_.info("BookLog already exists : " + bookLog.getId());
			} else {
				logger_.error("Error insert book log " + bookLog.getId() + " failed : " + e.getMessage());
				e.printStackTrace();
				throw new Exception(e);
			}
		}
	}
	
	public void deleteByID(String id) throws Exception {
		bookLogDAO.deleteByID(id);
	}
	
	public static void test() throws Exception {
		BookLog bookLog = new BookLog("admin", "test_book", "test_book", "test_entry"
				, "test_entry", "test_category", "test_category"
				, BookLog.BOOK_LOG_OPERATION.ADD, BookLog.BOOK_LOG_TYPE.BOOK, BookLog.BOOK_LOG_NOTE.BY_ID);
		
		logger_.info("BookLogConnector test ...");
		
		logger_.info("1. insert");
		
		BookLogConnector.instance().insert(bookLog);
		if (BookLogConnector.instance().getByID(bookLog.getId()).isEmpty()) {
			logger_.error("Error BookLogConnector test failed");
			throw new Exception("BookLogConnector test failed");
		}
		
		logger_.info("2. delete");
		
		BookLogConnector.instance().deleteByID(bookLog.getId());
		if (!BookLogConnector.instance().getByID(bookLog.getId()).isEmpty()) {
			logger_.error("Error BookLogConnector test failed");
			throw new Exception("BookLogConnector test failed");
		}
		
		logger_.info("BookLogConnector test passed");
	}
}
