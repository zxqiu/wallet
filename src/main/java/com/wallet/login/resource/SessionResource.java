package com.wallet.login.resource;

import java.net.URI;
import java.util.Date;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.wallet.books.resource.BooksEntryResource;
import com.wallet.login.core.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.wallet.login.core.Session;
import com.wallet.login.dao.SessionDAOConnector;
import com.wallet.login.dao.UserDAOConnector;
import com.wallet.utils.misc.ApiUtils;
import com.wallet.utils.misc.Dict;

import ch.qos.logback.core.status.Status;

@Path("/")
public class SessionResource {
	private static final Logger logger_ = LoggerFactory.getLogger(SessionResource.class);

    private UserDAOConnector userDAOC;
    private SessionDAOConnector sessionDAOC;

    public SessionResource() throws Exception {
        super();
        this.userDAOC = UserDAOConnector.instance();
        this.sessionDAOC = SessionDAOConnector.instance();
    }

    public static final String PATH_LOGIN = "login";
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Object login(
        @FormParam(Dict.ID) String id,
        @FormParam(Dict.PASSWORD) String password) throws Exception {

        String user_id = "";
        if (userDAOC.getByIDAndPassword(id, password) != null) {
            user_id = id;
        } else {
            User user = userDAOC.getByEmailAndPassword(id, password);
            if (user == null) {
                return Response.status(Status.ERROR)
                        .entity(ApiUtils.buildJSONResponse(false, "user id or password error"))
                        .build();
            }
            user_id = user.getUser_id();
        }

        Session session = new Session(user_id);
        sessionDAOC.insert(session);

        Cookie cookie = new Cookie("walletSessionCookie",
        		session.getUser_id() + ":" + session.getAccess_token());
        NewCookie cookies = new NewCookie(cookie);

        return Response
                .seeOther(URI.create(BooksEntryResource.PATH_BOOKS))
                .cookie(cookies)
                .build();
    }

    @POST
    @Path("/fblogin")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Object fbLogin(
            @FormParam(Dict.USER_ID) String user_id,
            @FormParam(Dict.ACCESS_TOKEN) String access_token,
            @FormParam(Dict.NAME) String name,
            @FormParam(Dict.EMAIL) String email) throws Exception {

        if (sessionDAOC.getByUserIDAndAccessToken(Dict.FACEBOOK_PREFIX + user_id,
                        Dict.FACEBOOK_PREFIX + access_token)
                != null) {
            return Response
                    .seeOther(URI.create(BooksEntryResource.PATH_BOOKS))
                    .build();
        }

        Session session = new Session(Dict.FACEBOOK_PREFIX + user_id);
        session.setAccess_token(Dict.FACEBOOK_PREFIX + access_token);
        sessionDAOC.insert(session);

        Cookie cookie = new Cookie("walletSessionCookie",
                session.getUser_id() + ":" + session.getAccess_token());
        NewCookie cookies = new NewCookie(cookie);

        return Response
                .seeOther(URI.create(BooksEntryResource.PATH_BOOKS))
                .cookie(cookies)
                .build();
    }

    public static final String PATH_LOGOUT = "logout";
    @GET
    @Timed
    @Path("/logout")
    @Produces(MediaType.TEXT_HTML)
    public Object logout(@CookieParam("walletSessionCookie") Cookie cookie) {
        String param[] = cookie.getValue().split(":");
        if (param.length < 2 || param[0].length() == 0 || param[1].length() == 0) {
            return false;
        }

        try {
            sessionDAOC.deleteByAccessToken(param[1]);
        } catch (Exception e) {
            logger_.info("Logout unexpected session : " + e.getMessage());
            e.printStackTrace();
        }

        return Response.ok().entity(views.login.template()).build();
    }

    public static final String PATH_RESTORE_SESSION = "/";
    @GET
    @Timed
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public Object restoreSession(
            @CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
    	if (sessionDAOC.verifySessionCookie(cookie)) {
            return Response
                    .seeOther(URI.create(BooksEntryResource.PATH_BOOKS))
                    .build();
    	}

        return Response.ok().entity(views.login.template()).build();
    }
}
