package com.wallet.login.core;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.wallet.utils.misc.Dict;

public class SessionMapper implements ResultSetMapper<Session> {

    public Session map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
    	Session session = new Session();

        session.setAccess_token(resultSet.getString(Dict.ACCESS_TOKEN));
        session.setUser_id(resultSet.getString(Dict.USER_ID));
        session.setCreate_time(resultSet.getDate(Dict.CREATE_TIME));

        return session;
    }
}
