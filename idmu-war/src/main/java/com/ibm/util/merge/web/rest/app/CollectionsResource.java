package com.ibm.util.merge.web.rest.app;
/**
 *
 */

import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Path("/collections")
public class CollectionsResource {
    private static final Logger log = Logger.getLogger(CollectionsResource.class);
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    @GET
    @Produces(MediaType.TEXT_XML)
    public List<String> getTodosBrowser() {
        log.info("TODO LIST");
        return new ArrayList<>();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<String> getTodos() {
        log.info("TODO LIST");
        return new ArrayList<>();
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCount() {
        return 0 + "";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newTodoFromJson(String collection) throws IOException {
        URI absoluteURI = uriInfo.getRequestUriBuilder().path("/" + collection).build();
        log.info("TODO CREATE NEW " + absoluteURI);
        return Response.created(absoluteURI).build();
    }

    @Path("{name}")
    public CollectionResource getData(@PathParam("name") String name) {
        return new CollectionResource(uriInfo, request, name);
    }
}
