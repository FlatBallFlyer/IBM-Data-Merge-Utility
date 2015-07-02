package com.ibm.util.merge.web.rest.sample;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;



@ApplicationPath("/test")
public class TestApp extends Application {
    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(Arrays.asList(ResourcesServlet.class, ResourceServlet.class));
    }
}
