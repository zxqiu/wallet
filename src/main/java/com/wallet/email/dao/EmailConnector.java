package com.wallet.email.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EmailConnector {
	private static Lock createLock_ = new ReentrantLock();
	private static final Logger logger_ = LoggerFactory.getLogger(EmailConnector.class);

	private static EmailDAO emailDAO = null;
	private static EmailConnector instance_ = null;

	public static final String TABLE_NAME = "category_table";

	public static EmailConnector instance() throws Exception {
		if ( instance_ == null )
		{
			EmailConnector.createLock_.lock();
			try
			{

				instance_ = new EmailConnector();
			}
			finally
			{
				EmailConnector.createLock_.unlock();
			}
		}

		return instance_;
	}

	private EmailConnector() throws Exception {
		this.createTable();
	}
	
	public static void init(EmailDAO emailDAO) {
		EmailConnector.emailDAO = emailDAO;
	}
	
	private void createTable() throws Exception {
		try {
			emailDAO.createTable();
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
		emailDAO.dropTable();
		instance_ = null;
	}
	
	public List<Email> getByUserID(String user_id) {
		return emailDAO.findByUserID(user_id);
	}
	
	public List<Email> getByID(String id) {
		return emailDAO.findByID(id);
	}

	public List<Email> getByGroupID(String group_id) {
		return emailDAO.findByGroupID(group_id);
	}

	public List<Email> getByBookGroupID(String book_group_id) throws SQLException {
		return emailDAO.findByBookGroupID(book_group_id);
	}

	public List<Email> getByUserIDAndGroupID(String user_id, String group_id) {
		return emailDAO.findByUserIDAndGroupID(user_id, group_id);
	}

	public List<Email> getByUserIDAndBookGroupID(String user_id, String book_group_id) {
		return emailDAO.findByUserIDAndBookGroupID(user_id, book_group_id);
	}

	public void insert(Email category) throws Exception {
		try {
			emailDAO.insert(category.getId(), category.getGroup_id(), category.getUser_id()
					, category.getBook_group_id(), category.getName(), category.getData().toByteArray());
			BookLogger.addCateory(category.getUser_id(), category.getBook_group_id(), category.getId()
					, category.getGroup_id(), BookLog.BOOK_LOG_NOTE.NONE);
		} catch (Exception e) {
			if (e.getMessage().contains("Duplicate entry")) {
				logger_.info("Email " + category.getName() + " already exists for user : " + category.getUser_id());
			} else {
				logger_.error("Error insert category " + category.getName() + " for " + category.getUser_id() + " failed : " + e.getMessage());
				e.printStackTrace();
				throw new Exception(e);
			}
		}
	}
	
	public void updateByID(Email category) throws Exception {
		emailDAO.updateByID(category.getId(), category.getData().toByteArray());
		BookLogger.updateCateory(category.getUser_id(), category.getBook_group_id(), category.getId()
				, category.getGroup_id(), BookLog.BOOK_LOG_NOTE.BY_ID);
	}

	public void updateByGroupID(Email category) throws Exception {
		emailDAO.updateByGroupID(category.getGroup_id(), category.getData().toByteArray());
		BookLogger.updateCateory(category.getUser_id(), category.getBook_group_id(), ""
				, category.getGroup_id(), BookLog.BOOK_LOG_NOTE.BY_CATEGORY_GROUP_ID);
	}

	public void deleteByID(String user_id, String id) throws Exception {
		emailDAO.deleteByID(id);
		BookLogger.deleteCateory(user_id, "", id, ""
				, BookLog.BOOK_LOG_NOTE.BY_ID);
	}

	public void deleteByGroupID(String user_id, String group_id) throws Exception {
		emailDAO.deleteByGroupID(group_id);
		BookLogger.deleteCateory(user_id, "", "", group_id
				, BookLog.BOOK_LOG_NOTE.BY_CATEGORY_GROUP_ID);
    }

	public void deleteByUserID(String user_id) throws Exception {
		emailDAO.deleteByUserID(user_id);
		BookLogger.deleteCateory(user_id, "", "", ""
				, BookLog.BOOK_LOG_NOTE.BY_USER_ID);
	}

	public void deleteByUserIDAndBookGroupID(String user_id, String book_group_id) throws Exception {
		emailDAO.deleteByUserIDAndBookGroupID(user_id, book_group_id);
		BookLogger.deleteCateory(user_id, book_group_id, "", ""
				, BookLog.BOOK_LOG_NOTE.BY_USER_ID_AND_BOOK_GROUP_ID);
	}

	public static void test() throws Exception {
		Email category = new Email("admin", "test_group", "test_name", "#FFFFFF");
		
		logger_.info("CategoryConnector test ...");
		
		logger_.info("1. insert");
		
		EmailConnector.instance().insert(category);
		if (EmailConnector.instance().getByUserID("admin").isEmpty()) {
			logger_.error("CategoryConnector test failed!");
			throw new Exception("CategoryConnector test failed!");
		}
		
		logger_.info("2. update");
		
		category.setPicture_id("#FFFFFF");
		EmailConnector.instance().updateByID(category);
		List<Email> category2 = EmailConnector.instance().getByID(category.getId());
		if (category2.isEmpty()) {
			logger_.error("CategoryConnector test failed!");
			throw new Exception("CategoryConnector test failed!");
		}
		logger_.info("updated picture id : " + category2.get(category2.size() - 1).getPicture_id());
		
		logger_.info("3. delete");

		EmailConnector.instance().deleteByID(category.getUser_id(), category.getId());
		if (!EmailConnector.instance().getByID(category.getId()).isEmpty()) {
			logger_.error("CategoryConnector test failed!");
			throw new Exception("CategoryConnector test failed!");
		}

		logger_.info("CategoryConnector test passed");
	}
}
