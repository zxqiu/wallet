package user.service;

import java.sql.SQLException;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import books.service.data.BooksTable;
import user.service.data.UserInfo;
import user.service.data.UserPriority;
import user.service.data.UserTable;
import utils.ApiUtils;
import utils.NameDef;
import utils.TimeUtils;

@Path("/user")
public class UserService {
	private static final Logger logger_ = LoggerFactory.getLogger(UserService.class);
	
	/**
	 * Create a new user and insert to user table
	 * @param id, name, password, picture_url(optional)
	 * @return
	 */
	@POST
    @Timed
    @Path("/insertuser")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Response insertItem(@Valid UserPostJsonObj request) {
		if (request == null) {
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		long currentTimeMS = TimeUtils.getUniqueTimeStampInMS();
		logger_.info(request.toString());
		
		// 1. extract request
		// 2. verify and parse request
		// 3. verify parameters 
		if (request.id == null || request.id.length() == 0 ||
				request.name == null || request.name.length() == 0 ||
				request.password == null || request.password.length() == 0) {
			logger_.error("ERROR: invalid new item request: " + request.toString());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		// 4. transaction
		UserInfo user = null;
		try {
			user = UserTable.instance().getUser(request.id);
		} catch (SQLException e) {
			e.printStackTrace();
			logger_.error("Error find out if user already exists : " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}
		
		
		if (user != null) {
			if (!request.name.equals(user.getName())) {
				user.setName(request.name);
			}
			if (!request.password.equals(user.getPassword())) {
				user.setPassword(request.password);
			}
			if (!request.picture_url.equals(user.getPicture_url())) {
				user.setPicture_url(request.picture_url);
			}
			try {
				logger_.info("Update user : " + user.toMap().toString());
				UserTable.instance().updateUser(user);
			} catch (SQLException e) {
				e.printStackTrace();
				logger_.error("Error update user \'" + user.getId() + "\' : " + e.getMessage());
				return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
			}
		} else {
			user = new UserInfo(request.id, request.name, request.password, UserPriority.NORMAL, currentTimeMS, request.picture_url);
			try {
				logger_.info("Insert new user : " + user.toMap().toString());
				UserTable.instance().insertNewUser(user);
			} catch (SQLException e) {
				e.printStackTrace();
				logger_.error("Error insert user \'" + user.getId() + "\' : " + e.getMessage());
				return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
			}
		}
		
		return Response.status(200).entity(ApiUtils.buildJSONResponse(true, ApiUtils.SUCCESS)).build();
	}
	
	public static class UserPostJsonObj {
		@JsonProperty(NameDef.ID)
		String id;
		@JsonProperty(NameDef.NAME)
		String name;
		@JsonProperty(NameDef.PASSWORD)
		String password;
		@JsonProperty(NameDef.PICTURE_URL)
		String picture_url;
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
	                .add(NameDef.ID, id)
	                .add(NameDef.NAME, name)
	                .add(NameDef.PASSWORD, password)
	                .add(NameDef.PICTURE_URL, picture_url)
	                .toString();
		}
	}
	
	/**
	 * Login
	 * @param user_id, password
	 * @return
	 */
	@GET
    @Timed
    @Path("/login")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Response deleteItem(@QueryParam(NameDef.ID) String id,
			@QueryParam(NameDef.PASSWORD) String password) {
		boolean success = false;
		
		// 1. extract request
		// 2. verify and parse request
		// 3. verify parameters 
		if (id == null || id.length() == 0 || password == null || password.length() == 0) {
			logger_.error("ERROR: invalid login request for \'" + id + "\'");
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.QUERY_ARG_ERROR)).build();
		}
		
		// 4. transaction
		UserInfo user = null;
		try {
			user = UserTable.instance().getUser(id);
		} catch (SQLException e) {
			e.printStackTrace();
			logger_.error("Error : failed to get User Info from DB \'" + id + "\' : " + e.getMessage());
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.INTERNAL_ERROR)).build();
		}
		
		if (user != null && user.getPassword().equals(password)) {
			success = true;
		}
		
		// 5. generate response
		
		if (success) {
			logger_.info("Login success \'" + id + "\'");
			return Response.status(200).entity(ApiUtils.buildJSONResponse(true, ApiUtils.SUCCESS)).build();
		} else {
			logger_.info("Login fail \'" + id + "\'");
			return Response.status(500).entity(ApiUtils.buildJSONResponse(false, ApiUtils.FAIL)).build();
		}
	}
}
