package user.service.data;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mySqlConnector.MySqlConnector;
import utils.ApiUtils;
import utils.NameDef;

public class UserTable {
	private static Lock createLock_ = new ReentrantLock();
	private static final Logger logger_ = LoggerFactory.getLogger(UserTable.class);
	private static UserTable instance_ = null;
	
	public static final String TABLE_NAME = "user_table";

	public static UserTable instance() {
		if ( instance_ == null )
		{
			UserTable.createLock_.lock();
			try
			{
				
				instance_ = new UserTable();
			}
			finally
			{
				UserTable.createLock_.unlock();
			}
		}
		
		return instance_;
	}
	
	private UserTable() {
		MySqlConnector.instance();
		this.createTable();
	}
	
	private void createTable() {
		Map<String, String> items = new HashMap<String, String>();
		
		// id = user_id + create_time
		items.put(NameDef.ID, MySqlConnector.VCHAR_128 + MySqlConnector.NOT_NULL + MySqlConnector.UNIQUE);
		items.put(NameDef.NAME, MySqlConnector.VCHAR_64 + MySqlConnector.NOT_NULL);
		items.put(NameDef.PASSWORD, MySqlConnector.VCHAR_64 + MySqlConnector.NOT_NULL);
		items.put(NameDef.PRIORITY, MySqlConnector.VCHAR_64 + MySqlConnector.NOT_NULL);
		items.put(NameDef.CREATE_TIME, MySqlConnector.LONG + MySqlConnector.NOT_NULL);
		items.put(NameDef.PICTURE_URL, MySqlConnector.VCHAR_255);
		
		try {
			MySqlConnector.instance().createTable(TABLE_NAME, items, utils.NameDef.ID);
		} catch (SQLException e) {
			if (e.getErrorCode() == MySqlConnector.ER_TABLE_EXISTS_ERROR) {
				logger_.error("user table already exists : " + e.getMessage());
			} else {
				e.printStackTrace();
				logger_.error("Error (CRITICAL) : failed to create user table : " + e.getMessage());
			}
		}
	}
	
	public void deleteTable() throws SQLException {
		MySqlConnector.instance().deleteTable(TABLE_NAME);
		instance_ = null;
	}
	
	public UserInfo getUser(String id) throws SQLException {
		UserInfo user = null;
		Map<String, Object> keys = new HashMap<String, Object>();
		keys.put(NameDef.ID, id);
		
		List<Map<String, Object>> ret = MySqlConnector.instance().selectFromTable(keys, TABLE_NAME);
		
		if (!ret.isEmpty()) {
			user = UserInfo.stringToObject(ret.get(ret.size() - 1).toString());
		}
		
		return user;
	}
	
	public void insertNewUser(UserInfo userInfo) throws SQLException {
		MySqlConnector.instance().insertToTable(userInfo.toMap(), TABLE_NAME);
	}
	
	public void updateUser(UserInfo userInfo) throws SQLException {
		Map<String, Object> values = userInfo.toMap();
		Map<String, Object> keys = new HashMap<String, Object>();
		
		keys.put(utils.NameDef.ID, userInfo.getId());
		
		MySqlConnector.instance().updateTable(values, keys, TABLE_NAME);;
	}
	
	// TODO : delete user's data from other tables asynchronously
	public void deleteUser(String id) {
		Map<String, Object> keys = new HashMap<String, Object>();
		keys.put(utils.NameDef.ID, id);
		
		try {
			MySqlConnector.instance().deleteFromTable(keys, TABLE_NAME);
		} catch (SQLException e) {
			e.printStackTrace();
			logger_.error("Error : failed to delete user \'" + id + "\' from " + TABLE_NAME + " : " + e.getMessage());
		}
	}
	
	public static void main(String[] args) throws Exception {
		UserTable.instance().deleteTable();
		UserTable.instance();
		UserInfo userInfo = new UserInfo("webuser", "name", "pass", UserPriority.NORMAL, (long)1, "");
		
		UserTable.instance().insertNewUser(userInfo);
		UserInfo user = UserTable.instance().getUser("webuser");
		logger_.info(user.toMap().toString());
		
		userInfo.setName("gooooooooooooooooood");
		//UserTable.instance().deleteUser(userInfo1.getId());
		UserTable.instance().updateUser(userInfo);
		user = UserTable.instance().getUser("webuser");
		logger_.info(user.toMap().toString());
		
		logger_.info(String.valueOf(ApiUtils.keyValueExists(NameDef.CREATE_TIME, userInfo.getCreate_time(), TABLE_NAME)));
		
	}
}
