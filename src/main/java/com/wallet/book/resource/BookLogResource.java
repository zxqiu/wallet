package com.wallet.book.resource;

import com.codahale.metrics.annotation.Timed;
import com.wallet.book.core.BookLog;
import com.wallet.book.dao.BookLogConnector;
import com.wallet.book.dao.BookLogDAO;
import com.wallet.login.dao.SessionDAOConnector;
import com.wallet.login.resource.SessionResource;
import com.wallet.utils.misc.ApiUtils;
import com.wallet.utils.misc.Dict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

/**
 * Created by zxqiu on 4/19/17.
 */
@Path("/books/log")
public class BookLogResource {
    private static final Logger logger_ = LoggerFactory.getLogger(BookLogResource.class);

    private static SessionDAOConnector sessionDAOC = null;
    private static BookLogConnector bookLogConnector = null;

    public BookLogResource() throws Exception {
        this.sessionDAOC = SessionDAOConnector.instance();
        this.bookLogConnector = BookLogConnector.instance();
    }

    @Path("/getall")
    @Timed
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public List<BookLog> getAll() throws Exception {
        return bookLogConnector.getAll();
    }

    @Path("/getbybook")
    @Timed
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public Object getByBook(
            @QueryParam(Dict.BOOK_GROUP_ID) String book_group_id
            , @CookieParam("walletSessionCookie") Cookie cookie
            ) throws Exception {
        String user_id = ApiUtils.getUserIDFromCookie(cookie);
        if (SessionDAOConnector.instance().verifySessionCookie(cookie)== false || user_id == null) {
            return Response.seeOther(URI.create(SessionResource.PATH_RESTORE_SESSION)).build();
        }

        return bookLogConnector.getByBookGroupIDAndUserID(book_group_id, user_id);
    }

    @Path("/getbyuser")
    @Timed
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public Object getByUser(
            @QueryParam(Dict.USER_ID) String user_id
            , @CookieParam("walletSessionCookie") Cookie cookie
    ) throws Exception {
        if (SessionDAOConnector.instance().verifySessionCookie(cookie)== false) {
            return Response.seeOther(URI.create(SessionResource.PATH_RESTORE_SESSION)).build();
        }

        return bookLogConnector.getByUserID(user_id);
    }
}
