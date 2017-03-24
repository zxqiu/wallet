package com.wallet.login.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.wallet.login.core.Session;
import com.wallet.login.core.SessionMapper;
import com.wallet.utils.misc.Dict;


public interface SessionDAO {
	public static final String TABLE_NAME = "session";
	
	@SqlUpdate("create table if not exists " + TABLE_NAME + " ("
			+ "`" + Dict.ACCESS_TOKEN + "` varchar(256) not null unique,"
			+ "`" + Dict.USER_ID + "` varchar(64) not null,"
			+ "`" + Dict.CREATE_TIME + "` datetime not null,"
			+ "primary key (`" + Dict.ACCESS_TOKEN + "`),"
			+ "key `fk_session_user` (`" + Dict.USER_ID + "`),"
			+ "constraint `fk_session_user` foreign key (`" + Dict.USER_ID + "`) "
			+ "references `" + UserDAO.TABLE_NAME + "` (`" + Dict.USER_ID + "`)"
			+ ")ENGINE = InnoDB DEFAULT CHARSET = utf8"
			)
	void createTable();
	
	@SqlUpdate("drop table if exists " + TABLE_NAME)
	void dropTable();
	
    @SqlUpdate("insert into " + TABLE_NAME + " ("
    		+ Dict.ACCESS_TOKEN
    		+ ", " + Dict.USER_ID
    		+ ", " + Dict.CREATE_TIME
    		+ ") values ("
    		+ ":" + Dict.ACCESS_TOKEN + ", "
    		+ ":" + Dict.USER_ID + ", "
    		+ ":" + Dict.CREATE_TIME + ")"
    		)
    void insert(
        @Bind(Dict.ACCESS_TOKEN) String access_token
        ,@Bind(Dict.USER_ID) String user_id
        ,@Bind(Dict.CREATE_TIME) java.util.Date create_time
    );
    
    @SqlQuery("select * from " + TABLE_NAME)
    @Mapper(SessionMapper.class)
    List<Session> findAll();
    
	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.USER_ID + " = :" + Dict.USER_ID)
    @Mapper(SessionMapper.class)
    List<Session> findByUserID(
        @Bind(Dict.USER_ID) String user_id
    );
	
	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.ACCESS_TOKEN + " = :" + Dict.ACCESS_TOKEN)
    @Mapper(SessionMapper.class)
    List<Session> findByAccessToken(
        @Bind(Dict.ACCESS_TOKEN) String access_token
    );
	
	@SqlQuery("select * from " + TABLE_NAME + " where "
			+ Dict.USER_ID + " = :" + Dict.USER_ID
			+ " and " + Dict.ACCESS_TOKEN + " = :" + Dict.ACCESS_TOKEN
			)
    @Mapper(SessionMapper.class)
    List<Session> findByUserIDAndAccessToken(
        @Bind(Dict.USER_ID) String user_id,
        @Bind(Dict.ACCESS_TOKEN) String access_token
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
