package com.wallet.email.dao;

import com.wallet.email.core.Email;
import com.wallet.email.core.EmailMapper;
import com.wallet.utils.misc.Dict;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.Date;
import java.util.List;

public interface EmailDAO {
	public static final String TABLE_NAME = "email";
	
	@SqlUpdate("create table if not exists " + TABLE_NAME + " ("
			+ "`" + Dict.ID + "` varchar(192) not null unique,"
			+ "`" + Dict.CREATE_TIME + "` datetime not null,"
			+ "`" + Dict.FROM_ADDRESS + "` varchar(64) not null,"
			+ "`" + Dict.TO_ADDRESS + "` varchar(64) not null,"
			+ "`" + Dict.TYPE + "` varchar(64) not null,"
			+ "`" + Dict.STATUS + "` varchar(64) not null,"
			+ "`" + Dict.DATA + "` varbinary(60000),"
			+ "primary key (`" + Dict.ID + "`),"
			+ ")ENGINE=InnoDB DEFAULT CHARSET=utf8 collate=utf8_unicode_ci;"
			)
	void createTable();
	
	@SqlUpdate("drop table if exists " + TABLE_NAME)
	void dropTable();
	
    @SqlUpdate("insert into " + TABLE_NAME + " ("
    		+ Dict.ID
			+ ", " + Dict.CREATE_TIME
    		+ ", " + Dict.FROM_ADDRESS
			+ ", " + Dict.TO_ADDRESS
    		+ ", " + Dict.TYPE
			+ ", " + Dict.STATUS
			+ ", " + Dict.DATA
    		+ ") values ("
    		+ ":" + Dict.ID
			+ ",:" + Dict.CREATE_TIME
    		+ ",:" + Dict.FROM_ADDRESS
			+ ",:" + Dict.TO_ADDRESS
    		+ ",:" + Dict.TYPE
			+ ",:" + Dict.STATUS
			+ ",:" + Dict.DATA
			+ ")"
    		)
    void insert(
            @Bind(Dict.ID) String id
            , @Bind(Dict.CREATE_TIME) Date create_time
            , @Bind(Dict.FROM_ADDRESS) String from_address
            , @Bind(Dict.TO_ADDRESS) String to_address
            , @Bind(Dict.TYPE) String type
			, @Bind(Dict.STATUS) String status
            , @Bind(Dict.DATA) byte[] data
    );

	@SqlUpdate("update " + TABLE_NAME + " set "
			+ Dict.STATUS + "= :" + Dict.STATUS
			+ " where " + Dict.ID + "= :" + Dict.ID
	)
	void updateByID(@Bind(Dict.ID) String id,
					@Bind(Dict.STATUS) String status
	);

    @SqlQuery("select * from " + TABLE_NAME)
    @Mapper(EmailMapper.class)
    List<Email> findAll();
    
	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.FROM_ADDRESS + " = :" + Dict.FROM_ADDRESS)
    @Mapper(EmailMapper.class)
    List<Email> findByFromAddress(
            @Bind(Dict.FROM_ADDRESS) String from_address
    );

	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.TO_ADDRESS + " = :" + Dict.TO_ADDRESS)
	@Mapper(EmailMapper.class)
	List<Email> findByToAddress(
			@Bind(Dict.TO_ADDRESS) String to_address
	);

	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.ID + " = :" + Dict.ID)
    @Mapper(EmailMapper.class)
    List<Email> findByID(
            @Bind(Dict.ID) String id
    );

	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.FROM_ADDRESS + " = :" + Dict.FROM_ADDRESS
			+ " and " + Dict.TO_ADDRESS + " = :" + Dict.TO_ADDRESS)
	@Mapper(EmailMapper.class)
	List<Email> findByFromAddressAndToAddress(
            @Bind(Dict.FROM_ADDRESS) String from_address
            , @Bind(Dict.TO_ADDRESS) String to_address
    );

	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.TO_ADDRESS + " = :" + Dict.TO_ADDRESS
			+ " and " + Dict.STATUS + " = :" + Dict.STATUS)
    @Mapper(EmailMapper.class)
    List<Email> findByToAddressAndStatus(
            @Bind(Dict.TO_ADDRESS) String to_address
            , @Bind(Dict.STATUS) String status
    );

	@SqlQuery("select * from " + TABLE_NAME + " where " + Dict.TYPE + " = :" + Dict.TYPE)
	@Mapper(EmailMapper.class)
	List<Email> findByType(
            @Bind(Dict.TYPE) String type
    );

	@SqlQuery("select from " + TABLE_NAME + " where " + Dict.STATUS + " = :" + Dict.STATUS)
    List<Email> findByStatus(
            @Bind(Dict.STATUS) String status
    );
	
	@SqlUpdate("delete from " + TABLE_NAME + " where " + Dict.ID + " = :" + Dict.ID)
    void deleteByID(
            @Bind(Dict.ID) String id
    );

	@SqlUpdate("delete from " + TABLE_NAME + " where " + Dict.FROM_ADDRESS + " = :" + Dict.FROM_ADDRESS)
	void deleteByFromAddress(
            @Bind(Dict.FROM_ADDRESS) String from_address
    );

	@SqlUpdate("delete from " + TABLE_NAME + " where " + Dict.TO_ADDRESS + " = :" + Dict.TO_ADDRESS
	)
	void deleteByToAddress(
            @Bind(Dict.TO_ADDRESS) String to_address
    );
}
