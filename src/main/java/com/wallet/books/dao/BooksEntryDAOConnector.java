package com.wallet.books.dao;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wallet.books.core.BooksEntry;

public class BooksEntryDAOConnector {
	private static Lock createLock_ = new ReentrantLock();
	private static final Logger logger_ = LoggerFactory.getLogger(BooksEntryDAOConnector.class);
	
	private static BooksEntryDAO booksEntryDAO = null;
	private static BooksEntryDAOConnector instance_ = null;
	
	public static final String TABLE_NAME = "books_table";

	public static BooksEntryDAOConnector instance() throws Exception {
		if ( instance_ == null )
		{
			BooksEntryDAOConnector.createLock_.lock();
			try
			{
				
				instance_ = new BooksEntryDAOConnector();
			}
			finally
			{
				BooksEntryDAOConnector.createLock_.unlock();
			}
		}
		
		return instance_;
	}
	
	private BooksEntryDAOConnector() throws Exception {
		if (booksEntryDAO == null) {
			throw new Exception("BooksEntryDAOConnector not initialized");
		}
		this.createTable();
	}
	
	public static void init(BooksEntryDAO booksEntryDAO) {
		BooksEntryDAOConnector.booksEntryDAO = booksEntryDAO;
	}
	
	private void createTable() {
		booksEntryDAO.createTable();
	}
	
	public void deleteTable() throws Exception {
		booksEntryDAO.dropTable();;
		instance_ = null;
	}

	public List<BooksEntry> getByID(String id) throws Exception {
		return booksEntryDAO.findByID(id);
	}
	
	public List<BooksEntry> getByUserID(String user_id) throws Exception {
		return booksEntryDAO.findByUserID(user_id);
	}
	
	public void insert(BooksEntry booksEntry) throws Exception {
		try {
			booksEntryDAO.insert(booksEntry.getId(), booksEntry.getUser_id(), booksEntry.getCategory(), booksEntry.getEvent_date()
				, booksEntry.getAmount(), booksEntry.getNote(), booksEntry.getPhoto(), booksEntry.getEdit_time());
		} catch (Exception e) {
			if (e.getMessage().contains("Duplicate entry")) {
				logger_.info("Books entry already exists : " + booksEntry.getId());
			} else {
				logger_.error("Error insert books entry " + booksEntry.getId() + " failed : " + e.getMessage());
				e.printStackTrace();
				throw new Exception(e);
			}
		}
	}
	
	public void update(BooksEntry booksEntry) throws Exception {
		booksEntryDAO.update(booksEntry.getId(), booksEntry.getUser_id(), booksEntry.getCategory(), booksEntry.getEvent_date()
				, booksEntry.getAmount(), booksEntry.getNote(), booksEntry.getPhoto(), booksEntry.getEdit_time());
	}
	
	public void deleteByID(String id) throws Exception {
		booksEntryDAO.deleteByID(id);
	}
	
	public void deleteByUserID(String user_id) throws Exception {
		booksEntryDAO.deleteByUserID(user_id);;
	}
	
	public static void test() throws Exception {
		BooksEntry booksEntry = new BooksEntry("admin232323", "admin", "asdfasf", new Date(), (long)10, "note", "photo");
		
		logger_.info("BooksEntryDAOConnector test ...");
		
		logger_.info("1. insert");
		
		BooksEntryDAOConnector.instance().insert(booksEntry);
		if (BooksEntryDAOConnector.instance().getByID(booksEntry.getId()).isEmpty()) {
			logger_.error("Error BooksEntryDAOConnector test failed");
			throw new Exception("BooksEntryDAOConnector test failed");
		}
		
		logger_.info("2. update");
		
		booksEntry.setNote("nooooooote");
		BooksEntryDAOConnector.instance().update(booksEntry);
		if (BooksEntryDAOConnector.instance().getByID(booksEntry.getId()).isEmpty()) {
			logger_.error("Error BooksEntryDAOConnector test failed");
			throw new Exception("BooksEntryDAOConnector test failed");
		}
		logger_.info("updated note : " + BooksEntryDAOConnector.instance().getByID(booksEntry.getId()).get(0).getNote());
		
		logger_.info("3. delete");
		
		BooksEntryDAOConnector.instance().deleteByID(booksEntry.getId());
		if (!BooksEntryDAOConnector.instance().getByID(booksEntry.getId()).isEmpty()) {
			logger_.error("Error BooksEntryDAOConnector test failed");
			throw new Exception("BooksEntryDAOConnector test failed");
		}
		
		logger_.info("BooksEntryDAOConnector test passed");
	}
}
