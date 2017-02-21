package com.project.wallet.service;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
public class TestGetResource {
    private final String name_;

    public TestGetResource(String name) {
        this.name_ = name;
    }

    @GET
    @Timed
	@Produces(value = MediaType.APPLICATION_JSON)
    public Response query(@Context UriInfo ui) {
    	System.out.println("Hello");
    	return Response.status(200).entity(name_).build();		
    }
}