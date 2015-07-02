package com.ibm.util.merge.web.rest.app;

/**
 *
 */

import com.ibm.util.merge.Template;
import com.ibm.util.merge.web.rest.sample.SampleResource;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.bind.JAXBElement;

public class TemplateResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    String id;

    @Inject
    public TemplateResource(UriInfo uriInfo, Request request, String id) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;

    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Template getData() {
        return null;
    }

    @GET
    @Produces(MediaType.TEXT_XML)
    public Template getForBrowser() {
        return null;
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


