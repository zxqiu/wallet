package com.wallet.books.core;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import utils.NameDef;

public class CategoryMapper implements ResultSetMapper<Category> {

	public Category map(int i, ResultSet resultSet, StatementContext context) throws SQLException {
		Category category = new Category();
		
		category.setId(resultSet.getString(NameDef.ID));
		category.setUser_id(resultSet.getString(NameDef.USER_ID));
		category.setName(resultSet.getString(NameDef.NAME));
		category.setPicture_id(resultSet.getString(NameDef.PICTURE_ID));
		
		return category;
	}

}
