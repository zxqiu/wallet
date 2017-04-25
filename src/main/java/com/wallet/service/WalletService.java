package com.wallet.service;

import com.fizzed.rocker.runtime.RockerRuntime;
import com.wallet.book.core.BookLog;
import com.wallet.book.core.syncHelper;
import com.wallet.book.dao.*;
import com.wallet.book.resource.BookEntryResource;
import com.wallet.book.resource.BookLogResource;
import com.wallet.book.resource.BookResource;
import com.wallet.filter.GeneralRequestFilter;
import com.wallet.healthCheck.resource.serverCheckResource;
import com.wallet.tinyUrl.dao.TinyUrlDAO;
import com.wallet.tinyUrl.dao.TinyUrlDAOConnector;
import com.wallet.tinyUrl.resource.TinyUrlResource;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wallet.book.resource.CategoryResource;
import com.wallet.login.dao.SessionDAO;
import com.wallet.login.dao.SessionDAOConnector;
import com.wallet.login.dao.UserDAO;
import com.wallet.login.dao.UserDAOConnector;
import com.wallet.login.resource.SessionResource;
import com.wallet.login.resource.UserResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.jdbi.DBIFactory;

import javax.servlet.DispatcherType;
import java.util.EnumSet;


public class WalletService extends Application<WalletConfiguration> {
    public static final String name = "";
    private static final Logger logger_ = LoggerFactory.getLogger(WalletService.class);
    private static String configFileName_ = "wallet.yml";

    private static boolean isCleanup = false;
    private static boolean isRelease = false;

	public static void main(String[] args) throws Exception {
		for (String s : args) {
			if (s.equals("--cleanup")) {
				isCleanup = true;
			} else if (s.equals("--release")) {
				isRelease = true;
			}
		}
        new WalletService().run(new String[] { "server" , configFileName_});
    }

    @Override
    public void initialize(Bootstrap<WalletConfiguration> bootstrap) {
		//TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    	bootstrap.addBundle(new AssetsBundle("/assets/css", "/css", null, "css"));
        bootstrap.addBundle(new AssetsBundle("/assets/js", "/js", null, "js"));
		bootstrap.addBundle(new AssetsBundle("/assets/fonts", "/fonts", null, "fonts"));
    }

	@Override
	public void run(WalletConfiguration configuration, Environment environment) throws Exception {
		final DBIFactory factory = new DBIFactory();
	    final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "mysql");
	    
	    final UserDAO userDAO = jdbi.onDemand(UserDAO.class);
	    final SessionDAO sessionDAO = jdbi.onDemand(SessionDAO.class);
		final BookLogDAO bookLogDAO = jdbi.onDemand(BookLogDAO.class);
	    final BookDAO bookDAO = jdbi.onDemand(BookDAO.class);
	    final BookEntryDAO bookEntryDAO = jdbi.onDemand(BookEntryDAO.class);
	    final CategoryDAO categoryDAO = jdbi.onDemand(CategoryDAO.class);
	    final TinyUrlDAO tinyUrlDAO = jdbi.onDemand(TinyUrlDAO.class);

	    if (isCleanup) {
			cleanupDB(sessionDAO, bookLogDAO, bookDAO, bookEntryDAO, categoryDAO, userDAO, tinyUrlDAO);
		}

		BookEntryCache.init(bookEntryDAO);

	    UserDAOConnector.init(userDAO);
	    SessionDAOConnector.init(sessionDAO);
		BookLogConnector.init(bookLogDAO);
	    BookConnector.init(bookDAO);
	    BookEntryConnector.init(bookEntryDAO);
	    CategoryConnector.init(categoryDAO);
		TinyUrlDAOConnector.init(tinyUrlDAO);

		BookEntryCache.test();

	    UserDAOConnector.test();
	    SessionDAOConnector.test();
	    BookLogConnector.test();
		BookConnector.test();
	    BookEntryConnector.test();
	    CategoryConnector.test();
	    TinyUrlDAOConnector.test();

	    environment.jersey().register(new UserResource());
	    environment.jersey().register(new SessionResource());
		environment.jersey().register(new BookLogResource());
		environment.jersey().register(new BookResource());
	    environment.jersey().register(new BookEntryResource());
	    environment.jersey().register(new CategoryResource());
		environment.jersey().register(new TinyUrlResource());
		environment.jersey().register(new serverCheckResource());

	    environment.jersey().register(new RockerMessageBodyWriter());

	    if (!isRelease) {
			RockerRuntime.getInstance().setReloading(true);
		}

		syncHelper.init();

	}

	private void cleanupDB(SessionDAO sessionDAO, BookLogDAO bookLogDAO, BookDAO bookDAO, BookEntryDAO bookEntryDAO, CategoryDAO categoryDAO
			, UserDAO userDAO, TinyUrlDAO tinyUrlDAO) {
	    logger_.info("Clean up all database");

		sessionDAO.dropTable();
		bookDAO.dropTable();
		bookEntryDAO.dropTable();
		categoryDAO.dropTable();
		bookLogDAO.dropTable();
		userDAO.dropTable();
		tinyUrlDAO.dropTable();

		userDAO.createTable();
		sessionDAO.createTable();
		bookLogDAO.createTable();
		bookDAO.createTable();
		bookEntryDAO.createTable();
		categoryDAO.createTable();
		tinyUrlDAO.createTable();
	}
}
