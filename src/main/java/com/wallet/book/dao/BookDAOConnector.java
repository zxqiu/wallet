package com.wallet.book.dao;

import com.wallet.book.core.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BookDAOConnector {
	private static Lock createLock_ = new ReentrantLock();
	private static final Logger logger_ = LoggerFactory.getLogger(BookDAOConnector.class);

	private static BookDAO bookDAO = null;
	private static BookDAOConnector instance_ = null;

	public static final String TABLE_NAME = "book_table";

	public static BookDAOConnector instance() throws Exception {
		if ( instance_ == null )
		{
			BookDAOConnector.createLock_.lock();
			try
			{

				instance_ = new BookDAOConnector();
			}
			finally
			{
				BookDAOConnector.createLock_.unlock();
			}
		}

		return instance_;
	}

	private BookDAOConnector() throws Exception {
		if (bookDAO == null) {
			throw new Exception("BookDAOConnector not initialized");
		}
		this.createTable();
	}
	
	public static void init(BookDAO bookDAO) {
		BookDAOConnector.bookDAO = bookDAO;
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

	public List<String> getGroupIDByID(String id) {
		return bookDAO.findGroupIDByID(id);
	}

	public void insert(Book book) throws Exception {
		try {
			bookDAO.insert(book.getId(), book.getUser_id(), book.getCreate_user_id(), book.getName(), book.getCreate_time()
					, book.getEdit_time(), book.getPicture_id(), book.getGroup_id(), book.getData());
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
		bookDAO.update(book.getId(), book.getName(), new Date(), book.getPicture_id(), book.getData());
	}

	public void updateByGroupID(Book book) throws Exception {
		bookDAO.updateByGroupID(book.getGroup_id(), book.getName(), new Date(), book.getPicture_id(), book.getData());
	}

	public void deleteByID(String id) throws Exception {
		bookDAO.deleteByID(id);
	}
	
	public void deleteByUserID(String user_id) throws Exception {
		bookDAO.deleteByUserID(user_id);;
	}
	
	public static void test() throws Exception {
		Book book = new Book("admin", "admin", "name", new Date()
				, "photo");
		
		logger_.info("BookDAOConnector test ...");
		
		logger_.info("1. insert");
		
		BookDAOConnector.instance().insert(book);
		if (BookDAOConnector.instance().getByID(book.getId()).isEmpty()) {
			logger_.error("Error BookDAOConnector test failed");
			throw new Exception("BookDAOConnector test failed");
		}
		
		logger_.info("2. update");
		
		book.setPicture_id("nooooooote");
		BookDAOConnector.instance().update(book);
		if (BookDAOConnector.instance().getByNameAndCreateUserID(book.getName(), book.getCreate_user_id()).isEmpty()) {
			logger_.error("Error BookDAOConnector test failed");
			throw new Exception("BookDAOConnector test failed");
		}
		logger_.info("updated note : " + BookDAOConnector.instance().getByID(book.getId()).get(0).getPicture_id());
		
		logger_.info("3. delete");
		
		BookDAOConnector.instance().deleteByID(book.getId());
		if (!BookDAOConnector.instance().getByID(book.getId()).isEmpty()) {
			logger_.error("Error BookDAOConnector test failed");
			throw new Exception("BookDAOConnector test failed");
		}
		
		logger_.info("BookDAOConnector test passed");
	}
}
