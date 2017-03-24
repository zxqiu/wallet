package com.wallet.books.dao;

import com.wallet.books.core.Books;
import com.wallet.books.core.BooksMapper;
import com.wallet.login.dao.UserDAO;
import com.wallet.utils.misc.Dict;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.Date;
import java.util.List;

public interface BooksDAO {
	public static final String TABLE_NAME = "books";
	
	@SqlUpdate("create table if not exists " + TABLE_NAME + " ("
			+ "`" + Dict.ID + "` varchar(64) not null unique,"
			+ "`" + Dict.USER_ID + "` varchar(64) not null,"
			+ "`" + Dict.NAME + "` varchar(64) not null,"
			+ "`" + Dict.CREATE_TIME + "` datetime not null,"
			+ "`" + Dict.EDIT_TIME + "` datetime not null,"
			+ "`" + Dict.PICTURE_ID + "` varchar(64),"
			+ "`" + Dict.DATA + "` text,"
			+ "primary key (`" + Dict.ID + "`),"
			+ "key `fk_books_user` (`" + Dict.USER_ID + "`),"
			+ "constraint `fk_books_user` foreign key (`" + Dict.USER_ID + "`) "
			+ "references `" + UserDAO.TABLE_NAME + "` (`" + Dict.USER_ID + "`)"
			+ ")ENGINE = InnoDB DEFAULT CHARSET = utf8"
			)
	void createTable();
	
	@SqlUpdate("drop table if exists " + TABLE_NAME)
	void dropTable();
	
    @SqlUpdate("insert into " + TABLE_NAME + " ("
    		+ Dict.ID
    		+ ", " + Dict.USER_ID
    		+ ", " + Dict.NAME
    		+ ", " + Dict.CREATE_TIME
			+ ", " + Dict.EDIT_TIME
			+ ", " + Dict.PICTURE_ID
			+ ", " + Dict.DATA
    		+ ") values ("
    		+ ":" + Dict.ID
    		+ ",:" + Dict.USER_ID
    		+ ",:" + Dict.NAME
			+ ",:" + Dict.CREATE_TIME
			+ ",:" + Dict.EDIT_TIME
			+ ",:" + Dict.PICTURE_ID
			+ ",:" + Dict.DATA
			+ ")"
    		)
    void insert(
            @Bind(Dict.ID) String id
            , @Bind(Dict.USER_ID) String user_id
            , @Bind(Dict.NAME) String name
            , @Bind(Dict.CREATE_TIME) Date create_time
			, @Bind(Dict.EDIT_TIME) Date edit_time
			, @Bind(Dict.PICTURE_ID) String picture_id
			, @Bind(Dict.DATA) String data
    );
    
    @SqlUpdate("update " + TABLE_NAME + " set "
			+ Dict.NAME + "= :" + Dict.NAME
			+ ", " + Dict.EDIT_TIME + "= :" + Dict.EDIT_TIME
			+ ", " + Dict.PICTURE_ID + "= :" + Dict.PICTURE_ID
			+ ", " + Dict.DATA + "= :" + Dict.DATA
			+ " where " + Dict.ID + "= :" + Dict.ID
		)
	void update(@Bind(Dict.ID) String id
            , @Bind(Dict.NAME) String name
			, @Bind(Dict.EDIT_TIME) Date edit_time
			, @Bind(Dict.PICTURE_ID) String picture_id
			, @Bind(Dict.DATA) String data
	);
    
    @SqlQuery("select * from " + TABLE_NAME)
    @Mapper(BooksMapper.class)
    List<Books> findAll();
    
	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.USER_ID + " = :" + Dict.USER_ID)
    @Mapper(BooksMapper.class)
    List<Books> findByUserID(
            @Bind(Dict.USER_ID) String user_id
    );
	
	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.ID + " = :" + Dict.ID)
    @Mapper(BooksMapper.class)
    List<Books> findByID(
            @Bind(Dict.ID) String id
    );
	
	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.NAME + " = :" + Dict.NAME
			+ " and " + Dict.USER_ID + " = : " + Dict.USER_ID)
    @Mapper(BooksMapper.class)
    List<Books> findByNameAndUserID(
            @Bind(Dict.NAME) String name,
            @Bind(Dict.USER_ID) String user_id
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
