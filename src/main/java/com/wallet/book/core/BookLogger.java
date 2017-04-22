package com.wallet.book.core;

import com.wallet.book.dao.BookLogConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zxqiu on 4/18/17.
 */
public class BookLogger {
    private static final Logger logger_ = LoggerFactory.getLogger(BookLogger.class);

    public static void log(String user_id, String book_id, String book_group_id, String book_entry_id
            , String book_entry_group_id, String category_id, String category_group_id
            , BookLog.BOOK_LOG_OPERATION operation, BookLog.BOOK_LOG_TYPE type, BookLog.BOOK_LOG_NOTE note) throws Exception {
        BookLogConnector.instance().insert(
                new BookLog(user_id, book_id, book_group_id, book_entry_id, book_entry_group_id, category_id
                        , category_group_id, operation, type, note));
    }

    public static void addBook(String user_id, String book_id, String book_group_id, BookLog.BOOK_LOG_NOTE note) throws Exception {
        log(user_id, book_id, book_group_id, "","", "", ""
                , BookLog.BOOK_LOG_OPERATION.ADD, BookLog.BOOK_LOG_TYPE.BOOK, note);
    }

    public static void deleteBook(String user_id, String book_id, String book_group_id, BookLog.BOOK_LOG_NOTE note) throws Exception {
        log(user_id, book_id, book_group_id, "","", "", ""
                , BookLog.BOOK_LOG_OPERATION.DELETE, BookLog.BOOK_LOG_TYPE.BOOK, note);
    }

    public static void updateBook(String user_id, String book_id, String book_group_id, BookLog.BOOK_LOG_NOTE note) throws Exception {
        log(user_id, book_id, book_group_id, "","", "", ""
                , BookLog.BOOK_LOG_OPERATION.UPDATE, BookLog.BOOK_LOG_TYPE.BOOK, note);
    }

    public static void addBookEntry(String user_id, String book_group_id, String book_entry_id
            , String book_entry_group_id, String category_group_id, BookLog.BOOK_LOG_NOTE note) throws Exception {
        log(user_id, "", book_group_id, book_entry_id, book_entry_group_id, "", category_group_id
                , BookLog.BOOK_LOG_OPERATION.ADD, BookLog.BOOK_LOG_TYPE.BOOK_ENTRY, note);
    }

    public static void deleteBookEntry(String user_id, String book_group_id, String book_entry_id
            , String book_entry_group_id, String category_group_id, BookLog.BOOK_LOG_NOTE note) throws Exception {
        log(user_id, "", book_group_id, book_entry_id, book_entry_group_id, "", category_group_id
                , BookLog.BOOK_LOG_OPERATION.DELETE, BookLog.BOOK_LOG_TYPE.BOOK_ENTRY, note);
    }

    public static void updateBookEntry(String user_id, String book_group_id, String book_entry_id
            , String book_entry_group_id, String category_group_id, BookLog.BOOK_LOG_NOTE note) throws Exception {
        log(user_id, "", book_group_id, book_entry_id, book_entry_group_id, "", category_group_id
                , BookLog.BOOK_LOG_OPERATION.UPDATE, BookLog.BOOK_LOG_TYPE.BOOK_ENTRY, note);
    }

    public static void addCateory(String user_id, String book_group_id, String category_id
            , String category_group_id, BookLog.BOOK_LOG_NOTE note) throws Exception {
        log(user_id, "", book_group_id, "", "", category_id, category_group_id
                , BookLog.BOOK_LOG_OPERATION.ADD, BookLog.BOOK_LOG_TYPE.CATEGORY, note);
    }

    public static void deleteCateory(String user_id, String book_group_id, String category_id
            , String category_group_id, BookLog.BOOK_LOG_NOTE note) throws Exception {
        log(user_id, "", book_group_id, "", "", category_id, category_group_id
                , BookLog.BOOK_LOG_OPERATION.DELETE, BookLog.BOOK_LOG_TYPE.CATEGORY, note);
    }

    public static void updateCateory(String user_id, String book_group_id, String category_id
            , String category_group_id, BookLog.BOOK_LOG_NOTE note) throws Exception {
        log(user_id, "", book_group_id, "", "", category_id, category_group_id
                , BookLog.BOOK_LOG_OPERATION.DELETE, BookLog.BOOK_LOG_TYPE.CATEGORY, note);
    }
}
