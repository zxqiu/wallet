package com.wallet.book.dao;


import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.wallet.book.core.BookEntry;
import com.wallet.utils.data.gauva.GuavaCache;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by neo on 4/11/17.
 */
public class BookEntryCache {
    private static final Logger logger_ = LoggerFactory.getLogger(GuavaCache.class);

    private static BookEntryCache instance_ = null;
    private static Lock createLock = new ReentrantLock();

    private static BookEntryDAO bookEntryDAO = null;
    private static CacheLoader<String, List<BookEntry>> cacheLoader = null;
    private static RemovalListener<String, List<BookEntry>> removalListener = null;
    // cache user_id --> book entry list
    private static GuavaCache<String, List<BookEntry>> cache = null;

    public static BookEntryCache instance() {
        if (instance_ == null) {
            createLock.lock();
            try {
                instance_ = new BookEntryCache();
            } catch (Exception e) {
                logger_.error("Error : cannot create BookEntryCache instance");
                e.printStackTrace();
            } finally {
                createLock.unlock();
            }
        }
        return instance_;
    }

    private BookEntryCache() throws Exception {
        if (bookEntryDAO == null) {
            throw new Exception("Error : BookEntryCache not initialized");
        }

        cacheLoader = new CacheLoader<String, List<BookEntry>>() {
            @Override
            public List<BookEntry> load(String user_id) throws Exception {
                return bookEntryDAO.findByUserID(user_id);
            }
        };

        removalListener = new RemovalListener<String, List<BookEntry>>() {
            @Override
            public void onRemoval(RemovalNotification<String, List<BookEntry>> removalNotification) {
                return;
            }
        };

        cache = new GuavaCache<>(10, 5, TimeUnit.MINUTES, 500, 1000, cacheLoader
                , removalListener);
    }

    public static void init(BookEntryDAO bookEntryDAO) {
        BookEntryCache.bookEntryDAO = bookEntryDAO;
    }

    public void refresh(String user_id) {
        cache.refresh(user_id);
    }

    public CacheStats getStats() {
        return cache.getStats();
    }

    public long getSize() {
        return cache.getSize();
    }

    public List<BookEntry> getByUserID(String user_id) throws ExecutionException {
        if (user_id == null || user_id.length() == 0) {
            return new ArrayList<>();
        }

        for (BookEntry entry : cache.get(user_id)) {
            logger_.info("read user " + user_id + " from cache : " + entry.toString());
        }
        return cache.get(user_id);
    }

    public List<BookEntry> getByUserIDAndID(String user_id, String id) throws ExecutionException {
        List<BookEntry> bookEntryList = new ArrayList<>();
        if (user_id == null || user_id.length() == 0
                || id == null || id.length() == 0) {
            return bookEntryList;
        }

        for (BookEntry entry : getByUserID(user_id)) {
            if (entry.getId().equals(id)) {
                logger_.info("read user " + user_id + " from cache : " + entry.toString());
                bookEntryList.add(entry);
            }
        }

        return bookEntryList;
    }

    public void insert(BookEntry bookEntry) throws ExecutionException {
        if (bookEntry == null) {
            return;
        }

        List<BookEntry> bookEntryList = cache.get(bookEntry.getUser_id());
        for (BookEntry entry : bookEntryList) {
            if (entry.getId().equals(bookEntry.getId())) {
                return;
            }
        }
        bookEntryList.add(new BookEntry(bookEntry));
        logger_.info("insert to cache for user " + bookEntry.getUser_id() + " : " + bookEntry.toString());
        //cache.put(bookEntry.getUser_id(), bookEntryList);
    }

    public void deleteByUserID(String user_id) {
        if (user_id == null || user_id.length() == 0) {
            return;
        }

        logger_.info("delete user " + user_id + " from cache");
        cache.invalidate(user_id);
    }

