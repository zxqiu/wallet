package com.wallet.healthCheck.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by zxqiu on 4/22/17.
 */

@Path("/health")
public class serverCheckResource {
    @Path("/echo")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response echo() {
        return Response.ok().build();
    }
}
