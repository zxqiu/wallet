package com.wallet.login.resource;

import java.net.URI;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.wallet.books.core.Books;
import com.wallet.login.core.User;
import com.wallet.login.core.UserPriority;
import com.wallet.login.dao.SessionDAOConnector;
import com.wallet.login.dao.UserDAOConnector;
import com.wallet.utils.misc.ApiUtils;
import com.wallet.utils.misc.Dict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/users")
public class UserResource {
    private static final Logger logger_ = LoggerFactory.getLogger(UserResource.class);

    private UserDAOConnector userDAOC;
    private SessionDAOConnector sessionDAOC;

    public UserResource() throws Exception {
        super();
        this.userDAOC = UserDAOConnector.instance();
        this.sessionDAOC = SessionDAOConnector.instance();
    }

    @GET
    @Path("/getall")
    @Produces(value = MediaType.APPLICATION_JSON)
    public List<User> fetch() throws Exception {
        return userDAOC.getAll();
        //return null;
    }

    @GET
    @Path("/insertuserview")
    @Produces(MediaType.TEXT_HTML)
    public Response insertUserView(@CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
        String user_id = ApiUtils.getUserIDFromCookie(cookie);
        User user = null;
        if (user_id != null && sessionDAOC.verifySessionCookie(cookie)) {
            user = userDAOC.getByID(user_id);
        }

        return Response.ok().entity(views.user.template(user)).build();
    }

    @POST
    @Path("insertuser")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response insertUser(
            @FormParam(Dict.USER_ID) String user_id,
            @FormParam(Dict.EMAIL) String email,
            @FormParam(Dict.NAME) String name,
            @FormParam(Dict.PASSWORD) String password,
            @CookieParam("walletSessionCookie") Cookie cookie
        ) throws Exception {

        if (user_id != null && user_id.length() > 1 &&
                sessionDAOC.verifySessionCookie(cookie)) {
            User user = userDAOC.getByID(user_id);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            user.setEmail(email);
            user.setName(name);
            if (password != null && password.length() > 0) {
                user.setPassword(password);
            }

            logger_.info("Update user : " + user_id);
            userDAOC.update(user);

        } else if ((user_id == null || user_id.length() == 0)
                && userDAOC.getByEmail(email) == null) {
            User user = new User(email, password, name, UserPriority.NORMAL.name(), "");
            logger_.info("Insert user : " + user_id);
            userDAOC.insert(user);
        }

        return Response.seeOther(URI.create(SessionResource.PATH_RESTORE_SESSION)).build();
    }
}