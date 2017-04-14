package com.wallet.book.core;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wallet.utils.misc.Dict;
import com.wallet.utils.misc.TimeUtils;
import org.json.JSONException;

public class BookEntry {
	@JsonProperty
	private String id;

	@JsonProperty
	private String group_id;

	@JsonProperty
	private String user_id;

	@JsonProperty
	private String create_user_id;

	@JsonProperty
	private String book_group_id;

	@JsonProperty
	private String category_group_id;
	
	@JsonProperty
	private Date event_date;
	
	@JsonProperty
	private long amount; // in cents
	
	@JsonProperty
	private Date edit_time;

	@JsonProperty
	private BookEntryData data;

	public BookEntry() {
	}
	
	public BookEntry(String user_id, String create_user_id, String book_group_id, String category_group_id
			, Date event_date, long amount, String note, String picture_id) throws JSONException {
		this.setId(user_id + TimeUtils.getUniqueTimeStampInMS());
		this.setUser_id(user_id);
		this.setCreate_user_id(create_user_id);
		this.setBook_group_id(book_group_id);
		this.setGroup_id(create_user_id + TimeUtils.getUniqueTimeStampInMS());
		this.setCategory_group_id(category_group_id);
		this.setEvent_date(event_date);
		this.setAmount(amount);
		this.setEdit_time(new Date());

		this.data = new BookEntryData(new Date(), note, picture_id);
	}

	public void update(String book_group_id, String category_group_id, Date event_date, long amount, String note
			, String picture_id) {
		this.setBook_group_id(book_group_id);
		this.setCategory_group_id(category_group_id);
		this.setAmount(amount);
		this.setEvent_date(event_date);

		this.setNote(note);
		this.setPicture_id(picture_id);
	}

	public void updateIDWithUserID() {
		this.setId(this.getUser_id() + TimeUtils.getUniqueTimeStampInMS());
	}

	@Override
	public String toString() {
		return "["
				+ Dict.USER_ID + ":" + id
				+ "," + Dict.GROUP_ID + ":" + group_id
				+ "," + Dict.USER_ID + ":" +user_id
				+ "," + Dict.CREATE_USER_ID + ":" + create_user_id
				+ "," + Dict.BOOK_GROUP_ID + ":" + book_group_id
				+ "," + Dict.CATEGORY_GROUP_ID + ":" + category_group_id
				+ "," + Dict.EVENT_DATE + ":" + event_date
				+ "," + Dict.AMOUNT + ":" + amount
				+ "," + Dict.EDIT_TIME + ":" + edit_time
				+ "," + Dict.DATA + ":" + data.toString()
				+ "]";
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

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
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

	public String getCreate_user_id() {
		return create_user_id;
	}

	public void setCreate_user_id(String create_user_id) {
		this.create_user_id = create_user_id;
	}

	public BookEntryData getData() {
		return data;
	}

	public void setData(BookEntryData data) {
		this.data = data;
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getBook_group_id() {
		return book_group_id;
	}

	public void setBook_group_id(String book_group_id) {
		this.book_group_id = book_group_id;
	}

	public String getCategory_group_id() {
		return category_group_id;
	}

	public void setCategory_group_id(String category_group_id) {
		this.category_group_id = category_group_id;
	}

	public Date getCreate_time() {
		return data.getCreate_time();
	}

	public void setCreate_time(Date create_time) {
		this.data.setCreate_time(create_time);
	}

	public String getNote() {
		return data.getNote();
	}

	public void setNote(String note) {
		this.data.setNote(note);
	}

	public String getPicture_id() {
		return data.getPicture_id();
	}

	public void setPicture_id(String picture_id) {
		this.data.setPicture_id(picture_id);
	}
}
