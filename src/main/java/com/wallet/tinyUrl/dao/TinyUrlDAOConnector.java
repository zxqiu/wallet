package com.wallet.tinyUrl.dao;

import com.wallet.tinyUrl.core.TinyUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TinyUrlDAOConnector {
	private static Lock createLock_ = new ReentrantLock();
	private static final Logger logger_ = LoggerFactory.getLogger(TinyUrlDAOConnector.class);

	private static TinyUrlDAO tinyUrlDAO = null;
	private static TinyUrlDAOConnector instance_ = null;

	public static final String TABLE_NAME = "tinyUrl";

	public static TinyUrlDAOConnector instance() throws Exception {
		if ( instance_ == null )
		{
			TinyUrlDAOConnector.createLock_.lock();
			try
			{

				instance_ = new TinyUrlDAOConnector();
			}
			finally
			{
				TinyUrlDAOConnector.createLock_.unlock();
			}
		}

		return instance_;
	}

	private TinyUrlDAOConnector() throws Exception {
		this.createTable();
	}
	
	public static void init(TinyUrlDAO tinyUrlDAO) {
		TinyUrlDAOConnector.tinyUrlDAO = tinyUrlDAO;
	}
	
	private void createTable() throws Exception {
		try {
			tinyUrlDAO.createTable();
		} catch (Exception e) {
			if (e.getMessage().contains("already exists")) {
				logger_.info("tinyUrl table already exists : " + e.getMessage());
			} else {
				e.printStackTrace();
				logger_.error("Error (CRITICAL) : failed to create tinyUrl table : " + e.getMessage());
				throw new Exception(e);
			}
		}
	}
	
	public void deleteTable(){
		tinyUrlDAO.dropTable();
		instance_ = null;
	}

	public List<TinyUrl> getAll() {
		return tinyUrlDAO.findAll();
	}
	
	public List<TinyUrl> getByShortUrl(String short_url) {
		return tinyUrlDAO.findByShortUrl(short_url);
	}
	
	public List<TinyUrl> getByFullUrl(String full_url) {
		return tinyUrlDAO.findByFullUrl(full_url);
	}
	
	public void insert(TinyUrl tinyUrl) throws Exception {
		try {
			tinyUrlDAO.insert(tinyUrl.getShort_url(), tinyUrl.getFull_url(), tinyUrl.getCreate_time(), tinyUrl.getExpire_click());
		} catch (Exception e) {
			if (e.getMessage().contains("Duplicate entry")) {
				logger_.info("TinyUrl " + tinyUrl.getShort_url() + " already exists");
			} else {
				logger_.error("Error insert tinyUrl " + tinyUrl.getShort_url() + " failed : " + e.getMessage());
				e.printStackTrace();
				throw new Exception(e);
			}
		}
	}
	
	public void updateExpireClickByFullUrl(TinyUrl tinyUrl) {
		tinyUrlDAO.updateExpireClickByFullUrl(tinyUrl.getFull_url(), tinyUrl.getExpire_click());
	}

	public void updateExpireClickByShortUrl(TinyUrl tinyUrl) {
		tinyUrlDAO.updateExpireClickByShortUrl(tinyUrl.getShort_url(), tinyUrl.getExpire_click());
	}

	public void deleteByShortUrl(String short_url) {
		tinyUrlDAO.deleteByShortUrl(short_url);
	}

	public void deleteByFullUrl(String full_url) {
		tinyUrlDAO.deleteByFullUrl(full_url);
	}

	public static void test() throws Exception {
		TinyUrl tinyUrl = new TinyUrl("/books", 2);

		logger_.info("TinyUrlDAOConnector test ...");

		logger_.info("0. clear old test url");
		TinyUrlDAOConnector.instance().deleteByFullUrl("/books");

		logger_.info("1. insert");
		
		TinyUrlDAOConnector.instance().insert(tinyUrl);
		if (TinyUrlDAOConnector.instance().getByFullUrl(tinyUrl.getFull_url()).isEmpty()) {
			logger_.error("TinyUrlDAOConnector test failed!");
			throw new Exception("TinyUrlDAOConnector test failed!");
		}
		
		logger_.info("2. update");
		
		tinyUrl.setExpire_click(10);
		TinyUrlDAOConnector.instance().updateExpireClickByFullUrl(tinyUrl);
		tinyUrl.setExpire_click(3);
		TinyUrlDAOConnector.instance().updateExpireClickByShortUrl(tinyUrl);
		List<TinyUrl> tinyUrl2 = TinyUrlDAOConnector.instance().getByFullUrl(tinyUrl.getFull_url());
		if (tinyUrl2.isEmpty()) {
			logger_.error("TinyUrlDAOConnector test failed!");
			throw new Exception("TinyUrlDAOConnector test failed!");
		}
		logger_.info("updated expire click : " + tinyUrl2.get(tinyUrl2.size() - 1).getExpire_click());
		
		logger_.info("3. delete");
		
		TinyUrlDAOConnector.instance().deleteByShortUrl(tinyUrl.getShort_url());
		if (!TinyUrlDAOConnector.instance().getByShortUrl(tinyUrl.getShort_url()).isEmpty()) {
			logger_.error("TinyUrlDAOConnector test failed!");
			throw new Exception("TinyUrlDAOConnector test failed!");
		}

		// reserve this url for testing
		TinyUrlDAOConnector.instance().insert(tinyUrl);

		logger_.info("TinyUrlDAOConnector test passed");
	}
}
