package com.wallet.book.resource;

/**
 * Created by kangli on 4/13/17.
 */

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class Api extends ResourceConfig {
    public Api() {
        packages("com.wallet.book.resource");
        register(MultiPartFeature.class);
    }
}
