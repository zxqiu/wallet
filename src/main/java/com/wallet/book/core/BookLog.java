package com.wallet.book.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wallet.utils.misc.TimeUtils;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * Created by zxqiu on 4/17/17.
 */
public class BookLog {

    public enum BOOK_LOG_OPERATION {ADD, DELETE, UPDATE}
    public enum BOOK_LOG_TYPE {BOOK, BOOK_ENTRY, CATEGORY}
    public enum BOOK_LOG_NOTE {
        NONE("none"),
        BY_ID("by ID"),
        BY_BOOK_GROUP_ID("by book group ID"),
        BY_BOOK_ENTRY_GROUP_ID("by book entry group ID"),
        BY_CATEGORY_GROUP_ID("by category group ID"),
        BY_USER_ID("by user ID"),
        BY_USER_ID_AND_ID("by user ID and ID"),
        BY_USER_ID_AND_BOOK_GROUP_ID("by user ID and book group ID"),
        BY_USER_ID_AND_BOOK_ENTRY_GROUP_ID("by user ID and book entry group ID"),
        BY_USER_ID_AND_CATEGORY_GROUP_ID("by user ID and category group ID");

        BOOK_LOG_NOTE(String name) {
            try {
                Field fieldName = getClass().getSuperclass().getDeclaredField("name");
                fieldName.setAccessible(true);
                fieldName.set(this, name);
                fieldName.setAccessible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @JsonProperty
    private String id;

    @JsonProperty
    private Date create_time;

    @JsonProperty
    private String user_id;

    @JsonProperty
    private String book_id;

    @JsonProperty
    private String book_group_id;

    @JsonProperty
    private String book_entry_id;

    @JsonProperty
    private String book_entry_group_id;

    @JsonProperty
    private String category_id;

    @JsonProperty
    private String category_group_id;

    @JsonProperty
    private BOOK_LOG_OPERATION operation;

    @JsonProperty
    private BOOK_LOG_TYPE type;

    @JsonProperty
    private BookLogData data;

    public BookLog() {}

    public BookLog(String user_id, String book_id, String book_group_id, String book_entry_id
            , String book_entry_group_id, String category_id, String category_group_id
            , BOOK_LOG_OPERATION operation, BOOK_LOG_TYPE type, BOOK_LOG_NOTE note) {

        this.id = user_id + TimeUtils.getUniqueTimeStampInMS();
        this.create_time = new Date();
        this.user_id = user_id;
        this.book_id = book_id;
        this.book_group_id = book_group_id;
        this.category_id = category_id;
        this.category_group_id = category_group_id;
        this.book_entry_id = book_entry_id;
        this.book_entry_group_id = book_entry_group_id;
        this.operation = operation;
        this.type = type;

        this.data = new BookLogData(note);
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getBook_group_id() {
        return book_group_id;
    }

    public void setBook_group_id(String book_group_id) {
        this.book_group_id = book_group_id;
    }

    public String getCategory_group_id() {
        return category_group_id;
    }

    public void setCategory_group_id(String category_group_id) {
        this.category_group_id = category_group_id;
    }

    public String getBook_entry_group_id() {
        return book_entry_group_id;
    }

    public void setBook_entry_group_id(String book_entry_group_id) {
        this.book_entry_group_id = book_entry_group_id;
    }

    public BOOK_LOG_OPERATION getOperation() {
        return operation;
    }

    public void setOperation(BOOK_LOG_OPERATION operation) {
        this.operation = operation;
    }

    public BookLogData getData() {
        return data;
    }

    public void setData(BookLogData data) {
        this.data = data;
    }

    public BOOK_LOG_TYPE getType() {
        return type;
    }

    public void setType(BOOK_LOG_TYPE type) {
        this.type = type;
    }

    public BOOK_LOG_NOTE getNote() {
        if (data == null) {
            return null;
        }
        return data.getNote();
    }

    public void setNote(BOOK_LOG_NOTE note) {
        if (data == null) {
            data = new BookLogData();
        }
        data.setNote(note);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public String getBook_entry_id() {
        return book_entry_id;
    }

    public void setBook_entry_id(String book_entry_id) {
        this.book_entry_id = book_entry_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }
}
