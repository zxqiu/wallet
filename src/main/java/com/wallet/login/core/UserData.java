package com.wallet.login.core;

import com.wallet.utils.misc.Serializer;

import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * Created by zxqiu on 4/6/17.
 */
public class UserData implements Serializable, Serializer<UserData> {
    static final long serialVersionUID = -2540990340508039435L;

    private Date create_time;
    private String picture_id;

    public UserData() {
    }

    public UserData(Date create_time, String picture_id) {
        this.setCreate_time(create_time);
        this.setPicture_id(picture_id);
    }

    public UserData(InputStream in) throws NullPointerException, IOException, ClassNotFoundException {
        if (in == null) {
            throw new NullPointerException("InputStream is null when build UserData");
        }
        ObjectInputStream ois = new ObjectInputStream(in);
        UserData userData = deserialize(ois);

        this.setCreate_time(userData.getCreate_time());
        this.setPicture_id(userData.getPicture_id());
        in.close();
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        this.serialize(this, oos);
        return baos.toByteArray();
    }

    @Override
    public void serialize(UserData userData, ObjectOutputStream oos) throws IOException {
        oos.writeObject(userData);
        oos.close();
    }

    @Override
    public UserData deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        UserData userData = (UserData) ois.readObject();
        ois.close();
        return userData;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public String getPicture_id() {
        return picture_id;
    }

    public void setPicture_id(String picture_id) {
        this.picture_id = picture_id;
    }
}
