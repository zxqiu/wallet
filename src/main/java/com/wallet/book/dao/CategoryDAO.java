package com.wallet.book.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.wallet.book.core.Category;
import com.wallet.book.core.CategoryMapper;
import com.wallet.login.dao.UserDAO;
import com.wallet.utils.misc.Dict;

public interface CategoryDAO {
	public static final String TABLE_NAME = "category";
	
	@SqlUpdate("create table if not exists " + TABLE_NAME + " ("
			+ "`" + Dict.ID + "` varchar(192) not null unique,"
			+ "`" + Dict.GROUP_ID + "` varchar(64) not null,"
			+ "`" + Dict.USER_ID + "` varchar(64) not null,"
			+ "`" + Dict.BOOK_GROUP_ID + "` varchar(64) not null,"
			+ "`" + Dict.NAME + "` varchar(64) not null,"
			+ "`" + Dict.PICTURE_ID + "` varchar(64),"
			+ "`" + Dict.DATA + "` text,"
			+ "primary key (`" + Dict.ID + "`),"
            + "unique key `category_unique_combined` (`" + Dict.USER_ID + "`,`" + Dict.BOOK_GROUP_ID + "`,`" + Dict.NAME + "`),"
			+ "key `fk_category_user` (`" + Dict.USER_ID + "`),"
			+ "constraint `fk_category_user` foreign key (`" + Dict.USER_ID + "`) "
			+ "references `" + UserDAO.TABLE_NAME + "` (`" + Dict.USER_ID + "`)"
			+ ")ENGINE=InnoDB DEFAULT CHARSET=utf8 collate=utf8_unicode_ci;"
			)
	void createTable();
	
	@SqlUpdate("drop table if exists " + TABLE_NAME)
	void dropTable();
	
    @SqlUpdate("insert into " + TABLE_NAME + " ("
    		+ Dict.ID
			+ ", " + Dict.GROUP_ID
    		+ ", " + Dict.USER_ID
			+ ", " + Dict.BOOK_GROUP_ID
    		+ ", " + Dict.NAME
    		+ ", " + Dict.PICTURE_ID
			+ ", " + Dict.DATA
    		+ ") values ("
    		+ ":" + Dict.ID
			+ ",:" + Dict.GROUP_ID
    		+ ",:" + Dict.USER_ID
			+ ",:" + Dict.BOOK_GROUP_ID
    		+ ",:" + Dict.NAME
    		+ ",:" + Dict.PICTURE_ID
			+ ",:" + Dict.DATA
			+ ")"
    		)
    void insert(
        	@Bind(Dict.ID) String id
			, @Bind(Dict.GROUP_ID) String group_id
        	, @Bind(Dict.USER_ID) String user_id
			, @Bind(Dict.BOOK_GROUP_ID) String book_group_id
        	, @Bind(Dict.NAME) String name
        	, @Bind(Dict.PICTURE_ID) String picture_id
			, @Bind(Dict.DATA) String data
    );
    
    @SqlUpdate("update " + TABLE_NAME + " set "
			+ Dict.PICTURE_ID + "= :" + Dict.PICTURE_ID
			+ ", " + Dict.DATA + "= :" + Dict.DATA
			+ " where " + Dict.ID + "= :" + Dict.ID
		)
	void updateByID(@Bind(Dict.ID) String id
			, @Bind(Dict.PICTURE_ID) String picture_id
			, @Bind(Dict.DATA) String data
	);

	@SqlUpdate("update " + TABLE_NAME + " set "
			+ Dict.PICTURE_ID + "= :" + Dict.PICTURE_ID
			+ ", " + Dict.DATA + "= :" + Dict.DATA
			+ " where " + Dict.GROUP_ID + "= :" + Dict.GROUP_ID
	)
	void updateByGroupID(@Bind(Dict.GROUP_ID) String group_id
			, @Bind(Dict.PICTURE_ID) String picture_id
			, @Bind(Dict.DATA) String data
	);

    @SqlQuery("select * from " + TABLE_NAME)
    @Mapper(CategoryMapper.class)
    List<Category> findAll();
    
	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.USER_ID + " = :" + Dict.USER_ID)
    @Mapper(CategoryMapper.class)
    List<Category> findByUserID(
        @Bind(Dict.USER_ID) String user_id
    );
	
	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.ID + " = :" + Dict.ID)
    @Mapper(CategoryMapper.class)
    List<Category> findByID(
        @Bind(Dict.ID) String id
    );

	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.GROUP_ID + " = :" + Dict.GROUP_ID)
	@Mapper(CategoryMapper.class)
	List<Category> findByGroupID(
			@Bind(Dict.ID) String group_id
	);

	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.BOOK_GROUP_ID + " = :" + Dict.BOOK_GROUP_ID
			+ " and " + Dict.USER_ID + " = :" + Dict.USER_ID)
    @Mapper(CategoryMapper.class)
    List<Category> findByUserIDAndBookGroupID(
        @Bind(Dict.USER_ID) String user_id
        , @Bind(Dict.BOOK_GROUP_ID) String book_group_id
    );

	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.BOOK_GROUP_ID + " = :" + Dict.BOOK_GROUP_ID)
	@Mapper(CategoryMapper.class)
	List<Category> findByBookGroupID(
			@Bind(Dict.BOOK_GROUP_ID) String book_group_id
	);

	@SqlUpdate("delete from " + TABLE_NAME + " where " + Dict.USER_ID + " = :" + Dict.USER_ID)
    void deleteByUserID(
        @Bind(Dict.USER_ID) String user_id
    );
	
	@SqlUpdate("delete from " + TABLE_NAME + " where " + Dict.ID + " = :" + Dict.ID)
    void deleteByID(
        @Bind(Dict.ID) String id
    );

	@SqlUpdate("delete from " + TABLE_NAME + " where " + Dict.GROUP_ID + " = :" + Dict.GROUP_ID)
	void deleteByGroupID(
			@Bind(Dict.GROUP_ID) String group_id
	);

	@SqlUpdate("delete from " + TABLE_NAME
			+ " where " + Dict.USER_ID + " = :" + Dict.USER_ID
			+ " and " + Dict.BOOK_GROUP_ID + " = :" + Dict.BOOK_GROUP_ID
	)
	void deleteByUserIDAndBookGroupID(
			@Bind(Dict.USER_ID) String user_id
			, @Bind(Dict.BOOK_GROUP_ID) String book_group_id
	);
}
