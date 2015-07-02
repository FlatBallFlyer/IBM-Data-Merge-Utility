package com.ibm.util.merge.web.rest.app;
/**
 *
 */

import com.ibm.util.merge.Template;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Path("/templates")
public class TemplatesResource {
    private static final Logger log = Logger.getLogger(TemplatesResource.class);
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    @GET
    @Produces(MediaType.TEXT_XML)
    public List<Template> getTodosBrowser() {
        return new ArrayList<>();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Template> getTodos() {
        return new ArrayList<>();
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCount() {
        return 0 + "";
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response newTodo(@FormParam("name") String name, @Context HttpServletResponse servletResponse) throws IOException {
        return Response.noContent().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newTodoFromJson(Template resource) throws IOException {
        return Response.noContent().build();
    }

    @Path("{name}")
    public TemplateResource getData(@PathParam("name") String name) {
        return new TemplateResource(uriInfo, request, name);
    }
}
