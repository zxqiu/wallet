package com.wallet.books.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by neo on 3/23/17.
 */
public class Books {
    @JsonProperty
    private String id;

    @JsonProperty
    private String user_id;

    @JsonProperty
    private String create_user_id;

    @JsonProperty
    private String name;

    @JsonProperty
    private Date create_time;

    @JsonProperty
    private Date edit_time; // last event date

    @JsonProperty
    private String picture_id;

    @JsonProperty
    private String data;

    public Books() {
    }

    public Books(String user_id, String create_user_id, String name, Date edit_time, String picture_id, String data) {
        this.setId(user_id + "-" + name);
        this.setUser_id(user_id);
        this.setCreate_user_id(create_user_id);
        this.setName(name);
        this.setCreate_time(new Date());
        this.setEdit_time(edit_time);
        this.setPicture_id(picture_id);
        this.setData(data);
    }

    public Books(String id, String user_id, String create_user_id, String name, Date create_time, Date edit_time, String picture_id
            , String data) {
        this.setId(id);
        this.setUser_id(user_id);
        this.setCreate_user_id(create_user_id);
        this.setName(name);
        this.setCreate_time(create_time);
        this.setEdit_time(edit_time);
        this.setPicture_id(picture_id);
        this.setData(data);
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

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getEdit_time() {
        return edit_time;
    }

    public void setEdit_time(Date edit_time) {
        this.edit_time = edit_time;
    }

    public String getPicture_id() {
        return picture_id;
    }

    public void setPicture_id(String picture_id) {
        this.picture_id = picture_id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCreate_user_id() {
        return create_user_id;
    }

    public void setCreate_user_id(String create_user_id) {
        this.create_user_id = create_user_id;
    }
}
