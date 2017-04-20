package com.wallet.login.core;

import java.util.Date;
import java.util.UUID;

public class Session {

    private String user_id;
    private String access_token;
    private Date create_time;
    
    public Session() {
    }

    public Session(String user_id) {
    	this.setUser_id(user_id);
        this.setAccess_token(UUID.randomUUID().toString() + UUID.randomUUID().toString());
        this.setCreate_time(new Date());
    }

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

}