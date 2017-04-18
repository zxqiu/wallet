package com.wallet.book.core;

import com.wallet.utils.misc.Serializer;

import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * Created by zxqiu on 4/6/17.
 */
public class BookLogData implements Serializable, Serializer<BookLogData> {
    static final long serialVersionUID = -2540990340508039435L;

    private String note;

    public BookLogData() {
    }

    public BookLogData(String note) {
        this.note = note;
    }

    public BookLogData(InputStream in) throws NullPointerException, IOException, ClassNotFoundException {
        if (in == null) {
            throw new NullPointerException("InputStream is null when build BookData");
        }
        ObjectInputStream ois = new ObjectInputStream(in);
        BookLogData bookLogData = deserialize(ois);

        in.close();
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        this.serialize(this, oos);
        return baos.toByteArray();
    }

    @Override
    public void serialize(BookLogData bookLogData, ObjectOutputStream oos) throws IOException {
        oos.writeObject(bookLogData);
        oos.close();
    }

    @Override
    public BookLogData deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        BookLogData bookLogData =  (BookLogData) ois.readObject();
        ois.close();
        return bookLogData;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
