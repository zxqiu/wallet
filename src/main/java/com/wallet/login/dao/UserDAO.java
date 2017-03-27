package com.wallet.login.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.wallet.login.core.User;
import com.wallet.login.core.UserMapper;
import com.wallet.utils.misc.Dict;

public interface UserDAO {
	public static final String TABLE_NAME = "user";
	
	@SqlUpdate("create table if not exists " + TABLE_NAME + " ("
			+ "`" + Dict.USER_ID + "` varchar(64) not null unique,"
			+ "`" + Dict.EMAIL + "` varchar(64) not null unique,"
			+ "`" + Dict.PASSWORD + "` varchar(64) not null,"
			+ "`" + Dict.NAME + "` varchar(16) not null,"
			+ "`" + Dict.PRIORITY + "` varchar(16) not null,"
			+ "`" + Dict.DATA + "` text,"
			+ "primary key (`" + Dict.USER_ID + "`)"
			+ ")ENGINE=InnoDB DEFAULT CHARSET=utf8 collate=utf8_unicode_ci;"
		)
	void createTable();
	
	@SqlUpdate("drop table if exists " + TABLE_NAME)
	void dropTable();
	
	@SqlUpdate("insert into " + TABLE_NAME + " ("
			+ Dict.USER_ID
			+ ", " + Dict.EMAIL
			+ ", " + Dict.PASSWORD
			+ ", " + Dict.NAME
			+ ", " + Dict.PRIORITY
			+ ", " + Dict.DATA
			+ ") values ("
			+ ":" + Dict.USER_ID
			+ ", :" + Dict.EMAIL
			+ ", :" + Dict.PASSWORD
			+ ", :" + Dict.NAME
			+ ", :" + Dict.PRIORITY
			+ ", :" + Dict.DATA
			+ ")"
		)
	void insert(@Bind(Dict.USER_ID) String user_id
			, @Bind(Dict.EMAIL) String email
			, @Bind(Dict.PASSWORD) String password
			, @Bind(Dict.NAME) String name
			, @Bind(Dict.PRIORITY) String priority
			, @Bind(Dict.DATA) String data
	);
	
	@SqlUpdate("update " + TABLE_NAME + " set "
			+ Dict.EMAIL + "= :" + Dict.EMAIL
			+ ", " + Dict.PASSWORD + "= :" + Dict.PASSWORD
			+ ", " + Dict.NAME + "= :" + Dict.NAME
			+ ", " + Dict.PRIORITY + "= :" + Dict.PRIORITY
			+ ", " + Dict.DATA + "= :" + Dict.DATA
			+ " where " + Dict.USER_ID + "= :" + Dict.USER_ID
		)
	void update(@Bind(Dict.USER_ID) String user_id
			, @Bind(Dict.EMAIL) String email
			, @Bind(Dict.PASSWORD) String password
			, @Bind(Dict.NAME) String name
			, @Bind(Dict.PRIORITY) String priority
			, @Bind(Dict.DATA) String data
	);
	
	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.USER_ID + " = :" + Dict.USER_ID)
    @Mapper(UserMapper.class)
    List<User> findByID(
        @Bind(Dict.USER_ID) String user_id
    );
	
    @SqlQuery("select * from " + TABLE_NAME + " where " +
    			Dict.USER_ID + " = :" + Dict.USER_ID +
    			" and " + Dict.PASSWORD + " = :" + Dict.PASSWORD)
    @Mapper(UserMapper.class)
    List<User> findByUserIDAndPassword(
        @Bind(Dict.USER_ID) String user_id
        , @Bind(Dict.PASSWORD) String password
    );

	@SqlQuery("select * from " + TABLE_NAME + " where " +
			Dict.EMAIL + " = :" + Dict.EMAIL +
			" and " + Dict.PASSWORD + " = :" + Dict.PASSWORD)
	@Mapper(UserMapper.class)
	List<User> findByEmailAndPassword(
			@Bind(Dict.EMAIL) String email
			, @Bind(Dict.PASSWORD) String password
	);

	@SqlQuery("select * from " + TABLE_NAME + " where " +
			Dict.EMAIL + " = :" + Dict.EMAIL)
	@Mapper(UserMapper.class)
	List<User> findByEmail(
			@Bind(Dict.EMAIL) String email
	);

    @SqlQuery("select * from " + TABLE_NAME)
    @Mapper(UserMapper.class)
    List<User> findAll();
    
	@SqlUpdate("delete from " + TABLE_NAME + " where " + Dict.USER_ID + " = :" + Dict.USER_ID)
    void deleteByID(
        @Bind(Dict.USER_ID) String user_id
    );
}
