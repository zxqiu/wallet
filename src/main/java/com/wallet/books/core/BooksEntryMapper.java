package com.wallet.books.core;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.wallet.utils.misc.Dict;

public class BooksEntryMapper implements ResultSetMapper<BooksEntry> {

	public BooksEntry map(int i, ResultSet resultSet, StatementContext context) throws SQLException {
		BooksEntry entry = new BooksEntry();
		
		entry.setId(resultSet.getString(Dict.ID));
		entry.setUser_id(resultSet.getString(Dict.USER_ID));
		entry.setCategory(resultSet.getString(Dict.CATEGORY));
		entry.setEvent_date(resultSet.getDate(Dict.EVENT_DATE));
		entry.setAmount(resultSet.getLong(Dict.AMOUNT));
		entry.setNote(resultSet.getString(Dict.NOTE));
		entry.setPhoto(resultSet.getString(Dict.PHOTO));
		entry.setAttributes(resultSet.getString(Dict.ATTRIBUTES));
		entry.setEdit_time(resultSet.getDate(Dict.EDIT_TIME));
		
		return entry;
	}

}
