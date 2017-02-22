package mySqlConnector;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySqlConnector {
	private static final Logger logger_ = LoggerFactory.getLogger(MySqlConnector.class);
	private static Lock createLock_ = new ReentrantLock();
	
	public static final String VALUES = "values";
	public static final String COLUMNS = "columns";
	
	public static final String TEXT = "text";
	public static final String BYTE = "tinyint (8)";
	public static final String SHORT = "smallint (16)";
	public static final String INT = "int (32)";
	public static final String LONG = "bigint (64)";
	public static final String VCHAR_16 = "varchar (16)";
	public static final String VCHAR_32 = "varchar (32)";
	public static final String VCHAR_64 = "varchar (64)";
	public static final String VCHAR_128 = "varchar (128)";
	public static final String VCHAR_255 = "varchar (255)";
	public static final String NOT_NULL = " NOT NULL";
	public static final String UNIQUE = " UNIQUE";
	
	/* MySql error codes */
	/**
	 * Error: 1062 SQLSTATE: 23000 (ER_DUP_ENTRY)
	 * Message: Duplicate entry '%s' for key %d
	 */
	public static final int ER_DUP_ENTRY = 1062;
	/**
	 * Error: 1050 SQLSTATE: 42S01 (ER_TABLE_EXISTS_ERROR)
	 * Message: Table '%s' already exists
	 */
	
	public static final int ER_TABLE_EXISTS_ERROR = 1050;
	
	
	private static DataSource dataSource = null;
	private static MySqlConnector instance_ = null;

	public static MySqlConnector instance() {
		if ( instance_ == null )
		{
			MySqlConnector.createLock_.lock();
			try
			{
				instance_ = new MySqlConnector();
			}
			finally
			{
				MySqlConnector.createLock_.unlock();
			}
		}
		
		return instance_;
	}
	
	protected MySqlConnector() {
		try {
			InputStream in = MySqlConnector.class.getClassLoader().getResourceAsStream("mySqlConnector/dbcpconfig.properties");
	        Properties properties = new Properties();
	        properties.load(in);

	        dataSource = BasicDataSourceFactory.createDataSource(properties);
	        logger_.warn(dataSource.toString());
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
	    }
	}
	
	/*
	 * Example:
		String sql = "CREATE TABLE `" + tableName + "` (" +
				"`" + OrderInfo.TRANSACTION_NUMBER + "` varchar (32) NOT NULL UNIQUE," +
				"`" + OrderInfo.PRODUCT_ID + "`varchar (32) NOT NULL," +
				"`" + OrderInfo.STATUS + "`varchar (16) NOT NULL," +
				"`" + OrderInfo.AMOUNT + "`int (16) NOT NULL," +
				"`" + OrderInfo.TOTAL_PRICE + "`int (16) NOT NULL," +
				"`" + OrderInfo.CLOSE_REASON + "`varchar (32) NOT NULL," +
				"`" + OrderInfo.ADDRESS + "`varchar (128) NOT NULL," +
				"`" + OrderInfo.DESCRIPTION + "`varchar (64) NOT NULL," +
				"`" + OrderInfo.QR_CODE_PATH + "`varchar (128) NOT NULL," +
				"`" + OrderInfo.CREATE_TIME + "`varchar (32) NOT NULL," +
				"`" + OrderInfo.USER_NAME + "`varchar (64) NOT NULL," +
				"PRIMARY KEY (`" + OrderInfo.TRANSACTION_NUMBER + "`)" +
				") ENGINE = InnoDB DEFAULT CHARSET = utf8 ;";
	 */
	public void createTable(String tableName, Map<String, String> items, String primaryKey) throws SQLException {
		String sql = "CREATE TABLE `" + tableName + "` (";
		
		for (Entry<String, String> entry : items.entrySet()) {
			sql += "`" + entry.getKey() + "`" + entry.getValue() + ",";
		}
		
		sql += "PRIMARY KEY (`" + primaryKey + "`)" +
				") ENGINE = InnoDB DEFAULT CHARSET = utf8 ;";
		
		MySqlConnector.instance().execute(sql, null);
	}
	
	public void deleteTable(String tableName) throws SQLException {
		String sql = "DROP TABLE " + tableName + ";";
		MySqlConnector.instance().execute(sql, null);
	}
	
	public void insertToTable(Map<String, Object> values, String tableName) throws SQLException {
		Map<String, String> colval = MapToSQLColumnsAndValue(values);
		if (colval == null) {
			logger_.error("ERROR: cannot insert to table " + tableName + ", " + values.toString());
			return;
		}
		
		String sql = "INSERT INTO " + tableName + colval.get(COLUMNS) + 
				" VALUES " + colval.get(VALUES) + ";";
		MySqlConnector.instance().execute(sql, null);
		
	}
		public void deleteFromTable(Map<String, Object> keys, String tableName) throws SQLException {
		String sql = "DELETE FROM " + tableName;
		sql += MapToSQLWhereAnd(keys) + ";";
		MySqlConnector.instance().execute(sql, null);
	}
	
	public List<Map<String, Object>> selectFromTable(Map<String, Object> keys, String tableName) throws SQLException {
		String sql = "SELECT * FROM " + tableName;
		sql += MapToSQLWhereAnd(keys) + ";";
		
		List<Map<String, Object>> ret = MySqlConnector.instance().getQueryList(sql, null);
		
		return ret;
	}
	
	public List<Map<String, Object>> selectAllFromTable(String tableName) throws SQLException {
		String sql = "SELECT * FROM " + tableName + ";";
		
		List<Map<String, Object>> ret = MySqlConnector.instance().getQueryList(sql, null);
		return ret;
	}
	
	public void updateTable(Map<String, Object> values, Map<String, Object> keys, String tableName) throws SQLException {
		String sql = "UPDATE " + tableName + " SET " + MapToSQLUpdateSet(values);
		sql += MapToSQLWhereAnd(keys) + ";";
		MySqlConnector.instance().execute(sql, null);
	}
	
	private static Map<String, String> MapToSQLColumnsAndValue(Map<String, Object> in) {
		Map<String, String> out = new HashMap<String, String>();
		String values = "(";
		String columns = "(";
		
		if (in == null) {
			return null;
		}
		
		for (Entry<String, Object> entry : in.entrySet()) {
			columns += entry.getKey() + ", ";
			values += "'" + entry.getValue() + "', ";
		}
		values = values.substring(0, values.length() - 2);
		values += ")";
		columns = columns.substring(0, columns.length() - 2);
		columns += ")";
		out.put(VALUES, values);
		out.put(COLUMNS, columns);
		
		return out;
	}
	
	private static String MapToSQLUpdateSet(Map<String, Object> in) {
		String out = "";
		
		for (Entry<String, Object> entry : in.entrySet()) {
			out += entry.getKey() + "='" + entry.getValue() + "', ";
		}
		out = out.substring(0, out.length() - 2);
		
		return out;
	}
	
	private static String MapToSQLWhereAnd(Map<String, Object> in) {
		String sql = "";
		int i = 0;
		
		for (Entry<String, Object> entry : in.entrySet()) {
			if (i++ > 0) {
				sql += " AND ";
			} else {
				sql += " WHERE ";
			}
			sql += entry.getKey() + "='" + entry.getValue() + "'";
		}
		
		return sql;
	}
	
	/**
	 * 该语句必须是一个 SQL INSERT、UPDATE 或 DELETE 语句
	 * @param sql
	 * @param paramList：参数，与SQL语句中的占位符一一对应
	 * @return
	 * @throws SQLException 
	 */
	public int execute(String sql, List<Object> paramList) throws SQLException {
		if(sql == null || sql.trim().equals("")) {
			//logger.info("parameter is valid!");
		}

		logger_.debug("MySql Running: " + sql);
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;
		try {
			conn = getConnection();
			pstmt = MySqlConnector.getPreparedStatement(conn, sql);
			setPreparedStatementParam(pstmt, paramList);
			if(pstmt == null) {
				return -1;
			}
			result = pstmt.executeUpdate();
		} finally {
			closeStatement(pstmt);
			closeConn(conn);
		}

		return result;
	}
	
	/**
	 * 将查询数据库获得的结果集转换为Map对象
	 * @param sql：查询语句
	 * @param paramList：参数
	 * @return
	 * @throws SQLException 
	 */
	public List<Map<String, Object>> getQueryList(String sql, List<Object> paramList) throws SQLException {
		if(sql == null || sql.trim().equals("")) {
			//logger.info("parameter is valid!");
			return null;
		}

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Map<String, Object>> queryList = null;
		try {
			conn = getConnection();
			pstmt = MySqlConnector.getPreparedStatement(conn, sql);
			setPreparedStatementParam(pstmt, paramList);
			if(pstmt == null) {
				return null;
			}
			rs = getResultSet(pstmt);
			queryList = getQueryList(rs);
		} catch (RuntimeException e) {
			logger_.warn("parameter is valid!");
			throw new RuntimeException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(pstmt);
			closeConn(conn);
		}
		return queryList;
	}
	
	private void setPreparedStatementParam(PreparedStatement pstmt, List<Object> paramList) throws SQLException {
		if(pstmt == null || paramList == null || paramList.isEmpty()) {
			return;
		}
		DateFormat df = DateFormat.getDateTimeInstance();
		for (int i = 0; i < paramList.size(); i++) {
			if(paramList.get(i) instanceof Integer) {
				int paramValue = ((Integer)paramList.get(i)).intValue();
				pstmt.setInt(i+1, paramValue);
			} else if(paramList.get(i) instanceof Float) {
				float paramValue = ((Float)paramList.get(i)).floatValue();
				pstmt.setFloat(i+1, paramValue);
			} else if(paramList.get(i) instanceof Double) {
				double paramValue = ((Double)paramList.get(i)).doubleValue();
				pstmt.setDouble(i+1, paramValue);
			} else if(paramList.get(i) instanceof Date) {
				pstmt.setString(i+1, df.format((Date)paramList.get(i)));
			} else if(paramList.get(i) instanceof Long) {
				long paramValue = ((Long)paramList.get(i)).longValue();
				pstmt.setLong(i+1, paramValue);
			} else if(paramList.get(i) instanceof String) {
				pstmt.setString(i+1, (String)paramList.get(i));
			}
		}
		return;
	}
	
	/**
	 * 获得数据库连接
	 * @return
	 * @throws SQLException 
	 * @throws Exception
	 */
	private Connection getConnection() throws SQLException  {
		if ( dataSource == null ) {
		   throw new SQLException("Data source not found!");
		}
		
		return dataSource.getConnection();
	}
	
	private static PreparedStatement getPreparedStatement(Connection conn, String sql) throws SQLException  {
		if(conn == null || sql == null || sql.trim().equals("")) {
			return null;
		}
		PreparedStatement pstmt = conn.prepareStatement(sql.trim());
		return pstmt;
	}
	
	/**
	 * 获得数据库查询结果集
	 * @param pstmt
	 * @return
	 * @throws SQLException 
	 */
	private ResultSet getResultSet(PreparedStatement pstmt) throws SQLException {
		if(pstmt == null) {
			return null;
		}
		logger_.debug("MySql Running: " + pstmt.toString());
		ResultSet rs = pstmt.executeQuery();
		return rs;
	}
	
	/**
	 * @param rs
	 * @return
	 * @throws SQLException 
	 * @throws Exception
	 */
	private List<Map<String, Object>> getQueryList(ResultSet rs) throws SQLException  {
		if(rs == null) {
			return null;
		}
		ResultSetMetaData rsMetaData = rs.getMetaData();
		int columnCount = rsMetaData.getColumnCount();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		while (rs.next()) {
			Map<String, Object> dataMap = new HashMap<String, Object>();
			for (int i = 0; i < columnCount; i++) {
				dataMap.put(rsMetaData.getColumnName(i+1), rs.getObject(i+1));
			}
			dataList.add(dataMap);
		}
		return dataList;
	}
	
	/**
	 * 关闭数据库连接
	 * @param conn
	 */
	private void closeConn(Connection conn) {
		if(conn == null) {
			return;
		}
		try {
			conn.close();
		} catch (SQLException e) {
			//logger.info(e.getMessage());
		}
	}
	
	/**
	 * 关闭
	 * @param stmt
	 */
	private void closeStatement(Statement stmt) {
		if(stmt == null) {
			return;
		}
		try {
			stmt.close();
		} catch (SQLException e) {
			//logger.info(e.getMessage());
		}
	}
	
	/**
	 * 关闭
	 * @param rs
	 */
	private void closeResultSet(ResultSet rs) {
		if(rs == null) {
			return;
		}
		try {
			rs.close();
		} catch (SQLException e) {
			//logger.info(e.getMessage());
		}
	}
	
	/*
	public static void main(String[] args) throws Exception {
		BooksInfo order = new BooksInfo("00005", "product000", BooksInfo.OrderStatus.PAYING, 1, 10, BooksInfo.CloseReason.NOT_CLOSED, "addddress", "good-good", "/abc", "19900329182231", "user");
		String insert = "INSERT INTO tmpOrder " + MySqlConnector.instance().MapToSQLColumnsAndValue(order.toMap()).get(COLUMNS) + 
				" VALUES " + MySqlConnector.instance().MapToSQLColumnsAndValue(order.toMap()).get(VALUES) + ";";
		logger_.info(insert);
		String select = "select * from tmpOrder";
		MySqlConnector.instance().execute(insert, null);
		List<Map<String, Object>> ret = MySqlConnector.instance().getQueryList(select, null);
		logger_.info(ret.get(0).toString());
	}*/
}