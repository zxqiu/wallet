package com.wallet.email.core;

import com.wallet.utils.misc.Dict;
import com.wallet.utils.misc.Serializer;

import java.io.*;
import java.util.List;

/**
 * Created by zxqiu on 5/2/17.
 */
public class EmailData implements Serializable, Serializer<EmailData> {
    static final long serialVersionUID = 2540954123698749435L;

    String subject;
    String text;
    String html;
    List picture_id_list;
    List attachment_id_list;


    @Override
    public String toString() {
        return "["
                + Dict.SUBJECT + ":" + subject
                + "," + Dict.TEXT + ":" + text
                + "," + Dict.HTML + ":" + html
                + "," + Dict.PICTURE_ID_LIST + ":" + picture_id_list
                + "," + Dict.ATTACHMET_ID_LIST + ":" + attachment_id_list
                + "," + Dict.USER_LIST + ":" + user_list
                + "]";
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        this.serialize(this, oos);
        return baos.toByteArray();
    }

    @Override
    public void serialize(EmailData emailData, ObjectOutputStream oos) throws IOException {
        oos.writeObject(emailData);
        oos.close();
    }

    @Override
    public EmailData deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        EmailData emailData =  (EmailData) ois.readObject();
        ois.close();
        return emailData;
    }
}
