package com.wallet.login.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.wallet.login.core.User;
import com.wallet.login.dao.UserDAOConnector;

@Path("/users")
@Produces(value = MediaType.APPLICATION_JSON)
public class UserResource {

    private UserDAOConnector userDAOC;

    public UserResource() throws Exception {
        super();
        this.userDAOC = UserDAOConnector.instance();
    }

    @GET
    @Path("getall")
    public List<User> fetch() throws Exception {
        return userDAOC.getAll();
    }
}