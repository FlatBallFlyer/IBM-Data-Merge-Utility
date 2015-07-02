package com.ibm.util.merge.web.rest.app;

/**
 *
 */

import com.ibm.util.merge.web.rest.sample.SampleResource;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.bind.JAXBElement;

public class CollectionResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    String id;


    @Inject
    public CollectionResource(UriInfo uriInfo, Request request, String id) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public String getData() {
        return "TODO";
    }

    @GET
    @Produces(MediaType.TEXT_XML)
    public String getForBrowser() {
        return "TODO";
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response update(JAXBElement<SampleResource> todo) {
        return Response.noContent().build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateJSON(SampleResource todo) {
        return Response.noContent().build();
    }

    @DELETE
    public Response delete() {
        return Response.noContent().build();
    }
}


