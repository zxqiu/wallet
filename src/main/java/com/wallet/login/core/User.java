package com.wallet.login.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wallet.utils.misc.Hashing;
import com.wallet.utils.misc.TimeUtils;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class User {
    public enum USER_PRIORITY {ADMIN, VIP, NORMAL}

    public enum USER_TYPE {FACEBOOK_USER, LOCAL_USER}

    @JsonProperty
    private String user_id;

    @JsonProperty
    private String email;

    @JsonProperty
    private String password;

    @JsonProperty
    private String name;

    @JsonProperty
    private USER_PRIORITY priority;

    @JsonProperty
    private USER_TYPE type;

    @JsonProperty
    private UserData data;

    public User() {
	}

    public User(String email, String password, String name, USER_PRIORITY priority, USER_TYPE type, String picture_id)
            throws NoSuchAlgorithmException {
        long timeStamp = TimeUtils.getUniqueTimeStampInMS();
        this.setUser_id(Hashing.MD5Hash(email + name + priority + timeStamp) + timeStamp);
        this.setEmail(email);
        this.setPassword(password);
        this.setName(name);
        this.setType(type);
        this.setPriority(priority);

        this.setData(new UserData(new Date(), picture_id));
    }

    // for test only
	public User(String user_id, String email, String password, String name, USER_PRIORITY priority, Date create_time
            , USER_TYPE type, String picture_id) {
		this.setUser_id(user_id);
		this.setEmail(email);
		this.setPassword(password);
		this.setName(name);
		this.setType(type);
		this.setPriority(priority);

		this.setData(new UserData(create_time, picture_id));
	}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

	public USER_PRIORITY getPriority() {
		return priority;
	}

	public void setPriority(USER_PRIORITY priority) {
		this.priority = priority;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }

    public Date getCreate_time() {
        if (this.getData() == null) {
            return null;
        }
        return this.getData().getCreate_time();
    }

    public void setCreate_time(Date create_time) {
        if (this.getData() == null) {
            this.setData(new UserData());
        }
        this.getData().setCreate_time(create_time);
    }

    public String getPicture_id() {
        if (this.getData() == null) {
            return null;
        }
        return this.getData().getPicture_id();
    }

    public void setPicture_id(String picture_id) {
        if (this.getData() == null) {
            this.setData(new UserData());
        }
        this.getData().setPicture_id(picture_id);
    }

    public USER_TYPE getType() {
        return type;
    }

    public void setType(USER_TYPE type) {
        this.type = type;
    }
}
