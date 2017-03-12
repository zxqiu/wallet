package com.wallet.books.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.wallet.books.core.Category;
import com.wallet.books.core.CategoryMapper;
import com.wallet.login.dao.UserDAO;
import com.wallet.utils.misc.NameDef;

public interface CategoryDAO {
public static final String TABLE_NAME = "category";
	
	@SqlUpdate("create table if not exists " + TABLE_NAME + " ("
			+ "`" + NameDef.ID + "` varchar(64) not null unique,"
			+ "`" + NameDef.USER_ID + "` varchar(64) not null,"
			+ "`" + NameDef.NAME + "` varchar(64) not null,"
			+ "`" + NameDef.PICTURE_ID + "` varchar(64),"
			+ "primary key (`" + NameDef.ID + "`),"
			+ "key `fk_category_user` (`" + NameDef.USER_ID + "`),"
			+ "constraint `fk_category_user` foreign key (`" + NameDef.USER_ID + "`) "
			+ "references `" + UserDAO.TABLE_NAME + "` (`" + NameDef.USER_ID + "`)"
			+ ")ENGINE = InnoDB DEFAULT CHARSET = utf8"
			)
	void createTable();
	
	@SqlUpdate("drop table if exists " + TABLE_NAME)
	void dropTable();
	
    @SqlUpdate("insert into " + TABLE_NAME + " ("
    		+ NameDef.ID
    		+ ", " + NameDef.USER_ID 
    		+ ", " + NameDef.NAME
    		+ ", " + NameDef.PICTURE_ID
    		+ ") values ("
    		+ ":" + NameDef.ID
    		+ ",:" + NameDef.USER_ID
    		+ ",:" + NameDef.NAME
    		+ ",:" + NameDef.PICTURE_ID + ")"
    		)
    void insert(
        @Bind(NameDef.ID) String id
        ,@Bind(NameDef.USER_ID) String user_id
        ,@Bind(NameDef.NAME) String name
        ,@Bind(NameDef.PICTURE_ID) String picture_id 
    );
    
    @SqlUpdate("update " + TABLE_NAME + " set "
			+ NameDef.PICTURE_ID + "= :" + NameDef.PICTURE_ID
			+ " where " + NameDef.ID + "= :" + NameDef.ID
		)
	void update(@Bind(NameDef.ID) String id,
			@Bind(NameDef.PICTURE_ID) String picture_id);
    
    @SqlQuery("select * from " + TABLE_NAME)
    @Mapper(CategoryMapper.class)
    List<Category> findAll();
    
	@SqlQuery("select * from " + TABLE_NAME + " where " + NameDef.USER_ID + " = :" + NameDef.USER_ID)
    @Mapper(CategoryMapper.class)
    List<Category> findByUserID(
        @Bind(NameDef.USER_ID) String user_id
    );
	
	@SqlQuery("select * from " + TABLE_NAME + " where " + NameDef.ID + " = :" + NameDef.ID)
    @Mapper(CategoryMapper.class)
    List<Category> findByID(
        @Bind(NameDef.ID) String id
    );
	
	@SqlQuery("select * from " + TABLE_NAME + " where " + NameDef.ID + " = :" + NameDef.ID
			+ " and " + NameDef.USER_ID + " = : " + NameDef.USER_ID)
    @Mapper(CategoryMapper.class)
    List<Category> findByIDAndUserID(
        @Bind(NameDef.ID) String id,
        @Bind(NameDef.USER_ID) String user_id
    );
	
	@SqlUpdate("delete from " + TABLE_NAME + " where " + NameDef.USER_ID + " = :" + NameDef.USER_ID)
    void deleteByUserID(
        @Bind(NameDef.USER_ID) String user_id
    );
	
	@SqlUpdate("delete from " + TABLE_NAME + " where " + NameDef.ID + " = :" + NameDef.ID)
    void deleteByID(
        @Bind(NameDef.ID) String id
    );
}
