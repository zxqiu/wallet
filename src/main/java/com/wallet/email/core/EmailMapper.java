package com.wallet.email.core;

import com.wallet.utils.misc.Dict;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmailMapper implements ResultSetMapper<Email> {

	public Email map(int i, ResultSet resultSet, StatementContext context) throws SQLException {
		Email email = new Email();
		
		email.setId(resultSet.getString(Dict.ID));
		email.setCreate_time(resultSet.getTime(Dict.CREATE_TIME));
		email.setFrom_address(resultSet.getString(Dict.FROM_ADDRESS));
		email.setTo_address(resultSet.getString(Dict.TO_ADDRESS));
		email.setType(Email.EMAIL_TYPE.valueOf(resultSet.getString(Dict.TYPE)));
		email.setStatus(Email.EMAIL_STATUS.valueOf(resultSet.getString(Dict.STATUS)));
		try {
			email.setData(new EmailData(resultSet.getBinaryStream(Dict.DATA)));
		} catch (Exception e) {
			throw new SQLException("Cannot map Email from resultSet : " + e.getMessage());
		}

		return email;
	}

}
