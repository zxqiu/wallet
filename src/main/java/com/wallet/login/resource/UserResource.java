package com.wallet.login.resource;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.wallet.login.core.User;
import com.wallet.login.core.UserPriority;
import com.wallet.login.dao.SessionDAOConnector;
import com.wallet.login.dao.UserDAOConnector;
import com.wallet.utils.misc.ApiUtils;
import com.wallet.utils.misc.NameDef;

@Path("/users")
public class UserResource {

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
            @FormParam(NameDef.USER_ID) String user_id,
            @FormParam(NameDef.EMAIL) String email,
            @FormParam(NameDef.NAME) String name,
            @FormParam(NameDef.PASSWORD) String password,
            @CookieParam("walletSessionCookie") Cookie cookie
        ) throws Exception {

        if (user_id != null && user_id.length() > 1 &&
                sessionDAOC.verifySessionCookie(cookie)) {
            User user = userDAOC.getByID(user_id);
            user.setEmail(email);
            user.setName(name);
            user.setPassword(password);

            userDAOC.update(user);

        } else {
            User user = new User(email, password, name, UserPriority.NORMAL.name());
            userDAOC.insert(user);
        }

        return Response.ok().entity((views.login.template())).build();
    }
}