package com.wallet.login.core;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.wallet.utils.misc.Dict;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements ResultSetMapper<User> {

    public User map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        User user = new User();

        user.setUser_id(resultSet.getString(Dict.USER_ID));
        user.setEmail(resultSet.getString(Dict.EMAIL));
        user.setPassword(resultSet.getString(Dict.PASSWORD));
        user.setName(resultSet.getString(Dict.NAME));
        user.setPriority(resultSet.getString(Dict.PRIORITY));
        try {
            user.setData(new UserData(resultSet.getBinaryStream(Dict.DATA)));
        } catch (Exception e) {
            throw new SQLException("Cannot map User from resultSet : " + e.getMessage());
        }

        return user;
    }
}
