package com.wallet.book.dao;

import com.google.cloud.storage.*;
import org.apache.commons.codec.binary.Base64;

import java.io.InputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by kangli on 5/10/17.
 */
public class BookEntryPictureConnector {
    private static Lock createLock_ = new ReentrantLock();
    private static BookEntryPictureConnector instance_ = null;

    /* google cloud storage bucket */
    private final String IMAGE_BUCKET = "wallet-image";
    Storage storage;
    Bucket bucket;

    public static BookEntryPictureConnector instance() throws Exception {
        if (instance_ == null) {
            BookEntryPictureConnector.createLock_.lock();
            try {
                instance_ = new BookEntryPictureConnector();
            } finally {
                BookEntryPictureConnector.createLock_.unlock();
            }
        }

        return instance_;
    }

    private BookEntryPictureConnector() throws Exception {
        storage = StorageOptions.getDefaultInstance().getService();
        bucket = storage.get(IMAGE_BUCKET, Storage.BucketGetOption.fields());
        if (bucket == null) {
            bucket = storage.create(BucketInfo.of(IMAGE_BUCKET));
        }
    }

    public void insert(String userID, String pictureTs, String hostURL, InputStream uploadedInputStream) {
        String fileName = userID + '#' + hostURL + '#' + pictureTs;
        bucket.create(fileName, uploadedInputStream);
    }

    public void delete(String userID, String pictureTs, String hostURL) {
        String fileName = userID + '#' + hostURL + '#' + pictureTs;
        Blob blob = bucket.get(fileName);
        if (blob == null)
            return;

        blob.delete();
    }

    public byte[] getByUserIDAndTs(String userID, String pictureTs, String hostURL) {
        String fileName = userID + '#' + hostURL + '#' + pictureTs;
        Blob blob = bucket.get(fileName);
        if (blob == null)
            return null;

        return blob.getContent();
    }
}
