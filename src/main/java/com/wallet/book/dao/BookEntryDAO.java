package com.wallet.book.dao;

import java.util.Date;
import java.util.List;

import com.wallet.book.core.BookEntry;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.wallet.book.core.BookEntryMapper;
import com.wallet.login.dao.UserDAO;
import com.wallet.utils.misc.Dict;

public interface BookEntryDAO {
public static final String TABLE_NAME = "book_entry";
	
	@SqlUpdate("create table if not exists " + TABLE_NAME + " ("
			+ "`" + Dict.ID + "` varchar(64) not null unique,"
			+ "`" + Dict.USER_ID + "` varchar(64) not null,"
			+ "`" + Dict.CREATE_USER_ID + "` varchar(64) not null,"
			+ "`" + Dict.BOOK_ID + "` varchar(64) not null,"
			+ "`" + Dict.BOOK_GROUP_ID + "` varchar(64) not null,"
			+ "`" + Dict.GROUP_ID + "` varchar(64) not null,"
			+ "`" + Dict.CATEGORY + "` varchar(32) not null,"
			+ "`" + Dict.EVENT_DATE + "` datetime not null,"
			+ "`" + Dict.AMOUNT + "` bigint not null,"
			+ "`" + Dict.NOTE + "` text,"
			+ "`" + Dict.PHOTO + "` varchar(64),"
			+ "`" + Dict.DATA + "` text,"
			+ "`" + Dict.EDIT_TIME + "` datetime not null,"
			+ "`" + Dict.CREATE_TIME + "` datetime not null,"
			+ "primary key (`" + Dict.ID + "`),"
			+ "key `fk_book_entry_user` (`" + Dict.USER_ID + "`),"
			+ "constraint `fk_book_entry_user` foreign key (`" + Dict.USER_ID + "`) "
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
			+ ", " + Dict.BOOK_ID
			+ ", " + Dict.BOOK_GROUP_ID
			+ ", " + Dict.GROUP_ID
			+ ", " + Dict.CATEGORY
			+ ", " + Dict.EVENT_DATE
			+ ", " + Dict.AMOUNT
			+ ", " + Dict.NOTE
			+ ", " + Dict.PHOTO
			+ ", " + Dict.DATA
			+ ", " + Dict.EDIT_TIME
			+ ", " + Dict.CREATE_TIME
			+ ") values ("
			+ ":" + Dict.ID
			+ ", :" + Dict.USER_ID
			+ ", :" + Dict.CREATE_USER_ID
			+ ", :" + Dict.BOOK_ID
			+ ", :" + Dict.BOOK_GROUP_ID
			+ ", :" + Dict.GROUP_ID
			+ ", :" + Dict.CATEGORY
			+ ", :" + Dict.EVENT_DATE
			+ ", :" + Dict.AMOUNT
			+ ", :" + Dict.NOTE
			+ ", :" + Dict.PHOTO
			+ ", :" + Dict.DATA
			+ ", :" + Dict.EDIT_TIME
			+ ", :" + Dict.CREATE_TIME
			+ ")"
		)
	void insert(@Bind(Dict.ID) String id
			, @Bind(Dict.USER_ID) String user_id
			, @Bind(Dict.CREATE_USER_ID) String create_user_id
			, @Bind(Dict.BOOK_ID) String book_id
			, @Bind(Dict.BOOK_GROUP_ID) String book_group_id
			, @Bind(Dict.GROUP_ID) String group_id
			, @Bind(Dict.CATEGORY) String category
			, @Bind(Dict.EVENT_DATE) Date event_date
			, @Bind(Dict.AMOUNT) long amount
			, @Bind(Dict.NOTE) String note
			, @Bind(Dict.PHOTO) String photo
			, @Bind(Dict.DATA) String data
			, @Bind(Dict.EDIT_TIME) Date edit_time
			, @Bind(Dict.CREATE_TIME) Date create_time
	);
	
