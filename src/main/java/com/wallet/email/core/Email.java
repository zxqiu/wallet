package com.wallet.email.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wallet.utils.misc.TimeUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by zxqiu on 5/1/17.
 */
public class Email {
    @JsonProperty
    private String id;

    @JsonProperty
    private Date create_time;

    @JsonProperty
    private String fromAddress;

    @JsonProperty
    private String toAddress;

    @JsonProperty
    private EMAIL_TYPE email_type;

    @JsonProperty
    private EmailData data;

    public enum EMAIL_TYPE {NOTIFICATION, ALERT, CUSTOMER_REQUEST}

    public Email(String fromAddress, String toAddress, EMAIL_TYPE email_type, String subject, String text, String html
            , List<String> picture_id_list, List<String> attachment_id_list) {
        this.setId(String.valueOf(TimeUtils.getUniqueTimeStampInMS()));
        this.setCreate_time(new Date());
        this.setFromAddress(fromAddress);
        this.setToAddress(toAddress);
        this.setEmail_type(email_type);

        this.setData(new EmailData(subject, text, html, picture_id_list, attachment_id_list));
    }

    public EMAIL_TYPE getEmail_type() {
        return email_type;
    }

    public void setEmail_type(EMAIL_TYPE email_type) {
        this.email_type = email_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public EmailData getData() {
        return data;
    }

    public void setData(EmailData data) {
        this.data = data;
    }

    public String getSubject() {
        if (data == null) {
            return null;
        }
        return data.getSubject();
    }

    public void setSubject(String subject) {
        if (data == null) {
            data = new EmailData();
        }
        data.setSubject(subject);;
    }

    public String getText() {
        if (data == null) {
            return null;
        }
        return data.getText();
    }

    public void setText(String text) {
        if (data == null) {
            data = new EmailData();
        }
        data.setText(text);
    }

    public String getHtml() {
        if (data == null) {
            return null;
        }
        return data.getHtml();
    }

    public void setHtml(String html) {
        if (data == null) {
            data = new EmailData();
        }
        data.setHtml(html);
    }

    public List<String> getPicture_id_list() {
        if (data == null) {
            return null;
        }
        return data.getPicture_id_list();
    }

    public void setPicture_id_list(List<String> picture_id_list) {
        if (data == null) {
            data = new EmailData();
        }
        data.setPicture_id_list(picture_id_list);
    }

    public List<String> getAttachment_id_list() {
        if (data == null) {
            return null;
        }
        return data.getAttachment_id_list();
    }

    public void setAttachment_id_list(List<String> attachment_id_list) {
        if (data == null) {
            data = new EmailData();
        }
        data.setAttachment_id_list(attachment_id_list);
    }

}
