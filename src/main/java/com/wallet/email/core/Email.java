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
    private String from_address;

    @JsonProperty
    private String toAddress;

    @JsonProperty
    private EMAIL_TYPE type;

    @JsonProperty
    private EMAIL_STATUS status;

    @JsonProperty
    private EmailData data;

    public enum EMAIL_STATUS {NEW, SENDING, SEND_FAILED, SENT, RECEIVED}

    public enum EMAIL_TYPE {NOTIFICATION, ALERT, URGENT, INCOMING}

    public Email(String from_address, String toAddress, EMAIL_TYPE type, String subject, String text, String html
            , List<String> picture_id_list, List<String> attachment_id_list) {
        this.setId(String.valueOf(TimeUtils.getUniqueTimeStampInMS()));
        this.setCreate_time(new Date());
        this.setFrom_address(from_address);
        this.setTo_address(toAddress);
        this.setType(type);
        this.setStatus(EMAIL_STATUS.NEW);

        this.setData(new EmailData(subject, text, html, picture_id_list, attachment_id_list));
    }

    public Email() {
    }

    public EMAIL_STATUS getStatus() {
        return status;
    }

    public void setStatus(EMAIL_STATUS status) {
        this.status = status;
    }

    public EMAIL_TYPE getType() {
        return type;
    }

    public void setType(EMAIL_TYPE type) {
        this.type = type;
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

    public String getFrom_address() {
        return from_address;
    }

    public void setFrom_address(String from_address) {
        this.from_address = from_address;
    }

    public String getTo_address() {
        return toAddress;
    }

    public void setTo_address(String toAddress) {
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
