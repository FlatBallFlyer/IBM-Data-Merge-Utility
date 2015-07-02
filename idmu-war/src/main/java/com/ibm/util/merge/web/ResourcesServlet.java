package com.ibm.util.merge.web;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.List;

@Path("/resources")
public class ResourcesServlet {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    private SampleResourcesRepository repo = new SampleResourcesRepository();

    @GET
    @Produces(MediaType.TEXT_XML)
    public List<SampleResource> getTodosBrowser() {
        List<SampleResource> todos = repo.list();
        return todos;
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<SampleResource> getTodos() {
        return repo.list();
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCount() {
        return repo.count() + "";
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void newTodo(@FormParam("name") String name, @Context HttpServletResponse servletResponse) throws IOException {
        SampleResource todo = new SampleResource();
        todo.setName(name);
        repo.persist(todo);
        servletResponse.sendRedirect("../create_todo.html");
    }

    @Path("{name}")
    public ResourceServlet getData(@PathParam("name") String name) {
        return new ResourceServlet(uriInfo, request, name, repo);
    }
}

