package books.service.data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import mySqlConnector.MySqlConnector;

public class BooksTable {
	private static Lock createLock_ = new ReentrantLock();
	private static final Logger logger_ = LoggerFactory.getLogger(BooksTable.class);
	private static BooksTable instance_ = null;
	
	public static final String BOOKS_TABLE = "books_table";

	public static BooksTable instance() {
		if ( instance_ == null )
		{
			BooksTable.createLock_.lock();
			try
			{
				
				instance_ = new BooksTable();
			}
			finally
			{
				BooksTable.createLock_.unlock();
			}
		}
		
		return instance_;
	}
	
	private BooksTable() {
		MySqlConnector.instance();
		this.createTable();
	}
	
	private void createTable() {
		Map<String, String> items = new HashMap<String, String>();
		
		// id = user_id + create_time
		items.put(utils.NameDef.ID, MySqlConnector.VCHAR_128 + MySqlConnector.NOT_NULL + MySqlConnector.UNIQUE);
		items.put(utils.NameDef.USER_ID, MySqlConnector.VCHAR_64 + MySqlConnector.NOT_NULL);
		items.put(utils.NameDef.CATEGORY, MySqlConnector.VCHAR_64 + MySqlConnector.NOT_NULL);
		items.put(utils.NameDef.EVENT_TIME, MySqlConnector.LONG + MySqlConnector.NOT_NULL);
		items.put(utils.NameDef.AMOUNT, MySqlConnector.LONG + MySqlConnector.NOT_NULL);
		items.put(utils.NameDef.NOTE, MySqlConnector.TEXT);
		items.put(utils.NameDef.PICTURE_URL, MySqlConnector.VCHAR_255);
		
		try {
			MySqlConnector.instance().createTable(BOOKS_TABLE, items, utils.NameDef.ID);
		} catch (SQLException e) {
			if (e.getErrorCode() == MySqlConnector.ER_TABLE_EXISTS_ERROR) {
				logger_.error("books table already exists : " + e.getMessage());
			} else {
				e.printStackTrace();
				logger_.error("Error (CRITICAL) : failed to create books table : " + e.getMessage());
			}
		}
	}
	
	public void deleteTable() throws SQLException {
		MySqlConnector.instance().deleteTable(BOOKS_TABLE);
		instance_ = null;
	}
	
	public List<BooksInfo> getAllBooksForUser(String user_id) throws SQLException {
		List<BooksInfo> books = new ArrayList<BooksInfo>();
		Map<String, Object> keys = new HashMap<String, Object>();
		keys.put("user_id", user_id);
		
		List<Map<String, Object>> ret = MySqlConnector.instance().selectFromTable(keys, BOOKS_TABLE);
		
		for (Map<String, Object> map : ret) {
			books.add(BooksInfo.stringToObject(map.toString()));
		}
		
		return books;
	}
	
	public void insertNewBooks(BooksInfo booksInfo) throws SQLException {
		MySqlConnector.instance().insertToTable(booksInfo.toMap(), BOOKS_TABLE);
	}
	
	public void updateBooks(BooksInfo booksInfo) throws SQLException {
		Map<String, Object> values = booksInfo.toMap();
		Map<String, Object> keys = new HashMap<String, Object>();
		
		keys.put(utils.NameDef.ID, booksInfo.getId());
		
		MySqlConnector.instance().updateTable(values, keys, BOOKS_TABLE);;
	}
	
	public void deleteBooks(String id) {
		Map<String, Object> keys = new HashMap<String, Object>();
		keys.put(utils.NameDef.ID, id);
		
		try {
			MySqlConnector.instance().deleteFromTable(keys, BOOKS_TABLE);
		} catch (SQLException e) {
			e.printStackTrace();
			logger_.error("Error : failed to delete books from " + BOOKS_TABLE + " : " + e.getMessage());
		}
	}
	
	public static void main(String[] args) throws Exception {
		BooksTable.instance().deleteTable();
		BooksTable.instance();
		BooksInfo booksInfo = new BooksInfo((long) 1, "me", "good", (long) 2, (long) 10, "note", "");
		BooksInfo booksInfo1 = new BooksInfo((long) 2, "me", "bad", (long) 2, (long) 10, "note", "");
		
		BooksTable.instance().insertNewBooks(booksInfo);
		BooksTable.instance().insertNewBooks(booksInfo1);
		for (BooksInfo books : BooksTable.instance().getAllBooksForUser("me")) {
			logger_.info(books.toMap().toString());
		}
		
		booksInfo.setNote("nooooooote");
		//BooksTable.instance().deleteBooks(booksInfo1.getId());
		BooksTable.instance().updateBooks(booksInfo);
		for (BooksInfo books : BooksTable.instance().getAllBooksForUser("me")) {
			logger_.info(books.toMap().toString());
		}
		
	}
}
