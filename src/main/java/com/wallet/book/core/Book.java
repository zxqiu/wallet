package com.wallet.book.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wallet.utils.misc.Dict;
import com.wallet.utils.misc.TimeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zxqiu on 3/23/17.
 */
public class Book {
    @JsonProperty
    private String id;

    @JsonProperty
    private String user_id;

    @JsonProperty
    private String create_user_id;

    @JsonProperty
    private String name;

    @JsonProperty
    private Date edit_time; // last event date

    @JsonProperty
    private String group_id;

    @JsonProperty
    private BookData data;

    public Book() {
    }

    public Book(String user_id, String create_user_id, String name, Date edit_time, String picture_id) throws JSONException {
        this.setId(user_id + TimeUtils.getUniqueTimeStampInMS());
        this.setUser_id(user_id);
        this.setCreate_user_id(create_user_id);
        this.setName(name);
        this.setEdit_time(edit_time);
        this.setGroup_id(create_user_id + TimeUtils.getUniqueTimeStampInMS());

        this.data = new BookData(new Date(), picture_id, new ArrayList<String>());
        this.appendUser(user_id);
    }

    public void update(String name, Date edit_time, String picture_id) {
        this.setName(name);
        this.setEdit_time(edit_time);
        this.setPicture_id(picture_id);
    }

    public void updateBookID() {
        this.setId(this.getUser_id() + "-" + this.getName());
    }

    public void appendUser(String user_id) {
        List<String> user_list = this.getUser_list();
        if (user_list == null) {
            user_list = new ArrayList<>();
        }
        user_list.add(user_id);
        this.setUser_list(user_list);
    }

    public void removeUser(String user_id) throws JSONException {
        List<String> user_list = this.getUser_list();
        if (user_list == null) {
            return;
        }
        user_list.remove(user_id);
        this.setUser_list(user_list);
    }

    @Override
    public String toString() {
        return "["
                + Dict.ID + ":" + id
                + "," + Dict.USER_ID + ":" + user_id
                + "," + Dict.CREATE_USER_ID + ":" + create_user_id
                + "," + Dict.NAME + ":" + name
                + "," + Dict.EDIT_TIME + ":" + edit_time
                + "," + Dict.GROUP_ID + ":" + group_id
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreate_time() {
        if (data == null) {
            return null;
        }
        return data.getCreate_time();
    }

    public void setCreate_time(Date create_time) {
        if (data == null) {
            data = new BookData();
        }
        this.data.setCreate_time(create_time);
    }

    public Date getEdit_time() {
        return edit_time;
    }

    public void setEdit_time(Date edit_time) {
        this.edit_time = edit_time;
    }

    public String getPicture_id() {
        if (data == null) {
            return null;
        }
        return data.getPicture_id();
    }

    public void setPicture_id(String picture_id) {
        if (data == null) {
            data = new BookData();
        }
        this.data.setPicture_id(picture_id);
    }

    public String getCreate_user_id() {
        return create_user_id;
    }

    public void setCreate_user_id(String create_user_id) {
        this.create_user_id = create_user_id;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public BookData getData() {
        return data;
    }

    public void setData(BookData data) {
        this.data = data;
    }

    public List<String> getUser_list() {
        if (data == null) {
            return null;
        }
        return data.getUser_list();
    }

    public void setUser_list(List<String> user_list) {
        if (data == null) {
            data = new BookData();
        }
        this.data.setUser_list(user_list);
    }
}
