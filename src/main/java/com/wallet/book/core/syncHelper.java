package com.wallet.book.core;

import com.wallet.book.dao.BookDAOConnector;
import com.wallet.book.dao.BookEntryDAOConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

/**
 * Created by neo on 3/31/17.
 */
public class syncHelper {
    private static final Logger logger_ = LoggerFactory.getLogger(syncHelper.class);

    private static BookDAOConnector bookDAOC = null;
    private static BookEntryDAOConnector bookEntryDAOC = null;

    public static void init() throws Exception {
        bookDAOC = BookDAOConnector.instance();
        bookEntryDAOC = BookEntryDAOConnector.instance();
    }

    public static void syncBook(Book book) throws Exception {
        bookDAOC.updateByGroupID(book);
    }

    public static List<BookEntry> syncBookEntries(String book_group_id, String target_user_id, String target_book_id) throws Exception {
        List<BookEntry> entryList = bookEntryDAOC.getByBookGroupID(book_group_id);
        List<BookEntry> existingEntryList = bookEntryDAOC.getByUserIDAndBookGroupID(target_user_id, book_group_id);
        HashMap<String, BookEntry> fingerPrintMap = new HashMap<>();

        for (BookEntry entry : existingEntryList) {
            fingerPrintMap.put(entry.getGroup_id(), entry);
        }

        logger_.info("sync book_group_id " + book_group_id + " entryList " + entryList.toString());
        for (BookEntry entry : entryList) {
            String group_id = entry.getGroup_id();
            if (fingerPrintMap.containsKey(group_id)) {
                BookEntry tmp = fingerPrintMap.get(group_id);
                tmp.update(tmp.getUser_id(), fingerPrintMap.get(group_id).getBook_id(), entry.getCategory(), entry.getEvent_date()
                        , entry.getAmount(), entry.getNote(), entry.getPhoto());
                bookEntryDAOC.update(tmp);
            } else {
                entry.setUser_id(target_user_id);
                entry.updateIDWithUserID();
                entry.setBook_id(target_book_id);
                bookEntryDAOC.insert(entry);
            }
        }

        return entryList;
    }
}
