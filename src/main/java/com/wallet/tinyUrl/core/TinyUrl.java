package com.wallet.tinyUrl.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wallet.utils.misc.TimeUtils;

import java.util.Date;

/**
 * Created by zxqiu on 3/27/17.
 */
public class TinyUrl {
    @JsonProperty
    private String short_url;

    @JsonProperty
    private String full_url;

    @JsonProperty
    private Date create_time;

    @JsonProperty
    private Integer expire_click;

    public TinyUrl() {
    }

    public TinyUrl(String full_url, Integer expire_click) {
        this.setShort_url(Long.toHexString(TimeUtils.getUniqueTimeStampInMS()));
        this.setFull_url(full_url);
        this.setCreate_time(new Date());
        this.setExpire_click(expire_click);
    }

    public TinyUrl(String short_url, String full_url, Date create_time, Integer expire_click) {
        this.setShort_url(short_url);
        this.setFull_url(full_url);
        this.setCreate_time(create_time);
        this.setExpire_click(expire_click);
    }

    public String getShort_url() {
        return short_url;
    }

    public void setShort_url(String short_url) {
        this.short_url = short_url;
    }

    public String getFull_url() {
        return full_url;
    }

    public void setFull_url(String full_url) {
        this.full_url = full_url;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Integer getExpire_click() {
        return expire_click;
    }

    public void setExpire_click(Integer expire_click) {
        this.expire_click = expire_click;
    }
}
