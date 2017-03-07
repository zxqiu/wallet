package com.wallet.books.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Category {
	@JsonProperty
	private String id;
	
	@JsonProperty
	private String user_id;
	
	@JsonProperty
	private String name;
	
	@JsonProperty
	private String picture_id;
	
	public Category() {
	}
	
	public Category(String id, String user_id, String name, String picture_id) {
		this.setId(id);
		this.setUser_id(user_id);
		this.setName(name);
		this.setPicture_id(picture_id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPicture_id() {
		return picture_id;
	}

	public void setPicture_id(String picture_id) {
		this.picture_id = picture_id;
	}
}
