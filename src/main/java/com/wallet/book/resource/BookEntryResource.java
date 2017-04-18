package com.wallet.book.resource;

import java.io.*;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;

import org.apache.commons.io.FileUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

import javax.ws.rs.core.StreamingOutput;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.wallet.book.core.Book;
import com.wallet.book.core.BookEntry;
import com.wallet.book.core.syncHelper;
import com.wallet.book.dao.BookDAOConnector;
import com.wallet.login.core.User;
import com.wallet.login.dao.UserDAOConnector;
import com.wallet.login.resource.SessionResource;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.wallet.book.core.Category;
import com.wallet.book.dao.BookEntryDAOConnector;
import com.wallet.book.dao.CategoryDAOConnector;
import com.wallet.login.dao.SessionDAOConnector;
import com.wallet.utils.misc.ApiUtils;
import com.wallet.utils.misc.Dict;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Blob;

@Path("/books")
public class BookEntryResource {
    private static final Logger logger_ = LoggerFactory.getLogger(BookEntryResource.class);
    private static final int bookEntrysEachLine = 4;

    private BookDAOConnector bookDAOC = null;
    private BookEntryDAOConnector bookEntryDAOC = null;
    private CategoryDAOConnector categoryDAOC = null;
    private UserDAOConnector userDAOC = null;

    private Map<String, Double> picture_ocr;

    /* google cloud storage buckets */
    private final String IMAGE_BUCKET = "wallet-image";
    private final String TEXT_BUCKET = "wallet-text";

    public BookEntryResource() throws Exception {
        this.bookDAOC = BookDAOConnector.instance();
        this.bookEntryDAOC = BookEntryDAOConnector.instance();
        this.categoryDAOC = CategoryDAOConnector.instance();
        this.userDAOC = UserDAOConnector.instance();
        this.picture_ocr = new HashMap<>();
    }

    @GET
    @Timed
    @Path("/allbookentry")
    @Produces(value = MediaType.APPLICATION_JSON)
    public List<BookEntry> getAll() throws Exception {
        return bookEntryDAOC.getByUserID("admin");
    }

    public static final String PATH_BOOKS = "/books";

    /**
     * @return bookEntryList view. This is the main view of path /books
     */
    @GET
    @Timed
    @Path("/")
    @Produces(value = MediaType.TEXT_HTML)
    public Response bookEntryListView(@CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
        String user_id = ApiUtils.getUserIDFromCookie(cookie);
        if (SessionDAOConnector.instance().verifySessionCookie(cookie) == false || user_id == null) {
            return Response.seeOther(URI.create(SessionResource.PATH_RESTORE_SESSION)).build();
        }

        User user = userDAOC.getByID(user_id);
        List<Book> bookList = bookDAOC.getByUserID(user_id);
        HashMap<String, Book> bookMap = new HashMap<>();
        List<BookEntry> bookEntryList = sortBookByEventTime(bookEntryDAOC.getByUserID(user_id));
        List<Category> categoryList = categoryDAOC.getByUserID(user_id);
        HashMap<String, Category> categoryMap = new HashMap<>();

        for (Category category : categoryList) {
            categoryMap.put(category.getGroup_id(), category);
        }

        for (Book book : bookList) {
            bookMap.put(book.getGroup_id(), book);
        }

        return Response.ok().entity(views.bookEntryList.template(bookEntryList, bookMap, categoryList, categoryMap, bookEntrysEachLine, user)).build();
    }

    public static final String PATH_INSERT_ENTRY_VIEW = "/books/entry";

