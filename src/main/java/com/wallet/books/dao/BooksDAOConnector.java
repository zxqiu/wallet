package com.wallet.books.dao;

import com.wallet.books.core.Books;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BooksDAOConnector {
	private static Lock createLock_ = new ReentrantLock();
	private static final Logger logger_ = LoggerFactory.getLogger(BooksDAOConnector.class);

	private static BooksDAO booksDAO = null;
	private static BooksDAOConnector instance_ = null;

	public static final String TABLE_NAME = "books_table";

	public static BooksDAOConnector instance() throws Exception {
		if ( instance_ == null )
		{
			BooksDAOConnector.createLock_.lock();
			try
			{

				instance_ = new BooksDAOConnector();
			}
			finally
			{
				BooksDAOConnector.createLock_.unlock();
			}
		}

		return instance_;
	}

	private BooksDAOConnector() throws Exception {
		if (booksDAO == null) {
			throw new Exception("BooksDAOConnector not initialized");
		}
		this.createTable();
	}
	
	public static void init(BooksDAO booksDAO) {
		BooksDAOConnector.booksDAO = booksDAO;
	}
	
	private void createTable() {
		booksDAO.createTable();
	}
	
	public void deleteTable() throws Exception {
		booksDAO.dropTable();;
		instance_ = null;
	}

	public List<Books> getByID(String id) throws Exception {
		return booksDAO.findByID(id);
	}
	
	public List<Books> getByUserID(String user_id) throws Exception {
		return booksDAO.findByUserID(user_id);
	}

	public List<Books> getByCreateUserID(String create_user_id) throws Exception {
		return booksDAO.findByCreateUserID(create_user_id);
	}

	public List<Books> getByNameAndCreateUserID(String name, String create_user_id) throws Exception {
		return booksDAO.findByNameAndCreateUserID(name, create_user_id);
	}

	public void insert(Books books) throws Exception {
		try {
			booksDAO.insert(books.getId(), books.getUser_id(), books.getCreate_user_id(), books.getName(), books.getCreate_time()
					, books.getEdit_time(), books.getPicture_id(), books.getData());
		} catch (Exception e) {
			if (e.getMessage().contains("Duplicate entry")) {
				logger_.info("Books already exists : " + books.getId());
			} else {
				logger_.error("Error insert books " + books.getId() + " failed : " + e.getMessage());
				e.printStackTrace();
				throw new Exception(e);
			}
		}
	}
	
	public void update(Books books) throws Exception {
		booksDAO.update(books.getId(), books.getName(), books.getEdit_time(), books.getPicture_id(), books.getData());
	}
	
	public void deleteByID(String id) throws Exception {
		booksDAO.deleteByID(id);
	}
	
	public void deleteByUserID(String user_id) throws Exception {
		booksDAO.deleteByUserID(user_id);;
	}
	
	public static void test() throws Exception {
		Books books = new Books("admin", "admin", "name", new Date()
				, "photo", "");
		
		logger_.info("BooksDAOConnector test ...");
		
		logger_.info("1. insert");
		
		BooksDAOConnector.instance().insert(books);
		if (BooksDAOConnector.instance().getByID(books.getId()).isEmpty()) {
			logger_.error("Error BooksDAOConnector test failed");
			throw new Exception("BooksDAOConnector test failed");
		}
		
		logger_.info("2. update");
		
		books.setPicture_id("nooooooote");
		BooksDAOConnector.instance().update(books);
		if (BooksDAOConnector.instance().getByNameAndCreateUserID(books.getName(), books.getCreate_user_id()).isEmpty()) {
			logger_.error("Error BooksDAOConnector test failed");
			throw new Exception("BooksDAOConnector test failed");
		}
		logger_.info("updated note : " + BooksDAOConnector.instance().getByID(books.getId()).get(0).getPicture_id());
		
		logger_.info("3. delete");
		
		BooksDAOConnector.instance().deleteByID(books.getId());
		if (!BooksDAOConnector.instance().getByID(books.getId()).isEmpty()) {
			logger_.error("Error BooksDAOConnector test failed");
			throw new Exception("BooksDAOConnector test failed");
		}
		
		logger_.info("BooksDAOConnector test passed");
	}
}
