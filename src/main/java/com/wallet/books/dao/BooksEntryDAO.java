package com.wallet.books.dao;

import java.util.Date;
import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.wallet.books.core.BooksEntry;
import com.wallet.books.core.BooksEntryMapper;
import com.wallet.login.dao.UserDAO;

import utils.NameDef;

public interface BooksEntryDAO {
public static final String TABLE_NAME = "books_entry";
	
	@SqlUpdate("create table if not exists " + TABLE_NAME + " ("
				+ "`" + NameDef.ID + "` varchar(64) not null unique,"
				+ "`" + NameDef.USER_ID + "` varchar(64) not null,"
				+ "`" + NameDef.CATEGORY + "` varchar(32) not null,"
				+ "`" + NameDef.EVENT_DATE + "` datetime not null,"
				+ "`" + NameDef.AMOUNT + "` bigint (64) not null,"
				+ "`" + NameDef.NOTE + "` text,"
				+ "`" + NameDef.PHOTO + "` varchar(64),"
				+ "`" + NameDef.EDIT_TIME + "` datetime not null,"
				+ "primary key (`" + NameDef.ID + "`),"
				+ "key `fk_books_entry_user` (`" + NameDef.USER_ID + "`),"
				+ "constraint `fk_books_entry_user` foreign key (`" + NameDef.USER_ID + "`) "
				+ "references `" + UserDAO.TABLE_NAME + "` (`" + NameDef.USER_ID + "`)"
				+ ")ENGINE = InnoDB DEFAULT CHARSET = utf8"
			)
	void createTable();
	
	@SqlUpdate("drop table if exists " + TABLE_NAME)
	void dropTable();
	
	@SqlUpdate("insert into " + TABLE_NAME + " ("
			+ NameDef.ID + ", "
			+ NameDef.USER_ID + ", "
			+ NameDef.CATEGORY + ", "
			+ NameDef.EVENT_DATE + ", "
			+ NameDef.AMOUNT + ", "
			+ NameDef.NOTE + ", "
			+ NameDef.PHOTO + ", "
			+ NameDef.EDIT_TIME
			+ ") values ("
			+ ":" + NameDef.ID
			+ ", :" + NameDef.USER_ID
			+ ", :" + NameDef.CATEGORY
			+ ", :" + NameDef.EVENT_DATE
			+ ", :" + NameDef.AMOUNT
			+ ", :" + NameDef.NOTE
			+ ", :" + NameDef.PHOTO
			+ ", :" + NameDef.EDIT_TIME
			+ ")"
		)
	void insert(@Bind(NameDef.ID) String id,
				@Bind(NameDef.USER_ID) String user_id,
				@Bind(NameDef.CATEGORY) String category,
				@Bind(NameDef.EVENT_DATE) Date event_date,
				@Bind(NameDef.AMOUNT) long amount,
				@Bind(NameDef.NOTE) String note,
				@Bind(NameDef.PHOTO) String photo,
				@Bind(NameDef.EDIT_TIME) Date edit_time);
	
	@SqlUpdate("update " + TABLE_NAME + " set "
			+ NameDef.USER_ID + "= :" + NameDef.USER_ID + ", "
			+ NameDef.CATEGORY + "= :" + NameDef.CATEGORY + ","
			+ NameDef.EVENT_DATE + "= :" + NameDef.EVENT_DATE + ","
			+ NameDef.AMOUNT + "= :" + NameDef.AMOUNT + ","
			+ NameDef.NOTE + "= :" + NameDef.NOTE + ","
			+ NameDef.PHOTO + "= :" + NameDef.PHOTO + ","
			+ NameDef.EDIT_TIME + "= :" + NameDef.EDIT_TIME
			+ " where " + NameDef.ID + "=" + NameDef.ID
		)
	void update(@Bind(NameDef.ID) String id,
			@Bind(NameDef.USER_ID) String user_id,
			@Bind(NameDef.CATEGORY) String category,
			@Bind(NameDef.EVENT_DATE) Date event_date,
			@Bind(NameDef.AMOUNT) long amount,
			@Bind(NameDef.NOTE) String note,
			@Bind(NameDef.PHOTO) String photo,
			@Bind(NameDef.EDIT_TIME) Date edit_time);
	
	@SqlQuery("select * from " + TABLE_NAME + " where " + NameDef.ID + " = :" + NameDef.ID)
    @Mapper(BooksEntryMapper.class)
    List<BooksEntry> findByID(
        @Bind(NameDef.ID) String id
    );
	
	@SqlQuery("select * from " + TABLE_NAME + " where " + NameDef.USER_ID + " = :" + NameDef.USER_ID)
    @Mapper(BooksEntryMapper.class)
    List<BooksEntry> findByUserID(
        @Bind(NameDef.USER_ID) String user_id
    );
    
    @SqlQuery("select * from " + TABLE_NAME)
    @Mapper(BooksEntryMapper.class)
    List<BooksEntry> findAll();
    
	@SqlUpdate("delete from " + TABLE_NAME + " where " + NameDef.ID + " = :" + NameDef.ID)
    void deleteByID(
        @Bind(NameDef.ID) String id
    );
	
	@SqlUpdate("delete from " + TABLE_NAME + " where " + NameDef.USER_ID + " = :" + NameDef.USER_ID)
    void deleteByUserID(
        @Bind(NameDef.USER_ID) String user_id
    );
}
