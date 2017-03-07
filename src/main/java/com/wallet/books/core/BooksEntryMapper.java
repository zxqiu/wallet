package com.wallet.books.core;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import utils.NameDef;

public class BooksEntryMapper implements ResultSetMapper<BooksEntry> {

	public BooksEntry map(int i, ResultSet resultSet, StatementContext context) throws SQLException {
		BooksEntry entry = new BooksEntry();
		
		entry.setId(resultSet.getString(NameDef.ID));
		entry.setUser_id(resultSet.getString(NameDef.USER_ID));
		entry.setCategory(resultSet.getString(NameDef.CATEGORY));
		entry.setEvent_date(resultSet.getDate(NameDef.EVENT_DATE));
		entry.setAmount(resultSet.getLong(NameDef.AMOUNT));
		entry.setNote(resultSet.getString(NameDef.NOTE));
		entry.setPhoto(resultSet.getString(NameDef.PHOTO));
		entry.setEdit_time(resultSet.getDate(NameDef.EDIT_TIME));
		
		return entry;
	}

}
