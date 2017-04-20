package com.wallet.book.core;

import com.wallet.utils.misc.Serializer;

import java.io.*;
import java.util.Date;

/**
 * Created by zxqiu on 4/6/17.
 */
public class BookEntryData implements Serializable, Serializer<BookEntryData> {
    static final long serialVersionUID = -3223924565786962317L;

    private Date create_time;
    private String note;
    private String pictureTimeStamp;
    private String picture_id;

    public BookEntryData(Date create_time, String note, String pictureTimeStamp, String picture_id) {
        this.setCreate_time(create_time);
        this.setNote(note);
        this.setPictureTimeStamp(pictureTimeStamp);
        this.setPicture_id(picture_id);
    }

    public BookEntryData(InputStream in) throws IOException, ClassNotFoundException {
        if (in == null) {
            throw new NullPointerException("InputStream is null when build BookEntryData");
        }
        ObjectInputStream ois = new ObjectInputStream(in);
        BookEntryData bookEntryData = deserialize(ois);

        this.setCreate_time(bookEntryData.getCreate_time());
        this.setNote(bookEntryData.getNote());
        this.setPictureTimeStamp(bookEntryData.getPictureTimeStamp());
        this.setPicture_id(bookEntryData.getPicture_id());
    }

    public BookEntryData clone() {
        return new BookEntryData(this.getCreate_time(), this.getNote(), this.getPicture_id());
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        this.serialize(this, oos);
        return baos.toByteArray();
    }

    @Override
    public void serialize(BookEntryData bookEntryData, ObjectOutputStream oos) throws IOException {
        oos.writeObject(bookEntryData);
        oos.close();
    }

    @Override
    public BookEntryData deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        BookEntryData bookEntryData =  (BookEntryData) ois.readObject();
        ois.close();
        return bookEntryData;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPictureTimeStamp() {
        return pictureTimeStamp;
    }

    public void setPictureTimeStamp(String pictureTimeStamp) {
        this.pictureTimeStamp = pictureTimeStamp;
    }

    public String getPicture_id() {
        return picture_id;
    }

    public void setPicture_id(String picture_id) {
        this.picture_id = picture_id;
    }
}
