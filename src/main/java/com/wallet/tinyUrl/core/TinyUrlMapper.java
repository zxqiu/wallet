package com.wallet.tinyUrl.core;

import com.wallet.utils.misc.Dict;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zxqiu on 3/27/17.
 */
public class TinyUrlMapper implements ResultSetMapper<TinyUrl> {

    public TinyUrl map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        TinyUrl tinyurl = new TinyUrl();

        tinyurl.setShort_url(resultSet.getString(Dict.SHORT_URL));
        tinyurl.setFull_url(resultSet.getString(Dict.FULL_URL));
        tinyurl.setCreate_time(resultSet.getDate(Dict.CREATE_TIME));
        tinyurl.setExpire_click(resultSet.getInt(Dict.EXPIRE_CLICK));

        return tinyurl;
    }
}
