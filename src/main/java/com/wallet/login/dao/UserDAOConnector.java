package com.wallet.login.dao;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wallet.login.core.User;
import com.wallet.login.core.UserPriority;


public class UserDAOConnector {
	private static Lock createLock_ = new ReentrantLock();
	private static final Logger logger_ = LoggerFactory.getLogger(UserDAOConnector.class);
	private static UserDAOConnector instance_ = null;
	private static UserDAO userDAO;
	
	public static final String TABLE_NAME = UserDAO.TABLE_NAME;

	public static UserDAOConnector instance() throws Exception {
		if ( instance_ == null )
		{
			UserDAOConnector.createLock_.lock();
			try {
				instance_ = new UserDAOConnector();
			} catch (Exception e) {
				logger_.error("Failed to get UserDAOConnector instance");
				throw new Exception("Failed to get UserDAOConnector instance");
			} finally {
				UserDAOConnector.createLock_.unlock();
			}
		}
		
		return instance_;
	}
	
	private UserDAOConnector() throws Exception {
		if (userDAO == null) {
			throw new Exception("UserDAOConnector not initialized");
		}
		this.createTable();
	}
	
	public static void init(UserDAO userDAO) {
		UserDAOConnector.userDAO = userDAO;
	}
	
	private void createTable() throws Exception {
		try {
			userDAO.createUserTable();
		} catch (Exception e) {
			if (e.getMessage().contains("already exists")) {
				logger_.info("user table already exists : " + e.getMessage());
			} else {
				e.printStackTrace();
				logger_.error("Error (CRITICAL) : failed to create user table : " + e.getMessage());
				throw new Exception(e);
			}
		}
	}
	
	public void dropTable() throws Exception {
		userDAO.dropUserTable();
		instance_ = null;
	}
	
	public List<User> getAll() throws Exception {
		return userDAO.findAll();
	}
	
	public User getByID(String user_id) throws Exception {
		List<User> ret = userDAO.findByID(user_id);
		
		if (ret.isEmpty()) {
			logger_.warn("User not found : " + user_id);
			return null;
		}
		
		User user = ret.get(ret.size() - 1);
		return user;
	}
	
	public User getByIDAndPassword(String user_id, String password) throws Exception {
		List<User> ret = userDAO.findByUserIDAndPassword(user_id, password);
		if (ret.isEmpty()) {
			logger_.warn("User not found : " + user_id);
			return null;
		}
		
		return ret.get(ret.size() - 1);
	}
	
	public void insert(User user) throws Exception {
		try {
			userDAO.insert(user.getUser_id(), user.getPassword(), user.getName(), user.getPriority());
		} catch (Exception e) {
			if (e.getMessage().contains("Duplicate entry")) {
				logger_.info("User already exists : " + user.getUser_id());
			} else {
				logger_.error("Error insert user " + user.getUser_id() + " failed : " + e.getMessage());
				e.printStackTrace();
				throw new Exception(e);
			}
		}
	}
	
	public void update(User user) throws Exception {
		userDAO.update(user.getUser_id(), user.getPassword(), user.getName(), user.getPriority());
	}
	
	// TODO : delete user's data from other tables asynchronously
	public void delete(String user_id) throws Exception {
		userDAO.deleteByID(user_id);
	}
	
	public static void test() throws Exception {
		User user = new User("admin", "admin", "admin", UserPriority.ADMIN.name());

		logger_.info("UserDAOConnector test ...");
		
		logger_.info("1. insert");
		
		UserDAOConnector.instance().insert(user);
		User user2 = UserDAOConnector.instance().getByID(user.getUser_id());
		if (user2 == null) {
			logger_.error("UserDAOConnector test failure");
			throw new Exception("UserDAOConnector test failed");
		}
		logger_.info("inserted user id : " + user2.getUser_id());

		logger_.info("2. update");
		
		user.setName("gooduser");
		UserDAOConnector.instance().update(user);
		user2 = UserDAOConnector.instance().getByID("admin");
		logger_.info("updated name" + user2.getName());
		if (!user2.getName().equals("gooduser")) {
			logger_.error("UserDAOConnector test failure");
			throw new Exception("UserDAOConnector test failure");
		}
		
		logger_.info("UserDAOConnector test passed");
	}
}