	@SqlUpdate("update " + TABLE_NAME + " set "
			+ Dict.BOOK_ID + "= :" + Dict.BOOK_ID + ", "
			+ Dict.BOOK_GROUP_ID + "= :" + Dict.BOOK_GROUP_ID + ", "
			+ Dict.CATEGORY + "= :" + Dict.CATEGORY + ","
			+ Dict.EVENT_DATE + "= :" + Dict.EVENT_DATE + ","
			+ Dict.AMOUNT + "= :" + Dict.AMOUNT + ","
			+ Dict.NOTE + "= :" + Dict.NOTE + ","
			+ Dict.PHOTO + "= :" + Dict.PHOTO + ","
			+ Dict.DATA + "= :" + Dict.DATA + ","
			+ Dict.EDIT_TIME + "= :" + Dict.EDIT_TIME
			+ " where " + Dict.ID + "= :" + Dict.ID
		)
	void update(@Bind(Dict.ID) String id,
				@Bind(Dict.BOOK_ID) String book_id,
				@Bind(Dict.BOOK_GROUP_ID) String book_group_id,
				@Bind(Dict.CATEGORY) String category,
				@Bind(Dict.EVENT_DATE) Date event_date,
				@Bind(Dict.AMOUNT) long amount,
				@Bind(Dict.NOTE) String note,
				@Bind(Dict.PHOTO) String photo,
				@Bind(Dict.DATA) String data,
				@Bind(Dict.EDIT_TIME) Date edit_time);

	@SqlUpdate("update " + TABLE_NAME + " set "
			+ Dict.CATEGORY + "= :" + Dict.CATEGORY + ","
			+ Dict.EVENT_DATE + "= :" + Dict.EVENT_DATE + ","
			+ Dict.AMOUNT + "= :" + Dict.AMOUNT + ","
			+ Dict.NOTE + "= :" + Dict.NOTE + ","
			+ Dict.PHOTO + "= :" + Dict.PHOTO + ","
			+ Dict.DATA + "= :" + Dict.DATA + ","
			+ Dict.EDIT_TIME + "= :" + Dict.EDIT_TIME
			+ " where " + Dict.GROUP_ID + "= :" + Dict.GROUP_ID
	)
	void updateByGroupID(@Bind(Dict.GROUP_ID) String group_id,
				@Bind(Dict.CATEGORY) String category,
				@Bind(Dict.EVENT_DATE) Date event_date,
				@Bind(Dict.AMOUNT) long amount,
				@Bind(Dict.NOTE) String note,
				@Bind(Dict.PHOTO) String photo,
				@Bind(Dict.DATA) String data,
				@Bind(Dict.EDIT_TIME) Date edit_time);

	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.ID + " = :" + Dict.ID)
    @Mapper(BookEntryMapper.class)
    List<BookEntry> findByID(
        @Bind(Dict.ID) String id
    );

	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.BOOK_ID + " = :" + Dict.BOOK_ID)
	@Mapper(BookEntryMapper.class)
	List<BookEntry> findByBookID(
			@Bind(Dict.BOOK_ID) String book_id
	);

	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.BOOK_GROUP_ID + " = :" + Dict.BOOK_GROUP_ID)
	@Mapper(BookEntryMapper.class)
	List<BookEntry> findByBookGroupID(
			@Bind(Dict.BOOK_GROUP_ID) String book_group_id
	);

	@SqlQuery("select * from " + TABLE_NAME + " where "
			+ Dict.BOOK_GROUP_ID + " = :" + Dict.BOOK_GROUP_ID
			+ " and " + Dict.USER_ID + " = :" + Dict.USER_ID
	)
	@Mapper(BookEntryMapper.class)
	List<BookEntry> findByUserIDAndBookGroupID(
			@Bind(Dict.USER_ID) String user_id
			, @Bind(Dict.BOOK_GROUP_ID) String book_group_id
	);

	@SqlQuery("select * from " + TABLE_NAME + " where "
			+ Dict.GROUP_ID + " = :" + Dict.GROUP_ID
			+ " and " + Dict.USER_ID + " = :" + Dict.USER_ID
	)
	@Mapper(BookEntryMapper.class)
	List<BookEntry> findByUserIDAndGroupID(
			@Bind(Dict.USER_ID) String user_id
			, @Bind(Dict.GROUP_ID) String group_id
	);

	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.USER_ID + " = :" + Dict.USER_ID)
    @Mapper(BookEntryMapper.class)
    List<BookEntry> findByUserID(
        @Bind(Dict.USER_ID) String user_id
    );
    
    @SqlQuery("select * from " + TABLE_NAME)
    @Mapper(BookEntryMapper.class)
    List<BookEntry> findAll();
    
	@SqlUpdate("delete from " + TABLE_NAME + " where " + Dict.ID + " = :" + Dict.ID)
    void deleteByID(
        @Bind(Dict.ID) String id
    );

	@SqlUpdate("delete from " + TABLE_NAME + " where " + Dict.GROUP_ID + " = :" + Dict.GROUP_ID)
	void deleteByGroupID(
			@Bind(Dict.GROUP_ID) String group_id
	);

	@SqlUpdate("delete from " + TABLE_NAME + " where " + Dict.USER_ID + " = :" + Dict.USER_ID)
    void deleteByUserID(
        @Bind(Dict.USER_ID) String user_id
    );
}
