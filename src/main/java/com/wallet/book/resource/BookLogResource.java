package com.wallet.book.resource;

import com.codahale.metrics.annotation.Timed;
import com.wallet.book.core.BookLog;
import com.wallet.book.dao.BookLogConnector;
import com.wallet.book.dao.BookLogDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by zxqiu on 4/19/17.
 */
@Path("/books/log")
public class BookLogResource {
    private static final Logger logger_ = LoggerFactory.getLogger(BookLogResource.class);

    private static BookLogConnector bookLogConnector = null;

    public BookLogResource() throws Exception {
        this.bookLogConnector = BookLogConnector.instance();
    }

    @Path("/getall")
    @Timed
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public List<BookLog> getAll() throws Exception {
        return bookLogConnector.getAll();
    }
}
