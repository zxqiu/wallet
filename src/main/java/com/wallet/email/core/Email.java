package com.wallet.email.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by zxqiu on 5/1/17.
 */
public class Email {
    @JsonProperty
    String ID;

    @JsonProperty
    Date create_time;

    @JsonProperty
    String fromAddress;

    @JsonProperty
    String toAddress;

    @JsonProperty
    EmailData data;


    public enum EMAIL_TYPE {NOTIFICATION, ALERT, CUSTOMER_REQUEST}
}
