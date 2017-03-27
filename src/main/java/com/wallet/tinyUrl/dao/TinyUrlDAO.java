package com.wallet.tinyUrl.dao;

import com.wallet.tinyUrl.core.TinyUrl;
import com.wallet.tinyUrl.core.TinyUrlMapper;
import com.wallet.utils.misc.Dict;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.Date;
import java.util.List;

/**
 * Created by zxqiu on 3/27/17.
 */
public interface TinyUrlDAO {
    public static final String TABLE_NAME = "tinyurl";

    @SqlUpdate("create table if not exists " + TABLE_NAME + " ("
            + "`" + Dict.SHORT_URL + "` varchar(128) not null unique,"
            + "`" + Dict.FULL_URL + "` varchar(128) not null unique,"
            + "`" + Dict.CREATE_TIME + "` datetime not null,"
            + "`" + Dict.EXPIRE_CLICK + "` int not null,"
            + "primary key (`" + Dict.SHORT_URL + "`),"
            + ")ENGINE=InnoDB DEFAULT CHARSET=utf8 collate=utf8_unicode_ci;"
    )
    void createTable();

    @SqlUpdate("drop table if exists " + TABLE_NAME)
    void dropTable();

    @SqlUpdate("insert into " + TABLE_NAME + " ("
            + Dict.SHORT_URL
            + ", " + Dict.FULL_URL
            + ", " + Dict.CREATE_TIME
            + ", " + Dict.EXPIRE_CLICK
            + ") values ("
            + ":" + Dict.SHORT_URL
            + ", :" + Dict.FULL_URL
            + ", :" + Dict.CREATE_TIME
            + ", :" + Dict.EXPIRE_CLICK
            + ")"
    )
    void insert(
            @Bind(Dict.SHORT_URL) String short_url
            ,@Bind(Dict.FULL_URL) String full_url
            ,@Bind(Dict.CREATE_TIME) Date create_time
            ,@Bind(Dict.EXPIRE_CLICK) int expire_click
    );

    @SqlQuery("select * from " + TABLE_NAME)
    @Mapper(TinyUrlMapper.class)
    List<TinyUrl> findAll();

    @SqlQuery("select * from " + TABLE_NAME + " where " + Dict.SHORT_URL + " = :" + Dict.SHORT_URL)
    @Mapper(TinyUrlMapper.class)
    List<TinyUrl> findByShortUrl(
            @Bind(Dict.SHORT_URL) String short_url
    );

    @SqlQuery("select * from " + TABLE_NAME + " where " + Dict.FULL_URL + " = :" + Dict.FULL_URL)
    @Mapper(TinyUrlMapper.class)
    List<TinyUrl> findByFullUrl(
            @Bind(Dict.FULL_URL) String full_url
    );

    @SqlUpdate("delete from " + TABLE_NAME + " where " + Dict.USER_ID + " = :" + Dict.USER_ID)
    void deleteByUserID(
            @Bind(Dict.USER_ID) String user_id
    );

    @SqlUpdate("delete from " + TABLE_NAME + " where " + Dict.ACCESS_TOKEN + " = :" + Dict.ACCESS_TOKEN)
    void deleteByAccessToken(
            @Bind(Dict.ACCESS_TOKEN) String access_token
    );
}
