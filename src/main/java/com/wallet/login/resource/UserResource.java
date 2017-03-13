package com.wallet.login.resource;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.wallet.login.core.User;
import com.wallet.login.dao.UserDAOConnector;
import com.wallet.utils.misc.ApiUtils;

@Path("/users")
public class UserResource {

    private UserDAOConnector userDAOC;

    public UserResource() throws Exception {
        super();
        this.userDAOC = UserDAOConnector.instance();
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
        if (user_id != null) {
            user = userDAOC.getByID(user_id);
        }

        return Response.ok().entity(views.user.template(user)).build();
    }

    @POST
    @Path("insertuser")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response insertUserView() throws Exception {
        return null;
    }
}