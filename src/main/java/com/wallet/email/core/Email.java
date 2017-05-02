package com.wallet.email.core;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by zxqiu on 5/1/17.
 */
public class Email {
    @JsonProperty
    String fromAddress;

    @JsonProperty
    String toAddress;

    @JsonProperty


    public enum EMAIL_TYPE = {NOTIFICATION, ALERT, CUSTOMER_REQUEST};
}
