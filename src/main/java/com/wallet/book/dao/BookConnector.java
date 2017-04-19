package com.wallet.book.dao;

import com.wallet.book.core.Book;
import com.wallet.book.core.BookLog;
import com.wallet.book.core.BookLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BookConnector {
	private static Lock createLock_ = new ReentrantLock();
	private static final Logger logger_ = LoggerFactory.getLogger(BookConnector.class);

	private static BookDAO bookDAO = null;
	private static BookConnector instance_ = null;

	public static final String TABLE_NAME = "book_table";

	public static BookConnector instance() throws Exception {
		if ( instance_ == null )
		{
			BookConnector.createLock_.lock();
			try
			{

				instance_ = new BookConnector();
			}
			finally
			{
				BookConnector.createLock_.unlock();
			}
		}

		return instance_;
	}

	private BookConnector() throws Exception {
		if (bookDAO == null) {
			throw new Exception("BookConnector not initialized");
		}
		this.createTable();
	}
	
	public static void init(BookDAO bookDAO) {
		BookConnector.bookDAO = bookDAO;
	}
	
	private void createTable() {
		bookDAO.createTable();
	}
	
	public void deleteTable() throws Exception {
		bookDAO.dropTable();;
		instance_ = null;
	}

	public List<Book> getByID(String id) throws Exception {
		return bookDAO.findByID(id);
	}

	public List<Book> getByGroupID(String group_id) throws Exception {
		return bookDAO.findByGroupID(group_id);
	}

	public List<Book> getByUserID(String user_id) throws Exception {
		return bookDAO.findByUserID(user_id);
	}

	public List<Book> getByCreateUserID(String create_user_id) throws Exception {
		return bookDAO.findByCreateUserID(create_user_id);
	}

	public List<Book> getByNameAndUserID(String name, String user_id) throws Exception {
		return bookDAO.findByNameAndUserID(name, user_id);
	}

	public List<Book> getByNameAndCreateUserID(String name, String create_user_id) throws Exception {
		return bookDAO.findByNameAndCreateUserID(name, create_user_id);
	}

	public void insert(Book book) throws Exception {
		try {
			bookDAO.insert(book.getId(), book.getUser_id(), book.getCreate_user_id(), book.getName()
					, book.getEdit_time(), book.getGroup_id(), book.getData().toByteArray());
			BookLogger.addBook(book.getUser_id(), book.getGroup_id(), BookLog.BOOK_LOG_NOTE.NONE.name());
		} catch (Exception e) {
			if (e.getMessage().contains("Duplicate entry")) {
				logger_.info("Book already exists : " + book.getId());
			} else {
				logger_.error("Error insert book " + book.getId() + " failed : " + e.getMessage());
				e.printStackTrace();
				throw new Exception(e);
			}
		}
	}
	
	public void update(Book book) throws Exception {
		bookDAO.update(book.getId(), book.getName(), new Date(), book.getData().toByteArray());
		BookLogger.updateBook(book.getUser_id(), book.getGroup_id(), BookLog.BOOK_LOG_NOTE.BY_ID.toString());
	}

	public void updateByGroupID(Book book) throws Exception {
		bookDAO.updateByGroupID(book.getGroup_id(), book.getName(), new Date(), book.getData().toByteArray());
		BookLogger.updateBook(book.getUser_id(), book.getGroup_id(), BookLog.BOOK_LOG_NOTE.BY_GROUP_ID.toString());
	}

	public void deleteByID(String user_id, String id) throws Exception {
		bookDAO.deleteByID(id);
		BookLogger.deleteBook(user_id, );
	}

	public void deleteByUserID(String user_id) throws Exception {
		bookDAO.deleteByUserID(user_id);;
	}

	public static void test() throws Exception {
		Book book = new Book("admin", "admin", "name", new Date()
				, "photo");

		logger_.info("BookConnector test ...");

		logger_.info("1. insert");

		BookConnector.instance().insert(book);
		if (BookConnector.instance().getByID(book.getId()).isEmpty()) {
			logger_.error("Error BookConnector test failed");
			throw new Exception("BookConnector test failed");
		}
		
		logger_.info("2. update");
		
		book.setPicture_id("nooooooote");
		BookConnector.instance().update(book);
		if (BookConnector.instance().getByNameAndCreateUserID(book.getName(), book.getCreate_user_id()).isEmpty()) {
			logger_.error("Error BookConnector test failed");
			throw new Exception("BookConnector test failed");
		}
		logger_.info("updated picture_id : " + BookConnector.instance().getByID(book.getId()).get(0).getPicture_id());

		logger_.info("3. delete");
		
		BookConnector.instance().deleteByID(book.getUser_id(), book.getId());
		if (!BookConnector.instance().getByID(book.getId()).isEmpty()) {
			logger_.error("Error BookConnector test failed");
			throw new Exception("BookConnector test failed");
		}
		
		logger_.info("BookConnector test passed");
	}
}
