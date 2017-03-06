package com.wallet.service;
import java.net.InetAddress;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class WalletConfiguration extends Configuration {
    @NotEmpty
    private String projectName_ = "wallet";
    
    @NotEmpty
    private static int timeOut_ = 5000;
    
    @JsonProperty
    public String getTemplate() {
        return projectName_;
    }

    @JsonProperty
    public void setProjectName(String projectName) {
        this.projectName_ = projectName;
    }
    
    @JsonProperty
    public String getProjectName() {
        return projectName_;
    }
    
    @JsonProperty
    public static int getTimeOut() {
        return timeOut_;
    }
    
    
    /******************** database *************************/
    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory factory) {
        this.database = factory;
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }
    
	/********************* custom ****************************/
    
    private static String hostAddr = "10.0.0.132:8080";
    private static String hostName = "10.0.0.132";
    
	public static String getHostAddr() {
		return hostAddr;
	}

	public static void setHostAddr(String hostAddr) {
		WalletConfiguration.hostAddr = hostAddr;
	}

	public static String getHostName() {
		return hostName;
	}

	public static void setHostName(String hostName) {
		WalletConfiguration.hostName = hostName;
	}


}
