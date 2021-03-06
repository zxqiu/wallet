package com.wallet.book.core;

import com.wallet.utils.misc.Serializer;

import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * Created by zxqiu on 4/6/17.
 */
public class BookLogData implements Serializable, Serializer<BookLogData> {
    static final long serialVersionUID = -4450997690508038751L;

    private BookLog.BOOK_LOG_NOTE note;

    public BookLogData() {
    }

    public BookLogData(BookLog.BOOK_LOG_NOTE note) {
        this.note = note;
    }

    public BookLogData(InputStream in) throws NullPointerException, IOException, ClassNotFoundException {
        if (in == null) {
            throw new NullPointerException("InputStream is null when build BookData");
        }
        ObjectInputStream ois = new ObjectInputStream(in);
        BookLogData bookLogData = deserialize(ois);

        this.setNote(bookLogData.getNote());

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

    public BookLog.BOOK_LOG_NOTE getNote() {
        return note;
    }

    public void setNote(BookLog.BOOK_LOG_NOTE note) {
        this.note = note;
    }
}
