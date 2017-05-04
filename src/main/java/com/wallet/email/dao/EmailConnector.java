package com.wallet.email.dao;

import com.wallet.email.core.Email;
import com.wallet.email.task.EmailTasks;
import com.wallet.utils.tools.concurrentHelper.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EmailConnector {
	private static Lock createLock_ = new ReentrantLock();
	private static final Logger logger_ = LoggerFactory.getLogger(EmailConnector.class);

	private static EmailDAO emailDAO = null;
	private static EmailConnector instance_ = null;

	public static final long EMAIL_CHECK_INTERVAL = 1000 * 60 * 1;
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
	
	public static void init(EmailDAO emailDAO) throws Exception {
		EmailConnector.emailDAO = emailDAO;

		EmailTasks.init();
		Scheduler.instance().schedule(new EmailTasks.CheckTask(), EMAIL_CHECK_INTERVAL, EMAIL_CHECK_INTERVAL);
	}
	
	private void createTable() throws Exception {
		try {
			emailDAO.createTable();
		} catch (Exception e) {
			if (e.getMessage().contains("already exists")) {
				logger_.info("email table already exists : " + e.getMessage());
			} else {
				e.printStackTrace();
				logger_.error("Error (CRITICAL) : failed to create email table : " + e.getMessage());
				throw new Exception(e);
			}
		}
	}
	
	public void deleteTable() {
		emailDAO.dropTable();
		instance_ = null;
	}

	public List<Email> getAll() {
		return emailDAO.findAll();
	}

	public List<Email> getByID(String id) {
		return emailDAO.findByID(id);
	}

	public List<Email> getByFromAddress(String from_address) {
		return emailDAO.findByFromAddress(from_address);
	}

	public List<Email> getByToAddress(String to_address) {
		return emailDAO.findByToAddress(to_address);
	}

	public List<Email> getByStatus(Email.EMAIL_STATUS status) {
		return emailDAO.findByStatus(status.name());
	}

	public List<Email> getByType(Email.EMAIL_TYPE type) {
		return emailDAO.findByType(type.name());
	}

	public List<Email> getByFromAddressAndToAddress(String from_address, String to_address) {
		return emailDAO.findByFromAddressAndToAddress(from_address, to_address);
	}

	public List<Email> getByToAddressAndStatus(String to_address, Email.EMAIL_STATUS status) {
		return emailDAO.findByToAddressAndStatus(to_address, status.name());
	}

	public void insert(Email email) throws Exception {
		try {
			emailDAO.insert(email.getId(), email.getCreate_time(), email.getFrom_address()
					, email.getTo_address(), email.getType().name(), email.getStatus().name()
					, email.getData().toByteArray());
		} catch (Exception e) {
			if (e.getMessage().contains("Duplicate entry")) {
				logger_.info("Email " + email.getId() + " already exists. From " + email.getFrom_address()
						+ " to " + email.getTo_address());
			} else {
				logger_.error("Error insert email " + email.getId() + ". From " + email.getFrom_address()
						+ " to " + email.getTo_address());
				e.printStackTrace();
				throw new Exception(e);
			}
		}
	}
	
	public void updateByID(Email email) throws Exception {
		emailDAO.updateByID(email.getId(), email.getStatus().name());
	}

	public void deleteByID(String id) throws Exception {
		emailDAO.deleteByID(id);
	}

	public void deleteByToAddress(String to_address) throws Exception {
		emailDAO.deleteByToAddress(to_address);
    }

	public static void test() throws Exception {
		Email email = new Email("a@b.com", "b@c.com", Email.EMAIL_TYPE.ALERT
				, "test", "test text", "", null, null);
		
		logger_.info("EmailConnector test ...");
		
		logger_.info("1. insert");
		
		EmailConnector.instance().insert(email);
		if (EmailConnector.instance().getByID(email.getId()).isEmpty()) {
			logger_.error("EmailConnector test failed!");
			throw new Exception("EmailConnector test failed!");
		}
		
		logger_.info("2. update");
		
		email.setStatus(Email.EMAIL_STATUS.SENT);
		EmailConnector.instance().updateByID(email);
		List<Email> emails = EmailConnector.instance().getByID(email.getId());
		if (emails.isEmpty() || !emails.get(emails.size() - 1).getStatus().equals(Email.EMAIL_STATUS.SENT)) {
			logger_.error("EmailConnector test failed!");
			logger_.error("updated email status : " + emails.get(emails.size() - 1).getStatus().name());
			throw new Exception("EmailConnector test failed!");
		}

		logger_.info("3. delete");

		EmailConnector.instance().deleteByID(email.getId());
		if (!EmailConnector.instance().getByID(email.getId()).isEmpty()) {
			logger_.error("EmailConnector test failed!");
			throw new Exception("EmailConnector test failed!");
		}

		logger_.info("EmailConnector test passed");
	}
}
