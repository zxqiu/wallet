package com.wallet.book.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.wallet.book.core.BookLog;
import com.wallet.book.core.BookLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wallet.book.core.Category;

public class CategoryConnector {
	private static Lock createLock_ = new ReentrantLock();
	private static final Logger logger_ = LoggerFactory.getLogger(CategoryConnector.class);
	
	private static CategoryDAO categoryDAO = null;
	private static CategoryConnector instance_ = null;
	
	public static final String TABLE_NAME = "category_table";

	public static CategoryConnector instance() throws Exception {
		if ( instance_ == null )
		{
			CategoryConnector.createLock_.lock();
			try
			{
				
				instance_ = new CategoryConnector();
			}
			finally
			{
				CategoryConnector.createLock_.unlock();
			}
		}
		
		return instance_;
	}
	
	private CategoryConnector() throws Exception {
		this.createTable();
	}
	
	public static void init(CategoryDAO categoryDAO) {
		CategoryConnector.categoryDAO = categoryDAO;
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
	
	public void deleteTable() {
		categoryDAO.dropTable();
		instance_ = null;
	}
	
	public List<Category> getByUserID(String user_id) {
		return categoryDAO.findByUserID(user_id);
	}
	
	public List<Category> getByID(String id) {
		return categoryDAO.findByID(id);
	}

	public List<Category> getByGroupID(String group_id) {
		return categoryDAO.findByGroupID(group_id);
	}

	public List<Category> getByBookGroupID(String book_group_id) throws SQLException {
		return categoryDAO.findByBookGroupID(book_group_id);
	}

	public List<Category> getByUserIDAndGroupID(String user_id, String group_id) {
		return categoryDAO.findByUserIDAndGroupID(user_id, group_id);
	}

	public List<Category> getByUserIDAndBookGroupID(String user_id, String book_group_id) {
		return categoryDAO.findByUserIDAndBookGroupID(user_id, book_group_id);
	}

	public void insert(Category category) throws Exception {
		try {
			categoryDAO.insert(category.getId(), category.getGroup_id(), category.getUser_id()
					, category.getBook_group_id(), category.getName(), category.getData().toByteArray());
			BookLogger.addCateory(category.getUser_id(), category.getBook_group_id(), category.getId()
					, category.getGroup_id(), BookLog.BOOK_LOG_NOTE.NONE);
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
	
	public void updateByID(Category category) throws Exception {
		categoryDAO.updateByID(category.getId(), category.getData().toByteArray());
		BookLogger.updateCateory(category.getUser_id(), category.getBook_group_id(), category.getId()
				, category.getGroup_id(), BookLog.BOOK_LOG_NOTE.BY_ID);
	}

	public void updateByGroupID(Category category) throws Exception {
		categoryDAO.updateByGroupID(category.getGroup_id(), category.getData().toByteArray());
		BookLogger.updateCateory(category.getUser_id(), category.getBook_group_id(), ""
				, category.getGroup_id(), BookLog.BOOK_LOG_NOTE.BY_CATEGORY_GROUP_ID);
	}

	public void deleteByID(String user_id, String id) throws Exception {
		categoryDAO.deleteByID(id);
		BookLogger.deleteCateory(user_id, "", id, ""
				, BookLog.BOOK_LOG_NOTE.BY_ID);
	}

	public void deleteByGroupID(String user_id, String group_id) throws Exception {
		categoryDAO.deleteByGroupID(group_id);
		BookLogger.deleteCateory(user_id, "", "", group_id
				, BookLog.BOOK_LOG_NOTE.BY_CATEGORY_GROUP_ID);
    }

	public void deleteByUserID(String user_id) throws Exception {
		categoryDAO.deleteByUserID(user_id);
		BookLogger.deleteCateory(user_id, "", "", ""
				, BookLog.BOOK_LOG_NOTE.BY_USER_ID);
	}

	public void deleteByUserIDAndBookGroupID(String user_id, String book_group_id) throws Exception {
		categoryDAO.deleteByUserIDAndBookGroupID(user_id, book_group_id);
		BookLogger.deleteCateory(user_id, book_group_id, "", ""
				, BookLog.BOOK_LOG_NOTE.BY_USER_ID_AND_BOOK_GROUP_ID);
	}

	public static void test() throws Exception {
		Category category = new Category("admin", "test_group", "test_name", "#FFFFFF");
		
		logger_.info("CategoryConnector test ...");
		
		logger_.info("1. insert");
		
		CategoryConnector.instance().insert(category);
		if (CategoryConnector.instance().getByUserID("admin").isEmpty()) {
			logger_.error("CategoryConnector test failed!");
			throw new Exception("CategoryConnector test failed!");
		}
		
		logger_.info("2. update");
		
		category.setPicture_id("#FFFFFF");
		CategoryConnector.instance().updateByID(category);
		List<Category> category2 = CategoryConnector.instance().getByID(category.getId());
		if (category2.isEmpty()) {
			logger_.error("CategoryConnector test failed!");
			throw new Exception("CategoryConnector test failed!");
		}
		logger_.info("updated picture id : " + category2.get(category2.size() - 1).getPicture_id());
		
		logger_.info("3. delete");

		CategoryConnector.instance().deleteByID(category.getUser_id(), category.getId());
		if (!CategoryConnector.instance().getByID(category.getId()).isEmpty()) {
			logger_.error("CategoryConnector test failed!");
			throw new Exception("CategoryConnector test failed!");
		}

		logger_.info("CategoryConnector test passed");
	}
}
