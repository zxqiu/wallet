package com.wallet.book.dao;

import com.wallet.book.core.Book;
import com.wallet.book.core.BookMapper;
import com.wallet.login.dao.UserDAO;
import com.wallet.utils.misc.Dict;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.Date;
import java.util.List;

public interface BookDAO {
	public static final String TABLE_NAME = "book";
	
	@SqlUpdate("create table if not exists " + TABLE_NAME + " ("
			+ "`" + Dict.ID + "` varchar(192) not null unique,"
			+ "`" + Dict.USER_ID + "` varchar(64) not null,"
			+ "`" + Dict.CREATE_USER_ID + "` varchar(64) not null,"
			+ "`" + Dict.NAME + "` varchar(64) not null,"
			+ "`" + Dict.EDIT_TIME + "` datetime not null,"
			+ "`" + Dict.GROUP_ID + "` varchar(64) not null,"
			+ "`" + Dict.DATA + "` varbinary(32768),"
			+ "primary key (`" + Dict.ID + "`),"
			+ "unique key `book_unique_combined` (`" + Dict.USER_ID + "`,`" + Dict.NAME + "`),"
			+ "key `fk_book_user` (`" + Dict.USER_ID + "`),"
			+ "constraint `fk_book_user` foreign key (`" + Dict.USER_ID + "`) "
			+ "references `" + UserDAO.TABLE_NAME + "` (`" + Dict.USER_ID + "`)"
			+ ")ENGINE=InnoDB DEFAULT CHARSET=utf8 collate=utf8_unicode_ci;"
			)
	void createTable();
	
	@SqlUpdate("drop table if exists " + TABLE_NAME)
	void dropTable();
	
    @SqlUpdate("insert into " + TABLE_NAME + " ("
    		+ Dict.ID
    		+ ", " + Dict.USER_ID
			+ ", " + Dict.CREATE_USER_ID
    		+ ", " + Dict.NAME
			+ ", " + Dict.EDIT_TIME
			+ ", " + Dict.GROUP_ID
			+ ", " + Dict.DATA
    		+ ") values ("
    		+ ":" + Dict.ID
    		+ ",:" + Dict.USER_ID
			+ ",:" + Dict.CREATE_USER_ID
    		+ ",:" + Dict.NAME
			+ ",:" + Dict.EDIT_TIME
			+ ",:" + Dict.GROUP_ID
			+ ",:" + Dict.DATA
			+ ")"
    		)
    void insert(
            @Bind(Dict.ID) String id
            , @Bind(Dict.USER_ID) String user_id
			, @Bind(Dict.CREATE_USER_ID) String create_user_id
            , @Bind(Dict.NAME) String name
			, @Bind(Dict.EDIT_TIME) Date edit_time
			, @Bind(Dict.GROUP_ID) String group_id
			, @Bind(Dict.DATA) byte[] data
    );
    
    @SqlUpdate("update " + TABLE_NAME + " set "
			+ Dict.NAME + "= :" + Dict.NAME
			+ ", " + Dict.EDIT_TIME + "= :" + Dict.EDIT_TIME
			+ ", " + Dict.DATA + "= :" + Dict.DATA
			+ " where " + Dict.ID + "= :" + Dict.ID
		)
	void update(@Bind(Dict.ID) String id
            , @Bind(Dict.NAME) String name
			, @Bind(Dict.EDIT_TIME) Date edit_time
			, @Bind(Dict.DATA) byte[] data
	);

	@SqlUpdate("update " + TABLE_NAME + " set "
			+ Dict.NAME + "= :" + Dict.NAME
			+ ", " + Dict.EDIT_TIME + "= :" + Dict.EDIT_TIME
			+ ", " + Dict.DATA + "= :" + Dict.DATA
			+ " where " + Dict.GROUP_ID + "= :" + Dict.GROUP_ID
	)
	void updateByGroupID(@Bind(Dict.GROUP_ID) String group_id
			, @Bind(Dict.NAME) String name
			, @Bind(Dict.EDIT_TIME) Date edit_time
			, @Bind(Dict.DATA) byte[] data
	);

    @SqlQuery("select * from " + TABLE_NAME)
    @Mapper(BookMapper.class)
    List<Book> findAll();
    
	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.USER_ID + " = :" + Dict.USER_ID)
    @Mapper(BookMapper.class)
    List<Book> findByUserID(
            @Bind(Dict.USER_ID) String user_id
    );

	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.CREATE_USER_ID + " = :" + Dict.CREATE_USER_ID)
	@Mapper(BookMapper.class)
	List<Book> findByCreateUserID(
			@Bind(Dict.CREATE_USER_ID) String create_user_id
	);

	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.ID + " = :" + Dict.ID)
    @Mapper(BookMapper.class)
    List<Book> findByID(
            @Bind(Dict.ID) String id
    );

	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.GROUP_ID + " = :" + Dict.GROUP_ID)
	@Mapper(BookMapper.class)
	List<Book> findByGroupID(
			@Bind(Dict.GROUP_ID) String group_id
	);

	@SqlQuery("select * from " + TABLE_NAME + " where "
			+ Dict.NAME + " = :" + Dict.NAME
			+ " and " + Dict.USER_ID + " = :" + Dict.USER_ID)
	@Mapper(BookMapper.class)
	List<Book> findByNameAndUserID(
			@Bind(Dict.NAME) String name,
			@Bind(Dict.USER_ID) String user_id
	);

	@SqlQuery("select * from " + TABLE_NAME + " where "
			+ Dict.NAME + " = :" + Dict.NAME
			+ " and " + Dict.CREATE_USER_ID + " = :" + Dict.CREATE_USER_ID)
	@Mapper(BookMapper.class)
	List<Book> findByNameAndCreateUserID(
			@Bind(Dict.NAME) String name,
			@Bind(Dict.CREATE_USER_ID) String create_user_id
	);

	@SqlQuery("select " + Dict.BOOK_GROUP_ID + " from " + TABLE_NAME + " where " + Dict.ID + " = :" + Dict.ID)
	List<String> findGroupIDByID(
			@Bind(Dict.ID) String id
	);

	@SqlUpdate("delete from " + TABLE_NAME + " where " + Dict.USER_ID + " = :" + Dict.USER_ID)
    void deleteByUserID(
            @Bind(Dict.USER_ID) String user_id
    );
	
	@SqlUpdate("delete from " + TABLE_NAME + " where " + Dict.ID + " = :" + Dict.ID)
    void deleteByID(
            @Bind(Dict.ID) String id
    );
}
