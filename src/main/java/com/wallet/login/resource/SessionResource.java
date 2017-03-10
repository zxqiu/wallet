package com.wallet.login.resource;

import java.net.URI;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.wallet.login.core.Session;
import com.wallet.login.dao.SessionDAOConnector;
import com.wallet.login.dao.UserDAOConnector;
import com.wallet.service.WalletConfiguration;
import com.wallet.utils.misc.ApiUtils;
import com.wallet.utils.misc.NameDef;

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

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Object login(
        @FormParam(NameDef.USER_ID) String user_id,
        @FormParam(NameDef.PASSWORD) String password) throws Exception {

        if (userDAOC.getByIDAndPassword(user_id, password) == null) {
        	return Response.status(Status.ERROR)
        			.entity(ApiUtils.buildJSONResponse(false, "user id or password error"))
        			.build();
        }

        Session session = new Session(user_id);
        sessionDAOC.insert(session);

        //return session;
        URI uri = UriBuilder.fromUri("/views/booksList.html").build();
        Cookie cookie = new Cookie("walletSessionCookie",
        		session.getUser_id() + ":" + session.getAccess_token(),
        		"/", WalletConfiguration.getHostName());
        NewCookie cookies = new NewCookie(cookie);
        return Response.seeOther(uri).cookie(cookies).build();
    }
    
    @POST
    @Timed
    @Path("/restoresession")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Object restoreSession(
        @FormParam(NameDef.SESSION_COOKIE) String session_cookie) throws Exception {
    	URI uri = UriBuilder.fromUri("/views/login.html").build();
        
    	if (session_cookie == null || session_cookie.length() == 0) {
    		return Response.seeOther(uri).build();
    	}
    	
    	String param[] = session_cookie.split(":");
    	if (param.length < 2) {
    		logger_.error("Error processing form data : " + session_cookie);
    		return Response.seeOther(uri).build();
    	}
    	
    	String user_id = param[0];
    	String access_token = param[1];
    	
    	if (sessionDAOC.getByUserIDAndAccessToken(user_id, access_token) == null) {
    		return Response.seeOther(uri).build();
    	}
    	
        uri = UriBuilder.fromUri("/views/booksList.html").build();
        return Response.seeOther(uri).build();
    }

    @GET
    @Timed
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public Response index() {
        return Response.serverError().entity(views.index.template("world")).build();
    }

}