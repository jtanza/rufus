package com.tanza.rufus.resources;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ResourceUtils {
    private ResourceUtils() {throw new AssertionError();}

    public static Response badRequest(String message) {
        return Response.status(Response.Status.BAD_REQUEST)
            .type(MediaType.TEXT_PLAIN)
            .entity(message)
            .build();
    }
}
