package com.wallet.book.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wallet.utils.misc.TimeUtils;

public class Category {
	@JsonProperty
	private String id;

	@JsonProperty
	private String group_id;

	@JsonProperty
	private String user_id;

	@JsonProperty
	private String book_group_id;

	@JsonProperty
	private String name;

	@JsonProperty
	private String picture_id;

	@JsonProperty
	private String data;

	public Category() {
	}
	
	public Category(String user_id, String book_group_id, String name, String picture_id, String data) {
		this.setId(user_id + book_group_id + name);
		this.setGroup_id(user_id + TimeUtils.getUniqueTimeStampInMS());
		this.setUser_id(user_id);
		this.setBook_group_id(book_group_id);
		this.setName(name);
		this.setPicture_id(picture_id);
		this.setData(data);
	}

	public void update(Category src) {
		this.setPicture_id(src.getPicture_id());
	}

	public void updateIDByUserID() {
	    this.setId(this.user_id + this.book_group_id + this.name);
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getBook_group_id() {
		return book_group_id;
	}

	public void setBook_group_id(String book_group_id) {
		this.book_group_id = book_group_id;
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
}
