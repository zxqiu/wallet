package com.wallet.login.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.wallet.login.core.Session;
import com.wallet.login.core.SessionMapper;

import utils.NameDef;


public interface SessionDAO {
	public static final String TABLE_NAME = "session";
	
	@SqlUpdate("create table if not exists " + TABLE_NAME + " ("
			+ "`" + NameDef.ACCESS_TOKEN + "` varchar(23) not null unique,"
			+ "`" + NameDef.USER_ID + "` varchar(64) not null,"
			+ "`" + NameDef.CREATE_DATE + "` datetime not null,"
			+ "primary key (`" + NameDef.ACCESS_TOKEN + "`),"
			+ "key `fk_session_user` (`" + NameDef.USER_ID + "`),"
			+ "constraint `fk_session_user` foreign key (`" + NameDef.USER_ID + "`) "
			+ "references `" + UserDAO.TABLE_NAME + "` (`" + NameDef.USER_ID + "`)"
			+ ")ENGINE = InnoDB DEFAULT CHARSET = utf8"
			)
	void createSessionTable();
	
	@SqlUpdate("drop table if exists " + TABLE_NAME)
	void dropSessionTable();
	
    @SqlUpdate("insert into " + TABLE_NAME + " ("
    		+ NameDef.ACCESS_TOKEN
    		+ ", " + NameDef.USER_ID 
    		+ ", " + NameDef.CREATE_DATE
    		+ ") values ("
    		+ ":" + NameDef.ACCESS_TOKEN + ", "
    		+ ":" + NameDef.USER_ID + ", "
    		+ ":" + NameDef.CREATE_DATE + ")"
    		)
    void insert(
        @Bind(NameDef.ACCESS_TOKEN) String access_token 
        ,@Bind(NameDef.USER_ID) String user_id
        ,@Bind(NameDef.CREATE_DATE) java.util.Date create_date 
    );
    
    @SqlQuery("select * from " + TABLE_NAME)
    @Mapper(SessionMapper.class)
    List<Session> findAll();
    
	@SqlQuery("select * from " + TABLE_NAME + " where " + NameDef.USER_ID + " = :" + NameDef.USER_ID)
    @Mapper(SessionMapper.class)
    List<Session> findSessionsByUserID(
        @Bind(NameDef.USER_ID) String user_id
    );
	
	@SqlQuery("select * from " + TABLE_NAME + " where " + NameDef.ACCESS_TOKEN + " = :" + NameDef.ACCESS_TOKEN)
    @Mapper(SessionMapper.class)
    List<Session> findSessionsByAccessToken(
        @Bind(NameDef.ACCESS_TOKEN) String access_token
    );
	
	@SqlQuery("select * from " + TABLE_NAME + " where "
			+ NameDef.USER_ID + " = :" + NameDef.USER_ID
			+ " and " + NameDef.ACCESS_TOKEN + " = :" + NameDef.ACCESS_TOKEN
			)
    @Mapper(SessionMapper.class)
    List<Session> findSessionsByUserIDAndAccessToken(
        @Bind(NameDef.USER_ID) String user_id,
        @Bind(NameDef.ACCESS_TOKEN) String access_token
    );
	
	@SqlUpdate("delete from " + TABLE_NAME + " where " + NameDef.USER_ID + " = :" + NameDef.USER_ID)
    void deleteByUserID(
        @Bind(NameDef.USER_ID) String user_id
    );
	
	@SqlUpdate("delete from " + TABLE_NAME + " where " + NameDef.ACCESS_TOKEN + " = :" + NameDef.ACCESS_TOKEN)
    void deleteByAccessToken(
        @Bind(NameDef.ACCESS_TOKEN) String access_token
    );
}
