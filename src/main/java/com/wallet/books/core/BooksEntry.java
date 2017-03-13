package com.wallet.books.core;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BooksEntry {
	@JsonProperty
	private String id;
	
	@JsonProperty
	private String user_id;
	
	@JsonProperty
	private String category;
	
	@JsonProperty
	private Date event_date;
	
	@JsonProperty
	private long amount;
	
	@JsonProperty
	private String note;
	
	@JsonProperty
	private String photo;

	@JsonProperty
	private String attributes;

	@JsonProperty
	private Date edit_time;
	
	public BooksEntry() {
	}
	
	public BooksEntry(String id, String user_id, String category, Date event_date, long amount, String note, String photo, String attributes) {
		this.setId(id);
		this.setUser_id(user_id);
		this.setCategory(category);
		this.setEvent_date(event_date);
		this.setAmount(amount);
		this.setNote(note);
		this.setPhoto(photo);
		this.setAttributes(attributes);
		this.setEdit_time(new Date());
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public Date getEdit_time() {
		return edit_time;
	}

	public void setEdit_time(Date edit_time) {
		this.edit_time = edit_time;
	}

	public Date getEvent_date() {
		return event_date;
	}

	public void setEvent_date(Date event_date) {
		this.event_date = event_date;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}
}
