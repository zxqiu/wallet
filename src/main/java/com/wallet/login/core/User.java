package com.wallet.login.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    @JsonProperty
    private String user_id;

    @JsonProperty
    private String password;

    @JsonProperty
    private String name;

    @JsonProperty
    private String priority;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	
	public User() {
	}
	
	public User(String user_id, String password, String name, String priority) {
		this.setUser_id(user_id);
		this.setPassword(password);
		this.setName(name);
		this.setPriority(priority);
	}
}
