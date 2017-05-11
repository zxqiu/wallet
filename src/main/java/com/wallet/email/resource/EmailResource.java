package com.wallet.email.resource;

import com.wallet.email.core.Email;
import com.wallet.email.dao.EmailConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by zxqiu on 5/2/17.
 */
@Path("/email")
public class EmailResource {
    private static final Logger logger_ = LoggerFactory.getLogger(EmailResource.class);

    private static EmailConnector emailConnector = null;

    public EmailResource() throws Exception {
        emailConnector = EmailConnector.instance();
    }

    @GET
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public Response test() throws Exception {
        Email email = new Email("zxqiu90@gmail.com", "zxqiu90@gmail.com", Email.EMAIL_TYPE.ALERT
                , "test", "test text", "", null, null);
        emailConnector.insert(email);

        return Response.ok().build();
    }
}
