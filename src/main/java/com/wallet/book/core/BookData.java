package com.wallet.book.core;

import com.wallet.utils.misc.Serializer;

import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * Created by zxqiu on 4/6/17.
 */
public class BookData implements Serializable, Serializer<BookData> {
    private Date create_time;

    private String picture_id;

    private List<String> user_list; // store user id

    public BookData() {
    }

    public BookData(InputStream in) throws NullPointerException, IOException, ClassNotFoundException {
        if (in == null) {
            throw new NullPointerException("InputStream is null when build BookData");
        }
        ObjectInputStream ois = new ObjectInputStream(in);
        BookData bookData = deserialize(ois);

        this.setCreate_time(bookData.getCreate_time());
        this.setPicture_id(bookData.getPicture_id());
        this.setUser_list(bookData.getUser_list());
        in.close();
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        this.serialize(this, oos);
        return baos.toByteArray();
    }

    @Override
    public void serialize(BookData bookData, ObjectOutputStream oos) throws IOException {
        oos.writeObject(bookData);
        oos.close();
    }

    @Override
    public BookData deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        BookData bookData =  (BookData) ois.readObject();
        ois.close();
        return bookData;
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

    public List<String> getUser_list() {
        return user_list;
    }

    public void setUser_list(List<String> user_list) {
        this.user_list = user_list;
    }
}
