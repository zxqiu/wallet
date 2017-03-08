package com.wallet.login.core;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.wallet.utils.misc.NameDef;

public class SessionMapper implements ResultSetMapper<Session> {

    public Session map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
    	Session session = new Session();

        session.setAccess_token(resultSet.getString(NameDef.ACCESS_TOKEN));
        session.setUser_id(resultSet.getString(NameDef.USER_ID));
        session.setCreate_date(resultSet.getDate(NameDef.CREATE_DATE));

        return session;
    }
}
