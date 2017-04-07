package com.wallet.book.core;

import java.io.IOException;
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
		entry.setBook_group_id(resultSet.getString(Dict.BOOK_GROUP_ID));
		entry.setGroup_id(resultSet.getString(Dict.GROUP_ID));
		entry.setCategory_group_id(resultSet.getString(Dict.CATEGORY_GROUP_ID));
		entry.setEvent_date(resultSet.getDate(Dict.EVENT_DATE));
		entry.setAmount(resultSet.getLong(Dict.AMOUNT));
		entry.setEdit_time(resultSet.getDate(Dict.EDIT_TIME));

		try {
			entry.setData(new BookEntryData(resultSet.getBinaryStream(Dict.DATA)));
		} catch (Exception e) {
			throw new SQLException("Cannot map Book from resultSet : " + e.getMessage());
		}

		return entry;
	}

}
