package com.wallet.book.dao;

import com.wallet.book.core.BookLog;
import com.wallet.book.core.BookLogMapper;
import com.wallet.utils.misc.Dict;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.Date;
import java.util.List;

public interface BookLogDAO {
	public static final String TABLE_NAME = "book_log";
	
	@SqlUpdate("create table if not exists " + TABLE_NAME + " ("
			+ "`" + Dict.ID + "` varchar(192) not null unique,"
			+ "`" + Dict.USER_ID + "` varchar(64) not null,"
			+ "`" + Dict.BOOK_GROUP_ID + "` varchar(192) not null,"
			+ "`" + Dict.BOOK_ENTRY_GROUP_ID + "` varchar(192) not null,"
			+ "`" + Dict.CATEGORY_GROUP_ID + "` varchar(192) not null,"
			+ "`" + Dict.CREATE_TIME + "` datetime not null,"
			+ "`" + Dict.OPERATION + "` varchar(32) not null,"
			+ "`" + Dict.TYPE + "` varchar(32) not null,"
			+ "`" + Dict.DATA + "` varbinary(60000),"
			+ "primary key (`" + Dict.ID + "`)"
			+ ")ENGINE=InnoDB DEFAULT CHARSET=utf8 collate=utf8_unicode_ci;"
			)
	void createTable();
	
	@SqlUpdate("drop table if exists " + TABLE_NAME)
	void dropTable();
	
    @SqlUpdate("insert into " + TABLE_NAME + " ("
    		+ Dict.ID
			+ ", " + Dict.CREATE_TIME
    		+ ", " + Dict.USER_ID
			+ ", " + Dict.BOOK_GROUP_ID
			+ ", " + Dict.BOOK_ENTRY_GROUP_ID
			+ ", " + Dict.CATEGORY_GROUP_ID
			+ ", " + Dict.OPERATION
			+ ", " + Dict.TYPE
			+ ", " + Dict.DATA
    		+ ") values ("
    		+ ":" + Dict.ID
			+ ",:" + Dict.CREATE_TIME
			+ ",:" + Dict.USER_ID
			+ ",:" + Dict.BOOK_GROUP_ID
			+ ",:" + Dict.BOOK_ENTRY_GROUP_ID
			+ ",:" + Dict.CATEGORY_GROUP_ID
			+ ",:" + Dict.OPERATION
			+ ",:" + Dict.TYPE
			+ ",:" + Dict.DATA
			+ ")"
    		)
    void insert(
            @Bind(Dict.ID) String id
			, @Bind(Dict.CREATE_TIME) Date create_time
            , @Bind(Dict.USER_ID) String user_id
			, @Bind(Dict.BOOK_GROUP_ID) String book_group_id
			, @Bind(Dict.BOOK_ENTRY_GROUP_ID) String book_entry_group_id
			, @Bind(Dict.CATEGORY_GROUP_ID) String category_group_id
			, @Bind(Dict.OPERATION) String operation
			, @Bind(Dict.TYPE) String type
            , @Bind(Dict.DATA) byte[] data
    );
    
    @SqlQuery("select * from " + TABLE_NAME)
    @Mapper(BookLogMapper.class)
    List<BookLog> findAll();

	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.ID + " = :" + Dict.ID)
	@Mapper(BookLogMapper.class)
	List<BookLog> findByID(
			@Bind(Dict.ID) String id
	);

	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.USER_ID + " = :" + Dict.USER_ID)
    @Mapper(BookLogMapper.class)
    List<BookLog> findByUserID(
            @Bind(Dict.USER_ID) String user_id
    );

	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.BOOK_GROUP_ID + " = :" + Dict.BOOK_GROUP_ID)
	@Mapper(BookLogMapper.class)
	List<BookLog> findByBookGroupID(
            @Bind(Dict.BOOK_GROUP_ID) String book_group_id
    );

	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.BOOK_ENTRY_GROUP_ID + " = :" + Dict.BOOK_ENTRY_GROUP_ID)
    @Mapper(BookLogMapper.class)
    List<BookLog> findByBookEntryGroupID(
            @Bind(Dict.BOOK_ENTRY_GROUP_ID) String book_entry_group_id
    );

	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.CATEGORY_GROUP_ID + " = :" + Dict.CATEGORY_GROUP_ID)
	@Mapper(BookLogMapper.class)
	List<BookLog> findByCategoryGroupGroupID(
            @Bind(Dict.CATEGORY_GROUP_ID) String category_group_id
    );

	@SqlQuery("select * from " + TABLE_NAME + " where "
			+ Dict.BOOK_GROUP_ID + " = :" + Dict.BOOK_GROUP_ID
			+ " and " + Dict.USER_ID + " = :" + Dict.USER_ID)
	@Mapper(BookLogMapper.class)
	List<BookLog> findByBookGroupIDAndUserID(
            @Bind(Dict.BOOK_GROUP_ID) String book_group_id,
            @Bind(Dict.USER_ID) String user_id
    );

	@SqlUpdate("delete from " + TABLE_NAME + " where " + Dict.ID + " = :" + Dict.ID)
    void deleteByID(
            @Bind(Dict.ID) String id
    );
}
