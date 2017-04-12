package com.wallet.book.dao;


import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.wallet.book.core.Book;
import com.wallet.book.core.BookEntry;
import com.wallet.utils.data.gauva.GuavaCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
            }
        };

        cache = new GuavaCache<>(10, 5, TimeUnit.MINUTES, 500, 1000, cacheLoader
                , removalListener);
    }

    public static void init(BookEntryDAO bookEntryDAO) {
        BookEntryCache.bookEntryDAO = bookEntryDAO;
    }

    public List<BookEntry> get(String user_id) throws ExecutionException {
        return cache.get(user_id);
    }

    public CacheStats getStats() {
        return cache.getStats();
    }

    public long getSize() {
        return cache.getSize();
    }

    public void cleanUp() {
        cache.cleanUp();
    }

    public static void test() throws ExecutionException {
        logger_.info("BookEntryCache test ...");
        BookEntryCache bookEntryCache = BookEntryCache.instance();

        logger_.info("1. load test");
        List<BookEntry> list = bookEntryCache.get("admin");
        bookEntryCache.get("admin");
        for (BookEntry bookEntry : list) {
            logger_.info(bookEntry.getId());
        }

        logger_.info("2. get status");
        logger_.info(bookEntryCache.getStats().toString());
        logger_.info("size : " + bookEntryCache.getSize());

        logger_.info("3. clean up");
        bookEntryCache.cleanUp();
        logger_.info(bookEntryCache.getStats().toString());
        logger_.info("size : " + bookEntryCache.getSize());
    }
}
