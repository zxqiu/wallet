package com.wallet.books.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wallet.utils.misc.Dict;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public Books(String user_id, String create_user_id, String name, Date edit_time, String picture_id, String data) throws JSONException {
        this.setId(user_id + "-" + name);
        this.setUser_id(user_id);
        this.setCreate_user_id(create_user_id);
        this.setName(name);
        this.setCreate_time(new Date());
        this.setEdit_time(edit_time);
        this.setPicture_id(picture_id);
        this.setData(data);

        this.appendUser(user_id);
    }

    public Books(String id, String user_id, String create_user_id, String name, Date create_time, Date edit_time, String picture_id
            , String data) throws JSONException {
        this.setId(id);
        this.setUser_id(user_id);
        this.setCreate_user_id(create_user_id);
        this.setName(name);
        this.setCreate_time(create_time);
        this.setEdit_time(edit_time);
        this.setPicture_id(picture_id);
        this.setData(data);

        this.appendUser(user_id);
    }

    public void appendUser(String user_id) throws JSONException {
        String dataString = this.getData();

        JSONObject data = new JSONObject(dataString);
        JSONArray user_list;

        if (!data.has(Dict.USER_LIST)) {
            data.put(Dict.USER_LIST, new JSONArray());
        }

        user_list = data.getJSONArray(Dict.USER_LIST);
        for (int i = 0; i < user_list.length(); i++) {
            if (user_id.equals(user_list.getString(i))) {
                return;
            }
        }

        user_list.put(user_id);

        this.setData(data.toString());
    }

    public void removeUser(String user_id) throws JSONException {
        String dataString = this.getData();

        JSONObject data = new JSONObject(dataString);
        JSONArray user_list;
        JSONArray new_user_list = new JSONArray();

        if (!data.has(Dict.USER_LIST)) {
            return;
        }

        user_list = data.getJSONArray(Dict.USER_LIST);

        for (int i = 0; i < user_list.length(); i++) {
            String cur_user_id = user_list.getString(i);
            if (!cur_user_id.equals(user_id)) {
                new_user_list.put(cur_user_id);
            }
        }

        data.put(Dict.USER_LIST, new_user_list);
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
