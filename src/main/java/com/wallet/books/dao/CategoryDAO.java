package com.wallet.books.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.wallet.books.core.Category;
import com.wallet.books.core.CategoryMapper;
import com.wallet.login.dao.UserDAO;
import com.wallet.utils.misc.Dict;

public interface CategoryDAO {
	public static final String TABLE_NAME = "category";
	
	@SqlUpdate("create table if not exists " + TABLE_NAME + " ("
			+ "`" + Dict.ID + "` varchar(64) not null unique,"
			+ "`" + Dict.USER_ID + "` varchar(64) not null,"
			+ "`" + Dict.NAME + "` varchar(64) not null,"
			+ "`" + Dict.PICTURE_ID + "` varchar(64),"
			+ "primary key (`" + Dict.ID + "`),"
			+ "key `fk_category_user` (`" + Dict.USER_ID + "`),"
			+ "constraint `fk_category_user` foreign key (`" + Dict.USER_ID + "`) "
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
    		+ ", " + Dict.PICTURE_ID
    		+ ") values ("
    		+ ":" + Dict.ID
    		+ ",:" + Dict.USER_ID
    		+ ",:" + Dict.NAME
    		+ ",:" + Dict.PICTURE_ID + ")"
    		)
    void insert(
        @Bind(Dict.ID) String id
        ,@Bind(Dict.USER_ID) String user_id
        ,@Bind(Dict.NAME) String name
        ,@Bind(Dict.PICTURE_ID) String picture_id
    );
    
    @SqlUpdate("update " + TABLE_NAME + " set "
			+ Dict.PICTURE_ID + "= :" + Dict.PICTURE_ID
			+ " where " + Dict.ID + "= :" + Dict.ID
		)
	void update(@Bind(Dict.ID) String id,
			@Bind(Dict.PICTURE_ID) String picture_id);
    
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
	
	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.ID + " = :" + Dict.ID
			+ " and " + Dict.USER_ID + " = : " + Dict.USER_ID)
    @Mapper(CategoryMapper.class)
    List<Category> findByIDAndUserID(
        @Bind(Dict.ID) String id,
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
