package com.wallet.book.core;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.wallet.utils.misc.Dict;

public class BookEntryMapper implements ResultSetMapper<BookEntry> {

	public BookEntry map(int i, ResultSet resultSet, StatementContext context) throws SQLException {
		BookEntry entry = new BookEntry();
		
		entry.setId(resultSet.getString(Dict.ID));
		entry.setUser_id(resultSet.getString(Dict.USER_ID));
		entry.setCreate_user_id(resultSet.getString(Dict.CREATE_USER_ID));
		entry.setBook_id(resultSet.getString(Dict.BOOK_ID));
		entry.setBook_group_id(resultSet.getString(Dict.BOOK_GROUP_ID));
		entry.setGroup_id(resultSet.getString(Dict.GROUP_ID));
		entry.setCategory(resultSet.getString(Dict.CATEGORY));
		entry.setEvent_date(resultSet.getDate(Dict.EVENT_DATE));
		entry.setAmount(resultSet.getLong(Dict.AMOUNT));
		entry.setNote(resultSet.getString(Dict.NOTE));
		entry.setPhoto(resultSet.getString(Dict.PHOTO));
		entry.setData(resultSet.getString(Dict.DATA));
		entry.setEdit_time(resultSet.getDate(Dict.EDIT_TIME));
		entry.setCreate_time(resultSet.getDate(Dict.CREATE_TIME));

		return entry;
	}

}
