package com.ibm.util.merge.web;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.bind.JAXBElement;

public class ResourceServlet {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    String id;
    private SampleResourcesRepository repo = new SampleResourcesRepository();

    @Inject
    public ResourceServlet(UriInfo uriInfo, Request request, String id, SampleResourcesRepository repo) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
        this.repo = repo;
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public SampleResource getData() {
        SampleResource todo = repo.get(id);
        if (todo == null) throw new RuntimeException("Get: Todo with " + id + " not found");
        return todo;
    }

    @GET
    @Produces(MediaType.TEXT_XML)
    public SampleResource getForBrowser() {
        SampleResource todo = repo.get(id);
        if (todo == null) throw new RuntimeException("Get: Todo with " + id + " not found");
        return todo;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response update(JAXBElement<SampleResource> todo) {
        SampleResource c = todo.getValue();
        Response res;
        if (repo.get(id) == null) {
            res = Response.noContent().build();
        } else {
            repo.merge(c);
            res = Response.ok(uriInfo.getAbsolutePath()).build();
        }
        return res;
    }

    @DELETE
    public void delete() {
        SampleResource c = repo.remove(id);
        if (c == null) throw new RuntimeException("Delete: Todo with " + id + " not found");
    }
}

