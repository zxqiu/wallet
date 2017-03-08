package com.wallet.login.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.wallet.login.core.User;
import com.wallet.login.core.UserMapper;
import com.wallet.utils.misc.NameDef;

public interface UserDAO {
	public static final String TABLE_NAME = "user";
	
	@SqlUpdate("create table if not exists " + TABLE_NAME + " (" +
				"`" + NameDef.USER_ID + "` varchar(64) not null unique," +
				"`" + NameDef.PASSWORD + "` varchar(64) not null," +
				"`" + NameDef.NAME + "` varchar(16) not null," +
				"`" + NameDef.PRIORITY + "` varchar(16) not null," +
				"primary key (`" + NameDef.USER_ID + "`)" +
				")ENGINE = InnoDB DEFAULT CHARSET = utf8"
			)
	void createTable();
	
	@SqlUpdate("drop table if exists " + TABLE_NAME)
	void dropTable();
	
	@SqlUpdate("insert into " + TABLE_NAME + " (" +
				NameDef.USER_ID + ", " +
				NameDef.PASSWORD + "," +
				NameDef.NAME + "," +
				NameDef.PRIORITY +
				") values (" +
				":" + NameDef.USER_ID +
				", :" + NameDef.PASSWORD +
				", :" + NameDef.NAME + 
				", :" + NameDef.PRIORITY +
				")"
			)
	void insert(@Bind(NameDef.USER_ID) String user_id,
				@Bind(NameDef.PASSWORD) String password,
				@Bind(NameDef.NAME) String name,
				@Bind(NameDef.PRIORITY) String priority);
	
	@SqlUpdate("update " + TABLE_NAME + " set " +
				NameDef.PASSWORD + "= :" + NameDef.PASSWORD + "," +
				NameDef.NAME + "= :" + NameDef.NAME + "," +
				NameDef.PRIORITY + "= :" + NameDef.PRIORITY +
				" where " + NameDef.USER_ID + "=" + NameDef.USER_ID
			)
	void update(@Bind(NameDef.USER_ID) String user_id,
				@Bind(NameDef.PASSWORD) String password,
				@Bind(NameDef.NAME) String name,
				@Bind(NameDef.PRIORITY) String priority);
	
	@SqlQuery("select * from " + TABLE_NAME + " where " + NameDef.USER_ID + " = :" + NameDef.USER_ID)
    @Mapper(UserMapper.class)
    List<User> findByID(
        @Bind(NameDef.USER_ID) String user_id
    );
	
    @SqlQuery("select * from " + TABLE_NAME + " where " +
    			NameDef.USER_ID + " = :" + NameDef.USER_ID +
    			" and " + NameDef.PASSWORD + " = :" + NameDef.PASSWORD)
    @Mapper(UserMapper.class)
    List<User> findByUserIDAndPassword(
        @Bind(NameDef.USER_ID) String user_id,
        @Bind(NameDef.PASSWORD) String password
    );
    
    @SqlQuery("select * from " + TABLE_NAME)
    @Mapper(UserMapper.class)
    List<User> findAll();
    
	@SqlUpdate("delete from " + TABLE_NAME + " where " + NameDef.USER_ID + " = :" + NameDef.USER_ID)
    void deleteByID(
        @Bind(NameDef.USER_ID) String user_id
    );
}