    public void deleteByUserIDAndID(String user_id, String id) throws ExecutionException {
        if (user_id == null || user_id.length() == 0) {
            return;
        }

        List<BookEntry> bookEntryList = cache.get(user_id);
        for (int i = 0; i < bookEntryList.size(); i++) {
            BookEntry entry = bookEntryList.get(i);
            if (entry.getId().equals(id)) {
                bookEntryList.remove(i--);
                logger_.info("delete from cache : " + id);
                break;
            }
        }
        //cache.put(user_id, bookEntryList);
    }

    public void deleteByUserIDAndBookGroupID(String user_id, String book_group_id) throws ExecutionException {
        if (user_id == null || user_id.length() == 0) {
            return;
        }

        List<BookEntry> bookEntryList = cache.get(user_id);
        for (int i = 0; i < bookEntryList.size(); i++) {
            BookEntry entry = bookEntryList.get(i);
            if (entry.getBook_group_id().equals(book_group_id)) {
                bookEntryList.remove(i--);
                logger_.info("delete from cache : " + entry.getId());
            }
        }
        //cache.put(user_id, bookEntryList);
    }

    public void updateByUserAndID(BookEntry bookEntry) throws ExecutionException {
        if (bookEntry == null) {
            return;
        }

        List<BookEntry> bookEntryList = cache.get(bookEntry.getUser_id());
        for (int i = 0; i < bookEntryList.size(); i++) {
            BookEntry entry = bookEntryList.get(i);
            if (entry.getId().equals(bookEntry.getId())) {
                logger_.info("update for user + " + bookEntry.getUser_id() + " in cache : " + bookEntry.toString());
                entry.update(bookEntry.getBook_group_id(), bookEntry.getCategory_group_id(), bookEntry.getEvent_date()
                        , bookEntry.getAmount(), bookEntry.getNote(), bookEntry.getPicture_id());
                break;
            }
        }
        //cache.put(bookEntry.getUser_id(), bookEntryList);
    }

    public void cleanUp() {
        cache.cleanUp();
    }

    public static void test() throws ExecutionException, JSONException {
        logger_.info("BookEntryCache test ...");
        BookEntryCache bookEntryCache = BookEntryCache.instance();

        List<BookEntry> tmpList = new ArrayList<>();
        BookEntry tmpBook = new BookEntry("test_user", "test_user", "test_book", "test_category"
                , new Date(), 1, "first test book", "test_picture");
        tmpList.add(tmpBook);

        logger_.info("0. initial status");
        logger_.info(bookEntryCache.getStats().toString());
        logger_.info("size : " + bookEntryCache.getSize());

        logger_.info("1. put test");
        cache.put(tmpBook.getUser_id(), tmpList);
        List<BookEntry> list = cache.get(tmpBook.getUser_id());
        for (BookEntry bookEntry : list) {
            logger_.info(bookEntry.getNote());
        }
        logger_.info(bookEntryCache.getStats().toString());
        logger_.info("size : " + bookEntryCache.getSize());

        logger_.info("2. update test");
        BookEntry tmpBook2 = new BookEntry(tmpBook.getUser_id(), tmpBook.getUser_id(), "test_book", "test_category"
                , new Date(), 1, "another test book", "test_picture");
        list.add(tmpBook2);
        cache.put(tmpBook2.getUser_id(), list);
        list = cache.get(tmpBook.getUser_id());
        for (BookEntry bookEntry : list) {
            logger_.info(bookEntry.getNote());
        }
        logger_.info(bookEntryCache.getStats().toString());
        logger_.info("size : " + bookEntryCache.getSize());

        logger_.info("3. load test");
        list = cache.get("admin");
        for (BookEntry bookEntry : list) {
            logger_.info(bookEntry.getId());
        }
        logger_.info(bookEntryCache.getStats().toString());
        logger_.info("size : " + bookEntryCache.getSize());

        logger_.info("4. clean up");
        bookEntryCache.cleanUp();
        logger_.info(bookEntryCache.getStats().toString());
        logger_.info("size : " + bookEntryCache.getSize());
    }


}
