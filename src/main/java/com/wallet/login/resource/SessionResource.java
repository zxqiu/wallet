package com.wallet.login.resource;

import java.net.URI;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.wallet.book.resource.BookEntryResource;
import com.wallet.login.core.User;
import com.wallet.login.core.UserPriority;
import org.json.JSONObject;
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
            @FormParam(Dict.REDIRECT) String redirect,
            @FormParam(Dict.ID) String id,
            @FormParam(Dict.PASSWORD) String password) throws Exception {

        if (id.startsWith(Dict.FACEBOOK_PREFIX) || password.startsWith(Dict.FACEBOOK_PREFIX)) {
            return Response.status(Status.ERROR)
                    .entity(ApiUtils.buildJSONResponse(false, "user id or password error"))
                    .build();
        }

        String user_id;
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

        logger_.info("User " + user_id + " login with session + " + session.getAccess_token());
        String redirect_path = BookEntryResource.PATH_BOOKS;
        if (redirect != null && redirect.length() > 1) {
            redirect_path = redirect;
        }
        return Response
                .seeOther(URI.create(redirect_path))
                .cookie(cookies)
                .build();
    }

    @POST
    @Path("/fblogin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Object fbLogin(
            String request
            ) throws Exception {
        JSONObject fbUser = new JSONObject(request);
        String user_id = Dict.FACEBOOK_PREFIX + fbUser.getString(Dict.USER_ID);
        String access_token = Dict.FACEBOOK_PREFIX + fbUser.getString(Dict.ACCESS_TOKEN);
        String email = Dict.FACEBOOK_PREFIX + fbUser.getString(Dict.EMAIL);
        String name = fbUser.getString(Dict.NAME);

        if (sessionDAOC.getByUserIDAndAccessToken(user_id,
                        access_token)
                != null) {
            return Response
                    .seeOther(URI.create(BookEntryResource.PATH_BOOKS))
                    .build();
        }

        if (userDAOC.getByID(user_id) == null) {
            User user = new User(user_id, email, Dict.FACEBOOK_PREFIX + System.currentTimeMillis(), name, UserPriority.NORMAL.name());
            try {
                userDAOC.insert(user);
                logger_.info("Insert new user " + user_id + ", " + name);
            } catch (Exception e) {
                logger_.error("Error failed to create new user for FaceBook user " + user_id + ", " + name);
                e.printStackTrace();
                return Response.status(Status.ERROR)
                        .entity(ApiUtils.buildJSONResponse(false, "failed to create new user for " + name))
                        .build();
            }
        }

        Session session = new Session(user_id);
        session.setAccess_token(access_token);
        sessionDAOC.insert(session);

        Cookie cookie = new Cookie("walletSessionCookie",
                session.getUser_id() + ":" + session.getAccess_token());
        NewCookie cookies = new NewCookie(cookie);

        logger_.info("User " + user_id + " login with session + " + session.getAccess_token());
        return Response
                .ok()
                .entity(ApiUtils.buildJSONResponse(true, "FaceBook login success"))
                .cookie(cookies)
                .build();
    }

    public static final String PATH_LOGOUT = "logout";
    @GET
    @Timed
    @Path("/logout")
    @Produces(MediaType.TEXT_HTML)
    public Object logout(@CookieParam("walletSessionCookie") Cookie cookie) {
        String access_token = ApiUtils.getSessionKeyFromCookie(cookie);
        if (access_token != null) {
            try {
                sessionDAOC.deleteByAccessToken(access_token);
                logger_.info("Logout session " + access_token);
            } catch (Exception e) {
                logger_.info("Logout unexpected session : " + e.getMessage());
                e.printStackTrace();
            }

            NewCookie cookies = new NewCookie(cookie, null, 0, false);
            return Response.ok().entity(views.login.template("")).cookie(cookies).build();
        }

        return Response.ok().entity(views.login.template("")).build();
    }

    public static final String PATH_RESTORE_SESSION = "/";
    @GET
    @Timed
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public Object restoreSession(
            @CookieParam("walletSessionCookie") Cookie cookie) throws Exception {
    	if (sessionDAOC.verifySessionCookie(cookie)) {
    	    logger_.info("Session restored " + cookie.getValue());
            return Response
                    .seeOther(URI.create(BookEntryResource.PATH_BOOKS))
                    .build();
    	}

        return Response.ok().entity(views.login.template("")).build();
    }


    @GET
    @Timed
    @Path("/allsessions")
    public List<Session> restoreSession() throws Exception {
        return sessionDAOC.getAll();
    }
}
