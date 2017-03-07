package com.wallet.books.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wallet.books.core.Category;

public class CategoryDAOConnector {
	private static Lock createLock_ = new ReentrantLock();
	private static final Logger logger_ = LoggerFactory.getLogger(CategoryDAOConnector.class);
	
	private static CategoryDAO categoryDAO = null;
	private static CategoryDAOConnector instance_ = null;
	
	public static final String TABLE_NAME = "category_table";

	public static CategoryDAOConnector instance() throws Exception {
		if ( instance_ == null )
		{
			CategoryDAOConnector.createLock_.lock();
			try
			{
				
				instance_ = new CategoryDAOConnector();
			}
			finally
			{
				CategoryDAOConnector.createLock_.unlock();
			}
		}
		
		return instance_;
	}
	
	private CategoryDAOConnector() throws Exception {
		this.createTable();
	}
	
	public static void init(CategoryDAO categoryDAO) {
		CategoryDAOConnector.categoryDAO = categoryDAO;
	}
	
	private void createTable() throws Exception {
		try {
			categoryDAO.createTable();
		} catch (Exception e) {
			if (e.getMessage().contains("already exists")) {
				logger_.info("category table already exists : " + e.getMessage());
			} else {
				e.printStackTrace();
				logger_.error("Error (CRITICAL) : failed to create category table : " + e.getMessage());
				throw new Exception(e);
			}
		}
	}
	
	public void deleteTable() throws SQLException {
		categoryDAO.dropTable();
		instance_ = null;
	}
	
	public List<Category> getByUserID(String user_id) throws SQLException {
		return categoryDAO.findByUserID(user_id);
	}
	
	public List<Category> getByID(String id) throws SQLException {
		return categoryDAO.findByID(id);
	}
	
	public void insert(Category category) throws Exception {
		try {
			categoryDAO.insert(category.getId(), category.getUser_id(), category.getName(), category.getPicture_id());
		} catch (Exception e) {
			if (e.getMessage().contains("Duplicate entry")) {
				logger_.info("Category " + category.getName() + " already exists for user : " + category.getUser_id());
			} else {
				logger_.error("Error insert category " + category.getName() + " for " + category.getUser_id() + " failed : " + e.getMessage());
				e.printStackTrace();
				throw new Exception(e);
			}
		}
	}
	
	public void update(Category category) throws SQLException {
		categoryDAO.update(category.getId(), category.getUser_id(), category.getName(), category.getPicture_id());
	}
	
	public void deleteByID(String id) {
		categoryDAO.deleteByID(id);
	}
	
	public void deleteByUserID(String user_id) {
		categoryDAO.deleteByUserID(user_id);
	}
	
	public static void test() throws Exception {
		Category category = new Category("admin", "good", "");
		
		CategoryDAOConnector.instance().insert(category);
		for (Category iter : CategoryDAOConnector.instance().getByUserID("admin")) {
			logger_.info(iter.getName());
		}
		
		category.setName("gooooooooooooooooood");
		CategoryDAOConnector.instance().update(category);
		for (Category iter : CategoryDAOConnector.instance().getByUserID("webuser")) {
			logger_.info(iter.getName());
		}
		
		CategoryDAOConnector.instance().deleteByID(category.getId());
		if (!CategoryDAOConnector.instance().getByID(category.getId()).isEmpty()) {
			logger_.error("CategoryDAOConnector test failed!");
			throw new Exception("CategoryDAOConnector test failed!");
		}
		
		logger_.info("CategoryDAOConnector test passed");
	}
}
