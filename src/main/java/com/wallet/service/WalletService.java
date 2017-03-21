package com.wallet.service;

import com.fizzed.rocker.runtime.RockerRuntime;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wallet.books.dao.BooksEntryDAO;
import com.wallet.books.dao.BooksEntryDAOConnector;
import com.wallet.books.dao.CategoryDAO;
import com.wallet.books.dao.CategoryDAOConnector;
import com.wallet.books.resource.BooksEntryResource;
import com.wallet.books.resource.CategoryResource;
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


public class WalletService extends Application<WalletConfiguration> {
    public static final String name = "";
    private static final Logger logger_ = LoggerFactory.getLogger(WalletService.class);
    private static String configFileName_ = "wallet.yml";
	public static void main(String[] args) throws Exception {
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
	    final BooksEntryDAO booksEntryDAO = jdbi.onDemand(BooksEntryDAO.class);
	    final CategoryDAO categoryDAO = jdbi.onDemand(CategoryDAO.class);
	    
	    UserDAOConnector.init(userDAO);
	    SessionDAOConnector.init(sessionDAO);
	    BooksEntryDAOConnector.init(booksEntryDAO);
	    CategoryDAOConnector.init(categoryDAO);
	    
	    sessionDAO.dropTable();
	    booksEntryDAO.dropTable();
	    categoryDAO.dropTable();
	    userDAO.dropTable();
	    
	    UserDAOConnector.test();
	    SessionDAOConnector.test();
	    BooksEntryDAOConnector.test();
	    CategoryDAOConnector.test();
	    
	    environment.jersey().register(new UserResource());
	    environment.jersey().register(new SessionResource());
	    environment.jersey().register(new BooksEntryResource());
	    environment.jersey().register(new CategoryResource());

	    environment.jersey().register(new RockerMessageBodyWriter());
	    //RockerRuntime.getInstance().setReloading(true);
	}
}
