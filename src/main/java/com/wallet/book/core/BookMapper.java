package com.wallet.book.core;

import com.wallet.utils.misc.Dict;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookMapper implements ResultSetMapper<Book> {

	public Book map(int i, ResultSet resultSet, StatementContext context) throws SQLException {
		Book book = new Book();
		
		book.setId(resultSet.getString(Dict.ID));
		book.setUser_id(resultSet.getString(Dict.USER_ID));
		book.setCreate_user_id(resultSet.getString(Dict.CREATE_USER_ID));
		book.setName(resultSet.getString(Dict.NAME));
		book.setCreate_time(resultSet.getDate(Dict.CREATE_TIME));
		book.setEdit_time(resultSet.getDate(Dict.EDIT_TIME));
		book.setPicture_id(resultSet.getString(Dict.PICTURE_ID));
		book.setData(resultSet.getString(Dict.DATA));
		book.setGroup_id(resultSet.getString(Dict.GROUP_ID));

		return book;
	}

}
