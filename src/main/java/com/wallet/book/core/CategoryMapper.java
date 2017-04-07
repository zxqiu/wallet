package com.wallet.book.core;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.wallet.utils.misc.Dict;

public class CategoryMapper implements ResultSetMapper<Category> {

	public Category map(int i, ResultSet resultSet, StatementContext context) throws SQLException {
		Category category = new Category();
		
		category.setId(resultSet.getString(Dict.ID));
		category.setGroup_id(resultSet.getString(Dict.GROUP_ID));
		category.setUser_id(resultSet.getString(Dict.USER_ID));
		category.setBook_group_id(resultSet.getString(Dict.BOOK_GROUP_ID));
		category.setName(resultSet.getString(Dict.NAME));
		try {
			category.setData(new CategoryData(resultSet.getBinaryStream(Dict.DATA)));
		} catch (Exception e) {
			throw new SQLException("Cannot map Category from resultSet : " + e.getMessage());
		}

		return category;
	}

}
