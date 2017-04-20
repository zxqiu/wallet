package com.wallet.book.core;

import com.wallet.utils.misc.Dict;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookLogMapper implements ResultSetMapper<BookLog> {

	public BookLog map(int i, ResultSet resultSet, StatementContext context) throws SQLException {
		BookLog bookLog = new BookLog();
		
		bookLog.setId(resultSet.getString(Dict.ID));
		bookLog.setCreate_time(resultSet.getTime(Dict.CREATE_TIME));
		bookLog.setBook_id(resultSet.getString(Dict.BOOK_ID));
		bookLog.setBook_group_id(resultSet.getString(Dict.BOOK_GROUP_ID));
		bookLog.setCategory_id(resultSet.getString(Dict.CATEGORY_ID));
		bookLog.setCategory_group_id(resultSet.getString(Dict.CATEGORY_GROUP_ID));
		bookLog.setBook_entry_id(resultSet.getString(Dict.BOOK_ENTRY_ID));
		bookLog.setBook_entry_group_id(resultSet.getString(Dict.BOOK_ENTRY_GROUP_ID));
		bookLog.setUser_id(resultSet.getString(Dict.USER_ID));
		bookLog.setOperation(BookLog.BOOK_LOG_OPERATION.valueOf(resultSet.getString(Dict.OPERATION)));
		bookLog.setType(BookLog.BOOK_LOG_TYPE.valueOf(resultSet.getString(Dict.TYPE)));
		try {
			bookLog.setData(new BookLogData(resultSet.getBinaryStream(Dict.DATA)));
		} catch (Exception e) {
			throw new SQLException("Cannot map BookLog from resultSet : " + e.getMessage());
		}

		return bookLog;
	}

}
