package com.wallet.book.core;

import com.wallet.book.dao.BookConnector;
import com.wallet.book.dao.BookEntryConnector;
import com.wallet.book.dao.CategoryConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

/**
 * Created by zxqiu on 3/31/17.
 */
public class syncHelper {
    private static final Logger logger_ = LoggerFactory.getLogger(syncHelper.class);

    private static BookConnector bookDAOC = null;
    private static BookEntryConnector bookEntryConnector = null;
    private static CategoryConnector categoryDAOC = null;

    public enum SYNC_ACTION {ADD, DELETE, UPDATE}

    public static void init() throws Exception {
        bookDAOC = BookConnector.instance();
        bookEntryConnector = BookEntryConnector.instance();
        categoryDAOC = CategoryConnector.instance();
    }

    public static void syncBook(Book book) throws Exception {
        logger_.info("Update book by group id : " + book.getGroup_id());
        logger_.info("name " + book.getName() + " data " + book.getData() + " picture_id " + book.getPicture_id());
        bookDAOC.updateByGroupID(book);
    }

    public static void syncBookEntry(BookEntry bookEntry, SYNC_ACTION action) throws Exception {
        switch (action) {
            case ADD:
                logger_.info("Add book entry by group id : " + bookEntry.getGroup_id());
                for (Book book : bookDAOC.getByGroupID(bookEntry.getBook_group_id())) {
                    logger_.info("Add book entry by group id : " + bookEntry.getGroup_id() + " for user " + book.getUser_id());
                    bookEntry.setUser_id(book.getUser_id());
                    bookEntry.updateIDWithUserID();
                    bookEntry.setBook_group_id(book.getGroup_id());
                    bookEntryConnector.insert(bookEntry);
                }
                break;
            case UPDATE:
                logger_.info("Update book entry by group id : " + bookEntry.getGroup_id());
                bookEntryConnector.updateByGroupID(bookEntry);
                break;
            case DELETE:
                logger_.info("Delete book entry by group id : " + bookEntry.getGroup_id());
                bookEntryConnector.deleteByGroupID(bookEntry.getGroup_id());
                break;
            default:
                logger_.info("Unknown action when syncBookEntry");
        }
    }

    public static List<BookEntry> syncBookEntries(String book_group_id, String target_user_id) throws Exception {
        List<BookEntry> entryList = bookEntryConnector.getByBookGroupID(book_group_id);
        List<BookEntry> existingEntryList = bookEntryConnector.getByUserIDAndBookGroupID(target_user_id, book_group_id);
        HashMap<String, BookEntry> fingerPrintMap = new HashMap<>();

        for (BookEntry entry : existingEntryList) {
            fingerPrintMap.put(entry.getGroup_id(), entry);
        }

        logger_.info("sync book_group_id " + book_group_id + " entryList " + entryList.toString());
        for (BookEntry entry : entryList) {
            String group_id = entry.getGroup_id();
            if (fingerPrintMap.containsKey(group_id)) {
                BookEntry tmp = fingerPrintMap.get(group_id);
                tmp.update(entry.getBook_group_id(), entry.getCategory_group_id(), entry.getEvent_date()
                        , entry.getAmount(), entry.getNote(), entry.getPicture_id());
                bookEntryConnector.updateByUserIDAndID(tmp);
            } else {
                entry.setUser_id(target_user_id);
                entry.updateIDWithUserID();
                bookEntryConnector.insert(entry);
            }
        }

        return entryList;
    }

    public static void syncCategory(Category category, SYNC_ACTION action) throws Exception {
        switch (action) {
            case ADD:
                logger_.info("Add category by group id : " + category.getGroup_id());
                for (Book book : bookDAOC.getByGroupID(category.getBook_group_id())) {
                    logger_.info("Add category by group id : " + category.getGroup_id() + "for user " + book.getUser_id());
                    category.setUser_id(book.getUser_id());
                    category.updateIDByUserID();
                    categoryDAOC.insert(category);
                }
                break;
            case UPDATE:
                logger_.info("Update category by group id : " + category.getGroup_id());
                categoryDAOC.updateByGroupID(category);
                break;
            case DELETE:
                logger_.info("Delete category by group id : " + category.getGroup_id());
                categoryDAOC.deleteByGroupID(category.getUser_id(), category.getGroup_id());
                break;
            default:
                logger_.info("Unknown action when syncCategory");
        }

    }
    public static List<Category> syncCategories(String book_group_id, String target_user_id) throws Exception {
        List<Category> categoryList = categoryDAOC.getByBookGroupID(book_group_id);
        List<Category> existingCategoryList = categoryDAOC.getByUserIDAndBookGroupID(target_user_id, book_group_id);
        HashMap<String, Category> fingerPrintMap = new HashMap<>();

        for (Category category : existingCategoryList) {
            fingerPrintMap.put(category.getBook_group_id(), category);
        }

        for (Category category : categoryList) {
            if (fingerPrintMap.containsKey(category.getBook_group_id())) {
                Category tmp = fingerPrintMap.get(category.getBook_group_id());
                tmp.update(category);
                categoryDAOC.updateByID(tmp);
            } else {
                category.setUser_id(target_user_id);
                category.updateIDByUserID();
                categoryDAOC.insert(category);
            }
        }

        return categoryList;
    }
}
