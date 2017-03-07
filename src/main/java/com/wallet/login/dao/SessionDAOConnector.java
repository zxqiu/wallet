package com.wallet.login.dao;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.ws.rs.core.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wallet.login.core.Session;

import ConcurrentHelper.Scheduler;

public class SessionDAOConnector {
	private static Lock createLock_ = new ReentrantLock();
	private static final Logger logger_ = LoggerFactory.getLogger(SessionDAOConnector.class);
	
	private static SessionDAOConnector instance_ = null;
	private static SessionDAO sessionDAO;
	
	public static final String TABLE_NAME = SessionDAO.TABLE_NAME;
	public static final int CLEANUP_EXPIRED_INTERVAL = 24 * 60 * 60 * 1000; // 1 day in ms
	public static final int EXPIRED_INTERVAL = 31; // days

	public static SessionDAOConnector instance() throws Exception {
		if ( instance_ == null )
		{
			SessionDAOConnector.createLock_.lock();
			try {
				instance_ = new SessionDAOConnector();
			} catch (Exception e) {
				logger_.error("Failed to get SessionDAOConnector instance");
				throw new Exception("Failed to get SessionDAOConnector instance");
			} finally {
				SessionDAOConnector.createLock_.unlock();
			}
		}
		
		return instance_;
	}
	
	private SessionDAOConnector() throws Exception {
		if (sessionDAO == null) {
			throw new Exception("SessionDAOConnector not initialized");
		}
		this.createTable();
		Scheduler.instance().schedule(new cleanExpiredTask(), CLEANUP_EXPIRED_INTERVAL, CLEANUP_EXPIRED_INTERVAL);
	}
	
	public static void init(SessionDAO sessionDAO) {
		SessionDAOConnector.sessionDAO = sessionDAO;
	}
	
	private void createTable() throws Exception {
		try {
			sessionDAO.createTable();
		} catch (Exception e) {
			if (e.getMessage().contains("already exists")) {
				logger_.info("session table already exists : " + e.getMessage());
			} else {
				e.printStackTrace();
				logger_.error("Error (CRITICAL) : failed to create session table : " + e.getMessage());
				throw new Exception(e);
			}
		}
	}
	
	public void dropTable() throws Exception {
		sessionDAO.dropTable();
		instance_ = null;
	}
	
	public List<Session> getAll() throws Exception {
		return sessionDAO.findAll();
	}
	
	public List<Session> getByUserID(String user_id) throws Exception {
		return sessionDAO.findByUserID(user_id);
	}
	
	public Session getByAccessToken(String access_token) throws Exception {
		List<Session> ret = sessionDAO.findByAccessToken(access_token);
		
		if (ret.isEmpty()) {
			return null;
		}
		
		return ret.get(ret.size() - 1);
	}
	
	public Session getByUserIDAndAccessToken(String user_id, String access_token) throws Exception {
		List<Session> ret = sessionDAO.findByUserIDAndAccessToken(user_id, access_token);
		
		if (ret.isEmpty()) {
			return null;
		}
		
		return ret.get(ret.size() - 1);
	}
	
	public boolean verifySessionCookie(Cookie cookie) throws Exception {
		if (cookie == null) {
			return false;
		}
		
		String param[] = cookie.getValue().split(":");
		if (param.length < 2 || param[0].length() == 0 || param[1].length() == 0) {
			return false;
		}
		
		if (SessionDAOConnector.instance().getByUserIDAndAccessToken(param[0], param[1]) == null) {
			return false;
		}
		
		return true;
	}
	
	public void insert(Session session) throws Exception {
		try {
			sessionDAO.insert(session.getAccess_token(), session.getUser_id(), session.getCreate_date());
		} catch (Exception e) {
			if (e.getMessage().contains("Duplicate entry")) {
				logger_.info("Session " + session.getAccess_token() + " already exists for user : " + session.getUser_id());
			} else {
				logger_.error("Error insert session for " + session.getUser_id() + " failed : " + e.getMessage());
				e.printStackTrace();
				throw new Exception(e);
			}
		}
	}
	
	public void deleteByUserID(String user_id) throws Exception {
		sessionDAO.deleteByUserID(user_id);
	}
	
	public void deleteByAccessToken(String access_token) throws Exception {
		sessionDAO.deleteByAccessToken(access_token);
	}
	
	private class cleanExpiredTask extends TimerTask {
		@Override
		public void run() {
			try {
				Date today = new Date();
				List<Session> sessions = SessionDAOConnector.instance().getAll();
				for (Session session : sessions) {
					long diff = today.getTime() - session.getCreate_date().getTime();
					if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) >= EXPIRED_INTERVAL) {
						logger_.info("Deleting expired session " + session.getAccess_token() + " for user " + session.getUser_id());
						SessionDAOConnector.instance().deleteByAccessToken(session.getAccess_token());
					}
				}
			} catch (Exception e) {
				logger_.error("Failed to read all sessions for cleanup expired task");
				e.printStackTrace();
			}
		}
	}
	
	public static void test() throws Exception {
		//SessionDAOConnector.instance().dropTable();
		Session session = new Session("admin");
		
		SessionDAOConnector.instance().insert(session);
		Session session2 = SessionDAOConnector.instance().getByAccessToken(session.getAccess_token());
		logger_.info(session2.toString());
		
		SessionDAOConnector.instance().deleteByAccessToken(session.getAccess_token());
		session2 = SessionDAOConnector.instance().getByAccessToken(session.getAccess_token());
		if (session2 != null) {
			logger_.error("SessionDAOConnector test failure");
			throw new Exception("SessionDAOConnector test failure");
		}
		
		logger_.info("SessionDAOConnector test passed");
	}
}
