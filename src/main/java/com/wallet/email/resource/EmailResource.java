package com.wallet.email.resource;

import org.simplejavamail.email.Email;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.config.TransportStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
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

    @GET
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public Response test() {
        Email email = new Email();
        email.setFromAddress("zxqiu", "zxqiu90@gmail.com");
        email.addRecipient("zxqiu", "zxqiu90@gmail.com", Message.RecipientType.TO);
        email.setSubject("test email");
        email.setText("walletnote.com test email");

        logger_.info("sending test email ... ");
        new Mailer("smtp.gmail.com", 465, "", ""
                , TransportStrategy.SMTP_SSL).sendMail(email);
        logger_.info("test email sent ");

        return Response.ok().build();
    }
}
