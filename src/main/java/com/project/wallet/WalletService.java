package com.project.wallet;

import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.project.wallet.service.TestGetResource;

import books.service.BooksControlService;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.assets.AssetsBundle;


public class WalletService extends Application<WalletProjectConfiguration> {
    public static final String name = "";
    private static final Logger logger_ = LoggerFactory.getLogger(WalletService.class);
    private static String configFileName_ = "wallet.yml";
	public static void main(String[] args) throws Exception {
        new WalletService().run(new String[] { "server" , configFileName_});
    }

    @Override
    public void initialize(Bootstrap<WalletProjectConfiguration> bootstrap) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    	//bootstrap.addBundle(new AssetsBundle("/assets/css", "/css", null, "css"));
        //bootstrap.addBundle(new AssetsBundle("/assets/js", "/js", null, "js"));
    	bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html", "html"));
    }

	@Override
	public void run(WalletProjectConfiguration configuration, Environment environment) throws Exception {
	    final TestGetResource resource = new TestGetResource( configuration.getProjectName());
	    environment.jersey().register(resource);
	    
	    //System.out.println(DynamoDBConnector.instance().listMyTables());
	    //initPostDataBase();
	    
	    environment.jersey().register(new BooksControlService());
	    
	    //logger_.warn("initiate store configuration");
	}
}
