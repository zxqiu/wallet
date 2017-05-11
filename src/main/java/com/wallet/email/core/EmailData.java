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

    private String subject;
    private String text;
    private String html;
    private List<String> picture_id_list;
    private List<String> attachment_id_list;

    public EmailData(String subject, String text, String html, List<String> picture_id_list
            , List<String> attachment_id_list) {
        this.setSubject(subject);
        this.setText(text);
        this.setHtml(html);
        this.setPicture_id_list(picture_id_list);
        this.setAttachment_id_list(attachment_id_list);
    }

    public EmailData() {
    }

    public EmailData(InputStream in) throws IOException, ClassNotFoundException {
        if (in == null) {
            throw new NullPointerException("InputStream is null when build EmailData");
        }
        ObjectInputStream ois = new ObjectInputStream(in);
        EmailData emailData = deserialize(ois);

        this.setSubject(emailData.getSubject());
        this.setText(emailData.getText());
        this.setHtml(emailData.getHtml());
        this.setPicture_id_list(emailData.getPicture_id_list());
        this.setAttachment_id_list(emailData.getAttachment_id_list());
    }

    @Override
    public String toString() {
        return "["
                + Dict.SUBJECT + ":" + subject
                + "," + Dict.TEXT + ":" + text
                + "," + Dict.HTML + ":" + html
                + "," + Dict.PICTURE_ID_LIST + ":" + picture_id_list
                + "," + Dict.ATTACHMENT_ID_LIST + ":" + attachment_id_list
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public List<String> getPicture_id_list() {
        return picture_id_list;
    }

    public void setPicture_id_list(List<String> picture_id_list) {
        this.picture_id_list = picture_id_list;
    }

    public List<String> getAttachment_id_list() {
        return attachment_id_list;
    }

    public void setAttachment_id_list(List<String> attachment_id_list) {
        this.attachment_id_list = attachment_id_list;
    }
}
