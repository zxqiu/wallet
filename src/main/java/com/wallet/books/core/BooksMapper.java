package com.wallet.books.core;

import com.wallet.utils.misc.Dict;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BooksMapper implements ResultSetMapper<Books> {

	public Books map(int i, ResultSet resultSet, StatementContext context) throws SQLException {
		Books books = new Books();
		
		books.setId(resultSet.getString(Dict.ID));
		books.setUser_id(resultSet.getString(Dict.USER_ID));
		books.setName(resultSet.getString(Dict.NAME));
		books.setCreate_time(resultSet.getDate(Dict.CREATE_TIME));
		books.setEdit_time(resultSet.getDate(Dict.EDIT_TIME));
		books.setPicture_id(resultSet.getString(Dict.PICTURE_ID));
		books.setData(resultSet.getString(Dict.DATA));

		return books;
	}

}
