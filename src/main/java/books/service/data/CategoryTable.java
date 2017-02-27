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
import utils.NameDef;

public class CategoryTable {
	private static Lock createLock_ = new ReentrantLock();
	private static final Logger logger_ = LoggerFactory.getLogger(CategoryTable.class);
	private static CategoryTable instance_ = null;
	
	public static final String CATEGORY_TABLE = "category_table";

	public static CategoryTable instance() {
		if ( instance_ == null )
		{
			CategoryTable.createLock_.lock();
			try
			{
				
				instance_ = new CategoryTable();
			}
			finally
			{
				CategoryTable.createLock_.unlock();
			}
		}
		
		return instance_;
	}
	
	private CategoryTable() {
		MySqlConnector.instance();
		this.createTable();
	}
	
	private void createTable() {
		Map<String, String> items = new HashMap<String, String>();
		
		// id = user_id + create_time
		items.put(NameDef.ID, MySqlConnector.VCHAR_128 + MySqlConnector.NOT_NULL + MySqlConnector.UNIQUE);
		items.put(NameDef.USER_ID, MySqlConnector.VCHAR_64 + MySqlConnector.NOT_NULL);
		items.put(NameDef.NAME, MySqlConnector.VCHAR_64 + MySqlConnector.NOT_NULL);
		items.put(NameDef.PICTURE_URL, MySqlConnector.VCHAR_255);
		
		try {
			MySqlConnector.instance().createTable(CATEGORY_TABLE, items, utils.NameDef.ID);
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
		MySqlConnector.instance().deleteTable(CATEGORY_TABLE);
		instance_ = null;
	}
	
	public List<CategoryInfo> getAllCategoriesForUser(String user_id) throws SQLException {
		List<CategoryInfo> categories = new ArrayList<CategoryInfo>();
		Map<String, Object> keys = new HashMap<String, Object>();
		keys.put("user_id", user_id);
		
		List<Map<String, Object>> ret = MySqlConnector.instance().selectFromTable(keys, CATEGORY_TABLE);
		
		for (Map<String, Object> map : ret) {
			categories.add(CategoryInfo.stringToObject(map.toString()));
		}
		
		return categories;
	}
	
	public void insertNewCategories(CategoryInfo categoryInfo) throws SQLException {
		MySqlConnector.instance().insertToTable(categoryInfo.toMap(), CATEGORY_TABLE);
	}
	
	public void updateCategory(CategoryInfo categoryInfo) throws SQLException {
		Map<String, Object> values = categoryInfo.toMap();
		Map<String, Object> keys = new HashMap<String, Object>();
		
		keys.put(utils.NameDef.ID, categoryInfo.getId());
		
		MySqlConnector.instance().updateTable(values, keys, CATEGORY_TABLE);;
	}
	
	public void deleteCategory(String id) {
		Map<String, Object> keys = new HashMap<String, Object>();
		keys.put(utils.NameDef.ID, id);
		
		try {
			MySqlConnector.instance().deleteFromTable(keys, CATEGORY_TABLE);
		} catch (SQLException e) {
			e.printStackTrace();
			logger_.error("Error : failed to delete category from " + CATEGORY_TABLE + " : " + e.getMessage());
		}
	}
	
	public static void main(String[] args) throws Exception {
		CategoryTable.instance().deleteTable();
		CategoryTable.instance();
		CategoryInfo categoryInfo = new CategoryInfo("webuser", "good", "");
		CategoryInfo categoryInfo1 = new CategoryInfo("webuser", "bad", "");
		
		CategoryTable.instance().insertNewCategories(categoryInfo);
		CategoryTable.instance().insertNewCategories(categoryInfo1);
		for (CategoryInfo category : CategoryTable.instance().getAllCategoriesForUser("webuser")) {
			logger_.info(category.toMap().toString());
		}
		
		categoryInfo.setName("gooooooooooooooooood");
		//BooksTable.instance().deleteBooks(booksInfo1.getId());
		CategoryTable.instance().updateCategory(categoryInfo);
		for (CategoryInfo category : CategoryTable.instance().getAllCategoriesForUser("webuser")) {
			logger_.info(category.toMap().toString());
		}
		
	}
}