    /**
     * @return bookEntry view. This is for showing and edit entry details.
     */
    @GET
    @Timed
    @Path("/entry/{id}")
    @Produces(value = MediaType.TEXT_HTML)
    public Response bookEntryView(@PathParam(Dict.ID) String id,
                                  @CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
        String user_id = ApiUtils.getUserIDFromCookie(cookie);
        if (SessionDAOConnector.instance().verifySessionCookie(cookie) == false || user_id == null) {
            return Response.seeOther(URI.create(SessionResource.PATH_RESTORE_SESSION)).build();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String date = sdf.format(new Date());
        BookEntry bookEntry = null;
        if (!id.equals("0")) {
            List<BookEntry> bookEntryList = bookEntryDAOC.getByID(id);
            if (!bookEntryList.isEmpty()) {
                bookEntry = bookEntryList.get(bookEntryList.size() - 1);
                date = sdf.format(bookEntry.getEvent_date());
            }
        }

        return Response.ok()
                .entity(views.bookEntry.template(bookEntry
                        , bookDAOC.getByUserID(user_id)
                        , categoryDAOC.getByUserID(user_id)
                        , date
                        , user_id
                ))
                .build();
    }

    /**
     * Create a insert new or update existing book entry.
     *
     * @param id, event_date, amount, category, note, picture_id, cookie
     * @return
     * @throws Exception
     */
    @POST
    @Timed
    @Path("/insertentry")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response insertEntry(@FormParam(Dict.ID) String id,
                                @FormParam(Dict.BOOK_ID) String book_id,
                                @FormParam(Dict.EVENT_DATE) String event_date,
                                @FormParam(Dict.AMOUNT) double amount_double,
                                @FormParam(Dict.CATEGORY_NAME) String category_name,
                                @FormParam(Dict.CATEGORY_ID) String category_id,
                                @FormParam(Dict.NOTE) String note,
                                @FormParam(Dict.PICTURE_ID) String picture_id,
                                @CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
        String user_id = ApiUtils.getUserIDFromCookie(cookie);
        if (SessionDAOConnector.instance().verifySessionCookie(cookie) == false || user_id == null) {
            return Response.seeOther(URI.create(SessionResource.PATH_RESTORE_SESSION)).build();
        }

        logger_.info("insertentry request : id:" + id + ", book_id:" + book_id + ", event_date:" + event_date
                + ", amount_double:" + amount_double + ", category_name:" + category_name + ", category_id:"
                + category_id + ", note:" + note + ", picture_id:" + picture_id + ".");

        long amount = (long) (amount_double * 100);
        Book book = null;

        // 1. extract request
        // 2. verify and parse request
        // 3. verify parameters
        if (category_name.length() == 0 || event_date.length() == 0 || book_id.length() == 0) {
            logger_.error("ERROR: invalid new item request: " + id);
            return Response.serverError().build();
        }

        // 4. transaction
        // 4.1 insert default book if not exists
        try {
            if (book_id != null) {
                List<Book> bookList = bookDAOC.getByID(book_id);
                if (!bookList.isEmpty()) {
                    book = bookList.get(bookList.size() - 1);
                }
            }

            if (book_id == null || book == null) {
                book = new Book(user_id, user_id, "default", new Date(), "#FFFFFF");
                bookDAOC.insert(book);
            }
        } catch (Exception e) {
            logger_.error("Error failed to get book or insert new book when insert book entry : " + book_id);
            e.printStackTrace();
            return Response.serverError().build();
        }

        List<Category> categoryList = null;
        Category category = null;
        try {
            if (category_id != null && category_id.length() > 1) {
                categoryList = categoryDAOC.getByID(category_id);
                if (categoryList != null && !categoryList.isEmpty()) {
                    category = categoryList.get(categoryList.size() - 1);
                }
            }
            if (category == null || !category.getName().equals(category_name)) {
                // Prioritize category_name over category_id
                category = new Category(user_id, book.getGroup_id(), category_name, "#FFFFFF");
                //categoryDAOC.insert(category);
                syncHelper.syncCategory(category, syncHelper.SYNC_ACTION.ADD);
            }
        } catch (Exception e1) {
            logger_.error("Error failed to get category or insert new category when insert book entry : " + category_id);
            e1.printStackTrace();
            return Response.serverError().build();
        }

        // 4.2 insert book
        // 4.2.1 update if id is received and exists. Otherwise insert new.
        BookEntry bookEntry = null;
        if (id.length() != 0) {
            try {
                List<BookEntry> list = bookEntryDAOC.getByID(id);
                if (!list.isEmpty()) {
                    bookEntry = list.get(list.size() - 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger_.error("Error find out if item already exists : " + e.getMessage());
                return Response.serverError().build();
            }
        }

        // 4.2.2 insert
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        try {
            if (bookEntry != null) {
                logger_.info("Update book item : " + bookEntry.getId());
                if (bookEntry.getBook_group_id().equals(book.getGroup_id())) {
                    bookEntry.update(book.getGroup_id(), category.getGroup_id(), sdf.parse(event_date), amount, note
                            , picture_id);
                    bookEntryDAOC.updateByID(bookEntry);
                    // Update if book id is not changed.
                    syncHelper.syncBookEntry(bookEntry, syncHelper.SYNC_ACTION.UPDATE);
                } else {
                    // Re-insert if book id is changed.
                    syncHelper.syncBookEntry(bookEntry, syncHelper.SYNC_ACTION.DELETE);
                    bookEntry.update(book.getGroup_id(), category.getGroup_id(), sdf.parse(event_date), amount, note
                            , picture_id);
                    //bookEntryDAOC.insert(bookEntry);
                    syncHelper.syncBookEntry(bookEntry, syncHelper.SYNC_ACTION.ADD);
                }
            } else {
                bookEntry = new BookEntry(user_id, user_id, book.getGroup_id(), category.getGroup_id()
                        , sdf.parse(event_date), amount, note, picture_id);
                logger_.info("Insert new book item : " + bookEntry.getId());
                //bookEntryDAOC.insert(bookEntry); // remove this to avoid duplication
                syncHelper.syncBookEntry(bookEntry, syncHelper.SYNC_ACTION.ADD);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger_.error("Error : failed to insert or update book entry : " + e.getMessage());
            return Response.serverError().build();
        }

        return Response.seeOther(URI.create(PATH_BOOKS)).build();
    }

    /**
     * Delete book entry
     *
     * @param id
     * @return
     * @throws Exception
     */
    @POST
    @Timed
    @Path("/deleteentry")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response deleteEntry(@FormParam(Dict.ID) String id,
                                @CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
        String user_id = ApiUtils.getUserIDFromCookie(cookie);
        if (SessionDAOConnector.instance().verifySessionCookie(cookie) == false || user_id == null) {
            return Response.seeOther(URI.create(SessionResource.PATH_RESTORE_SESSION)).build();
        }
        // 1. extract request
        // 2. verify and parse request
        // 3. verify parameters
        if (id == null || id.length() == 0) {
            logger_.error("ERROR: invalid delete book entry request for \'" + id + "\'");
            return Response.serverError().build();
        }

        // 4. transaction
        try {
            List<BookEntry> bookEntryList = bookEntryDAOC.getByID(id);
            if (!bookEntryList.isEmpty()) {
                BookEntry bookEntry = bookEntryList.get(bookEntryList.size() - 1);
                logger_.info("Delete book entry : " + bookEntry.getId());
                bookEntryDAOC.deleteByID(id);
                syncHelper.syncBookEntry(bookEntry, syncHelper.SYNC_ACTION.DELETE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger_.error("Error : failed to delete new book entry \'" + id + "\' : " + e.getMessage());
            return Response.serverError().build();
        }

        // 5. generate response
        logger_.info("Book entry \'" + id + "\' removed");
        return Response.seeOther(URI.create(PATH_BOOKS)).build();
    }

    @GET
    @Timed
    @Path("/getpicture")
    @Produces(value = MediaType.APPLICATION_FORM_URLENCODED)
    public Response getBookEntryPicture(@QueryParam(Dict.USER_ID) String user_id,
                                        @QueryParam("host_url") String hostURL,
                                        @QueryParam(Dict.PICTURE_ID) String pictureID,
                                        @CookieParam("walletSessionCookie") Cookie cookie
    ) throws Exception {

        if (hostURL == null || pictureID == null || hostURL.length() == 0 || pictureID.length() == 0) {
            return Response.status(200).build();
        }
        String fileName = createPictureName(user_id, hostURL, pictureID);

        // Instantiates a client
        Storage storage = StorageOptions.getDefaultInstance().getService();

        // Gets the new bucket
        Bucket bucket = storage.get(IMAGE_BUCKET, Storage.BucketGetOption.fields());
        if (bucket == null) {
            logger_.error("IMAGE_BUCKET doesn't exist");
            return Response.serverError().build();
        }

        Blob blob = bucket.get(fileName);
        if (blob == null) {
            return Response.status(200).build();
        }

        String decode = Base64.encodeBase64String(blob.getContent());

        JSONObject obj = new JSONObject();
        obj.put("image", "data:image/jpeg;base64," + decode);
        return Response.status(200).entity(obj.toString()).build();
    }

    @POST
    @Timed
    @Path("/uploadpicture")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadBookEntryPicture(@FormDataParam("hosturl") String hostURL,
                                           @FormDataParam("image") InputStream uploadedInputStream,
                                           @FormDataParam("image") FormDataContentDisposition fileDetail,
                                           @CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
        String user_id = ApiUtils.getUserIDFromCookie(cookie);
        if (SessionDAOConnector.instance().verifySessionCookie(cookie) == false || user_id == null) {
            return Response.seeOther(URI.create(SessionResource.PATH_RESTORE_SESSION)).build();
        }

        String fileName = createPictureName(user_id, hostURL, fileDetail.getFileName());

        // Instantiates a client
        Storage storage = StorageOptions.getDefaultInstance().getService();

        // Creates the new bucket
        Bucket bucket = storage.get(IMAGE_BUCKET, Storage.BucketGetOption.fields());
        if (bucket == null) {
            bucket = storage.create(BucketInfo.of(IMAGE_BUCKET));
        }
        bucket.create(fileName, uploadedInputStream);

        Blob blob = bucket.get(fileName);
        if (blob == null) {
            logger_.error("IMAGE_BUCKET doesn't exist");
            return Response.serverError().build();
        }

        byte[] byteArray = blob.getContent();
        String pathName = "/home/kangli/Desktop/" + fileName;
        FileUtils.writeByteArrayToFile(new File(pathName), byteArray);

        return Response.status(200).build();
    }

    @GET
    @Timed
    @Path("/getocramount")
    @Produces(MediaType.TEXT_HTML)
    public Response getOcrAmount(@QueryParam(Dict.USER_ID) String user_id,
                                 @QueryParam("picture_timestamp") Double ts) {
        logger_.error("enter get ocr amount user_id = "+user_id +" ts "+ts);
        String key = user_id + ts;
        return Response.status(200).build();
    }

    @POST
    @Timed
    @Path("/postocramount")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postOcrAmount(@FormParam(Dict.USER_ID) String user_id,
                                  @FormParam("picture_timestamp") Double picture_ts,
                                  @FormParam("amount") String amount) throws Exception {
        logger_.error("receive postOcrAmount!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! kangli amount " + amount + " user "+user_id);

        return Response.status(200).build();
    }

    private static Comparator<BookEntry> bookEntryTimeComparator = new Comparator<BookEntry>() {
        public int compare(BookEntry a, BookEntry b) {
            if (a.getEvent_date().before(b.getEvent_date())) {
                return -1;
            } else {
                return 1;
            }
        }
    };

    private List<BookEntry> sortBookByEventTime(List<BookEntry> list) {
        if (list == null) {
            return null;
        }

        Collections.sort(list, bookEntryTimeComparator);

        return list;
    }

    private String createPictureName(String user_id, String hostURL, String pictureID) {
        String s = user_id + '#' + hostURL + '#' + pictureID;
        return Base64.encodeBase64String(s.getBytes());
    }
}
