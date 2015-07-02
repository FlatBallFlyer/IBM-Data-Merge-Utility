package com.ibm.util.merge.web.rest.sample;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@Path("/resources")
public class ResourcesServlet {

    private static final Logger log = Logger.getLogger(ResourcesServlet.class);

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
    public Response newTodo(@FormParam("name") String name, @Context HttpServletResponse servletResponse) throws IOException {
        SampleResource resource = new SampleResource();
        resource.setName(name);
        repo.persist(resource);
        URI absoluteURI=uriInfo.getRequestUriBuilder().path("/" + resource.getName()).build();
        log.info("New Item URI = " + absoluteURI);
        return Response.created(absoluteURI).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newTodoFromJson(SampleResource resource) throws IOException {
        repo.persist(resource);
        URI absoluteURI=uriInfo.getRequestUriBuilder().path("/" + resource.getName()).build();
        log.info("New Item URI = " + absoluteURI);
        return Response.created(absoluteURI).build();
    }


    @Path("{name}")
    public ResourceServlet getData(@PathParam("name") String name) {
        return new ResourceServlet(uriInfo, request, name, repo);
    }
}

