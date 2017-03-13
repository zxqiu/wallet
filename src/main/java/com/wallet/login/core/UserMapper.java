package com.wallet.login.core;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.wallet.utils.misc.NameDef;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements ResultSetMapper<User> {

    public User map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        User user = new User();

        user.setUser_id(resultSet.getString(NameDef.USER_ID));
        user.setEmail(resultSet.getString(NameDef.EMAIL));
        user.setPassword(resultSet.getString(NameDef.PASSWORD));
        user.setName(resultSet.getString(NameDef.NAME));
        user.setPriority(resultSet.getString(NameDef.PRIORITY));

        return user;
    }
}
